package io.laniakia.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SaxNode 
{
	private static final XPathFactory XPATH = XPathFactory.newInstance();
    private Node node;	
    
    public SaxNode(Node root) 
    {
    	node = root;
    }

    public SaxNode xPathQueryNode(String xPath) throws Exception
    {
    	Node nodeQueryResult = (Node) compileXPath(xPath, XPathConstants.NODE);
    	if (nodeQueryResult != null) 
    	{
    		return new SaxNode(nodeQueryResult);
    	}
    	return null;
    }
    
    public List<SaxNode> xPathQueryNodeList(String xPath) throws Exception
    {
    	 NodeList nodeList = (NodeList) compileXPath(xPath, XPathConstants.NODESET);
         List<SaxNode> saxNodeList = new ArrayList<SaxNode>();
         for (int i = 0; i < nodeList.getLength(); i++) 
         {
        	 saxNodeList.add(new SaxNode(nodeList.item(i)));
         }
         return saxNodeList;
    }
    
    public String getAllNodeText(String xPath) throws Exception 
    {
        Object textNode = compileXPath(xPath, XPathConstants.NODE);
        if (textNode != null) 
        {
        	if (textNode instanceof Node)
        	{
                String text = ((Node) textNode).getTextContent();
                if (StringUtils.isNotBlank(text)) 
                {
                    return text;
                }
            }
        	else if(textNode instanceof String && StringUtils.isNotBlank(textNode.toString()))
        	{
        		return textNode.toString().trim();
        	}
        }
		return null;
    }
    
    public String getXPathNodeText(String xPath) throws Exception
    {
    	if(StringUtils.isNotBlank(xPath))
    	{
    		xPath = xPath + "/text()";
    		return getAllNodeText(xPath);
    	}
    	return null;
    }
    
    private Object compileXPath(String xPath, QName xPathMode) throws Exception
    {
    	return XPATH.newXPath().compile(xPath).evaluate(node, xPathMode);
    }

	@Override
	public String toString() {
		return "SaxNode [node=" + printNode(node) + "]";
	}
	
	private String printNode(Node rootNode) {
	    String xml = " " + rootNode.getNodeName() + "[" + getLogAttributesToString(rootNode.getAttributes()) + "] -> " + rootNode.getNodeValue();
	    NodeList nl = rootNode.getChildNodes();
	    for (int i = 0; i < nl.getLength(); i++)
	    {
	    	 xml = xml + " - " + printNode(nl.item(i));
	    }
	    return xml;
	}
	
	private String getLogAttributesToString(NamedNodeMap attributes)
	{
		String attributeString = "";
		if(attributes != null)
		{
			for (int i = 0; i < attributes.getLength(); i++) 
			{
				attributeString = attributeString + " | Key=" + attributes.item(i) + ", Value=" + attributes.item(i).getNodeValue();
			}
		}
		return attributeString;
	}

	private Node getTrueNode()
	{
		return node;
	}
}
