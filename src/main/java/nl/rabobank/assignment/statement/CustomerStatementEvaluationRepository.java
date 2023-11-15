package nl.rabobank.assignment.statement;

import nl.rabobank.assignment.entity.CustomerStatementEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CustomerStatementEvaluationRepository extends JpaRepository<CustomerStatementEvaluation, Long> {

    Optional<CustomerStatementEvaluation> findByUuid(final UUID uuid);
    void deleteByUuid(final UUID uuid);

    void deleteByCreatedAtBefore(final LocalDateTime upto);
}
