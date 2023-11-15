package nl.rabobank.assignment.statement.util;

import lombok.extern.slf4j.Slf4j;
import nl.rabobank.assignment.exception.InvalidStatementDataException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

@Slf4j
public class CsvStatementParser implements StatementParser {


    private BufferedReader statementBufferedReader;

    /**
     * Sets the statements record data reader.
     *
     * @param reader The statement record data reader.
     */
    @Override
    public void setReader(final Reader reader) throws Exception {
        if (reader instanceof BufferedReader) {
            statementBufferedReader = (BufferedReader) reader;
        } else {
            statementBufferedReader = new BufferedReader(reader);
        }

        statementBufferedReader.readLine(); //First line is the header line, we just omit it.
    }


    @Override
    public StatementRecord readNext() throws InvalidStatementDataException {

        StatementRecord statementRecord = null;

        try {
            if (statementBufferedReader.ready()) {

                statementRecord = new StatementRecord();

                String line = statementBufferedReader.readLine();

                StringTokenizer tokens = new StringTokenizer(line, ",");

                try {
                    statementRecord.setTransactionReference(Long.parseLong(tokens.nextToken().trim()));
                    statementRecord.setAccountNumber(tokens.nextToken().trim());
                    statementRecord.setDescription(tokens.nextToken().trim());
                    statementRecord.setStartBalance(new BigDecimal(tokens.nextToken().trim()));
                    statementRecord.setMutation(new BigDecimal(tokens.nextToken().trim()));
                    statementRecord.setEndBalance(new BigDecimal(tokens.nextToken().trim()));
                } catch (NoSuchElementException | IllegalArgumentException e) {
                    log.error("Line '{}' is in invalid format: {}", line, e.getMessage());

                    throw new InvalidStatementDataException("csvLineRecordInInvalidFormat: " + line);
                }
            }
        } catch (IOException e) {
            log.error("Error: Cannot read statement data.");
            throw new InvalidStatementDataException("csvStatementRecordsDataCannotBeRead");
        }

        return statementRecord;
    }

    @Override
    public void close() throws Exception {
        try {
            statementBufferedReader.close();
        } catch (IOException e) {
            log.error("Error closing the reader: ", e);
        }
    }
}
