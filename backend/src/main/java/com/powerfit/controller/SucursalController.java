package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursales")
public class SucursalController {

    private final SucursalRepository  sucursalRepo;
    private final UsuarioRepository   usuarioRepo;
    private final RolRepository       rolRepo;
    private final PasswordEncoder     passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Sucursal>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(
            sucursalRepo.findByEstado(Sucursal.EstadoGeneral.ACTIVO)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sucursal>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(sucursalRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Sucursal>> crear(@RequestBody Map<String, Object> body) {
        String nombre = body.get("nombre").toString();
        String codigo = body.get("codigo").toString();
        if (sucursalRepo.existsByNombre(nombre)) throw new BadRequestException("Nombre ya existe: " + nombre);
        if (sucursalRepo.existsByCodigo(codigo)) throw new BadRequestException("Código ya existe: " + codigo);

        Sucursal s = Sucursal.builder()
            .nombre(nombre).codigo(codigo)
            .direccion(body.get("direccion").toString())
            .ciudad(body.get("ciudad").toString())
            .telefono(body.get("telefono") != null ? body.get("telefono").toString() : null)
            .emailContacto(body.get("emailContacto") != null ? body.get("emailContacto").toString() : null)
            .fechaApertura(body.get("fechaApertura") != null ? LocalDate.parse(body.get("fechaApertura").toString()) : null)
            .estado(Sucursal.EstadoGeneral.ACTIVO)
            .build();
        Sucursal saved = sucursalRepo.save(s);

        // Crear usuario admin de sucursal si se envían credenciales
        if (body.get("email") != null && body.get("password") != null) {
            String email = body.get("email").toString();
            if (usuarioRepo.existsByEmail(email)) throw new BadRequestException("Email ya registrado: " + email);
            Rol rol = rolRepo.findByNombreRol("ADMIN_SUCURSAL")
                .orElseThrow(() -> new ResourceNotFoundException("Rol ADMIN_SUCURSAL no encontrado"));
            Usuario u = Usuario.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(body.get("password").toString()))
                .nombreCompleto(body.getOrDefault("nombreAdmin", "Admin " + nombre).toString())
                .rol(rol).sucursal(saved)
                .estado(Usuario.EstadoGeneral.ACTIVO)
                .build();
            usuarioRepo.save(u);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(saved, "Sucursal creada"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Sucursal>> actualizar(@PathVariable Long id,
                                                             @RequestBody Map<String, Object> body) {
        Sucursal s = sucursalRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id));
        if (body.get("nombre")    != null) s.setNombre(body.get("nombre").toString());
        if (body.get("direccion") != null) s.setDireccion(body.get("direccion").toString());
        if (body.get("ciudad")    != null) s.setCiudad(body.get("ciudad").toString());
        if (body.get("telefono")  != null) s.setTelefono(body.get("telefono").toString());
        return ResponseEntity.ok(ApiResponse.ok(sucursalRepo.save(s), "Sucursal actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        Sucursal s = sucursalRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id));
        s.setEstado(Sucursal.EstadoGeneral.INACTIVO);
        sucursalRepo.save(s);
        return ResponseEntity.ok(ApiResponse.ok(null, "Sucursal desactivada"));
    }
}
