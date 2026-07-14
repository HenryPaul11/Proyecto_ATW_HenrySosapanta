package com.powerfit.service;

import com.powerfit.dto.request.AsignarMembresiaConPagoRequest;
import com.powerfit.dto.response.AsignarMembresiaConPagoResponse;

public interface RegistroService {
    /**
     * Proceso transaccional: asigna membresía + registra pago en una sola operación.
     * Si falla cualquier parte, se revierte todo (@Transactional).
     */
    AsignarMembresiaConPagoResponse asignarMembresiaConPago(AsignarMembresiaConPagoRequest request);
}
