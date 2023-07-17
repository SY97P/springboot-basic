package com.tangerine.voucher_system.application.customer.controller;

import com.tangerine.voucher_system.application.customer.model.Name;
import com.tangerine.voucher_system.application.customer.service.CustomerService;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public List<CustomerDto> blackCustomerList() {
        return customerService.findBlackCustomers().stream()
                .map(CustomerDto::of)
                .toList();
    }

    public CustomerDto registerCustomer(CustomerDto customerDto) {
        return CustomerDto.of(customerService.createCustomer(customerDto.to()));
    }

    public CustomerDto updateCustomer(CustomerDto customerDto) {
        return CustomerDto.of(customerService.updateCustomer(customerDto.to()));
    }

    public List<CustomerDto> customerList() {
        return customerService.findAllCustomers()
                .stream()
                .map(CustomerDto::of)
                .toList();
    }

    public CustomerDto customerById(UUID customerId) {
        return CustomerDto.of(customerService.findCustomerById(customerId));
    }

    public CustomerDto customerByName(Name name) {
        return CustomerDto.of(customerService.findCustomerByName(name));
    }

    public CustomerDto unregisterCustomerById(UUID customerId) {
        return CustomerDto.of(customerService.deleteCustomerById(customerId));
    }

}