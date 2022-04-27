package com.mercadolibre.grupo1.projetointegrador.repositories;

import com.mercadolibre.grupo1.projetointegrador.entities.BatchStock;
import com.mercadolibre.grupo1.projetointegrador.entities.enums.ProductCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author Nayara Coca
 * Criação repositório de batch stock
 */
@Repository
public interface BatchStockRepository extends JpaRepository<BatchStock,Long> {
    /**
     * Listagem do estoque de lotes da sessao
     * @param sectionId
     * @author Weverton Bruno
     */
    @Query(value =
            "SELECT b FROM InboundOrder i " +
                "INNER JOIN BatchStock b ON b.inboundOrder.id = i.id " +
                "INNER JOIN Section s ON s.id = i.section.id " +
            "WHERE s.id = :sectionId")
    Set<BatchStock> findStockBySectionId(Long sectionId);

    /**
     * Listagem do o estoque de lotes da sessao filtrados por dias de validade
     * @param sectionId
     * @param start
     * @param end
     * @return
     */
    @Query(value =
            "SELECT b FROM InboundOrder i " +
                    "INNER JOIN BatchStock b ON b.inboundOrder.id = i.id " +
                    "INNER JOIN Section s ON s.id = i.section.id " +
                    "WHERE s.id = :sectionId AND b.dueDate BETWEEN :start AND :end " +
                    "ORDER BY b.dueDate ASC")
    List<BatchStock> findStockBySectionIdAndDueDateBetween(Long sectionId, LocalDate start, LocalDate end);

    /**
     * Listagem do o estoque de lotes de todos os warehouses
     * @param category
     * @param start
     * @param end
     * @param sort
     * @return
     */
    @Query(value =
            "SELECT b FROM BatchStock b " +
                    "INNER JOIN InboundOrder i ON b.inboundOrder.id = i.id " +
                    "INNER JOIN Product p ON p.id = b.product.id " +
                    "INNER JOIN Section s ON s.id = i.section.id " +
                    "INNER JOIN Warehouse w ON w.id = s.warehouse.id " +
                    "WHERE b.dueDate BETWEEN :start AND :end AND p.category = :category AND w.id = :warehouseId")
    List<BatchStock> findWarehouseStockByCategoryAndDueDateBetween(Long warehouseId, ProductCategory category, LocalDate start, LocalDate end, Sort sort);
}
