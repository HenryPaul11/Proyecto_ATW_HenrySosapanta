package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.service.OllamaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@Tag(name = "Inteligencia Artificial")
public class IAController {

    private final OllamaService ollamaService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String, String>>> chat(@RequestBody Map<String, String> body) {
        String mensaje = body.getOrDefault("mensaje", "");
        String contexto = body.get("contexto");
        String respuesta = ollamaService.chat(mensaje, contexto);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("respuesta", respuesta)));
    }

    @PostMapping("/chat-datos")
    public ResponseEntity<ApiResponse<Map<String, String>>> chatConDatos(@RequestBody Map<String, String> body) {
        String mensaje = body.getOrDefault("mensaje", "");
        String respuesta = ollamaService.chatConDatos(mensaje);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("respuesta", respuesta)));
    }

    @PostMapping("/recomendar")
    public ResponseEntity<ApiResponse<Map<String, String>>> recomendar(@RequestBody Map<String, String> body) {
        String datosCliente = body.getOrDefault("datosCliente", "");
        String respuesta = ollamaService.recomendarMembresia(datosCliente);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("respuesta", respuesta)));
    }

    @PostMapping("/resumen")
    public ResponseEntity<ApiResponse<Map<String, String>>> resumen(@RequestBody Map<String, String> body) {
        String estadisticas = body.getOrDefault("estadisticas", "");
        String respuesta = ollamaService.resumenEjecutivo(estadisticas);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("respuesta", respuesta)));
    }

    @PostMapping("/analisis-asistencia")
    public ResponseEntity<ApiResponse<Map<String, String>>> analisisAsistencia(@RequestBody Map<String, String> body) {
        String datos = body.getOrDefault("datos", "");
        String respuesta = ollamaService.analisisAsistencia(datos);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("respuesta", respuesta)));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        try {
            new org.springframework.web.client.RestTemplate().getForObject("http://localhost:11434/api/tags", Object.class);
            return ResponseEntity.ok(ApiResponse.ok(Map.of("status", "connected", "message", "Ollama is running")));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.ok(Map.of("status", "disconnected", "message", "Ollama not available: " + e.getMessage())));
        }
    }
}
