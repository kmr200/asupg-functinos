package org.asupg.functions.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.asupg.functions.model.TransactionDTO;
import org.asupg.functions.util.ConstantsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ExcelParserService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelParserService.class);

    private final CosmosDbService  cosmosDbService;

    @Inject
    public ExcelParserService(CosmosDbService cosmosDbService) {
        this.cosmosDbService = cosmosDbService;
    }

    public void parse(InputStream stream) {
        try (Workbook workbook = new XSSFWorkbook(stream)) {
            Sheet sheet = workbook.getSheetAt(0);

            List<TransactionDTO> transactions = new ArrayList<>();

            for (int i = ConstantsUtil.START_ROW; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                if (!isTransaction(row)) continue;

                if (!isCreditTransaction(row)) continue;

                TransactionDTO transaction = parseRow(row);
                logger.info("Parsed transaction: {}", transaction);

                transactions.add(transaction);
            }

            cosmosDbService.bulkSaveTransaction(transactions);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse excel", e);
        }
    }

    private boolean isTransaction(Row row) {
        try {
            parseDate(row.getCell(ConstantsUtil.DATE_COLUMN));
            Cell docNumCell = row.getCell(ConstantsUtil.DOC_NUM_COLUMN);

            return docNumCell.getStringCellValue() != null && !docNumCell.getStringCellValue().isBlank();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCreditTransaction(Row row) {
        BigDecimal credit = parseAmount(row.getCell(ConstantsUtil.CREDIT_COLUMN));
        return credit != null && credit.compareTo(BigDecimal.ZERO) > 0;
    }

    private BigDecimal parseAmount(Cell cell) {
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

    private LocalDate parseDate(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return LocalDate.parse(
                cell.getStringCellValue().trim(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
        );
    }

    private String getString(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            default -> null;
        };
    }

    private String extractInn(String value) {
        if (value == null) return null;
        Matcher m = Pattern.compile("(\\d{9})").matcher(value);
        return m.find() ? m.group(1) : null;
    }


    private String extractName(String value) {
        if (value == null) return null;
        return value.replaceAll("\\d{9}", "").trim();
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String buildTransactionHash(
            LocalDate date,
            String counterpartyInn,
            String accountNumber,
            String mfo,
            BigDecimal amount,
            String description
    ) {
        String raw = String.join(
                "|",
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

    private TransactionDTO parseRow(Row row) {
        LocalDate date = parseDate(row.getCell(ConstantsUtil.DATE_COLUMN));

        String counterpartyRaw = getString(row.getCell(ConstantsUtil.ACCOUNT_NAME_COLUMN));
        String counterpartyName = extractName(counterpartyRaw);
        String counterpartyInn = extractInn(counterpartyRaw);

        String accountNumber = getString(row.getCell(ConstantsUtil.ACCOUNT_NUM_COLUMN));
        String mfo = getString(row.getCell(ConstantsUtil.MFO_COLUMN));
        BigDecimal amount = parseAmount(row.getCell(ConstantsUtil.CREDIT_COLUMN));
        String description = getString(row.getCell(ConstantsUtil.DESCRIPTION_COLUMN));

        String transactionId = buildTransactionHash(
                date, counterpartyInn, accountNumber, mfo, amount, description
        );

        return new TransactionDTO(
                date,
                transactionId,
                counterpartyName,
                counterpartyInn,
                accountNumber,
                mfo,
                amount,
                description
        );

    }

}
