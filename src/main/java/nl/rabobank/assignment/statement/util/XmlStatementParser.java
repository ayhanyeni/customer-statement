package nl.rabobank.assignment.statement.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.math.BigDecimal;

@Slf4j
public class XmlStatementParser implements StatementParser {

    private XMLEventReader xmlEventReader;

    @Override
    public StatementRecord readNext() {

        StatementRecord statementRecord = null;

        try {
            while (xmlEventReader.hasNext()) {
                XMLEvent nextEvent = xmlEventReader.nextEvent();

                if (nextEvent.isStartElement()) {

                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()) {
                        case "record":
                            if (statementRecord == null) {
                                statementRecord = new StatementRecord();
                            } else {
                                log.error("'record' element is found at unexpected location.");
                                return null;
                            }

                            Attribute referenceAttribute = startElement.getAttributeByName(new QName("reference"));
                            if (referenceAttribute != null) {
                                statementRecord.setTransactionReference(Long.parseLong(referenceAttribute.getValue().trim()));
                            }

                            break;
                        case "accountNumber":
                            nextEvent = xmlEventReader.nextEvent();
                            statementRecord.setAccountNumber(nextEvent.asCharacters().getData());
                            break;
                        case "description":
                            nextEvent = xmlEventReader.nextEvent();
                            statementRecord.setDescription(nextEvent.asCharacters().getData());
                            break;
                        case "startBalance":
                            nextEvent = xmlEventReader.nextEvent();
                            statementRecord.setStartBalance(new BigDecimal(nextEvent.asCharacters().getData().trim()));
                            break;
                        case "mutation":
                            nextEvent = xmlEventReader.nextEvent();
                            statementRecord.setMutation(new BigDecimal(nextEvent.asCharacters().getData().trim()));
                            break;
                        case "endBalance":
                            nextEvent = xmlEventReader.nextEvent();
                            statementRecord.setEndBalance(new BigDecimal(nextEvent.asCharacters().getData().trim()));
                            break;
                    }
                } else if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("record")) {
                        return statementRecord;
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.error("Error: Cannot read the XML file.");
            return null;
        }

        return statementRecord;
    }

    /**
     * Sets the statements record data reader.
     *
     * @param reader The statement record data reader.
     */
    @Override
    public void setReader(final Reader reader) throws Exception {

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            xmlEventReader = xmlInputFactory.createXMLEventReader(reader);
        } catch (XMLStreamException e) {
            log.error("Cannot set reader for XML parser.", e);
            throw new Exception("errorSettingReaderForXMLParser");
        }
    }

    @Override
    public void close() throws Exception {
        try {
            xmlEventReader.close();
        } catch (XMLStreamException e) {
            log.error("Error closing the XML reader: ", e.getMessage());
        }
    }
}
