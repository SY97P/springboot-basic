package com.tangerine.voucher_system.application.voucher.service.mapper;

import com.tangerine.voucher_system.application.voucher.model.Voucher;
import com.tangerine.voucher_system.application.voucher.service.dto.VoucherParam;
import com.tangerine.voucher_system.application.voucher.service.dto.VoucherResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VoucherServiceMapper {

    VoucherServiceMapper INSTANCE = Mappers.getMapper(VoucherServiceMapper.class);

    Voucher paramToDomain(VoucherParam voucherParam);

    VoucherResult domainToResult(Voucher domain);

}
