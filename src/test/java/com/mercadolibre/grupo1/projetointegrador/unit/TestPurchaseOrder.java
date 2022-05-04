package com.mercadolibre.grupo1.projetointegrador.unit;

import com.mercadolibre.grupo1.projetointegrador.dtos.PurchaseOrderDTO;
import com.mercadolibre.grupo1.projetointegrador.entities.Customer;
import com.mercadolibre.grupo1.projetointegrador.entities.Product;
import com.mercadolibre.grupo1.projetointegrador.entities.PurchaseItem;
import com.mercadolibre.grupo1.projetointegrador.entities.PurchaseOrder;
import com.mercadolibre.grupo1.projetointegrador.entities.enums.OrderStatus;
import com.mercadolibre.grupo1.projetointegrador.exceptions.MissingProductExceptions;
import com.mercadolibre.grupo1.projetointegrador.exceptions.UnregisteredProducts;
import com.mercadolibre.grupo1.projetointegrador.exceptions.UnregisteredUser;
import com.mercadolibre.grupo1.projetointegrador.repositories.BatchStockRepository;
import com.mercadolibre.grupo1.projetointegrador.repositories.CustomerRepository;
import com.mercadolibre.grupo1.projetointegrador.repositories.ProductRepository;
import com.mercadolibre.grupo1.projetointegrador.repositories.PurchaseOrderRepository;
import com.mercadolibre.grupo1.projetointegrador.services.PurchaseOrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TestPurchaseOrder {

    @Mock
    BatchStockRepository batchStockRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    PurchaseOrderServiceImpl purchaseOrderService;

    private PurchaseOrderDTO createPurchaseOrderDTO() {
        PurchaseOrderDTO.PurchaseOrder purchaseOrder = new PurchaseOrderDTO.PurchaseOrder();
        List<PurchaseOrderDTO.ProductItemDTO> products = new ArrayList<>(Arrays.asList(
                PurchaseOrderDTO.ProductItemDTO.builder().productId(1L).quantity(20).build()
        ));
        purchaseOrder.setProducts(products);

        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();
        purchaseOrderDTO.setPurchaseOrder(purchaseOrder);
        return purchaseOrderDTO;
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setPassword("123456");
        customer.setCpf("111.111.111-01");
        customer.setEmail("user@mercadolivre.com");

        return customer;
    }

    private Product createProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setPrice(BigDecimal.valueOf(1.));
        product.setVolume(10.);

        return product;
    }

    private PurchaseItem createPurchaseItem () {
        PurchaseItem purchaseItem = new PurchaseItem();
        purchaseItem.setId(1L);
        purchaseItem.setQuantity(20);
        purchaseItem.setProduct(createProduct());

        return purchaseItem;
    }

    private PurchaseOrder createPurchaseOrder () {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderStatus(OrderStatus.OPENED);
        purchaseOrder.setCustomer(createCustomer());
        purchaseOrder.setId(1L);
        purchaseOrder.setCreatedDate(LocalDateTime.now());
        purchaseOrder.setUpdatedDate(LocalDateTime.now());
        ArrayList<PurchaseItem> purchaseItems = new ArrayList<>();
        purchaseItems.add(createPurchaseItem());
        purchaseOrder.setProducts(purchaseItems);

        return purchaseOrder;
    }

    @Test
    @DisplayName("Testa se retorna exception de produto nao cadastrado")
    public void testExceptionUregisteredProduct () {
        PurchaseOrderDTO purchaseOrderDTO = createPurchaseOrderDTO();
        Customer customer = createCustomer();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Throwable messageUnregisteredProduct = Assertions.assertThrows(UnregisteredProducts.class, () ->
                purchaseOrderService.createPurchaseOrder(purchaseOrderDTO, customer));

        Assertions.assertEquals(
                messageUnregisteredProduct.getMessage(), "Produto não cadastrado!"
        );
    }

    @Test
    @DisplayName("Testa se retorna exception de produtos insuficiente")
    public void testExceptionMissingProduct () {
        PurchaseOrderDTO purchaseOrderDTO = createPurchaseOrderDTO();
        Customer customer = createCustomer();
        Product product = createProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));
        Mockito.when(batchStockRepository.findValidDateItems(Mockito.anyLong())).thenReturn(0D);

        Throwable messageMissingProduct = Assertions.assertThrows(MissingProductExceptions.class, () ->
                purchaseOrderService.createPurchaseOrder(purchaseOrderDTO, customer));

        Assertions.assertEquals(
                messageMissingProduct.getMessage(), "Product insuficiente em estoque!"
        );
    }

    @Test
    @DisplayName("Testa se cadastra purchase order com sucesso!")
    public void testSuccessfulCreatePurchaseOrder() {
        PurchaseOrderDTO purchaseOrderDTO = createPurchaseOrderDTO();
        PurchaseOrder purchaseOrder = createPurchaseOrder();
        Customer customer = createCustomer();
        Product product = createProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));
        Mockito.when(batchStockRepository.findValidDateItems(Mockito.anyLong())).thenReturn(20D);
        Mockito.when(purchaseOrderRepository.save(Mockito.any())).thenReturn(purchaseOrder);

        PurchaseOrder testePurchaseOrderSuccessful = purchaseOrderService.createPurchaseOrder(purchaseOrderDTO, customer);

        Assertions.assertEquals(
                testePurchaseOrderSuccessful.getOrderStatus(), OrderStatus.OPENED
        );

        Assertions.assertEquals(
                testePurchaseOrderSuccessful.getProducts().size(), 1
        );

        Assertions.assertEquals(
                testePurchaseOrderSuccessful.getCustomer(), customer
        );

    }


}
