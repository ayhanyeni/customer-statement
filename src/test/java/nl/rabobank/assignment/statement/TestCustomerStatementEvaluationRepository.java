package nl.rabobank.assignment.statement;

import nl.rabobank.assignment.entity.CustomerStatementEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TestCustomerStatementEvaluationRepository extends JpaRepository<CustomerStatementEvaluation, Long> {

    Optional<CustomerStatementEvaluation> findByUuid(final UUID uuid);
}
