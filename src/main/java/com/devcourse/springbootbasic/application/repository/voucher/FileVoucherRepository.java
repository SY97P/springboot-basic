package com.devcourse.springbootbasic.application.repository.voucher;

import com.devcourse.springbootbasic.application.constant.YamlProperties;
import com.devcourse.springbootbasic.application.domain.Voucher;
import com.devcourse.springbootbasic.application.exception.InvalidDataException;
import com.devcourse.springbootbasic.application.constant.Message;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@Profile({"default"})
public class FileVoucherRepository implements VoucherRepository {

    private final String filepath;

    public FileVoucherRepository(YamlProperties yamlProperties) {
        this.filepath = yamlProperties.getVoucherRecordPath();
    }

    @Override
    public Optional<Voucher> insert(Voucher voucher) {
        try (
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath, true));
        ) {
            String voucherInfo = "%s\n".formatted(voucher.toString());
            bufferedWriter.write(voucherInfo);
            return Optional.of(voucher);
        } catch (IOException e) {
            throw new InvalidDataException(Message.INVALID_FILE_ACCESS, e.getCause());
        }
    }

    @Override
    public List<String> findAll() {
        List<String> voucherRecord = new ArrayList<>();
        try (
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath))
        ) {
            String record;
            while ((record = bufferedReader.readLine()) != null) {
                voucherRecord.add(record);
            }
        } catch (IOException e) {
            throw new InvalidDataException(Message.INVALID_FILE_ACCESS, e.getCause());
        }
        return voucherRecord;
    }
}