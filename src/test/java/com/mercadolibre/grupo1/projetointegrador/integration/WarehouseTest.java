package com.mercadolibre.grupo1.projetointegrador.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.grupo1.projetointegrador.dtos.WarehouseProductDTO;
import com.mercadolibre.grupo1.projetointegrador.entities.Agent;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração para Warehouse
 * descrição de cada teste no displayName
 * @author Nayara Coca
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class WarehouseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "http://localhost:8080/api/v1/fresh-products";

    @Test
    @WithMockUser(username = "agent1", roles = {"AGENT"})
    @DisplayName("Testa se retorna a quantidade total de produtos por armazém")
    public void itShouldReturnTheTotalQuantityOfProductOnTheWarehouses() throws Exception {
        MvcResult result =
                mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/warehouse?productId=1"))
                        .andExpect(status().isOk())
                        .andExpect((ResultMatcher) jsonPath("$.length()", Matchers.is(2)))
                        .andReturn();

        List<WarehouseProductDTO> finalResult =
                Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(),
                        WarehouseProductDTO[].class));
        assertEquals(2, finalResult.size());
        assertEquals(30, finalResult.get(0).getTotalQuantity());
    }

    @Test
    @WithMockUser(username = "agent1", roles = {"AGENT"})
    @DisplayName("Testa se retorna mensagem de erro quando o produto não existe em nenhum armazém")
    public void itShouldReturnAnException() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/warehouse?productId=15").contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("PRODUTO NÃO ENCONTRADO"));
    }
}
