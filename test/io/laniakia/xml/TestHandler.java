package io.laniakia.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class TestHandler extends DefaultHandler2
{
	private static final Logger logger = LogManager.getLogger(TestHandler.class);
	
	public void process(InputStream xmlStream) throws Exception
	{
		 SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		 org.xml.sax.XMLReader reader = saxParser.getXMLReader();
		 reader.setContentHandler(this);
		 reader.parse(new InputSource(xmlStream));
	}
	
	@Override
    public void startDocument() throws SAXException 
	{
		logger.debug("Start Document");
    }
	
	@Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException 
	{
		logger.debug("Start Element | URI = " + uri  + ", localName = " + ", name =" + name + ", attributes = " + attributes);
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException 
	{

	}
	
	@Override
    public void characters(char[] ch, int start, int length) throws SAXException 
	{

	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		logger.debug("End Element | URI = " + uri  + ", localName = " + ", name =" + name);
	}

	@Override
	public void endDocument() throws SAXException 
	{
		logger.debug("End Document");
    }
}
