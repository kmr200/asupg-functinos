package org.asupg.functions.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.asupg.functions.model.TransactionDTO;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtil {

    private ParserUtil() {}

    public static String buildTransactionHash(
            TransactionDTO.TransactionType transactionType,
            LocalDate date,
            String counterpartyInn,
            String accountNumber,
            String mfo,
            BigDecimal amount,
            String description
    ) {
        String raw = String.join(
                "|",
                normalize(transactionType.toString()),
                normalize(date.toString()),
                normalize(counterpartyInn),
                normalize(accountNumber),
                normalize(mfo),
                normalize(amount.toPlainString()),
                normalize(description)
        );

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String buildTransactionHash(
            TransactionDTO.TransactionType transactionType,
            YearMonth date,
            String counterpartyInn,
            String deviceId,
            BigDecimal amount
    ) {
        String raw = String.join(
                "|",
                normalize(transactionType.toString()),
                normalize(date.toString()),
                normalize(counterpartyInn),
                normalize(amount.toPlainString()),
                normalize(deviceId)
        );

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TransactionDTO parseRow(Row row) {
        LocalDate date = parseDate(row.getCell(ConstantsUtil.DATE_COLUMN));

        String counterpartyRaw = getString(row.getCell(ConstantsUtil.ACCOUNT_NAME_COLUMN));
        String counterpartyName = extractName(counterpartyRaw);
        String counterpartyInn = extractInn(counterpartyRaw);

        String accountNumber = getString(row.getCell(ConstantsUtil.ACCOUNT_NUM_COLUMN));
        String mfo = getString(row.getCell(ConstantsUtil.MFO_COLUMN));
        BigDecimal amount = parseAmount(row.getCell(ConstantsUtil.CREDIT_COLUMN));
        String description = getString(row.getCell(ConstantsUtil.DESCRIPTION_COLUMN));

        String transactionId = buildTransactionHash(
                TransactionDTO.TransactionType.BANK_PAYMENT, date, counterpartyInn, accountNumber, mfo, amount, description
        );

        return new TransactionDTO(
                date,
                transactionId,
                counterpartyName,
                counterpartyInn,
                accountNumber,
                mfo,
                amount,
                description,
                TransactionDTO.TransactionType.BANK_PAYMENT
        );

    }

    public static String extractInn(String value) {
        if (value == null) return null;
        Matcher m = Pattern.compile("(\\d{9})").matcher(value);
        return m.find() ? m.group(1) : null;
    }


    public static String extractName(String value) {
        if (value == null) return null;
        return value.replaceAll("\\d{9}", "").trim();
    }

    public static String normalize(String s) {
        if (s == null) return "";
        return s
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static LocalDate parseDate(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return LocalDate.parse(
                cell.getStringCellValue().trim(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        );
    }

    public static String getString(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            default -> null;
        };
    }

    public static BigDecimal parseAmount(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        String raw = cell.getStringCellValue()
                .replace(" ", "")
                .replace(",", ".");

        if (raw.isBlank()) return null;

        return new BigDecimal(raw);
    }

}
