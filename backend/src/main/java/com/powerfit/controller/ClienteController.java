package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.Cliente;
import com.powerfit.entity.Rol;
import com.powerfit.entity.Sucursal;
import com.powerfit.entity.Usuario;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.RolRepository;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes")
public class ClienteController {

    private final ClienteRepository clienteRepo;
    private final SucursalRepository sucursalRepo;
    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> listar(
            @RequestParam(required = false) Long sucursalId) {
        List<Cliente> lista = (sucursalId != null)
            ? clienteRepo.buscar(sucursalId, Pageable.unpaged()).getContent()
            : clienteRepo.findAll();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<Page<Cliente>>> paginado(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    String q,
            @RequestParam(required = false)    Long sucursalId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> result = (q != null && !q.isBlank())
            ? clienteRepo.buscarConBusqueda(q.trim(), sucursalId, pageable)
            : clienteRepo.buscar(sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id))));
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<ApiResponse<Cliente>> porUsuario(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado para usuario: " + email))));
    }

    @GetMapping("/perfil")
    public ResponseEntity<ApiResponse<Cliente>> perfil(
            @RequestParam(required = false) String usuario) {
        if (usuario == null || usuario.isBlank())
            throw new BadRequestException("Parámetro 'usuario' es obligatorio");
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.findByEmail(usuario)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado para usuario: " + usuario))));
    }

    @GetMapping("/documento/{doc}")
    public ResponseEntity<ApiResponse<Cliente>> porDocumento(@PathVariable String doc) {
        return ResponseEntity.ok(ApiResponse.ok(
            clienteRepo.findByDocumentoIdentidadAndEstado(doc, Cliente.EstadoGeneral.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + doc))));
    }

    // Alias para compatibilidad con frontend que usa /cedula/{cedula}
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<ApiResponse<Cliente>> porCedula(@PathVariable String cedula) {
        return ResponseEntity.ok(ApiResponse.ok(
            clienteRepo.findByDocumentoIdentidadAndEstado(cedula, Cliente.EstadoGeneral.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con cédula: " + cedula))));
    }

    @GetMapping("/sin-membresia")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<Cliente>>> sinMembresia(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    Long sucursalId) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.sinMembresia(sucursalId, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> crear(@RequestBody Map<String, Object> body) {
        log.info("Registrando nuevo Cliente");
        Long sucursalId = body.get("sucursalId") != null ? Long.valueOf(body.get("sucursalId").toString()) : null;
        if (sucursalId == null) throw new BadRequestException("sucursalId es obligatorio");
        Sucursal sucursal = sucursalRepo.findById(sucursalId)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId));

        String doc = body.getOrDefault("documentoIdentidad", "").toString();
        if (clienteRepo.existsByDocumentoIdentidad(doc))
            throw new BadRequestException("Documento ya registrado: " + doc);

        String email = body.get("email") != null ? body.get("email").toString() : null;
        String contrasena = body.get("contrasena") != null ? body.get("contrasena").toString() : null;

        Cliente c = Cliente.builder()
            .sucursal(sucursal)
            .nombreCompleto(body.getOrDefault("nombreCompleto", "").toString())
            .documentoIdentidad(doc)
            .email(email)
            .telefono(body.get("telefono") != null ? body.get("telefono").toString() : null)
            .genero(body.get("genero") != null ? body.get("genero").toString() : null)
            .estado(Cliente.EstadoGeneral.ACTIVO)
            .build();

        // Crear Usuario para login si se proporcionó email y contraseña
        if (email != null && !email.isBlank() && contrasena != null && !contrasena.isBlank()) {
            if (usuarioRepo.existsByEmail(email))
                throw new BadRequestException("Ya existe un usuario con ese correo: " + email);

            Rol rolCliente = rolRepo.findByNombreRol("CLIENTE")
                .orElseThrow(() -> new ResourceNotFoundException("Rol CLIENTE no encontrado"));

            Usuario usuario = Usuario.builder()
                .email(email)
                .nombreCompleto(body.getOrDefault("nombreCompleto", "").toString())
                .telefono(body.get("telefono") != null ? body.get("telefono").toString() : null)
                .passwordHash(passwordEncoder.encode(contrasena))
                .rol(rolCliente)
                .sucursal(sucursal)
                .estado(Usuario.EstadoGeneral.ACTIVO)
                .build();
            usuario = usuarioRepo.save(usuario);
            c.setUsuario(usuario);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(clienteRepo.save(c), "Cliente registrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> actualizar(@PathVariable Long id,
                                                            @RequestBody Map<String, Object> body) {
        log.info("Actualizando Cliente id={}", id);
        Cliente c = clienteRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
        if (body.get("nombreCompleto") != null) c.setNombreCompleto(body.get("nombreCompleto").toString());
        if (body.get("email")          != null) c.setEmail(body.get("email").toString());
        if (body.get("telefono")       != null) c.setTelefono(body.get("telefono").toString());
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.save(c), "Cliente actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        log.info("Eliminando Cliente id={}", id);
        Cliente c = clienteRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
        c.setEstado(Cliente.EstadoGeneral.INACTIVO);
        clienteRepo.save(c);
        return ResponseEntity.ok(ApiResponse.ok(null, "Cliente desactivado"));
    }
}
