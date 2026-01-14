package org.asupg.functions.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.asupg.functions.model.TransactionDTO;
import org.asupg.functions.repository.CosmosTransactionRepository;
import org.asupg.functions.util.ConstantsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.asupg.functions.util.ParserUtil.*;

@Singleton
public class ExcelParserService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelParserService.class);

    private final CosmosTransactionRepository cosmosTransactionRepository;

    @Inject
    public ExcelParserService(CosmosTransactionRepository cosmosTransactionRepository) {
        this.cosmosTransactionRepository = cosmosTransactionRepository;
    }

    public List<TransactionDTO> parse(InputStream stream) {
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

            //Save transactions
            List<TransactionDTO> savedTransactions = cosmosTransactionRepository.bulkSaveTransaction(transactions);
            logger.info("Successfully saved transactions: " + savedTransactions.size());

            return savedTransactions;
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

}
