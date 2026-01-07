package com.orderflow.controller;

import com.orderflow.domain.Customer;
import com.orderflow.service.CustomerService;
import com.orderflow.service.InventoryService;
import com.orderflow.service.OrderService;
import com.orderflow.service.workflow.OrderWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    // Mock other beans to avoid ApplicationContext failure if they are needed by security or other configs
    @MockBean
    private InventoryService inventoryService;
    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderWorkflowService orderWorkflowService;


    @Test
    public void testListCustomers() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(view().name("customers/list"))
                .andExpect(model().attributeExists("customers"));
    }

    @Test
    public void testCreateCustomerForm() throws Exception {
        mockMvc.perform(get("/customers/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("customers/create"))
                .andExpect(model().attributeExists("customer"));
    }

    @Test
    public void testCreateCustomerSubmit() throws Exception {
        mockMvc.perform(post("/customers/create")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));
    }
}
