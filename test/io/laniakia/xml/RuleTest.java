package io.laniakia.xml;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/*
 * Due to the nature of how Rules are processed, assertions are not included in these unit tests.
 * They may be added in the future.
 */

public class RuleTest 
{
	private static final Logger logger = LogManager.getLogger(RuleTest.class);
	
	@Test
	public void testXML() throws Exception
	{
		String testXML = "<test>test<b>bold test</b><i>italics test</i>random text</test>";
		TestHandler test = new TestHandler();
		test.process(new ByteArrayInputStream(testXML.getBytes(StandardCharsets.UTF_8)));
	}
	
	@Test
	public void testNodeTracker() throws Exception
	{
		String testXML = "<test>test<b test=\"blah\">bold test</b><i>italics test</i>random text</test>";
		XMLProcessor test = new XMLProcessor();
		test.process(new ByteArrayInputStream(testXML.getBytes(StandardCharsets.UTF_8)));
		logger.debug(test.document);
	}
	
	@Test
	public void ruleTestText() throws Exception
	{
		String testXML = "<bookstore>ExcludeText<test>test</test><book>testingText<testing123></testing123></book></bookstore>";
		XMLProcessor test = new XMLProcessor();
		test.addRule(new RuleImpl("/bookstore/book"));
		test.process(new ByteArrayInputStream(testXML.getBytes(StandardCharsets.UTF_8)));
	}
	
	@Test
	public void ruleTestAttributes() throws Exception
	{
		String testXML = "<bookstore dummyAttribute='testdummy attribute blah'>ExcludeText<blah>more random text to test</blah><test>test</test><book title='blah title'>testingText<testing123></testing123></book></bookstore>";
		XMLProcessor test = new XMLProcessor();
		test.addRule(new RuleImpl("/bookstore/book"));
		test.process(new ByteArrayInputStream(testXML.getBytes(StandardCharsets.UTF_8)), true);
	}
	
	@Test
	public void ruleTestSelectAttributes() throws Exception
	{
		String testXML = "<bookstore dummyAttribute='testdummy attribute blah'>ExcludeText<blah>more random text to test</blah><test>test</test><book title='blah title'>testingText<testing123></testing123></book></bookstore>";
		XMLProcessor test = new XMLProcessor();
		test.addRule(new RuleImpl("/bookstore/book[@title='blah title']"));
		test.process(new ByteArrayInputStream(testXML.getBytes(StandardCharsets.UTF_8)), true);
	}
	
	@Test
	public void ruleTestReadFromFile() throws Exception
	{
		InputStream in = getClass().getResourceAsStream("/testExampleFile.xml"); 
		XMLProcessor test = new XMLProcessor();
		test.addRule(new RuleImpl("/bookstore/book"));
		test.process(in);
	}
	
	@Test
	public void ruleTestReadFromLargeFile() throws Exception
	{
		//Large File 1 GB size.
		InputStream in = new FileInputStream("/home/none/testing/standard.xml"); 
		XMLProcessor test = new XMLProcessor();
		test.addRule(new RuleImpl("/site/regions/africa/item/location"));
		test.process(in, true);
	}
}
