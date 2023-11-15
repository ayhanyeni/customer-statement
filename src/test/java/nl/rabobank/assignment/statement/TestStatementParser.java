package nl.rabobank.assignment.statement;

import jakarta.annotation.Nullable;
import nl.rabobank.assignment.statement.util.StatementParser;
import nl.rabobank.assignment.statement.util.StatementRecord;

import java.io.Reader;
import java.util.List;

public class TestStatementParser implements StatementParser {

    private List<StatementRecord> testRecords;
    private int index = 0;

    public void setReader(@Nullable final Reader reader) {
    }

    @Override
    public StatementRecord readNext() {
        if (testRecords != null && index < testRecords.size())
            return testRecords.get(index++);
        else return null;
    }

    @Override
    public void close() throws Exception { }

    public void setTestRecords(List<StatementRecord> testRecords) {
        this.testRecords = testRecords;
    }
}
