package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ForbiddenException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import com.powerfit.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entrenadores")
@RequiredArgsConstructor
@Tag(name = "Entrenadores")
public class EntrenadorController {

    private final EntrenadorRepository entrenadorRepo;
    private final SucursalRepository   sucursalRepo;
    private final UsuarioRepository    usuarioRepo;
    private final RolRepository        rolRepo;
    private final PasswordEncoder      passwordEncoder;
    private final ClienteRepository    clienteRepo;
    private final MembresiaRepository  membresiaRepo;
    private final SesionRepository     sesionRepo;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Entrenador>>> listar() {
        Long sucursalId = securityUtil.getSucursalIdEfectiva();
        return ResponseEntity.ok(ApiResponse.ok(entrenadorRepo.findBySucursal(sucursalId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Entrenador>> porId(@PathVariable Long id) {
        Entrenador entrenador = entrenadorRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id));
        Long sucursalId = securityUtil.getSucursalIdEfectiva();
        if (sucursalId != null && !entrenador.getSucursal().getId().equals(sucursalId)) {
            throw new ForbiddenException("No tienes acceso a entrenadores de otra sucursal");
        }
        return ResponseEntity.ok(ApiResponse.ok(entrenador));
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<ApiResponse<Entrenador>> porUsuario(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado para usuario: " + email))));
    }

    @GetMapping("/{id}/clientes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> clientesAsignados(@PathVariable Long id) {
        Entrenador entrenador = entrenadorRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id));
        Long sucursalId = securityUtil.getSucursalIdEfectiva();
        if (sucursalId != null && !entrenador.getSucursal().getId().equals(sucursalId)) {
            throw new ForbiddenException("No tienes acceso a entrenadores de otra sucursal");
        }

        List<Cliente> clientes = clienteRepo.findBySucursalId(entrenador.getSucursal().getId());

        List<Map<String, Object>> resultado = clientes.stream().map(c -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", c.getId());
            String[] partes = c.getNombreCompleto() != null ? c.getNombreCompleto().split(" ", 2) : new String[]{""};
            map.put("nombre", partes[0]);
            map.put("apellido", partes.length > 1 ? partes[1] : "");
            map.put("cedula", c.getDocumentoIdentidad());

            Optional<Membresia> membresiaOpt = membresiaRepo.findPrimeraActivaByClienteId(c.getId());
            if (membresiaOpt.isPresent()) {
                Membresia m = membresiaOpt.get();
                map.put("plan", m.getPlan() != null ? m.getPlan().getNombrePlan() : "");
                map.put("estado_membresia", m.getEstado().name().toLowerCase());
            } else {
                map.put("plan", "Sin plan");
                map.put("estado_membresia", "vencida");
            }

            Optional<Sesion> proximaSesion = sesionRepo.findFirstByEntrenadorIdAndFechaGreaterThanEqualOrderByFechaAsc(id, java.time.LocalDate.now());
            map.put("proxima_sesion", proximaSesion.map(Sesion::getFecha).orElse(null));

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Entrenador>> crear(@RequestBody Map<String, Object> body) {
        Long sucursalId = securityUtil.getSucursalIdEfectiva();

        Sucursal sucursal;
        if (sucursalId != null) {
            sucursal = sucursalRepo.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
        } else {
            Object sidObj = body.get("sucursalId");
            if (sidObj == null) throw new BadRequestException("sucursalId es obligatorio para registrar un entrenador");
            Long requestedId = Long.valueOf(sidObj.toString());
            sucursal = sucursalRepo.findById(requestedId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + requestedId));
        }

        // Soportar tanto documentoIdentidad como cedula
        String doc = body.get("documentoIdentidad") != null
            ? body.get("documentoIdentidad").toString()
            : (body.get("cedula") != null ? body.get("cedula").toString() : "");

        if (!doc.isBlank() && entrenadorRepo.existsByDocumentoIdentidad(doc))
            throw new BadRequestException("Documento ya registrado: " + doc);

        // Soportar tanto nombreCompleto como nombre+apellido
        String nombreCompleto = body.get("nombreCompleto") != null
            ? body.get("nombreCompleto").toString()
            : ((body.get("nombre") != null ? body.get("nombre").toString() : "") + " " +
               (body.get("apellido") != null ? body.get("apellido").toString() : "")).trim();

        String email = body.get("email") != null ? body.get("email").toString() : null;
        String contrasena = body.get("contrasena") != null ? body.get("contrasena").toString() : null;

        Entrenador e = Entrenador.builder()
            .sucursal(sucursal)
            .nombreCompleto(nombreCompleto)
            .documentoIdentidad(doc.isBlank() ? "SIN-DOC-" + System.currentTimeMillis() : doc)
            .especialidad(body.get("especialidad")   != null ? body.get("especialidad").toString()   : null)
            .telefono(body.get("telefono")           != null ? body.get("telefono").toString()       : null)
            .email(email)
            .fechaContratacion(body.get("fechaIngreso") != null ? LocalDate.parse(body.get("fechaIngreso").toString())
                             : body.get("fechaContratacion") != null ? LocalDate.parse(body.get("fechaContratacion").toString()) : null)
            .estado(Entrenador.EstadoGeneral.ACTIVO)
            .build();

        // Crear Usuario para login si se proporcionó email y contraseña
        if (email != null && !email.isBlank() && contrasena != null && !contrasena.isBlank()) {
            if (usuarioRepo.existsByEmail(email))
                throw new BadRequestException("Ya existe un usuario con ese correo: " + email);

            Rol rolEntrenador = rolRepo.findByNombreRol("ENTRENADOR")
                .orElseThrow(() -> new ResourceNotFoundException("Rol ENTRENADOR no encontrado"));

            Usuario usuario = Usuario.builder()
                .email(email)
                .nombreCompleto(nombreCompleto)
                .telefono(body.get("telefono") != null ? body.get("telefono").toString() : null)
                .passwordHash(passwordEncoder.encode(contrasena))
                .rol(rolEntrenador)
                .sucursal(sucursal)
                .estado(Usuario.EstadoGeneral.ACTIVO)
                .build();
            usuario = usuarioRepo.save(usuario);
            e.setUsuario(usuario);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(entrenadorRepo.save(e), "Entrenador registrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Entrenador>> actualizar(@PathVariable Long id,
                                                               @RequestBody Map<String, Object> body) {
        Entrenador e = entrenadorRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id));
        if (body.get("nombreCompleto") != null) e.setNombreCompleto(body.get("nombreCompleto").toString());
        if (body.get("especialidad")   != null) e.setEspecialidad(body.get("especialidad").toString());
        if (body.get("telefono")       != null) e.setTelefono(body.get("telefono").toString());
        if (body.get("horario")        != null) e.setHorario(body.get("horario").toString());
        if (body.get("email")          != null) e.setEmail(body.get("email").toString());
        if (body.get("fechaContratacion") != null || body.get("fechaIngreso") != null) {
            String fecha = body.get("fechaContratacion") != null
                ? body.get("fechaContratacion").toString()
                : body.get("fechaIngreso").toString();
            e.setFechaContratacion(LocalDate.parse(fecha));
        }

        // Actualizar usuario asociado si cambió el email
        if (body.get("email") != null && e.getUsuario() != null) {
            e.getUsuario().setEmail(body.get("email").toString());
            if (body.get("nombreCompleto") != null) {
                e.getUsuario().setNombreCompleto(body.get("nombreCompleto").toString());
            }
            usuarioRepo.save(e.getUsuario());
        }

        return ResponseEntity.ok(ApiResponse.ok(entrenadorRepo.save(e), "Entrenador actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        Entrenador e = entrenadorRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id));
        e.setEstado(Entrenador.EstadoGeneral.INACTIVO);
        entrenadorRepo.save(e);
        return ResponseEntity.ok(ApiResponse.ok(null, "Entrenador desactivado"));
    }
}
