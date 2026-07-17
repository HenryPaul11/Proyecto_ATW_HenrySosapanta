package com.powerfit.service.impl;

import com.powerfit.dto.request.AsignarMembresiaConPagoRequest;
import com.powerfit.dto.response.AsignarMembresiaConPagoResponse;
import com.powerfit.entity.Cliente;
import com.powerfit.entity.Membresia;
import com.powerfit.entity.Pago;
import com.powerfit.entity.Plan;
import com.powerfit.entity.Sucursal;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.MembresiaRepository;
import com.powerfit.repository.PagoRepository;
import com.powerfit.repository.PlanRepository;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.RegistroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroServiceImpl implements RegistroService {

    private final ClienteRepository clienteRepository;
    private final PlanRepository planRepository;
    private final MembresiaRepository membresiaRepository;
    private final PagoRepository pagoRepository;
    private final SucursalRepository sucursalRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public AsignarMembresiaConPagoResponse asignarMembresiaConPago(AsignarMembresiaConPagoRequest request) {
        log.info("Iniciando registro de membresia y pago para clienteId={}", request.getClienteId());

        Cliente cliente = clienteRepository.findById(request.getClienteId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.getClienteId()));
        if (cliente.getEstado() != Cliente.EstadoGeneral.ACTIVO) {
            throw new BadRequestException("El cliente esta inactivo y no puede recibir membresias");
        }

        Plan plan = planRepository.findById(request.getTipoMembresiaId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + request.getTipoMembresiaId()));
        if (plan.getEstado() != Plan.EstadoGeneral.ACTIVO) {
            throw new BadRequestException("El plan '" + plan.getNombrePlan() + "' no esta disponible");
        }

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + request.getSucursalId()));
        if (!plan.getSucursal().getId().equals(sucursal.getId())) {
            throw new BadRequestException("El plan no pertenece a la sucursal indicada");
        }
        if (!cliente.getSucursal().getId().equals(sucursal.getId())) {
            throw new BadRequestException("El cliente no pertenece a la sucursal indicada");
        }

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(plan.getDuracionDias());

        Membresia membresia = Membresia.builder()
                .cliente(cliente)
                .plan(plan)
                .sucursal(sucursal)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .build();
        Membresia membresiaGuardada = membresiaRepository.save(membresia);

        Pago pago = Pago.builder()
                .membresia(membresiaGuardada)
                .cliente(cliente)
                .sucursal(sucursal)
                .monto(request.getMontoPersonalizado() != null ? request.getMontoPersonalizado() : plan.getPrecio())
                .metodoPago(Pago.MetodoPago.valueOf(request.getMetodoPago().toUpperCase()))
                .fechaPago(LocalDateTime.now())
                .build();
        Pago pagoGuardado = pagoRepository.save(pago);

        auditoriaService.registrar(
                "membresias",
                "INSERT",
                cliente.getDocumentoIdentidad(),
                null,
                "{\"membresiaId\":" + membresiaGuardada.getId() + ",\"pagoId\":" + pagoGuardado.getId() + "}",
                null
        );

        return AsignarMembresiaConPagoResponse.builder()
                .membresiaId(membresiaGuardada.getId())
                .clienteId(cliente.getId())
                .clienteNombre(cliente.getNombreCompleto())
                .tipoMembresia(plan.getNombrePlan())
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estadoMembresia(membresiaGuardada.getEstado().name())
                .pagoId(pagoGuardado.getId())
                .monto(pagoGuardado.getMonto())
                .metodoPago(pagoGuardado.getMetodoPago().name())
                .fechaPago(pagoGuardado.getFechaPago())
                .sucursal(sucursal.getNombre())
                .build();
    }
}
