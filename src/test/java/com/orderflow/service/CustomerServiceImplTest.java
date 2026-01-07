package com.orderflow.service;

import com.orderflow.domain.Customer;
import com.orderflow.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
    }

    @Test
    public void testCreateCustomer_Success() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer createdCustomer = customerService.createCustomer(customer);

        assertNotNull(createdCustomer);
        assertEquals(customer.getEmail(), createdCustomer.getEmail());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void testCreateCustomer_EmailExists() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            customerService.createCustomer(customer);
        });

        assertTrue(exception.getMessage().contains("Customer with email already exists"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void testGetCustomerById_Found() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> foundCustomer = customerService.getCustomerById(1L);

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getId(), foundCustomer.get().getId());
    }

    @Test
    public void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = customerService.getCustomerById(1L);

        assertFalse(foundCustomer.isPresent());
    }

    @Test
    public void testGetCustomerByEmail_Found() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));

        Optional<Customer> foundCustomer = customerService.getCustomerByEmail("john.doe@example.com");

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
    }

    @Test
    public void testGetAllCustomers() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> foundCustomers = customerService.getAllCustomers();

        assertNotNull(foundCustomers);
        assertEquals(1, foundCustomers.size());
    }

    @Test
    public void testUpdateCustomer_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer updatedCustomer = customerService.updateCustomer(customer);

        assertNotNull(updatedCustomer);
        assertEquals(customer.getId(), updatedCustomer.getId());
    }

    @Test
    public void testUpdateCustomer_NotFound() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            customerService.updateCustomer(customer);
        });

        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void testDeleteCustomer() {
        doNothing().when(customerRepository).deleteById(1L);

        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }
}
