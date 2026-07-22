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

    private static final String SISTEMA_BASE = """
        Eres PowerFit AI, el asistente del gimnasio PowerFit. Responde SIEMPRE en español.

        REGLAS ESTRICTAS:
        1. Te proporciono datos REALES y ACTUALES de la base de datos. USA ESOS DATOS para responder.
        2. NUNCA digas que no tienes acceso a la información si los datos están en el contexto.
        3. Responde con NÚMEROS EXACTOS basándote en los datos proporcionados.
        4. Sé conciso: máximo 2-3 oraciones.
        5. Si preguntan "cuántos clientes hay", responde el número exacto del contexto.
        6. Si preguntan "cuántas transacciones", responde el número exacto del contexto.
        7. Si preguntan por sucursales, responde con los datos que tengas.
        8. NUNCA inventes datos. Si no tienes la información específica, di "no tengo ese dato específico".
        """;

    public String chat(String mensaje, String contexto) {
        try {
            StringBuilder prompt = new StringBuilder();
            if (contexto != null && !contexto.isBlank()) {
                prompt.append("Contexto del sistema: ").append(contexto).append("\n\n");
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

    public String chatConDatos(String mensaje) {
        String contextoLower = mensaje.toLowerCase();
        String datosContexto = construirContexto(contextoLower);

        String prompt = SISTEMA_BASE + "\n\n" +
            "=== DATOS REALES DE LA BASE DE DATOS (usa estos números para responder) ===\n" +
            datosContexto + "\n" +
            "=== FIN DE DATOS ===\n\n" +
            "PREGUNTA DEL USUARIO: " + mensaje + "\n" +
            "RESPUESTA (usa los datos de arriba):";

        return chat(mensaje, prompt);
    }

    private String construirContexto(String pregunta) {
        StringBuilder ctx = new StringBuilder();
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (pregunta.contains("cliente") || pregunta.contains("miembros") || pregunta.contains("socios") || pregunta.contains("cuantos")) {
            long totalClientes = clienteRepository.countByEstado(Cliente.EstadoGeneral.ACTIVO);
            List<Cliente> sinMembresia = clienteRepository.sinMembresia(null, Pageable.unpaged()).getContent();
            ctx.append("CLIENTES:\n");
            ctx.append("- Total clientes activos: ").append(totalClientes).append("\n");
            ctx.append("- Clientes sin membresia activa: ").append(sinMembresia.size()).append("\n");
            if (!sinMembresia.isEmpty()) {
                String nombres = sinMembresia.stream()
                    .limit(10)
                    .map(c -> c.getNombreCompleto())
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
            ctx.append("- Monto promedio por transacción: $").append(promedioPago != null ? promedioPago.setScale(2) : "0.00").append("\n");
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
            ctx.append("- El sistema maneja sucursales. Consulta la lista desde el panel de administración.\n");
        }

        if (ctx.isEmpty()) {
            long totalClientes = clienteRepository.countByEstado(Cliente.EstadoGeneral.ACTIVO);
            Long activas = membresiaRepository.countActivas(null);
            BigDecimal totalIngresos = pagoRepository.sumIngresos(null);
            long totalPagos = pagoRepository.count();
            ctx.append("DATOS GENERALES DEL GIMNASIO:\n");
            ctx.append("- Total clientes activos: ").append(totalClientes).append("\n");
            ctx.append("- Total membresías activas: ").append(activas).append("\n");
            ctx.append("- Total ingresos: $").append(totalIngresos != null ? totalIngresos.setScale(2) : "0.00").append("\n");
            ctx.append("- Total transacciones: ").append(totalPagos).append("\n");
            ctx.append("- Fecha: ").append(hoy.format(fmt)).append("\n");
        }

        return ctx.toString();
    }

    public String recomendarMembresia(String datosCliente) {
        String contexto = "Eres un asistente de un gimnasio llamado PowerFit. " +
            "Ayuda a recomendar planes de membresia basados en las necesidades del cliente. " +
            "Planes disponibles: Basico (30 dias, $25), Premium (60 dias, $45), VIP (90 dias, $70). " +
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
