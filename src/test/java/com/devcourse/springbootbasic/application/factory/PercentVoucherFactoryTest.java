package com.devcourse.springbootbasic.application.factory;

import com.devcourse.springbootbasic.application.dto.VoucherType;
import com.devcourse.springbootbasic.application.factory.PercentVoucherFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PercentVoucherFactoryTest {

    @Test
    @DisplayName("비율값 할인 바우처 생성")
    void 비율할인바우처생성테스트() {
        var voucher = new PercentVoucherFactory().create(0.1);
        assertThat(voucher.getVoucherType(), is(VoucherType.PERCENT_DISCOUNT));
    }

}