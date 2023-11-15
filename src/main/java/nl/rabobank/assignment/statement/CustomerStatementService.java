package nl.rabobank.assignment.statement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nl.rabobank.assignment.statement.pojo.StatementReferenceInfo;
import nl.rabobank.assignment.statement.util.StatementEvaluator;
import nl.rabobank.assignment.statement.util.StatementInputType;
import nl.rabobank.assignment.statement.util.StatementParser;
import nl.rabobank.assignment.statement.util.StatementParserFactory;
import nl.rabobank.assignment.entity.CustomerStatementEvaluation;
import nl.rabobank.assignment.entity.EvaluationStatus;
import nl.rabobank.assignment.exception.ProcessingFailedException;
import nl.rabobank.assignment.exception.ItemNotFoundException;
import nl.rabobank.assignment.exception.ProcessingNotReadyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * A spring-boot service class for customer statement processing operations.
 *
 * @author Ayhan Yeni
 */

@RequiredArgsConstructor
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CustomerStatementService {

    private static final Long MAX_RESULT_AGE_IN_MINS = 30L;
    private final CustomerStatementEvaluationRepository customerStatementEvaluationRepository;
    private final StatementParserFactory statementParserFactory;

    public UUID initializeCustomerStatementProcess() {

        UUID evaluationUuid = UUID.randomUUID();

        CustomerStatementEvaluation customerStatementEvaluation = CustomerStatementEvaluation.builder()
                .uuid(evaluationUuid)
                .status(EvaluationStatus.INITIALIZED)
                .createdAt(LocalDateTime.now())
                .build();

        customerStatementEvaluationRepository.save(customerStatementEvaluation);

        return evaluationUuid;
    }

    @Async
    public void process(final UUID evaluationUuid, final StatementInputType statementInputType,
                        final byte[] statementRecordsData) {

        CustomerStatementEvaluation customerStatementEvaluation = customerStatementEvaluationRepository
                .findByUuid(evaluationUuid).orElseThrow(() ->
                        new ItemNotFoundException("initializedEvaluationCannotBeFound"));

        if (customerStatementEvaluation.getStatus() != EvaluationStatus.INITIALIZED) {
            throw new RuntimeException("processRequestForStatementWithInvalidStatus");
        }

        try (StatementParser statementParser = statementParserFactory.createStatementParser(statementInputType)) {

            statementParser.setReader(new InputStreamReader(new ByteArrayInputStream(statementRecordsData)));

            List<StatementReferenceInfo> failedStatementReferenceInfos =
                    new StatementEvaluator(statementParser).evaluate();

            customerStatementEvaluation
                    .setResult(new ObjectMapper().writeValueAsString(failedStatementReferenceInfos));
            customerStatementEvaluation.setStatus(EvaluationStatus.COMPLETED);
        } catch (Exception e) {
            customerStatementEvaluation.setResult(e.getMessage());
            customerStatementEvaluation.setStatus(EvaluationStatus.FAILED);
        }

        customerStatementEvaluationRepository.save(customerStatementEvaluation);
    }

    public List<StatementReferenceInfo> getEvaluationResult(final UUID evaluationUuid) {

        CustomerStatementEvaluation customerStatementEvaluation = customerStatementEvaluationRepository
                .findByUuid(evaluationUuid)
                .orElseThrow(() -> new ItemNotFoundException("customerStatementProcessingDoesNotExist"));

        if (customerStatementEvaluation.getStatus() == EvaluationStatus.COMPLETED) {
            try {
                return new ObjectMapper().readValue(customerStatementEvaluation.getResult(),
                        new TypeReference<List<StatementReferenceInfo>>(){});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("resultListCannotBeBuilt: " + customerStatementEvaluation.getResult());
            }
        } else if (customerStatementEvaluation.getStatus() == EvaluationStatus.INITIALIZED) {
            throw new ProcessingNotReadyException();
        } else {
            throw new ProcessingFailedException("processingFailed-" + customerStatementEvaluation.getResult());
        }
    }

    public void deleteTimedOutResults() {
        customerStatementEvaluationRepository.deleteByCreatedAtBefore(LocalDateTime.now().
                minusMinutes(MAX_RESULT_AGE_IN_MINS));
    }
}
