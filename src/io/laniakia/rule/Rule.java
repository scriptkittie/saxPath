package io.laniakia.rule;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import io.laniakia.xml.SaxNode;

public abstract class Rule 
{
	private static final XPathFactory XPATH = XPathFactory.newInstance();
	private String xpathTrigger;
	
	public Rule(String path) throws Exception
	{
		this.xpathTrigger = path;
	}
	
	public abstract void processRule(SaxNode saxNode) throws Exception;
	
	public boolean IsXPathEqual(Node comparisonNode) 
	{
		try 
		{
			return XPATH.newXPath().compile(this.xpathTrigger).evaluate(comparisonNode, XPathConstants.NODE) != null;
		} 
		catch (XPathExpressionException e) 
		{
			e.printStackTrace();
			return false;
		}
	}

	public String getXpathTrigger() {
		return xpathTrigger;
	}

	public void setXpathTrigger(String xpathTrigger) {
		this.xpathTrigger = xpathTrigger;
	}
}
