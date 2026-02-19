package tech.josef.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import tech.josef.dto.ProductDTO;
import tech.josef.entity.Product;
import tech.josef.entity.RawMaterial;
import tech.josef.repository.ProductRepository;
import tech.josef.repository.RawMaterialRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    public List<ProductDTO> findAll() {
        return productRepository.listAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) throw new NotFoundException("Produto não encontrado");
        return mapToDTO(product);
    }

    @Transactional
    public ProductDTO create(ProductDTO dto) {
        if (productRepository.find("name", dto.getName()).firstResult() != null) {
            throw new WebApplicationException("Já existe um produto cadastrado com o nome: " + dto.getName(), 400);
        }
        Product product = new Product();
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        // Mapeia os materiais do DTO para a Entidade usando o Helper Method que você criou
        if (dto.getMaterials() != null) {
            dto.getMaterials().forEach(m -> {
                RawMaterial rm = rawMaterialRepository.findById(m.getRawMaterialId());
                if (rm == null) {
                    throw new NotFoundException("Matéria-prima com ID " + m.getRawMaterialId() + " não existe.");
                }
                product.addRawMaterial(rm, m.getQuantityRequired());
            });
        }

        productRepository.persist(product);
        return mapToDTO(product);
    }


    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        Product entity = productRepository.findById(id);
        if (entity == null) throw new NotFoundException("Produto não encontrado");

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setCode(dto.getCode());

        if (dto.getMaterials() != null) {
            // 1. Limpa a lista atual
            entity.getRawMaterials().clear();

            // 2. FORÇA o Hibernate a sincronizar a deleção agora
            // Isso evita o erro de "NonUniqueObjectException"
            productRepository.getEntityManager().flush();

            // 3. Adiciona os novos materiais
            for (var matDto : dto.getMaterials()) {
                RawMaterial rm = rawMaterialRepository.findById(matDto.getRawMaterialId());
                if (rm != null) {
                    entity.addRawMaterial(rm, matDto.getQuantityRequired());
                }
            }
        }

        return mapToDTO(entity);
    }

    @Transactional
    public ProductDTO patch(Long id, ProductDTO dto) {
        Product entity = productRepository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Produto não encontrado com o ID: " + id);
        }

        // Atualização Parcial: verifica campo por campo
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }

        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }

        // Para materiais no PATCH, geralmente ou você envia a lista completa para substituir,
        // ou ignora. Aqui, se enviou algo na lista, nós atualizamos.
        if (dto.getMaterials() != null && !dto.getMaterials().isEmpty()) {
            entity.getRawMaterials().clear();
            for (var matDto : dto.getMaterials()) {
                RawMaterial rm = rawMaterialRepository.findById(matDto.getRawMaterialId());
                if (rm != null) {
                    entity.addRawMaterial(rm, matDto.getQuantityRequired());
                }
            }
        }

        return mapToDTO(entity);
    }



    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) throw new NotFoundException("Produto não encontrado");
        productRepository.delete(product);
    }

    // Mapper manual (mais rápido e seguro para este teste que MapStruct)
    private ProductDTO mapToDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());

        if (entity.getRawMaterials() != null) {
            dto.setMaterials(entity.getRawMaterials().stream()
                    .map(prm -> {
                        ProductDTO.ProductMaterialRequestDTO mDto = new ProductDTO.ProductMaterialRequestDTO();
                        mDto.setRawMaterialId(prm.getRawMaterial().getId());
                        mDto.setQuantityRequired(prm.getQuantityRequired());
                        return mDto;
                    }).collect(Collectors.toList()));
        }
        return dto;
    }



}