package com.devcourse.springbootbasic.application.customer.service;

import com.devcourse.springbootbasic.application.customer.service.CustomerService;
import com.devcourse.springbootbasic.application.customer.model.Customer;
import com.devcourse.springbootbasic.application.customer.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CustomerServiceTest {

    @Test
    @DisplayName("블랙고객 리스트 반환 시 성공한다.")
    void GetBlackCustomers_VoidParam_ReturnVoucherList() {
        var customerRepositoryMock = mock(CustomerRepository.class);
        given(customerRepositoryMock.findAllBlackCustomers()).willReturn(
                List.of(
                        new Customer(UUID.fromString("061d89ad-1a6a-11ee-aed4-0242ac110002"),"사과","apple@gmail.com", LocalDateTime.parse("2023-07-04T12:55:16.649232", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnn"))),
                        new Customer(UUID.fromString("06201b27-1a6a-11ee-aed4-0242ac110002"),"딸기","strawberry@gmail.com",LocalDateTime.parse("2023-07-04T12:55:16.668232", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnn"))),
                        new Customer(UUID.fromString("06223606-1a6a-11ee-aed4-0242ac110002"),"포도","grape@gmail.com",LocalDateTime.parse("2023-07-04T12:55:16.682232", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnn"))),
                        new Customer(UUID.fromString("06223606-1a6a-11ee-aed4-0242ac110003"),"배","peach@gmail.com", LocalDateTime.parse("2023-05-23T12:42:12.121232", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnn")))
                )
        );
        var sut = new CustomerService(customerRepositoryMock);
        var blackCustomers = sut.getBlackCustomers();
        assertThat(blackCustomers, notNullValue());
        assertThat(blackCustomers, not(empty()));
        assertThat(blackCustomers, instanceOf(List.class));
        assertThat(blackCustomers.get(0), instanceOf(Customer.class));
    }

    @Test
    @DisplayName("정상적인 고객 정보 조회 시 해당 고객 반환하면 성공한다.")
    void findById_

}