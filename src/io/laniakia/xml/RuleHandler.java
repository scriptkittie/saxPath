package io.laniakia.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import io.laniakia.rule.Rule;

public class RuleHandler
{
	private Document document;
	private Node localRoot;
	private Node currentNode;
	private Rule rule;
	private String endTag;
	private int levelsDeep = 0;
	
	private static final Logger logger = LogManager.getLogger(RuleHandler.class);
	
	public RuleHandler(Rule rule, Document document) 
	{
        this.rule = rule;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
		try
		{
			builder = dbf.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) 
		{
			logger.debug("Error creating document: " + e.getMessage());
			return;
		}
        this.document = builder.newDocument();
        this.localRoot = this.document.importNode(document.getDocumentElement(), true);
        this.document.appendChild(this.localRoot);
        this.currentNode = getLastElement(this.localRoot);
        this.endTag = this.currentNode.getNodeName();
        this.levelsDeep = 1;
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException 
	{
        this.levelsDeep++;
    	Element localXMLElement = document.createElement(name);
        for (int i = 0; i < attributes.getLength(); i++) 
        {
            String attributeKey = attributes.getLocalName(i);
            if (StringUtils.isBlank(attributeKey)) 
            {
                attributeKey = attributes.getQName(i);
            }
            if (StringUtils.isNotBlank(attributeKey)) 
            {
                localXMLElement.setAttribute(attributeKey, attributes.getValue(i));
            }
        }
        if (currentNode != null) 
        {
            currentNode.appendChild(localXMLElement);;
        } 
        else 
        {
            localRoot = localXMLElement;
        }
        currentNode = localXMLElement;
	}
	
	public void processingInstruction(String target, String data) throws SAXException 
	{
		  ProcessingInstruction instruction = document.createProcessingInstruction(target, data);
	      currentNode.appendChild(instruction);
	}
	
    public void characters(char[] ch, int start, int length) throws SAXException 
	{
    	currentNode.appendChild(document.createTextNode(new String(ch).substring(start, start + length)));
	}
	
	public boolean endElement(String uri, String localName, String name, boolean asynchronous) throws SAXException
	{
		this.levelsDeep--;
		if (name.equalsIgnoreCase(this.endTag) && this.levelsDeep == 0) 
		{
			try 
			{
				logger.debug("Processing Rule: " + rule.getClass() + ", on element: " + name + ", asynchronous=" + asynchronous);
				if(asynchronous)
				{
					new Thread(() -> {
						try 
						{
							rule.processRule(new SaxNode(document.getFirstChild()));
						} 
						catch (Exception e) 
						{
							logger.debug("Error triggering rule processing with asynchronous: " + e.getMessage());
						}
					}).start();
				}
				else
				{
					rule.processRule(new SaxNode(document.getFirstChild()));
				}
			}
			catch (Exception e) 
			{
				logger.debug("Error triggering rule processing: " + e.getMessage());
			}
			return true;
		}
		return false;
	}
	
	private Node getLastElement(Node node)
    {
        node = node.getLastChild();
        while (node != null && node.getLastChild() != null)
        {
            node = node.getLastChild();
        }
        return node;
    }

	
	public String getXPath()
	{
		return rule.getXpathTrigger();
	}
}
