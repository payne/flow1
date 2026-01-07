package com.orderflow.controller;

import com.orderflow.domain.Customer;
import com.orderflow.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers/list";
    }

    @GetMapping("/create")
    public String createCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/create";
    }

    @PostMapping("/create")
    public String createCustomer(@ModelAttribute Customer customer) {
        customerService.createCustomer(customer);
        return "redirect:/customers";
    }
}
