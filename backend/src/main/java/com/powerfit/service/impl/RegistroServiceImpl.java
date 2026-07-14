package com.powerfit.service.impl;

import com.powerfit.dto.request.AsignarMembresiaConPagoRequest;
import com.powerfit.dto.response.AsignarMembresiaConPagoResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.RegistroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Servicio de registro transaccional.
 *
 * @Transactional garantiza que las operaciones sobre membresias, pagos y auditoria
 * se ejecuten como una sola unidad. Si falla cualquier paso, se hace rollback
 * y ningún cambio queda guardado en la base de datos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroServiceImpl implements RegistroService {

    private final ClienteRepository        clienteRepository;
    private final TipoMembresiaRepository  tipoRepository;
    private final MembresiaRepository      membresiaRepository;
    private final PagoRepository           pagoRepository;
    private final SucursalRepository       sucursalRepository;
    private final UsuarioSistemaRepository usuarioRepository;
    private final AuditoriaService         auditoriaService;

    @Override
    @Transactional
    public AsignarMembresiaConPagoResponse asignarMembresiaConPago(
            AsignarMembresiaConPagoRequest request) {

        log.info("Iniciando proceso transaccional: asignarMembresiaConPago para clienteId={}",
                request.getClienteId());

        // ── Paso 1: Validar cliente ──────────────────────────────────────────
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado: " + request.getClienteId()));

        if (!cliente.getActivo()) {
            throw new BadRequestException("El cliente está inactivo y no puede recibir membresías");
        }

        // ── Paso 2: Validar tipo de membresía ────────────────────────────────
        TipoMembresia tipo = tipoRepository.findById(request.getTipoMembresiaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de membresía no encontrado: " + request.getTipoMembresiaId()));

        if (!tipo.getActivo()) {
            throw new BadRequestException("El tipo de membresía '" + tipo.getNombre() + "' no está disponible");
        }

        // ── Paso 3: Validar sucursal ─────────────────────────────────────────
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal no encontrada: " + request.getSucursalId()));

        // ── Paso 4: Calcular fechas de la membresía ──────────────────────────
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin    = fechaInicio.plusDays(tipo.getDuracionDias());

        // ── Paso 5: Crear membresía ──────────────────────────────────────────
        Membresia membresia = Membresia.builder()
                .cliente(cliente)
                .tipoMembresia(tipo)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .build();
        Membresia membresiaGuardada = membresiaRepository.save(membresia);
        log.info("Membresía creada: id={}, fechaFin={}", membresiaGuardada.getId(), fechaFin);

        // ── Paso 6: Calcular monto ───────────────────────────────────────────
        java.math.BigDecimal monto = request.getMontoPersonalizado() != null
                ? request.getMontoPersonalizado()
                : tipo.getPrecio();

        // ── Paso 7: Registrar pago ───────────────────────────────────────────
        UsuarioSistema registradoPor = null;
        if (request.getRegistradoPorId() != null) {
            registradoPor = usuarioRepository.findById(request.getRegistradoPorId()).orElse(null);
        }

        Pago pago = Pago.builder()
                .membresia(membresiaGuardada)
                .cliente(cliente)
                .monto(monto)
                .metodoPago(Pago.MetodoPago.valueOf(request.getMetodoPago()))
                .fechaPago(LocalDateTime.now())
                .registradoPor(registradoPor)
                .sucursal(sucursal)
                .build();
        Pago pagoGuardado = pagoRepository.save(pago);
        log.info("Pago registrado: id={}, monto={}, metodo={}", pagoGuardado.getId(), monto, request.getMetodoPago());

        // ── Paso 8: Registrar auditoría ──────────────────────────────────────
        auditoriaService.registrar(
                "membresias", "INSERT", cliente.getCedula(),
                null,
                "membresiaId=" + membresiaGuardada.getId() + ",pagoId=" + pagoGuardado.getId(),
                null
        );

        log.info("Proceso transaccional completado exitosamente para clienteId={}", request.getClienteId());

        // ── Paso 9: Construir respuesta ──────────────────────────────────────
        return AsignarMembresiaConPagoResponse.builder()
                .membresiaId(membresiaGuardada.getId())
                .clienteId(cliente.getId())
                .clienteNombre(cliente.getNombre() + " " + cliente.getApellido())
                .tipoMembresia(tipo.getNombre())
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estadoMembresia(membresiaGuardada.getEstado())
                .pagoId(pagoGuardado.getId())
                .monto(monto)
                .metodoPago(request.getMetodoPago())
                .fechaPago(pagoGuardado.getFechaPago())
                .sucursal(sucursal.getNombre())
                .build();
    }
}
