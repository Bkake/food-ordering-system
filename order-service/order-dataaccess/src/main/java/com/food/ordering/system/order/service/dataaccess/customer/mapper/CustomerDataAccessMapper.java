package com.food.ordering.system.order.service.dataaccess.customer.mapper;

import com.food.ordering.system.order.service.dataaccess.customer.entity.CustomerEntity;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {
    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        Customer customer = new Customer();
        customer.setId(new CustomerId(customerEntity.getId()));
        return customer;
    }
}
