package com.mercadolibre.grupo1.projetointegrador.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.grupo1.projetointegrador.dtos.ProductDTO;
import com.mercadolibre.grupo1.projetointegrador.dtos.PurchaseOrderDTO;
import com.mercadolibre.grupo1.projetointegrador.entities.Role;
import com.mercadolibre.grupo1.projetointegrador.entities.Seller;
import com.mercadolibre.grupo1.projetointegrador.entities.enums.ProductCategory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Gabriel Essenio
 * teste de integração de products
 */
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @return
     * @author Ederson Rodrigues Araujo
     */
    private ProductDTO createProductDTOFake() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Product1");
        productDTO.setVolume(20.0);
        productDTO.setPrice(BigDecimal.valueOf(350));
        productDTO.setCategory(ProductCategory.FRESCO);

        return productDTO;
    }

    /**
     * @author Gabriel Essenio
     * Caminho Feliz All Product
     */
    @Test
    @DisplayName("Testando endpoint para retornar todos os produtos cadastrados")
    public void testReturnAllProducts() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/fresh-products/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(9)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is("MACA")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("UVA")));
    }

    /**
     * @author Gabriel Essenio
     * Caminho Feliz Product By Category
     */
    @Test
    @DisplayName("Testando se retorna os produtos pela categoria passada pelo parametro")

    public void testReturnProductsByCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/fresh-products/list?status=FRESCO"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("UVA")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].category", Matchers.is("FRESCO")));
    }

    /**
     * @author Gabriel Essenio
     * Testa status quando passar uma categoria que nao existe
     */
    @Test
    @DisplayName("Testando se o status retorna 404 apois tentar mudar status quando passada uma categoria que nao existe")
    public void testStatusReturn404WhenIdCatogyDontExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/fresh-products/list?status=FRESCA"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Categoria inválida")));
    }

    @Test
    @DisplayName("Testa de cadastra produto com sucesso")
    @WithMockUser(username = "seller1", roles = {"SELLER"})
    public void testRegisterProductSuccessful() throws Exception {
        ProductDTO productDTOFake = createProductDTOFake();

        String stringProductDTO = objectMapper.writeValueAsString(productDTOFake);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/fresh-products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringProductDTO))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.productId", Matchers.is(10)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.name", Matchers.is("Product1")))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.price", Matchers.is(350)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.category", Matchers.is("FRESCO")))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.volume", Matchers.is(20.)))
                .andReturn();
    }

    @Test
    @DisplayName("Testa passando valor null no nome ao criar um product")
    @WithMockUser(username = "seller1", roles = "SELLER")
    public void testNameValueNullInCreateProduct() throws Exception {
        ProductDTO productDTOFake = createProductDTOFake();
        productDTOFake.setName(null);

        String stringProductDTO = objectMapper.writeValueAsString(productDTOFake);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/fresh-products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringProductDTO))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message", Matchers.is("O nome do produto não pode estar vazio")))
                .andReturn();
    }
}