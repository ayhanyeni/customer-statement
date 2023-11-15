package nl.rabobank.assignment.statement.util;

import org.springframework.stereotype.Component;

@Component
public class StatementParserFactory {

    public StatementParser createStatementParser(final StatementInputType inputType) throws Exception {

        if (inputType == StatementInputType.CSV) return new CsvStatementParser();
        else if (inputType == StatementInputType.XML) return new XmlStatementParser();
        else throw new Exception ("unknownStatementFormatType");
    }
}
