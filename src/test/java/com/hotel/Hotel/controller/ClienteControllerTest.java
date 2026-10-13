package com.hotel.Hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hotel.Hotel.dto.request.ActualizarClienteRequest;
import com.hotel.Hotel.dto.request.CrearClienteRequest;
import com.hotel.Hotel.dto.response.ClienteResumenResponse;
import com.hotel.Hotel.dto.response.ClienteResponse;
import com.hotel.Hotel.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClienteControllerTest {

    private ClienteService clienteService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        clienteService = mock(ClienteService.class);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(new ClienteController(clienteService))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void crear_devuelve201ConUriYBody() throws Exception {
        UUID id = UUID.randomUUID();
        when(clienteService.crear(any(CrearClienteRequest.class)))
                .thenReturn(new ClienteResponse(id, "Ana Torres", "ana@hotel.com", true, 0));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Torres","email":"ana@hotel.com"}"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/clientes/" + id)))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nombre").value("Ana Torres"))
                .andExpect(jsonPath("$.email").value("ana@hotel.com"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.penalizaciones").value(0));

        verify(clienteService).crear(any(CrearClienteRequest.class));
    }

    @Test
    void listar_devuelve200ConLaListaDeClientes() throws Exception {
        when(clienteService.listarTodos()).thenReturn(List.of(
                new ClienteResponse(UUID.randomUUID(), "Ana", "ana@hotel.com", true, 0),
                new ClienteResponse(UUID.randomUUID(), "Luis", "luis@hotel.com", true, 0)));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[1].email").value("luis@hotel.com"));
    }

    @Test
    void buscarPorId_devuelve200ConElCliente() throws Exception {
        UUID id = UUID.randomUUID();
        when(clienteService.obtenerPorId(id))
                .thenReturn(new ClienteResponse(id, "Ana Torres", "ana@hotel.com", true, 0));

        mockMvc.perform(get("/api/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nombre").value("Ana Torres"));
    }

    @Test
    void buscarResumen_devuelve200ConLosAgregados() throws Exception {
        UUID id = UUID.randomUUID();
        when(clienteService.obtenerResumen(id)).thenReturn(
                new ClienteResumenResponse(id, "Ana Torres", "ana@hotel.com", true, 0, 2, 450.0, List.of()));

        mockMvc.perform(get("/api/clientes/{id}/resumen", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReservasRealizadas").value(2))
                .andExpect(jsonPath("$.montoTotalGastado").value(450.0))
                .andExpect(jsonPath("$.reservasRecientes").isArray());
    }

    @Test
    void actualizar_devuelve200ConElClienteActualizado() throws Exception {
        UUID id = UUID.randomUUID();
        when(clienteService.actualizar(eq(id), any(ActualizarClienteRequest.class)))
                .thenReturn(new ClienteResponse(id, "Ana Maria", "ana@hotel.com", true, 0));

        mockMvc.perform(patch("/api/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Maria"));

        verify(clienteService).actualizar(eq(id), any(ActualizarClienteRequest.class));
    }
}
