package nl.rabobank.assignment.statement.util;

import lombok.RequiredArgsConstructor;
import nl.rabobank.assignment.statement.pojo.StatementReferenceInfo;
import nl.rabobank.assignment.exception.InvalidStatementDataException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The purpose of this class is to evaluate the customer statement records.
 * For evaluation of the records correctness of end balance value and reference id
 * uniqueness are checked.
 * @author Ayhan Yeni
 */
@RequiredArgsConstructor
public class StatementEvaluator {

    private final StatementParser statementParser;

    private final Set<Long> transactionReferenceSet = new HashSet<>();

    /**
     * Evaluates the records read by the statement parser. For evaluation of the records, correctness of end
     * balance value and reference id uniqueness are checked.
     * @return List of invalid records.
     */
    public List<StatementReferenceInfo> evaluate() throws InvalidStatementDataException {

        List<StatementReferenceInfo> invalidStatementReferenceInfos = new ArrayList<>();

        StatementRecord statementRecord = statementParser.readNext();
        while (statementRecord != null) {

            boolean isValid = checkEndBalance(statementRecord);
            if (isValid) {
                // Check if this reference id is seen before.
                isValid = !transactionReferenceSet.contains(statementRecord.getTransactionReference());
            }

            transactionReferenceSet.add(statementRecord.getTransactionReference());

            if (!isValid) {
                invalidStatementReferenceInfos.add(StatementReferenceInfo.from(statementRecord));
            }

            statementRecord = statementParser.readNext();
        }

        return invalidStatementReferenceInfos;
    }

    private boolean checkEndBalance(final StatementRecord statementRecord) {

        if (statementRecord.getStartBalance() == null || statementRecord.getMutation() == null || statementRecord.getEndBalance() == null) {
            return false;
        } else {
            BigDecimal expectedEndBalance = statementRecord.getStartBalance().add(statementRecord.getMutation());
            return statementRecord.getEndBalance().compareTo(expectedEndBalance) == 0;
        }
    }
}
