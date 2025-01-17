package com.devcourse.springbootbasic.application.model;

import com.devcourse.springbootbasic.application.exception.InvalidDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscountValueTest {

    public static Stream<Arguments> provideNegetiveDiscountValues() {
        return Stream.of(
                Arguments.arguments(VoucherType.PERCENT_DISCOUNT, "-2.9"),
                Arguments.arguments(VoucherType.FIXED_AMOUNT, "-0.1"),
                Arguments.arguments(VoucherType.PERCENT_DISCOUNT, "-10000")
        );
    }

    public static Stream<Arguments> provideUpper100DiscountValues() {
        return Stream.of(
                Arguments.arguments(VoucherType.PERCENT_DISCOUNT, "100.1"),
                Arguments.arguments(VoucherType.PERCENT_DISCOUNT, "101"),
                Arguments.arguments(VoucherType.PERCENT_DISCOUNT, "230230")
        );
    }

    @ParameterizedTest
    @DisplayName("할인값이 음수면 실패")
    @MethodSource("provideNegetiveDiscountValues")
    void testNegitiveDiscountValue(VoucherType voucherType, String input) {
        assertThrows(InvalidDataException.class, () -> new DiscountValue(voucherType, input));
    }

    @ParameterizedTest
    @DisplayName("할인율이 100% 넘으면 실패")
    @MethodSource("provideUpper100DiscountValues")
    void testGreaterThan100Percent(VoucherType voucherType, String input) {
        assertThrows(InvalidDataException.class, () -> new DiscountValue(voucherType, input));
    }

}