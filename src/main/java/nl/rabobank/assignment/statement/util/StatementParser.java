package nl.rabobank.assignment.statement.util;

import nl.rabobank.assignment.exception.InvalidStatementDataException;

import java.io.Reader;

public interface StatementParser extends AutoCloseable {

    /**
     * Reads next record in a statements record file.
     * @return Next StatementRecord in the file. If no record exists to read anymore, null is returned.
     */
    StatementRecord readNext() throws InvalidStatementDataException;

    /**
     * Sets the statements record data reader.
     * @param reader The statement record data reader.
     */
    void setReader(final Reader reader) throws Exception;
}
