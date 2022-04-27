package com.mercadolibre.grupo1.projetointegrador.services;

import com.mercadolibre.grupo1.projetointegrador.dtos.BatchStockDTO;
import com.mercadolibre.grupo1.projetointegrador.entities.BatchStock;
import com.mercadolibre.grupo1.projetointegrador.entities.Product;
import com.mercadolibre.grupo1.projetointegrador.entities.enums.ProductCategory;
import com.mercadolibre.grupo1.projetointegrador.exceptions.EntityNotFoundException;
import com.mercadolibre.grupo1.projetointegrador.repositories.BatchStockRepository;
import com.mercadolibre.grupo1.projetointegrador.services.mappers.BatchStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchStockService {
    private final ProductService productService;
    private final BatchStockRepository batchStockRepository;

    private final BatchStockMapper batchStockMapper;

    public BatchStock createFromDTO(BatchStockDTO batchStockDTO) {
        Product product = productService.findById(batchStockDTO.getProductId());

        BatchStock batchStock = batchStockMapper.toBatchStock(batchStockDTO);
        batchStock.setId(null);
        batchStock.setProduct(product);
        return batchStock;
    }

    public BatchStock updateFromDTO(BatchStockDTO batchStockDTO){
        Product product = productService.findById(batchStockDTO.getProductId());
        findById(batchStockDTO.getBatchNumber());

        BatchStock batchStock = batchStockMapper.toBatchStock(batchStockDTO);
        batchStock.setId(batchStockDTO.getBatchNumber());
        batchStock.setProduct(product);
        return batchStock;
    }

    public void saveAll(List<BatchStock> batchStocks){
        batchStockRepository.saveAll(batchStocks);
    }

    public BatchStock findById(Long batchNumber) {
        return batchStockRepository.findById(batchNumber).orElseThrow(() -> new EntityNotFoundException("Lote com ID " + batchNumber + " não encontrado."));
    }

    public Set<BatchStock> findBatchStockBySectionId(Long sectionId){
        return batchStockRepository.findStockBySectionId(sectionId);
    }

    public List<BatchStockDTO.SimpleBatchStockDTO> findBatchStockBySectionIdAndExpiresIn(Long sectionId, Long expiresIn) {
        return batchStockRepository.findStockBySectionIdAndDueDateBetween(sectionId, LocalDate.now(), LocalDate.now().plusDays(expiresIn))
                .stream().map(BatchStockDTO::toSimpleBatchDTO)
                .collect(Collectors.toList());
    }

    public List<BatchStockDTO.SimpleBatchStockDTO> findBatchStockByCategoryAndExpiresIn(ProductCategory productCategory, Long expiresIn, Sort.Direction direction) {
        return batchStockRepository.findWarehouseStockByCategoryAndDueDateBetween(1L, productCategory, LocalDate.now(), LocalDate.now().plusDays(expiresIn), Sort.by(direction, "dueDate"))
                .stream().map(BatchStockDTO::toSimpleBatchDTO)
                .collect(Collectors.toList());
    }
}
