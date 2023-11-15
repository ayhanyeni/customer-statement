package nl.rabobank.assignment.statement;

import nl.rabobank.assignment.statement.pojo.StatementReferenceInfo;
import nl.rabobank.assignment.statement.util.StatementEvaluator;
import nl.rabobank.assignment.statement.util.StatementRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class StatementEvaluatorTest {

    @Test
    public void evaluate_UsingTestParserAndHavingFailingStatements_Success() throws Exception {
        List<StatementRecord> testRecords = Arrays.asList(
            StatementRecord.builder()
                .transactionReference(1L)
                .accountNumber("NL93ABNA0123456780")
                .description("Test 1")
                .startBalance(new BigDecimal("10.012"))
                .mutation(new BigDecimal("0.008"))
                .endBalance(new BigDecimal("10.02"))
                .build(),
            StatementRecord.builder()
                .transactionReference(2L)
                .accountNumber("NL93ABNA0123456780")
                .description("Test 2")
                .startBalance(new BigDecimal("10"))
                .mutation(new BigDecimal("0.0"))
                .endBalance(new BigDecimal("10.00"))
                .build(),
            StatementRecord.builder()
                .transactionReference(3L)
                .accountNumber("NL93ABNA0123456781")
                .description("Test 3")
                .startBalance(new BigDecimal("1000000000000.01"))
                .mutation(new BigDecimal("1000000000000.09"))
                .endBalance(new BigDecimal("2000000000000.1"))
                .build(),
            StatementRecord.builder()
                .transactionReference(1L)
                .accountNumber("NL93ABNA0123456781")
                .description("Test 3")
                .startBalance(new BigDecimal("1.01"))
                .mutation(new BigDecimal("2"))
                .endBalance(new BigDecimal("3.01"))
                .build(),
            StatementRecord.builder()
                .transactionReference(4L)
                .accountNumber("NL93ABNA0123456781")
                .description("Test 4")
                .startBalance(new BigDecimal("1000000000000.01"))
                .mutation(new BigDecimal("1000000000000.09"))
                .endBalance(new BigDecimal("2000000000000.101"))
                .build()
        );

        TestStatementParser statementParser = new TestStatementParser();
        statementParser.setTestRecords(testRecords);

        List<StatementReferenceInfo> failedRecords = new StatementEvaluator(statementParser).evaluate();

        Assertions.assertEquals(2, failedRecords.size());
        Assertions.assertTrue(failedRecords.stream().anyMatch(record -> record.getTransactionReference().equals(1L)));
        Assertions.assertTrue(failedRecords.stream().anyMatch(record -> record.getTransactionReference().equals(4L)));
        Assertions.assertFalse(failedRecords.stream().anyMatch(record -> record.getTransactionReference().equals(2L)));
    }
}
