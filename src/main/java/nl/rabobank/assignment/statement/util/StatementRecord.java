package nl.rabobank.assignment.statement.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatementRecord {

    private Long       transactionReference;
    private String     accountNumber;
    private BigDecimal startBalance;
    private BigDecimal mutation;
    private BigDecimal endBalance;
    private String     description;
}
