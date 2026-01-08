package com.orderflow.controller;

import com.orderflow.domain.Customer;
import com.orderflow.domain.Order;
import com.orderflow.dto.OrderApprovalDTO;
import com.orderflow.service.CustomerService;
import com.orderflow.service.InventoryService;
import com.orderflow.service.OrderService;
import com.orderflow.service.workflow.OrderWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private OrderWorkflowService orderWorkflowService;


    @Test
    public void testListOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    public void testOrderDetails() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setCustomer(new Customer());
        when(orderService.getOrderById(anyLong())).thenReturn(Optional.of(order));
        when(orderWorkflowService.getActiveTasksForOrder(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/details"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("activeTasks"))
                .andExpect(model().attributeExists("approvalDTO"));
    }

    @Test
    public void testCreateOrderForm() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());
        when(inventoryService.getAllInventory()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/create"))
                .andExpect(model().attributeExists("orderDTO"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attributeExists("inventoryItems"));
    }

    @Test
    public void testCompleteTask() throws Exception {
        mockMvc.perform(post("/orders/1/tasks/123/complete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("approved", "true")
                        .param("comments", "Great job"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1"));

        verify(orderService).approveOrder(eq(1L), eq("123"), any(OrderApprovalDTO.class));
    }
}
