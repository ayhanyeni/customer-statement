package nl.rabobank.assignment.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "CUSTOMER_STATEMENT_EVALUATION",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "UUID", name = "UX_CUSTOMER_STATEMENT_PROCESS_RESULT__UUID")
    }
)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CustomerStatementEvaluation {

    private static final int RESULT_MAX_LENGTH = 100000000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "UUID", columnDefinition = "uuid", nullable = false)
    private UUID uuid;

    @Column(name = "RESULT", length = RESULT_MAX_LENGTH)
    private String result;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private EvaluationStatus status;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}
