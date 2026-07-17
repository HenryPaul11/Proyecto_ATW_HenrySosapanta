package com.powerfit.service;

import com.powerfit.dto.request.AsignarMembresiaConPagoRequest;
import com.powerfit.dto.response.AsignarMembresiaConPagoResponse;

public interface RegistroService {
    AsignarMembresiaConPagoResponse asignarMembresiaConPago(AsignarMembresiaConPagoRequest request);
}
