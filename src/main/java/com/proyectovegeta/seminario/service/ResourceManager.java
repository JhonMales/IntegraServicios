package com.proyectovegeta.seminario.service;

import com.proyectovegeta.seminario.model.Recurso;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.repository.RecursoRepository;
import com.proyectovegeta.seminario.repository.TipoRecursoRepository;
import com.proyectovegeta.seminario.repository.UnidadServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceManager {

    private final TipoRecursoRepository tipoRecursoRepository;
    private final RecursoRepository recursoRepository;
    private final UnidadServicioRepository unidadServicioRepository;

    public ResourceManager(TipoRecursoRepository tipoRecursoRepository, RecursoRepository recursoRepository, UnidadServicioRepository unidadServicioRepository) {
        this.unidadServicioRepository = unidadServicioRepository;
        this.tipoRecursoRepository = tipoRecursoRepository;
        this.recursoRepository = recursoRepository;
    }

    public Recurso createResource(Long serviceUnitID, Long resourceTypeID, String name, String description) {
        UnidadServicio serviceUnit = unidadServicioRepository.findById(serviceUnitID).orElseThrow(() -> new RuntimeException("Unidad de servicio no encontrada"));
        TipoRecurso resourceType = tipoRecursoRepository.findById(resourceTypeID).orElseThrow(() -> new RuntimeException("Tipo de recurso no encontrado"));
        Recurso recurso = new Recurso();
        recurso.setUnidadId(serviceUnit.getUnidadId());
        recurso.setTipoRecursoId(resourceType.getTipoRecursoId());
        recurso.setNombre(name);
        recurso.setDescripcion(description);
        return recursoRepository.save(recurso);
    }
    public Recurso getResource(Long resourceID){
        return recursoRepository.findById(resourceID).orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
    }
    public List<Recurso> getResourceByServiceUnit(Long serviceUnitID){
        return recursoRepository.findByUnidadId(serviceUnitID);
    }
    public List<Recurso> getResourceByType(Long resourceTypeID){
        return recursoRepository.findByTipoRecursoId(resourceTypeID);
    }
    public List<Recurso> getAllResources() {
        return recursoRepository.findAll();
    }
}
