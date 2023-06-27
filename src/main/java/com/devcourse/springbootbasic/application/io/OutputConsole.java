package com.devcourse.springbootbasic.application.io;

import com.devcourse.springbootbasic.application.constant.Message;
import com.devcourse.springbootbasic.application.domain.Voucher;
import com.devcourse.springbootbasic.application.dto.ListMenu;
import com.devcourse.springbootbasic.application.dto.Menu;
import com.devcourse.springbootbasic.application.dto.VoucherType;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@Component
public class OutputConsole {

    private static final TextIO textIO = TextIoFactory.getTextIO();
    private static final TextTerminal<?> textTerminal = textIO.getTextTerminal();

    public void showMenu() {
        textTerminal.println(Message.START_GAME_PROMPT);

        Arrays.stream(Menu.values())
                .forEach(menu -> {
                    textTerminal.print("Type ");
                    textTerminal.print(menu.getMenuCommand());
                    textTerminal.print(MessageFormat.format("({0}) ", menu.getMenuOrdinal()));
                    textTerminal.println(menu.getMenuPrompt());
                });
    }

    public void showVoucherType() {
        textTerminal.println(Message.VOUCHER_TYPE_PROMPT);

        Arrays.stream(VoucherType.values())
                .forEach(voucherType -> {
                    textTerminal.print(MessageFormat.format("{0}: ", voucherType.getTypeOrdinal()));
                    textTerminal.println(voucherType.getTypeString());
                });
    }

    public void showListMenu() {
        textTerminal.println(Message.LIST_MENU_PROMPT);

        Arrays.stream(ListMenu.values())
                .forEach(listMenu -> {
                    textTerminal.print(MessageFormat.format("{0}: ", listMenu.getListMenuOrdinal()));
                    textTerminal.println(listMenu.getListMenuPrompt());
                });
    }

    public void printMessage(String message) {
        textTerminal.println(message);
    }

    public void printError(Exception e) {
        printMessage(e.getMessage());
    }

    public void endPlatform() {
        printMessage(Message.END_GAME_PROMPT);
    }

    public void printVoucher(Voucher voucher) {
        printMessage(MessageFormat.format("{0} {1}", voucher.toString(), Message.CREATION_DONE_PROMPT));
    }

    public void printVouchers(List<String> voucherList) {
        printMessage(Message.LIST_VOUCHERS_PROMPT);
        voucherList.forEach(this::printMessage);
        textTerminal.println();
    }

    public void printBlackCustomers(List<String> blackCustomers) {
        printMessage(Message.BLACK_CUSTOMER_PROMPT);
        blackCustomers.forEach(this::printMessage);
        textTerminal.println();
    }
}