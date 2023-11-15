package nl.rabobank.assignment.statement;

import lombok.RequiredArgsConstructor;
import nl.rabobank.assignment.statement.pojo.StatementReferenceInfo;
import nl.rabobank.assignment.statement.util.StatementInputType;
import nl.rabobank.assignment.exception.InvalidFileException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * A rest controller for Rabobank customer statement processing.
 *
 * @author Ayhan Yeni
 */

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/customer-statement")
public class CustomerStatementController {

    private final CustomerStatementService customerStatementService;

    /**
     * Starts processing of customer statements records file. This API starts the processing asynchronously.
     * When a client requests processing of customer statement records data, the API responds with
     * a success message having a UUID reference of the newly started process to use in further API calls to get
     * the result of the processing.
     *
     * @param customerStatementFile Customer statements records file. Can be either text/csv or one of xml mimetypes.
     * @return Uuid value of the newly started processing.
     */
    @PreAuthorize("hasAuthority('SCOPE_PROCESS')")
    @PostMapping("")
    public ResponseEntity<UUID> process(@RequestParam("file") final MultipartFile customerStatementFile) {

        try {

            byte[] data = customerStatementFile.getBytes();
            StatementInputType statementInputType = getStatementInputType(customerStatementFile);

            UUID evaluationUuid = customerStatementService.initializeCustomerStatementProcess();

            customerStatementService.process(evaluationUuid, statementInputType, data);

            return ResponseEntity.ok(evaluationUuid);
        } catch (IOException e) {
            throw new InvalidFileException("cannotGetFileContent");
        } catch (Exception e) {
            throw new InvalidFileException(e.getMessage());
        }
    }

    /**
     * The client queries the result of the started customer statement records data processing using this API.
     * The uuid value in the URI parameter is obtained from the response body of the API call to
     * "POST /api/customer-statement". If the result of the process is not ready yet, the API responds with Http
     * status code 202. If the process is finished and the result is successful, the API responds with status code 200,
     * if an error occurs because of invalid data, the API responds with the status code of 400 (Bad request).
     * @param uuid Uuid value of the queried processing.
     * @return List of invalid statement records.
     */
    @PreAuthorize("hasAuthority('SCOPE_PROCESS')")
    @GetMapping("/{uuid}")
    public ResponseEntity<List<StatementReferenceInfo>> getEvaluationResult(@PathVariable final UUID uuid) {

        List<StatementReferenceInfo> evaluationResults = customerStatementService.getEvaluationResult(uuid);

        return ResponseEntity.ok(evaluationResults);
    }

    /**
     * Deletes timed out customer statement records data processing results.
     */
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("")
    public ResponseEntity<Void> deleteTimedOut() {

        customerStatementService.deleteTimedOutResults();

        return ResponseEntity.ok(null);
    }

    private StatementInputType getStatementInputType(final MultipartFile customerStatementFile) throws Exception {

        String contentType = customerStatementFile.getContentType();

        if (contentType.toLowerCase().endsWith("xml")) { // To different xml mime types
            return StatementInputType.XML;
        } else if (contentType.toLowerCase().equals("text/csv")) {
            return StatementInputType.CSV;
        } else {
            throw new Exception("invalidCustomerStatementFileFormat");
        }
    }
}
