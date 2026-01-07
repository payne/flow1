package com.orderflow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.orderflow.service.CustomerService;
import com.orderflow.service.InventoryService;
import com.orderflow.service.OrderService;
import com.orderflow.service.workflow.OrderWorkflowService;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // We need to mock these because they might be auto-configured/scanned even if not used in this specific controller
    // or if there's a global advice/security config that depends on them.
    // Just to be safe and avoid context loading errors.
    @MockBean
    private CustomerService customerService;
    @MockBean
    private InventoryService inventoryService;
    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderWorkflowService orderWorkflowService;

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"));
    }
}
