package com.mercadolibre.grupo1.projetointegrador.dtos;

import com.mercadolibre.grupo1.projetointegrador.entities.enums.OrderStatus;
import lombok.Data;

@Data
public class PurchaseOrderStatusDTO {
    private OrderStatus status;
}
