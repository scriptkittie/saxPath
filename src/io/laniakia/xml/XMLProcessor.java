package io.laniakia.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import io.laniakia.rule.Rule;

public class XMLProcessor extends DefaultHandler2
{
	public Document document;
	private Node lastProcessedNode;
	private List<Rule> ruleList = new ArrayList<Rule>();
	private List<RuleHandler> activeRules = new ArrayList<RuleHandler>();
	private boolean asynchronousRuleHandling = false;
	
	private static final Logger logger = LogManager.getLogger(XMLProcessor.class);
	
	
	public void process(File file, boolean asynchronous) throws Exception
	{
		if(file.isFile())
		{
			process(new FileInputStream(file), asynchronous);
		}
	}
	
	public void process(File file) throws Exception
	{
		if(file.isFile())
		{
			process(new FileInputStream(file));
		}
	}
	
	public void process(InputStream xmlStream) throws Exception
	{
		process(xmlStream, false);
	}

	public void process(InputStream xmlStream, boolean asynchronous) throws Exception
	{
		if(asynchronous)
		{
			this.asynchronousRuleHandling = asynchronous;
			logger.debug("Warning, asynchronous is on. This means Rules are processed faster, but in an unordered manner");
		}
		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		org.xml.sax.XMLReader reader = saxParser.getXMLReader();
		reader.setContentHandler(this);
		reader.parse(new InputSource(xmlStream));
	}

	public void addRule(Rule rule)
	{
		logger.debug("Added processing rule with trigger: " + rule.getXpathTrigger());
		ruleList.add(rule);
		logger.debug("Total rules in RuleList: " + ruleList.size());
	}
	
	@Override
    public void startDocument() throws SAXException 
	{
		try 
		{
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = documentBuilder.newDocument();
		} 
		catch (Exception e) 
		{
			logger.debug("Error initializing new document creation in startDocument(): " + e.getMessage());
		}
	}
	
	@Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException 
	{
		Element root = document.createElementNS(uri, name);
		for (int i = 0; i < attributes.getLength(); i++) 
		{
			root.setAttribute(attributes.getLocalName(i), attributes.getValue(i));
		}
		if(lastProcessedNode != null)
		{
			lastProcessedNode.appendChild(root);
			lastProcessedNode = root;
		}
		else
		{
			lastProcessedNode = root;
			document.appendChild(lastProcessedNode);
		}
		
		for(RuleHandler ruleHandler : activeRules)
		{
			ruleHandler.startElement(uri, localName, name, attributes);
		}
		
		for(Rule rule: ruleList)
		{
			if(!checkHandler(rule) && rule.IsXPathEqual((document.getFirstChild())))
			{
				logger.debug("Creating new RuleHandler for Rule: " + rule.getClass() + ", with xPath trigger: " + rule.getXpathTrigger());
				RuleHandler ruleHandler = new RuleHandler(rule, document);
				activeRules.add(ruleHandler);
				
			}
		}
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException 
	{
		for(RuleHandler ruleHandler : activeRules)
		{
			ruleHandler.processingInstruction(target, data);
		}
	}
	
	@Override
    public void characters(char[] ch, int start, int length) throws SAXException 
	{
		for(RuleHandler ruleHandler : activeRules)
		{
			ruleHandler.characters(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		if(lastProcessedNode.getParentNode() != null)
		{
			lastProcessedNode = lastProcessedNode.getParentNode();
			lastProcessedNode.removeChild(lastProcessedNode.getFirstChild());
		}
		else
		{
			lastProcessedNode  = null;
		}
		
		for (final Iterator<RuleHandler> iter = activeRules.iterator(); iter.hasNext();) 
		{
			RuleHandler ruleHandler = iter.next();
			if(ruleHandler.endElement(uri, localName, name, this.asynchronousRuleHandling))
			{
				logger.debug("Remove Rule with xPath trigger: " + ruleHandler.getXPath());
				iter.remove();
			}
		}
	}

	@Override
	public void endDocument() throws SAXException 
	{
		if(!activeRules.isEmpty())
		{
			throw new SAXException("Unfinished Rules were not processed: " + activeRules.toString());
		}
		logger.debug("End Document, active rules left: " + activeRules.size());
    }
	
	private boolean checkHandler(Rule rule)
	{
		for(RuleHandler ruleHandler: activeRules)
		{
			if(ruleHandler.getXPath().equalsIgnoreCase(rule.getXpathTrigger()))
			{
				return true;
			}
		}
		return false;
	}
	
}
