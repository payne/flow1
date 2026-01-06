package com.orderflow.service;

import com.orderflow.domain.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for customer operations.
 */
public interface CustomerService {

    Customer createCustomer(Customer customer);

    Optional<Customer> getCustomerById(Long id);

    Optional<Customer> getCustomerByEmail(String email);

    List<Customer> getAllCustomers();

    Customer updateCustomer(Customer customer);

    void deleteCustomer(Long id);
}
