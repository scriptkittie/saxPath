package io.laniakia.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.laniakia.rule.Rule;

public class RuleImpl extends Rule
{
	private static final Logger logger = LogManager.getLogger(RuleImpl.class);
	
	public static boolean processed = false;
	
	public RuleImpl(String path) throws Exception 
	{
		super(path);
	}

	@Override
	public void processRule(SaxNode saxNode) throws Exception 
	{
		logger.debug("Node value: " + saxNode.toString());
		if(saxNode != null)
		{
			processed = true;
		}
	}
}
