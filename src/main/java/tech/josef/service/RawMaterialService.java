package tech.josef.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import tech.josef.dto.RawMaterialDTO;
import tech.josef.entity.RawMaterial;
import tech.josef.repository.RawMaterialRepository;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RawMaterialService {

    @Inject
    RawMaterialRepository repository;

    public List<RawMaterialDTO> listAll() {
        return repository.listAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public RawMaterialDTO findById(Long id) {
        RawMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Matéria-prima não encontrada");
        }
        return mapToDTO(entity);
    }

    @Transactional
    public RawMaterialDTO create(RawMaterialDTO dto) {
        // 1. Validação de Unicidade
        if (repository.find("name", dto.getName()).firstResult() != null) {
            throw new WebApplicationException("Já existe uma matéria-prima com o nome: " + dto.getName(), 400);
        }

        // 2. Validação de Código (Também recomendado ser único)
        if (repository.find("code", dto.getCode()).firstResult() != null) {
            throw new WebApplicationException("Já existe uma matéria-prima com o código: " + dto.getCode(), 400);
        }
        RawMaterial entity = new RawMaterial();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setStockQuantity(dto.getStockQuantity());
        repository.persist(entity);
        return mapToDTO(entity);
    }

    @Transactional
    public RawMaterialDTO update(Long id, RawMaterialDTO dto) {
        RawMaterial entity = repository.findById(id);
        if (entity == null) throw new NotFoundException();

        entity.setName(dto.getName());
        entity.setStockQuantity(dto.getStockQuantity());
        return mapToDTO(entity);
    }

    @Transactional
    public RawMaterialDTO patch(Long id, RawMaterialDTO dto) {
        RawMaterial entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Matéria-prima não encontrada");
        }

        // Atualização parcial baseada no DTO
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getStockQuantity() != null) entity.setStockQuantity(dto.getStockQuantity());

        return mapToDTO(entity);
    }



    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private RawMaterialDTO mapToDTO(RawMaterial entity) {
        RawMaterialDTO dto = new RawMaterialDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setStockQuantity(entity.getStockQuantity());
        return dto;
    }


}