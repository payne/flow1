package com.orderflow.controller;

import com.orderflow.dto.OrderApprovalDTO;
import com.orderflow.dto.OrderDTO;
import com.orderflow.dto.OrderItemDTO;
import com.orderflow.service.CustomerService;
import com.orderflow.service.InventoryService;
import com.orderflow.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found")));
        return "orders/details";
    }

    @GetMapping("/create")
    public String createOrderForm(Model model) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setItems(new ArrayList<>());
        // Add one empty item for the form
        orderDTO.getItems().add(new OrderItemDTO());

        model.addAttribute("orderDTO", orderDTO);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("inventoryItems", inventoryService.getAllInventory());
        return "orders/create";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute OrderDTO orderDTO) {
        orderService.createOrder(orderDTO);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/tasks/{taskId}/complete")
    public String completeTask(@PathVariable Long id, @PathVariable String taskId, @ModelAttribute OrderApprovalDTO approvalDTO) {
        orderService.approveOrder(id, taskId, approvalDTO);
        return "redirect:/orders/" + id;
    }
}
