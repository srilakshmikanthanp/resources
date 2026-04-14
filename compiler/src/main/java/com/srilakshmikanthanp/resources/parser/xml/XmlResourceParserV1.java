package com.srilakshmikanthanp.resources.parser.xml;

import com.srilakshmikanthanp.resources.parser.ResourceParser;
import com.srilakshmikanthanp.resources.parser.ResourceParserException;
import com.srilakshmikanthanp.resources.tree.ResourceBundleNode;
import com.srilakshmikanthanp.resources.tree.resource.ResourceNode;
import com.srilakshmikanthanp.resources.tree.resource.body.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;

public class XmlResourceParserV1 implements ResourceParser {
  private static final String SCHEMA_RESOURCE_PATH = "resourse.xsd";
  private static final String PACKAGE_ATTR = "package";
  private static final String IMPLEMENTING_ATTR = "implementing";
  private static final String NAME_ATTR = "name";
  private static final String INLINE_TAG = "inline";
  private static final String FILE_TAG = "file";
  private static final String PATH_ATTR = "path";
  private static final String TYPE_ATTR = "type";

  private ResourceBundleNode parseResourceBundleNode(Element element) {
    String packageName = element.getAttribute(PACKAGE_ATTR);
    String name = element.getAttribute(NAME_ATTR);
    String implementing = element.hasAttribute(IMPLEMENTING_ATTR) ? element.getAttribute(IMPLEMENTING_ATTR) : null;
    ResourceBundleNode bundle = new ResourceBundleNode(packageName, name, implementing);
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      if (element.getChildNodes().item(i).getNodeType() == Element.ELEMENT_NODE) {
        bundle.addResource(parseResourceNode((Element) element.getChildNodes().item(i)));
      }
    }
    return bundle;
  }

  private ResourceNode parseResourceNode(Element element) {
    Node firstNode = (Element) element.getElementsByTagName("*").item(0);
    String resourceName = element.getAttribute(NAME_ATTR);
    return new ResourceNode(resourceName, parseResourceBody(firstNode));
  }

  private ResourceBodyNode parseResourceBody(Node node) {
    if (node.getNodeType() != Node.ELEMENT_NODE) {
      return new InlineResourceBodyNode(node.getTextContent(), ResourceFieldType.STRING);
    }

    var element = (Element) node;

    if (element.getTagName().equals(FILE_TAG)) {
      ResourceFieldType type = element.hasAttribute(TYPE_ATTR) ? ResourceFieldType.valueOf(element.getAttribute(TYPE_ATTR)) : ResourceFieldType.STREAM;
      String filePath = element.getAttribute(PATH_ATTR);
      return new FileResourceBodyNode(filePath, type);
    } else if (element.getTagName().equals(INLINE_TAG)) {
      ResourceFieldType type = element.hasAttribute(TYPE_ATTR) ? ResourceFieldType.valueOf(element.getAttribute(TYPE_ATTR)) : ResourceFieldType.STRING;
      String content = element.getTextContent();
      return new InlineResourceBodyNode(content, type);
    } else {
      throw new ResourceParserException("Unsupported resource type: " + element.getTagName());
    }
  }

  private Schema getSchema() throws SAXException, IOException {
    try (InputStream schemaStream = XmlResourceParserV1.class.getResourceAsStream(SCHEMA_RESOURCE_PATH)) {
      if (schemaStream == null) throw new IllegalStateException("XSD schema not found at: " + SCHEMA_RESOURCE_PATH);
      return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(schemaStream));
    }
  }

  @Override
  public ResourceBundleNode parse(InputStream stream) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setSchema(getSchema());
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(stream);
      document.getDocumentElement().normalize();
      return parseResourceBundleNode(document.getDocumentElement());
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new ResourceParserException("Failed to parse XML resource", e);
    }
  }
}
