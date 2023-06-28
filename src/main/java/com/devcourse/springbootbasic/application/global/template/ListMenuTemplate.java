package com.devcourse.springbootbasic.application.global.template;

import com.devcourse.springbootbasic.application.global.dto.ListMenu;
import com.devcourse.springbootbasic.application.domain.customer.service.CustomerService;
import com.devcourse.springbootbasic.application.domain.voucher.service.VoucherService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListMenuTemplate {

    private final CustomerService customerService;
    private final VoucherService voucherService;

    public ListMenuTemplate(CustomerService customerService, VoucherService voucherService) {
        this.customerService = customerService;
        this.voucherService = voucherService;
    }

    public List<String> listTask(ListMenu listMenu) {
        return switch (listMenu) {
            case VOUCHER_LIST -> voucherService.getAllVouchers();
            case BLACK_CUSTOMER_LIST -> customerService.getBlackCustomers();
        };
    }
}
