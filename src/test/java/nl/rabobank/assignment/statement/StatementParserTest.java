package nl.rabobank.assignment.statement;

import nl.rabobank.assignment.statement.util.StatementInputType;
import nl.rabobank.assignment.statement.util.StatementParser;
import nl.rabobank.assignment.statement.util.StatementParserFactory;
import nl.rabobank.assignment.statement.util.StatementRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

public class StatementParserTest {

    private static final String CSV_INPUT_FILE = "test-data/records.csv";
    private static final String XML_INPUT_FILE = "test-data/records.xml";

    @Test
    public void read_AllStatementsFromCsvInput_Success() throws Exception {

        try {
            StatementParser statementParser = new StatementParserFactory().createStatementParser(StatementInputType.CSV);

            statementParser.setReader(new BufferedReader(new InputStreamReader(ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(CSV_INPUT_FILE))));

            int count = 0;

            StatementRecord statementRecord = statementParser.readNext();
            while (statementRecord != null) {
                count++;
                statementRecord = statementParser.readNext();
            }

            Assertions.assertEquals(10, count);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void read_AllStatementsFromXmlInput_Success() throws Exception {

        try {
            StatementParser statementParser =
                new StatementParserFactory().createStatementParser(StatementInputType.XML);

            statementParser.setReader(new BufferedReader(new InputStreamReader(ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(XML_INPUT_FILE))));

            int count = 0;

            StatementRecord statementRecord = statementParser.readNext();
            while (statementRecord != null) {
                statementRecord = statementParser.readNext();
                count++;
            }

            Assertions.assertEquals(10, count);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
