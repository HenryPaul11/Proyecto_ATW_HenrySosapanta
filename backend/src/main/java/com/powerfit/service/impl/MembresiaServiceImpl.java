package com.powerfit.service.impl;

import com.powerfit.dto.request.MembresiaRequest;
import com.powerfit.dto.response.MembresiaResponse;
import com.powerfit.dto.response.PagedResponse;
import com.powerfit.entity.Cliente;
import com.powerfit.entity.Membresia;
import com.powerfit.entity.TipoMembresia;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.MembresiaRepository;
import com.powerfit.repository.TipoMembresiaRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.MembresiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembresiaServiceImpl implements MembresiaService {

    private final MembresiaRepository     membresiaRepository;
    private final ClienteRepository       clienteRepository;
    private final TipoMembresiaRepository tipoRepository;
    private final AuditoriaService        auditoriaService;

    @Override
    public List<MembresiaResponse> listarTodas() {
        log.debug("Listando todas las membresías");
        return membresiaRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<MembresiaResponse> listarActivas() {
        return membresiaRepository.findActivas(LocalDate.now()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<MembresiaResponse> listarVencidas() {
        return membresiaRepository.findVencidas(LocalDate.now()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public PagedResponse<MembresiaResponse> listarActivasPaginado(int pagina, int tamanio) {
        Page<Membresia> page = membresiaRepository.findActivasPaged(
                LocalDate.now(),
                PageRequest.of(pagina, tamanio, Sort.by("fechaFin").ascending()));
        return toPagedResponse(page);
    }

    @Override
    public PagedResponse<MembresiaResponse> listarVencidasPaginado(int pagina, int tamanio) {
        Page<Membresia> page = membresiaRepository.findVencidasPaged(
                LocalDate.now(),
                PageRequest.of(pagina, tamanio, Sort.by("fechaFin").descending()));
        return toPagedResponse(page);
    }

    @Override
    public List<MembresiaResponse> listarPorCliente(Integer clienteId) {
        return membresiaRepository.findByClienteId(clienteId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MembresiaResponse asignar(MembresiaRequest request) {
        log.info("Asignando membresía: clienteId={}, tipoId={}",
                request.getClienteId(), request.getTipoMembresiaId());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado: " + request.getClienteId()));

        TipoMembresia tipo = tipoRepository.findById(request.getTipoMembresiaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de membresía no encontrado: " + request.getTipoMembresiaId()));

        LocalDate inicio = LocalDate.now();
        LocalDate fin    = inicio.plusDays(tipo.getDuracionDias());

        Membresia m = Membresia.builder()
                .cliente(cliente)
                .tipoMembresia(tipo)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .build();

        Membresia saved = membresiaRepository.save(m);
        log.info("Membresía asignada: id={}, fin={}", saved.getId(), fin);

        auditoriaService.registrar("membresias", "INSERT", "system",
                null, "id=" + saved.getId() + ",cliente=" + cliente.getCedula(), null);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public MembresiaResponse renovar(Integer id) {
        Membresia m = findOrThrow(id);

        LocalDate base  = m.getFechaFin().isBefore(LocalDate.now())
                ? LocalDate.now() : m.getFechaFin();
        LocalDate nuevo = base.plusDays(m.getTipoMembresia().getDuracionDias());

        String datosAnt = "fecha_fin=" + m.getFechaFin();
        m.setFechaFin(nuevo);
        Membresia saved = membresiaRepository.save(m);
        log.info("Membresía renovada: id={}, nueva fecha_fin={}", id, nuevo);
        auditoriaService.registrar("membresias", "UPDATE", "system",
                datosAnt, "fecha_fin=" + saved.getFechaFin(), null);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        findOrThrow(id);
        log.info("Eliminando membresía: id={}", id);
        auditoriaService.registrar("membresias", "DELETE", "system", "id=" + id, null, null);
        membresiaRepository.deleteById(id);
    }

    private Membresia findOrThrow(Integer id) {
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Membresía no encontrada con id: " + id));
    }

    private MembresiaResponse toResponse(Membresia m) {
        return MembresiaResponse.builder()
                .id(m.getId())
                .clienteId(m.getCliente().getId())
                .clienteNombre(m.getCliente().getNombre())
                .clienteApellido(m.getCliente().getApellido())
                .clienteCedula(m.getCliente().getCedula())
                .tipoMembresiaId(m.getTipoMembresia().getId())
                .tipoMembresiaNombre(m.getTipoMembresia().getNombre())
                .fechaInicio(m.getFechaInicio())
                .fechaFin(m.getFechaFin())
                .estado(m.getEstado())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private PagedResponse<MembresiaResponse> toPagedResponse(Page<Membresia> page) {
        return PagedResponse.<MembresiaResponse>builder()
                .contenido(page.getContent().stream()
                        .map(this::toResponse).collect(Collectors.toList()))
                .paginaActual(page.getNumber())
                .totalPaginas(page.getTotalPages())
                .totalElementos(page.getTotalElements())
                .tamanioPagina(page.getSize())
                .esUltima(page.isLast())
                .build();
    }
}
