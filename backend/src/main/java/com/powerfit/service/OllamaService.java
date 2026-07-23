package com.powerfit.service;

import com.powerfit.entity.Cliente;
import com.powerfit.entity.Membresia;
import com.powerfit.entity.Pago;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.MembresiaRepository;
import com.powerfit.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OllamaService {

    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${ollama.model:qwen2.5:0.5b}")
    private String modelo;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;
    private final PagoRepository pagoRepository;

    // ── Prompts por rol ──────────────────────────────────────────────

    private static final String SISTEMA_ADMIN = """
        Eres PowerFit AI, el asistente administrativo del gimnasio PowerFit.

        Tu rol: ayudar al ADMINISTRADOR con datos del negocio, estadísticas, reportes y gestión.

        REGLAS:
        1. Responde SIEMPRE en español.
        2. Usa los datos reales que te proporciono del sistema.
        3. Sé conciso: máximo 3-4 oraciones.
        4. Da números exactos, nunca inventes datos.
        5. Si no tienes el dato di "no tengo ese dato disponible".
        6. Puedes responder sobre: clientes, membresías, pagos, ingresos, egresos, sucursales, equipos, entrenadores, reportes financieros.
        7. Para reportes ejecutivos incluye: resumen, tendencias, recomendaciones.
        8. NO des consejos de ejercicio o nutrición — eso es del dominio del entrenador.
        """;

    private static final String SISTEMA_CLIENTE = """
        Eres PowerFit AI, el asistente de bienestar del gimnasio PowerFit.

        Tu rol: ayudar al CLIENTE con recomendaciones de ejercicio, nutrición y información de su membresía.

        REGLAS:
        1. Responde SIEMPRE en español.
        2. Sé amigable, motivador y profesional.
        3. Máximo 4-5 oraciones.
        4. Puedes responder sobre:
           - Ejercicios por grupo muscular (pecho, espalda, piernas, brazos, hombros, abdomen)
           - Rutinas de entrenamiento (fuerza, cardio, funcional, hipertrofia, pérdida de peso)
           - Nutrición básica (proteínas, carbohidratos, grasas, hidratación, comidas pre/post entrenamiento)
           - Técnicas de ejercicio correctas
           - Información de su membresía y pagos
           - Horarios y servicios del gimnasio
        5. NO des diagnósticos médicos. Si el usuario tiene una lesión o condición, recomienda consultar a un profesional.
        6. Adapta las recomendaciones al nivel del usuario (principiante, intermedio, avanzado).
        7. Si preguntan por datos del sistema (cuántos clientes, pagos, etc.), di que esa información es solo para administradores.
        """;

    private static final String SISTEMA_ENTRENADOR = """
        Eres PowerFit AI, el asistente técnico del gimnasio PowerFit.

        Tu rol: ayudar al ENTRENADOR con programación de entrenamiento, ejercicios y seguimiento de clientes.

        REGLAS:
        1. Responde SIEMPRE en español.
        2. Sé técnico pero claro. Usa terminología fitness correcta.
        3. Máximo 5-6 oraciones.
        4. Puedes responder sobre:
           - Programación de entrenamiento (periodización, volumen, intensidad, frecuencia)
           - Ejercicios detallados (ejecución, músculos implicados, variaciones, errores comunes)
           - Nutrición deportiva (macros, timing nutricional, suplementación básica)
           - Evaluación física (mediciones, tests, progresos)
           - Planes de entrenamiento personalizados
           - Prevención de lesiones
           - Técnicas de calentamiento y vuelta a la calma
           - Información de clientes asignados
        5. Considera el nivel del cliente: principiante (0-6 meses), intermedio (6-24 meses), avanzado (2+ años).
        6. Si preguntan por datos administrativos (pagos, sucursales), di que esa información es del administrador.
        """;

    // ── Chat principal ───────────────────────────────────────────────

    public String chat(String mensaje, String contexto) {
        try {
            StringBuilder prompt = new StringBuilder();
            if (contexto != null && !contexto.isBlank()) {
                prompt.append(contexto).append("\n\n");
            }
            prompt.append("Usuario: ").append(mensaje).append("\nAsistente:");

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelo);
            body.put("prompt", prompt.toString());
            body.put("stream", false);
            body.put("options", Map.of("temperature", 0.3, "num_predict", 300));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(ollamaUrl + "/api/generate", request, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("response")) {
                return response.getBody().get("response").toString();
            }
            return "No se pudo obtener respuesta del modelo.";
        } catch (Exception e) {
            return "Error al conectar con Ollama: " + e.getMessage();
        }
    }

    // ── Chat con datos (ahora acepta rol) ────────────────────────────

    public String chatConDatos(String mensaje, String rol) {
        String contextoLower = mensaje.toLowerCase();
        String datosContexto = construirContexto(contextoLower, rol);
        String systemPrompt = getSystemPrompt(rol);

        String prompt = systemPrompt + "\n\n" +
            "=== DATOS REALES DE LA BASE DE DATOS ===\n" +
            datosContexto + "\n" +
            "=== FIN DE DATOS ===\n\n" +
            "PREGUNTA DEL USUARIO: " + mensaje + "\n" +
            "RESPUESTA:";

        return chat(mensaje, prompt);
    }

    // Legacy: sin rol (backward compatibility)
    public String chatConDatos(String mensaje) {
        return chatConDatos(mensaje, "admin");
    }

    private String getSystemPrompt(String rol) {
        if (rol == null) return SISTEMA_ADMIN;
        return switch (rol.toLowerCase()) {
            case "cliente" -> SISTEMA_CLIENTE;
            case "entrenador" -> SISTEMA_ENTRENADOR;
            default -> SISTEMA_ADMIN;
        };
    }

    // ── Construcción de contexto por rol ──────────────────────────────

    private String construirContexto(String pregunta, String rol) {
        StringBuilder ctx = new StringBuilder();
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String rolLower = rol != null ? rol.toLowerCase() : "admin";

        // ── CLIENTE: solo info de membresía y pagos propios ──
        if ("cliente".equals(rolLower)) {
            ctx.append("INFORMACIÓN DEL GIMNASIO POWERFIT:\n");
            ctx.append("- Servicios: gimnasio, clases grupales, entrenamiento personal\n");
            ctx.append("- Planes: Básico (30 días, $25), Premium (30 días, $45), Élite (30 días, $75)\n");
            ctx.append("- Horario: Lunes a Viernes 6:00-22:00, Sábados 8:00-18:00\n");

            if (pregunta.contains("ejercicio") || pregunta.contains("entren") || pregunta.contains("muscul") ||
                pregunta.contains("pecho") || pregunta.contains("espalda") || pregunta.contains("pierna") ||
                pregunta.contains("brazo") || pregunta.contains("hombro") || pregunta.contains("abdomen") ||
                pregunta.contains("cardio") || pregunta.contains("rutina") || pregunta.contains("peso") ||
                pregunta.contains("fuerza") || pregunta.contains("hipertrofia") || pregunta.contains("perder") ||
                pregunta.contains("ganar") || pregunta.contains("tonificar")) {
                ctx.append("\nEl usuario pregunta sobre ejercicios/entrenamiento. ",
                    "Responde con recomendaciones específicas y seguras.\n");
            }
            if (pregunta.contains("comida") || pregunta.contains("nutri") || pregunta.contains("dieta") ||
                pregunta.contains("proteina") || pregunta.contains("calori") || pregunta.contains("comer")) {
                ctx.append("\nEl usuario pregunta sobre nutrición. ",
                    "Responde con consejos generales saludables. No des planes dietéticos específicos.\n");
            }
            return ctx.toString();
        }

        // ── ENTRENADOR: contexto técnico + datos de clientes ──
        if ("entrenador".equals(rolLower)) {
            ctx.append("ROL: ENTRENADOR DEL GIMNASIO POWERFIT\n");

            if (pregunta.contains("cliente") || pregunta.contains("ejercicio") || pregunta.contains("rutina") ||
                pregunta.contains("entren") || pregunta.contains("muscul") || pregunta.contains("pecho") ||
                pregunta.contains("espalda") || pregunta.contains("pierna") || pregunta.contains("fuerza") ||
                pregunta.contains("hipertrofia") || pregunta.contains("cardio") || pregunta.contains("plan")) {
                long totalClientes = clienteRepository.countByEstado(Cliente.EstadoGeneral.ACTIVO);
                Long membresiasActivas = membresiaRepository.countActivas(null);
                ctx.append("DATOS DEL GIMNASIO:\n");
                ctx.append("- Clientes activos: ").append(totalClientes).append("\n");
                ctx.append("- Membresías activas: ").append(membresiasActivas).append("\n");
                ctx.append("\nPuedes dar recomendaciones técnicas de entrenamiento.\n");
            }
            return ctx.toString();
        }

        // ── ADMIN: datos completos del negocio ──
        if (pregunta.contains("cliente") || pregunta.contains("miembros") || pregunta.contains("socios") || pregunta.contains("cuantos")) {
            long totalClientes = clienteRepository.countByEstado(Cliente.EstadoGeneral.ACTIVO);
            List<Cliente> sinMembresia = clienteRepository.sinMembresia(null, Pageable.unpaged()).getContent();
            ctx.append("CLIENTES:\n");
            ctx.append("- Total clientes activos: ").append(totalClientes).append("\n");
            ctx.append("- Clientes sin membresia activa: ").append(sinMembresia.size()).append("\n");
            if (!sinMembresia.isEmpty()) {
                String nombres = sinMembresia.stream()
                    .limit(10)
                    .map(Cliente::getNombreCompleto)
                    .collect(Collectors.joining(", "));
                ctx.append("- Ejemplo sin membresia: ").append(nombres).append("\n");
            }
        }

        if (pregunta.contains("membresia") || pregunta.contains("membresías") || pregunta.contains("plan") || pregunta.contains("vencida") || pregunta.contains("vencimiento")) {
            Long activas = membresiaRepository.countActivas(null);
            List<Membresia> vencidas = membresiaRepository.findBySucursalIdAndEstado(
                null, Membresia.EstadoMembresia.VENCIDA, PageRequest.of(0, 50)).getContent();
            ctx.append("MEMBRESIAS:\n");
            ctx.append("- Membresias activas: ").append(activas).append("\n");
            ctx.append("- Membresias vencidas: ").append(vencidas.size()).append("\n");
            if (!vencidas.isEmpty()) {
                String vencidasInfo = vencidas.stream()
                    .limit(10)
                    .map(m -> m.getCliente().getNombreCompleto() + " (fin: " + m.getFechaFin().format(fmt) + ")")
                    .collect(Collectors.joining(", "));
                ctx.append("- Clientes con membresia vencida: ").append(vencidasInfo).append("\n");
            }
        }

        if (pregunta.contains("pago") || pregunta.contains("ingreso") || pregunta.contains("dinero") || pregunta.contains("factura") || pregunta.contains("cobro") || pregunta.contains("transaccion") || pregunta.contains("transacciones")) {
            BigDecimal totalIngresos = pagoRepository.sumIngresos(null);
            BigDecimal promedioPago = pagoRepository.avgMonto();
            long totalPagos = pagoRepository.count();
            ctx.append("PAGOS:\n");
            ctx.append("- Total transacciones realizadas: ").append(totalPagos).append("\n");
            ctx.append("- Total ingresos: $").append(totalIngresos != null ? totalIngresos.setScale(2) : "0.00").append("\n");
            ctx.append("- Monto promedio por transaccion: $").append(promedioPago != null ? promedioPago.setScale(2) : "0.00").append("\n");
        }

        if (pregunta.contains("resumen") || pregunta.contains("estadistica") || pregunta.contains("reporte") || pregunta.contains("dashboard")) {
            long totalClientes = clienteRepository.countByEstado(Cliente.EstadoGeneral.ACTIVO);
            Long activas = membresiaRepository.countActivas(null);
            BigDecimal totalIngresos = pagoRepository.sumIngresos(null);
            ctx.append("RESUMEN GENERAL:\n");
            ctx.append("- Clientes activos: ").append(totalClientes).append("\n");
            ctx.append("- Membresias activas: ").append(activas).append("\n");
            ctx.append("- Ingresos totales: $").append(totalIngresos != null ? totalIngresos.setScale(2) : "0.00").append("\n");
            ctx.append("- Fecha de consulta: ").append(hoy.format(fmt)).append("\n");
        }

        if (pregunta.contains("sucursal") || pregunta.contains("sucursales") || pregunta.contains("sede") || pregunta.contains("sedes")) {
            ctx.append("SUCURSALES:\n");
            ctx.append("- El sistema maneja multiples sucursales. Consulta la lista desde el panel de administracion.\n");
        }

        if (ctx.isEmpty()) {
            long totalClientes = clienteRepository.countByEstado(Cliente.EstadoGeneral.ACTIVO);
            Long activas = membresiaRepository.countActivas(null);
            BigDecimal totalIngresos = pagoRepository.sumIngresos(null);
            long totalPagos = pagoRepository.count();
            ctx.append("DATOS GENERALES DEL GIMNASIO:\n");
            ctx.append("- Total clientes activos: ").append(totalClientes).append("\n");
            ctx.append("- Total membresias activas: ").append(activas).append("\n");
            ctx.append("- Total ingresos: $").append(totalIngresos != null ? totalIngresos.setScale(2) : "0.00").append("\n");
            ctx.append("- Total transacciones: ").append(totalPagos).append("\n");
            ctx.append("- Fecha: ").append(hoy.format(fmt)).append("\n");
        }

        return ctx.toString();
    }

    public String recomendarMembresia(String datosCliente) {
        String contexto = "Eres un asistente de un gimnasio llamado PowerFit. " +
            "Ayuda a recomendar planes de membresia basados en las necesidades del cliente. " +
            "Planes disponibles: Basico (30 dias, $25), Premium (30 dias, $45), Elite (30 dias, $75). " +
            "Responde en formato JSON con {\"plan\": \"nombre\", \"razon\": \"explicacion\"}.";
        return chat("Recomienda una membresia para este cliente: " + datosCliente, contexto);
    }

    public String resumenEjecutivo(String estadisticas) {
        String contexto = "Eres un analista de datos de un gimnasio llamado PowerFit. " +
            "Genera un resumen ejecutivo mensual conciso basado en las estadisticas. " +
            "Incluye: ingresos, egresos, balance, tendencias y recomendaciones. " +
            "Formato: texto plano en espanol, maximo 3 parrafos.";
        return chat("Genera un resumen ejecutivo con estas estadisticas: " + estadisticas, contexto);
    }

    public String analisisAsistencia(String datosAsistencia) {
        String contexto = "Eres un analista de retencion de clientes de un gimnasio. " +
            "Analiza el patron de asistencia y predice clientes en riesgo de abandonar. " +
            "Responde con recomendaciones para mejorar la retencion.";
        return chat("Analiza esta asistencia: " + datosAsistencia, contexto);
    }
}
