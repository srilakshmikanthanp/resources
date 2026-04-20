package com.srilakshmikanthanp.resources.parser.xml;

import com.srilakshmikanthanp.resources.parser.ResourceParserException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class XmlResourceParserErrorHandler extends DefaultHandler {
  @Override
  public void error(SAXParseException e) {
    throw new ResourceParserException(e.getLineNumber(), e.getColumnNumber(), String.format("Error parsing XML %s", e.getMessage()), e);
  }
}
