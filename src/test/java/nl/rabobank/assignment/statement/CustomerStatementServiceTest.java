package nl.rabobank.assignment.statement;


//import nl.rabobank.assignment.entity.CustomerStatement;
//import org.junit.jupiter.api.Assertions;
import nl.rabobank.assignment.entity.CustomerStatementEvaluation;
import nl.rabobank.assignment.entity.EvaluationStatus;
import nl.rabobank.assignment.statement.util.StatementInputType;
import nl.rabobank.assignment.statement.util.StatementParser;
import nl.rabobank.assignment.statement.util.StatementParserFactory;
import nl.rabobank.assignment.statement.util.StatementRecord;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
//import org.mockito.Mockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerStatementServiceTest {

    @Mock
    private CustomerStatementEvaluationRepository statementEvaluationRepository;
    @Mock
    private StatementParserFactory statementParserFactory;

    private CustomerStatementService customerStatementService;

    @BeforeEach
    public void setup() {
        customerStatementService = new CustomerStatementService(statementEvaluationRepository, statementParserFactory);
    }

    @Test
    public void process_WithValidData_Success() throws Exception {

        List<StatementRecord> testStatementRecords = Arrays.asList(
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
                        .build());


        TestStatementParser testStatementParser = new TestStatementParser();
        testStatementParser.setTestRecords(testStatementRecords);
        Mockito.when(statementParserFactory.createStatementParser(Mockito.any())).thenReturn(testStatementParser);

        Mockito.when(statementEvaluationRepository.findByUuid(Mockito.any(UUID.class))).thenReturn(
                Optional.of(CustomerStatementEvaluation.builder()
                        .uuid(UUID.randomUUID())
                        .status(EvaluationStatus.INITIALIZED)
                        .createdAt(LocalDateTime.now())
                        .build()));

        customerStatementService.process(UUID.randomUUID(), StatementInputType.XML, "".getBytes());

        Mockito.verify(statementEvaluationRepository).save(Mockito.any(CustomerStatementEvaluation.class));
    }

}
