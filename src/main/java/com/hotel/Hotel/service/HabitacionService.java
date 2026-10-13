package com.hotel.Hotel.service;

import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.SuitePresidencial;
import com.hotel.Hotel.dto.request.CrearHabitacionEstandarRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.mapper.HabitacionMapper;
import com.hotel.Hotel.repository.HabitacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HabitacionMapper habitacionMapper;

    public HabitacionService(HabitacionRepository habitacionRepository, HabitacionMapper habitacionMapper) {
        this.habitacionRepository = habitacionRepository;
        this.habitacionMapper = habitacionMapper;
    }

    @Transactional
    public HabitacionResponse crearEstandar(CrearHabitacionEstandarRequest request) {
        HabitacionEstandar habitacion = new HabitacionEstandar(
                request.numero(),
                request.capacidadMaxima(),
                request.precioPorNoche(),
                request.camasIndividuales());
        Habitacion guardada = habitacionRepository.save(habitacion);
        return habitacionMapper.toResponse(guardada);
    }

    @Transactional
    public HabitacionResponse crearSuite(CrearSuitePresidencialRequest request) {
        SuitePresidencial habitacion = new SuitePresidencial(
                request.numero(),
                request.capacidadMaxima(),
                request.precioPorNoche(),
                request.incluyeMayordomo(),
                request.jacuzziPrivado());
        Habitacion guardada = habitacionRepository.save(habitacion);
        return habitacionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<HabitacionResponse> listarTodas() {
        return habitacionMapper.toResponseList(habitacionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public HabitacionResponse obtenerPorId(UUID id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habitacion no encontrada con ID: " + id));
        return habitacionMapper.toResponse(habitacion);
    }
}
