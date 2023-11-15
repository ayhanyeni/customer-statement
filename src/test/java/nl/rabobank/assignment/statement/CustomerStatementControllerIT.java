package nl.rabobank.assignment.statement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import nl.rabobank.assignment.configuration.WebSecurityConfiguration;
import nl.rabobank.assignment.entity.CustomerStatementEvaluation;
import nl.rabobank.assignment.entity.EvaluationStatus;
import nl.rabobank.assignment.statement.pojo.StatementReferenceInfo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(WebSecurityConfiguration.class)
public class CustomerStatementControllerIT {


    private static final String[] TEST_EVALUATION_UUIDS = {"46badd6f-647d-4c0d-9fdc-e9acdb2b0303",
            "46badd6f-647d-4c0d-9fdc-e9acdb2b0304"};

    private MockMvc mockMvc;

    @Autowired
    TestCustomerStatementEvaluationRepository statementEvaluationRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String getTokenForUser(final String userName, final String password) throws Exception {

        MvcResult result = this.mockMvc.perform(post("/api/token")
                        .with(httpBasic(userName, password)))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    @Test
    @Sql({"/sql/init_test_data.sql"})
    public void process_WhenValidCsvFileAndValidUser_Success() throws Exception {

        String token = getTokenForUser("user1", "password1");

        final MockMultipartFile customerStatementsFileCsv = new MockMultipartFile("file",
                "records.csv", "text/csv",
                ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream("test-data/records.csv"));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/customer-statement")
                        .file(customerStatementsFileCsv)
                        .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String uuidString = JsonPath.read(result.getResponse().getContentAsString(), "$");

        CustomerStatementEvaluation statementEvaluation = statementEvaluationRepository.findByUuid(UUID.fromString(uuidString))
                .orElseThrow(() -> new RuntimeException("CustomerStatementEvaluation information cannot be found"));

        Assertions.assertTrue(statementEvaluation.getStatus() == EvaluationStatus.INITIALIZED ||
                statementEvaluation.getStatus() == EvaluationStatus.COMPLETED);

        if (statementEvaluation.getStatus() == EvaluationStatus.INITIALIZED) {
            Thread.sleep(2000);    //Wait for processing to complete.
        }

        statementEvaluation = statementEvaluationRepository.findByUuid(UUID.fromString(uuidString))
                .orElseThrow(() -> new RuntimeException("CustomerStatementEvaluation information cannot be found"));

        Assertions.assertTrue(statementEvaluation.getStatus() == EvaluationStatus.COMPLETED);

        List<StatementReferenceInfo> invalidStataments = new ObjectMapper()
                .readValue(statementEvaluation.getResult(), new TypeReference<List<StatementReferenceInfo>>(){});
        Assertions.assertEquals(2, invalidStataments.size());
    }


    @Test
    @Sql({"/sql/init_test_data.sql"})
    public void process_WhenValidXmlFileAndValidUser_Success() throws Exception {

        String token = getTokenForUser("user1", "password1");

        final MockMultipartFile customerStatementsFileCsv = new MockMultipartFile("file",
                "records.xml", "text/xml",
                ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream("test-data/records.xml"));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/customer-statement")
                        .file(customerStatementsFileCsv)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String uuidString = JsonPath.read(result.getResponse().getContentAsString(), "$");

        CustomerStatementEvaluation statementEvaluation = statementEvaluationRepository.findByUuid(UUID.fromString(uuidString))
                .orElseThrow(() -> new RuntimeException("CustomerStatementEvaluation information cannot be found"));

        Assertions.assertTrue(statementEvaluation.getStatus() == EvaluationStatus.INITIALIZED ||
                statementEvaluation.getStatus() == EvaluationStatus.COMPLETED);

        if (statementEvaluation.getStatus() == EvaluationStatus.INITIALIZED) {
            Thread.sleep(2000);    //Wait for processing to complete.
        }

        statementEvaluation = statementEvaluationRepository.findByUuid(UUID.fromString(uuidString))
                .orElseThrow(() -> new RuntimeException("CustomerStatementEvaluation information cannot be found"));

        Assertions.assertTrue(statementEvaluation.getStatus() == EvaluationStatus.COMPLETED);

        List<StatementReferenceInfo> invalidStataments = new ObjectMapper()
                .readValue(statementEvaluation.getResult(), new TypeReference<List<StatementReferenceInfo>>(){});
        Assertions.assertEquals(2, invalidStataments.size());
    }

    @Test
    @Sql({"/sql/init_test_data.sql"})
    public void getResult_WhenValidUuidAndValidUserAndProcessCompleted_Success() throws Exception {

        String token = getTokenForUser("user1", "password1");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/customer-statement/{uuid}", TEST_EVALUATION_UUIDS[1])
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].transactionReference", Matchers.is(1000)));
    }


    @Test
    @Sql({"/sql/init_test_data.sql"})
    public void getWaitResponse_WhenValidUuidAndValidUserAndProcessInitialized_Success() throws Exception {

        String token = getTokenForUser("user1", "password1");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/customer-statement/{uuid}", TEST_EVALUATION_UUIDS[0])
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(HttpStatus.ACCEPTED.value()));
    }

    @Test
    @Sql({"/sql/init_test_data.sql"})
    public void process_WhenNoAuthenticationToken_Fail() throws Exception {

        final MockMultipartFile customerStatementsFileCsv = new MockMultipartFile("file",
                "records.csv", "text/csv",
                ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream("test-data/records.csv"));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/customer-statement")
                        .file(customerStatementsFileCsv)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
