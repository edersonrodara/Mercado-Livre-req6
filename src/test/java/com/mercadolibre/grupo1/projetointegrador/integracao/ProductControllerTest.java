package com.mercadolibre.grupo1.projetointegrador.integracao;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;

/**
 * @author Gabriel Essenio
 * teste de integração de products
 */
@AutoConfigureMockMvc
@SpringBootTest
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
    @author Gabriel Essenio
    Caminho Feliz All Product
     */
    @Transactional
    @Test
    @DisplayName("Testando endpoint para retornar todos os produtos cadastrados")
    public void testReturnAllProducts() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/fresh-products/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nome", Matchers.is("Melancia")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome", Matchers.is("Maçã")));
    }

    /**
    @author Gabriel Essenio
    Caminho Feliz Product By Category
     */
    @Transactional
    @Test
    @DisplayName("Testando se retorna os produtos pela categoria passada pelo parametro")
    public void testReturnProductsByCategory() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/fresh-products/list?status=FRESCO"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome", Matchers.is("Maçã")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].category", Matchers.is("FRESCO")));
    }
}