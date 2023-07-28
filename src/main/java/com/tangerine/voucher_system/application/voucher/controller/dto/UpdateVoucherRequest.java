package com.tangerine.voucher_system.application.voucher.controller.dto;

import com.tangerine.voucher_system.application.voucher.model.DiscountValue;
import com.tangerine.voucher_system.application.voucher.model.VoucherType;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateVoucherRequest(
        UUID voucherId,
        VoucherType voucherType,
        double discountValue
) {
}