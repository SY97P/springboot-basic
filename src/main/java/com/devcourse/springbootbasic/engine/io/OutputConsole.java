package com.devcourse.springbootbasic.engine.io;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutputConsole {

    public static final String END_GAME = "\n프로그램을 종료합니다.";
    public static final String CREATION_DONE = "\n바우처가 생성되었습니다.";
    public static final String LIST_VOUCHERS = "\n-Your Voucher List-";
    private static final String BLACK_CUSTOMER = "\n진상 목록입니다.";

    public void printLine() {
        System.out.println();
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printError(Exception e) {
        printMessage(e.getMessage());
        printLine();
    }

    public void endPlatform() {
        printMessage(END_GAME);
        printLine();
    }

    public <T> void printVoucher(T t) {
        printMessage(t.toString());
    }

    public <T> void printVouchers(List<T> voucherList) {
        printMessage(LIST_VOUCHERS);
        voucherList.forEach(this::printVoucher);
        printLine();
    }

    public void printBlackCustomers(List<String> blackCustomers) {
        printMessage(BLACK_CUSTOMER);
        blackCustomers.forEach(this::printMessage);
    }
}
