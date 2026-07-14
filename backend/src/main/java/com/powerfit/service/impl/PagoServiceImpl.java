package com.powerfit.service.impl;

import com.powerfit.dto.request.PagoRequest;
import com.powerfit.dto.response.PagoEstadisticasResponse;
import com.powerfit.dto.response.PagoResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository           pagoRepository;
    private final MembresiaRepository      membresiaRepository;
    private final ClienteRepository        clienteRepository;
    private final UsuarioSistemaRepository usuarioRepository;
    private final AuditoriaService         auditoriaService;

    // Paginación FÍSICA — consulta paginada directo a la BD
    @Override
    public Page<PagoResponse> listarPaginado(Pageable pageable) {
        return pagoRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public List<PagoResponse> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public PagoResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<PagoResponse> listarPorCliente(Integer clienteId) {
        return pagoRepository.findByClienteId(clienteId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public PagoEstadisticasResponse estadisticas() {
        BigDecimal total   = pagoRepository.sumTotalIngresos();
        BigDecimal promedio = pagoRepository.avgMonto();
        return PagoEstadisticasResponse.builder()
                .totalIngresos(total   != null ? total   : BigDecimal.ZERO)
                .totalPagos(pagoRepository.count())
                .promedio(promedio != null ? promedio : BigDecimal.ZERO)
                .conteoEfectivo(pagoRepository.countEfectivo())
                .conteoTarjeta(pagoRepository.countTarjeta())
                .conteoTransferencia(pagoRepository.countTransferencia())
                .build();
    }

    @Override
    public PagoResponse registrar(PagoRequest request) {
        Membresia membresia = membresiaRepository.findById(request.getMembresiaId())
                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada: " + request.getMembresiaId()));
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.getClienteId()));

        UsuarioSistema registradoPor = null;
        if (request.getRegistradoPorId() != null) {
            registradoPor = usuarioRepository.findById(request.getRegistradoPorId()).orElse(null);
        }

        Pago pago = Pago.builder()
                .membresia(membresia)
                .cliente(cliente)
                .monto(request.getMonto())
                .metodoPago(Pago.MetodoPago.valueOf(request.getMetodoPago()))
                .registradoPor(registradoPor)
                .build();

        Pago saved = pagoRepository.save(pago);
        auditoriaService.registrar("pagos", "INSERT", "system",
                null, "id=" + saved.getId() + ",monto=" + saved.getMonto(), null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        findOrThrow(id);
        auditoriaService.registrar("pagos", "DELETE", "system", "id=" + id, null, null);
        pagoRepository.deleteById(id);
    }

    private Pago findOrThrow(Integer id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
    }

    private PagoResponse toResponse(Pago p) {
        return PagoResponse.builder()
                .id(p.getId())
                .membresiaId(p.getMembresia().getId())
                .clienteId(p.getCliente().getId())
                .clienteNombre(p.getCliente().getNombre())
                .clienteApellido(p.getCliente().getApellido())
                .cedula(p.getCliente().getCedula())
                .tipoMembresia(p.getMembresia().getTipoMembresia().getNombre())
                .monto(p.getMonto())
                .metodoPago(p.getMetodoPago().name())
                .fechaPago(p.getFechaPago())
                .fechaInicio(p.getMembresia().getFechaInicio())
                .fechaFin(p.getMembresia().getFechaFin())
                .estadoMembresia(p.getMembresia().getEstado())
                .registradoPorNombre(p.getRegistradoPor() != null ? p.getRegistradoPor().getNombre() : null)
                .build();
    }
}
