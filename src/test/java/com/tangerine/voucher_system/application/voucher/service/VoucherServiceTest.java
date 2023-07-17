package com.tangerine.voucher_system.application.voucher.service;

import com.tangerine.voucher_system.application.global.exception.InvalidDataException;
import com.tangerine.voucher_system.application.voucher.model.DiscountValue;
import com.tangerine.voucher_system.application.voucher.model.Voucher;
import com.tangerine.voucher_system.application.voucher.model.VoucherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@SpringBootTest
@ActiveProfiles("test")
class VoucherServiceTest {

    static List<Voucher> vouchers = List.of(
            new Voucher(UUID.randomUUID(), VoucherType.FIXED_AMOUNT, new DiscountValue(VoucherType.FIXED_AMOUNT, "100"), LocalDateTime.now(), Optional.empty()),
            new Voucher(UUID.randomUUID(), VoucherType.PERCENT_DISCOUNT, new DiscountValue(VoucherType.PERCENT_DISCOUNT, "0"), LocalDateTime.now(), Optional.empty()),
            new Voucher(UUID.randomUUID(), VoucherType.FIXED_AMOUNT, new DiscountValue(VoucherType.FIXED_AMOUNT, "1240"), LocalDateTime.now(), Optional.empty()),
            new Voucher(UUID.randomUUID(), VoucherType.PERCENT_DISCOUNT, new DiscountValue(VoucherType.PERCENT_DISCOUNT, "10"), LocalDateTime.now(), Optional.empty())
    );
    @Autowired
    VoucherService service;

    static Stream<Arguments> provideVouchers() {
        return vouchers.stream()
                .map(Arguments::of);
    }

    @BeforeEach
    void init() {
        service.deleteAllVouchers();
    }

    @ParameterizedTest
    @DisplayName("존재하지 않은 바우처를 추가하면 성공한다.")
    @MethodSource("provideVouchers")
    void createVoucher_ParamNotExistVoucher_InsertAndReturnVoucher(Voucher voucher) {

        service.createVoucher(voucher);

        Voucher createdVoucher = service.findVoucherById(voucher.getVoucherId());
        assertThat(createdVoucher).isEqualTo(voucher);
    }

    @ParameterizedTest
    @DisplayName("존재하는 바우처를 추가하면 실패한다.")
    @MethodSource("provideVouchers")
    void createVoucher_ParamExistVoucher_Exception(Voucher voucher) {
        service.createVoucher(voucher);

        Exception exception = catchException(() -> service.createVoucher(voucher));

        assertThat(exception).isInstanceOf(InvalidDataException.class);
    }

    @ParameterizedTest
    @DisplayName("존재하는 바우처를 갱신하면 성공한다.")
    @MethodSource("provideVouchers")
    void createVoucher_ParamExistVoucher_UpdateAndReturnVoucher(Voucher voucher) {
        service.createVoucher(voucher);

        Voucher newVoucher = new Voucher(voucher.getVoucherId(), VoucherType.FIXED_AMOUNT, new DiscountValue(VoucherType.FIXED_AMOUNT, 23.1), LocalDateTime.now(), Optional.empty());
        service.updateVoucher(newVoucher);

        Voucher updatedVoucher = service.findVoucherById(voucher.getVoucherId());
        assertThat(updatedVoucher).isEqualTo(newVoucher);
    }

    @ParameterizedTest
    @DisplayName("존재하지 않은 바우처를 추가하면 실패한다.")
    @MethodSource("provideVouchers")
    void createVoucher_ParamNotExistVoucher_Exception(Voucher voucher) {

        Exception exception = catchException(() -> service.updateVoucher(voucher));

        assertThat(exception).isInstanceOf(InvalidDataException.class);
    }

    @Test
    @DisplayName("생성된 바우처가 리스트 형태로 반환되면 성공한다.")
    void findVouchers_ParamVoid_ReturnVoucherList() {
        vouchers.forEach(service::createVoucher);

        List<Voucher> result = service.findVouchers();

        assertThat(result).isNotEmpty();
    }

    @ParameterizedTest
    @DisplayName("존재하는 바우처를 아이디로 검색하는 경우 성공한다.")
    @MethodSource("provideVouchers")
    void findVoucherById_ParamExistVoucher_ReturnVoucher(Voucher voucher) {
        service.createVoucher(voucher);

        Voucher foundVoucher = service.findVoucherById(voucher.getVoucherId());

        assertThat(foundVoucher).isEqualTo(voucher);
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 바우처를 아이디로 검색하는 경우 실패한다.")
    @MethodSource("provideVouchers")
    void findVoucherById_ParamNotExistVoucher_Exception(Voucher voucher) {

        Exception exception = catchException(() -> service.findVoucherById(voucher.getVoucherId()));

        assertThat(exception).isInstanceOf(InvalidDataException.class);
    }

    @ParameterizedTest
    @DisplayName("존재하는 바우처를 바우처 타입으로 검색하는 경우 성공한다.")
    @MethodSource("provideVouchers")
    void findVoucherByVoucherType_ParamExistVoucher_ReturnVoucher(Voucher voucher) {
        service.createVoucher(voucher);

        Voucher foundVoucher = service.findVoucherByVoucherType(voucher.getVoucherType());

        assertThat(foundVoucher).isEqualTo(voucher);
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 바우처를 바우처 타입으로 검색하는 경우 실패한다.")
    @MethodSource("provideVouchers")
    void findVoucherByVoucherType_ParamNotExistVoucher_Exception(Voucher voucher) {

        Exception exception = catchException(() -> service.findVoucherByVoucherType(voucher.getVoucherType()));

        assertThat(exception).isInstanceOf(InvalidDataException.class);
    }

    @ParameterizedTest
    @DisplayName("존재하는 바우처를 생성일자로 검색하는 경우 성공한다.")
    @MethodSource("provideVouchers")
    void findVoucherByCreatedAt_ParamExistVoucher_ReturnVoucher(Voucher voucher) {
        service.createVoucher(voucher);

        Voucher foundVoucher = service.findVoucherByCreatedAt(voucher.getCreatedAt());

        assertThat(foundVoucher).isEqualTo(voucher);
    }

    @ParameterizedTest
    @DisplayName("존재하지 않는 바우처를 생성일자로 검색하는 경우 실패한다.")
    @MethodSource("provideVouchers")
    void findVoucherByCreatedAt_ParamNotExistVoucher_Exception(Voucher voucher) {

        Exception exception = catchException(() -> service.findVoucherByCreatedAt(voucher.getCreatedAt()));

        assertThat(exception).isInstanceOf(InvalidDataException.class);
    }

    @Test
    @DisplayName("모든 바우처를 제거한다.")
    void deleteAllVouchers_ParamVoid_DeleteAllVouchers() {

        service.deleteAllVouchers();

        assertThat(service.findVouchers()).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("존재하는 바우처를 아이디로 제거하면 성공한다.")
    @MethodSource("provideVouchers")
    void deleteVoucherById_ParamExistVoucherId_DeleteVoucher(Voucher voucher) {
        service.createVoucher(voucher);

        Voucher deletedVoucher = service.deleteVoucherById(voucher.getVoucherId());

        assertThat(deletedVoucher).isEqualTo(voucher);
    }

    @ParameterizedTest
    @DisplayName("없는 바우처를 아이디로 제거하면 실패한다.")
    @MethodSource("provideVouchers")
    void deleteVoucherById_ParamNotExistVoucherId_Exception(Voucher voucher) {

        Exception exception = catchException(() -> service.deleteVoucherById(voucher.getVoucherId()));

        assertThat(exception).isInstanceOf(InvalidDataException.class);
    }

}