package com.devcourse.springbootbasic.application.customer.controller;

import com.devcourse.springbootbasic.application.customer.model.Customer;
import com.devcourse.springbootbasic.application.customer.service.CustomerService;
import com.devcourse.springbootbasic.application.global.exception.InvalidDataException;
import com.wix.mysql.EmbeddedMysql;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v8_0_17;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerTest {

    static List<Customer> validCustomers = List.of(
            new Customer(UUID.randomUUID(), "사과", "apple@naver.com", LocalDateTime.now()),
            new Customer(UUID.randomUUID(), "딸기", "strawberry@naver.com", LocalDateTime.now()),
            new Customer(UUID.randomUUID(), "포도", "grape@naver.com", LocalDateTime.now()),
            new Customer(UUID.randomUUID(), "배", "peach@naver.com", LocalDateTime.now())
    );

    static List<CustomerDto> validCustomerDtos = List.of(
            new CustomerDto(UUID.randomUUID(), "사과", "apple@naver.com", LocalDateTime.now()),
            new CustomerDto(UUID.randomUUID(), "딸기", "strawberry@naver.com", LocalDateTime.now()),
            new CustomerDto(UUID.randomUUID(), "포도", "grape@naver.com", LocalDateTime.now()),
            new CustomerDto(UUID.randomUUID(), "배", "peach@naver.com", LocalDateTime.now())
    );

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:8070/test-voucher_system")
                    .username("test")
                    .password("test1234!")
                    .type(HikariDataSource.class)
                    .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }
    }

    @Autowired
    CustomerController customerController;

    EmbeddedMysql embeddedMysql;

    @BeforeAll
    void init() {
        var mysqlConfig = aMysqldConfig(v8_0_17)
                .withCharset(UTF8)
                .withPort(8070)
                .withUser("test", "test1234!")
                .withTimeZone("Asia/Seoul")
                .build();
        embeddedMysql = anEmbeddedMysql(mysqlConfig)
                .addSchema("test-voucher_system", classPathScript("test-schema.sql"))
                .start();
    }

    @BeforeEach
    void cleanup() {
        customerController.deleteAllCustomers();
    }

    @AfterAll
    void destroy() {
        embeddedMysql.stop();
    }

    @Test
    @DisplayName("고객 정보를 리스트로 반환하면 성공한다.")
    void findBlackCustomers_ParamVoid_ReturnCustomerDtoList() {
        var serviceMock = mock(CustomerService.class);
        given(serviceMock.findAllCustomers()).willReturn(validCustomers);
        var sut = new CustomerController(serviceMock);
        var result = sut.findAllCustomers();
        assertThat(result, notNullValue());
        assertThat(result.isEmpty(), is(false));
        assertThat(result.get(0), instanceOf(CustomerDto.class));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 추가하면 성공한다.")
    @MethodSource("provideValidCustomerDtos")
    void registCustomer_ParamCustomer_InsertAndReturnCustomerDto(CustomerDto customerDto) {
        customerController.registCustomer(customerDto);
        var insertedCustomer = customerController.findCustomerById(customerDto);
        assertThat(insertedCustomer, notNullValue());
        assertThat(insertedCustomer, instanceOf(CustomerDto.class));
        assertThat(insertedCustomer.name(), is(customerDto.name()));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 추가하면 실패한다.")
    @MethodSource("provideValidCustomers")
    void registCustomer_ParamNotExistCustomerDto_Exception(Customer customer) {
        var dto = CustomerDto.of(customer);
        customerController.registCustomer(dto);
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.registCustomer(dto));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 업데이트하면 성공한다.")
    @MethodSource("provideValidCustomers")
    void updateCustomer_ParamExistCustomerDto_UpdateCustomerDto(Customer customer) {
        var dto = CustomerDto.of(customer);
        customerController.registCustomer(dto);
        var otherCustomer = new Customer(
                customer.getCustomerId(),
                "토끼",
                customer.getEmail(),
                customer.getcreatedTime()
        );
        var otherDto = CustomerDto.of(otherCustomer);
        customerController.updateCustomer(otherDto);
        var updatedDto = customerController.findCustomerById(otherDto);
        assertThat(updatedDto.name(), is(otherDto.name()));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 업데이트하면 실패한다.")
    @MethodSource("provideValidCustomers")
    void updateCustomer_ParamNotExistCustomerDto_Exception(Customer customer) {
        var otherCustomer = new Customer(
                customer.getCustomerId(),
                "토끼",
                customer.getEmail(),
                customer.getcreatedTime()
        );
        var otherDto = CustomerDto.of(otherCustomer);
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.updateCustomer(otherDto));
    }

    @Test
    @DisplayName("모든 고객을 반환하면 성공한다.")
    void findAllCustomers_PararmVoid_ReturnCustomerDtoList() {
        var customerServiceMock = mock(CustomerService.class);
        given(customerServiceMock.findAllCustomers()).willReturn(validCustomers);
        var sut = new CustomerController(customerServiceMock);
        var list = sut.findAllCustomers();
        assertThat(list, notNullValue());
        assertThat(list.get(0), instanceOf(CustomerDto.class));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 아이디로 조회 시 성공한다.")
    @MethodSource("provideValidCustomerDtos")
    void findCustomerById_ParamExistCustomerDto_ReturnCustomerDto(CustomerDto customerDto) {
        customerController.registCustomer(customerDto);
        var findedCustomerDto = customerController.findCustomerById(customerDto);
        assertThat(findedCustomerDto, samePropertyValuesAs(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 아이디로 조회 시 실패한다.")
    @MethodSource("provideValidCustomerDtos")
    void findCustomerById_ParamNotExistCustomerDto_Exception(CustomerDto customerDto) {
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.findCustomerById(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 이름으로 조회 시 성공한다.")
    @MethodSource("provideValidCustomerDtos")
    void findCustomerByName_ParamExistCustomerDto_ReturnCustomerDto(CustomerDto customerDto) {
        customerController.registCustomer(customerDto);
        var findedCustomerDto = customerController.findCustomerByName(customerDto);
        assertThat(findedCustomerDto, samePropertyValuesAs(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 이름으로 조회 시 실패한다.")
    @MethodSource("provideValidCustomerDtos")
    void findCustomerByName_ParamNotExistCustomerDto_Exception(CustomerDto customerDto) {
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.findCustomerByName(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 이메일로 조회 시 성공한다.")
    @MethodSource("provideValidCustomerDtos")
    void findCustomerByEmail_ParamExistCustomerDto_ReturnCustomerDto(CustomerDto customerDto) {
        customerController.registCustomer(customerDto);
        var findedCustomerDto = customerController.findCustomerByEmail(customerDto);
        assertThat(findedCustomerDto, samePropertyValuesAs(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 이메일로 조회 시 실패한다.")
    @MethodSource("provideValidCustomerDtos")
    void findCustomerByEmail_ParamNotExistCustomerDto_Exception(CustomerDto customerDto) {
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.findCustomerByEmail(customerDto));
    }

    @Test
    @DisplayName("모든 고객을 제거하면 성공한다.")
    void deleteAllCustomers_ParamVoid_DeleteAllCustomers() {
        customerController.deleteAllCustomers();
        var allCustomers = customerController.findAllCustomers();
        assertThat(allCustomers.isEmpty(), is(true));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 아이디로 제거하면 성공한다.")
    @MethodSource("provideValidCustomerDtos")
    void deleteCustomerById_ParamExistCustomer_ReturnAndDeleteCustomer(CustomerDto customerDto) {
        customerController.registCustomer(customerDto);
        var deletedCustomer = customerController.deleteCustomerById(customerDto);
        assertThat(deletedCustomer, samePropertyValuesAs(customerDto));
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.findCustomerById(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 아이디로 제거하면 실패한다.")
    @MethodSource("provideValidCustomerDtos")
    void deletCustomerById_ParamNotExistCustomer_Exception(CustomerDto customerDto) {
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.deleteCustomerById(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 이름으로 제거하면 성공한다.")
    @MethodSource("provideValidCustomerDtos")
    void deleteCustomerByName_ParamExistCustomer_ReturnAndDeleteCustomer(CustomerDto customerDto) {
        customerController.registCustomer(customerDto);
        var deletedCustomer = customerController.deleteCustomerByName(customerDto);
        assertThat(deletedCustomer, samePropertyValuesAs(customerDto));
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.findCustomerById(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 이름으로 제거하면 실패한다.")
    @MethodSource("provideValidCustomerDtos")
    void deletCustomerByName_ParamNotExistCustomer_Exception(CustomerDto customerDto) {
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.deleteCustomerByName(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하는 고객을 이메일로 제거하면 성공한다.")
    @MethodSource("provideValidCustomerDtos")
    void deleteCustomerByEmail_ParamExistCustomer_ReturnAndDeleteCustomer(CustomerDto customerDto) {
        customerController.registCustomer(customerDto);
        var deletedCustomer = customerController.deleteCustomerByEmail(customerDto);
        assertThat(deletedCustomer, samePropertyValuesAs(customerDto));
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.findCustomerById(customerDto));
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 고객을 이메일로 제거하면 실패한다.")
    @MethodSource("provideValidCustomerDtos")
    void deletCustomerByEmail_ParamNotExistCustomer_Exception(CustomerDto customerDto) {
        Assertions.assertThrows(InvalidDataException.class, () -> customerController.deleteCustomerByEmail(customerDto));
    }

    static Stream<Arguments> provideValidCustomers() {
        return validCustomers.stream()
                .map(Arguments::of);
    }

    static Stream<Arguments> provideValidCustomerDtos() {
        return validCustomerDtos.stream()
                .map(Arguments::of);
    }


}