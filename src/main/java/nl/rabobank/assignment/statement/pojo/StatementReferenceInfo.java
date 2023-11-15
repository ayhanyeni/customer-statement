package nl.rabobank.assignment.statement.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.assignment.statement.util.StatementRecord;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementReferenceInfo {

    private Long   transactionReference;
    private String description;

    public static StatementReferenceInfo from(final StatementRecord statementRecord) {

        return StatementReferenceInfo.builder()
                .transactionReference(statementRecord.getTransactionReference())
                .description(statementRecord.getDescription())
                .build();
    }
}
