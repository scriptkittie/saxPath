## saxPath


### Overview

saxPath is an rule based SAX parser built for processing large XML files. saxPath performs the same functions as a typical SAX parser, while maintaining low memory usage for files of all sizes by only invoking callbacks for specific XML user determined XPATH expressions, therefore avoiding large heap growth for larger sized files. Once the parser reaches an XML path that matches an XPATH expression, it will load the XML only for that sub-tree that was matched. Text Content is only parsed in sub-trees, and not processed if a sub-tree is not being processed. You can choose to process each rule asynchronously, with the sacrifice of guaranteed order for each rule processed. Due to the nature this parsing and to maintain a low level of memory usage, only XPATH attributes and elements are able to be triggered, XPATH expressions such as the examples below will not work to trigger XPATH rules. However, all XPATH expressions will work in sub-tree's.

```xml
//text()[. = 'Text Content']
```
Or

```xml
//text()[contains(.,'Text Content that is not parsed')]
```

Expressions such the below will work:

```xml
//title[@lang]
```
```xml
/bookstore/book[price>35.00]
```

```xml
/bookstore/book[@test='title']/test123
```

```xml
/bookstore/book[@test='title'][@test2]/bookings
```

Once an XPATH expression has been matched, it is loaded into a special class called a SaxNode, which is a wrapper for the node with helper methods to query deeper into the subtree.

## Changelog

<details> 
  <summary>Open spoiler to view changelog </summary>
  
### 1.0.0
- Initial release.
</details>


## Installation
### Install from Maven Central

Just add the following dependencies to your maven pom.xml

```xml
<dependency>
    <groupId>io.laniakia</groupId>
    <artifactId>saxPath</artifactId>
    <version>1.0.0</version>
</dependency>
```
## Example Usage

All rules have to have an XPATH expression specified to be triggered when the SAX parser reaches that path in the XML.

**Create a Rule**

Below is the skeleton of a basic Rule 

```java
import io.laniakia.rule.Rule;
...
public class RuleImpl extends Rule
{
	public static boolean processed = false;
	
	public RuleImpl(String path) throws Exception 
	{
		super(path);
	}

	@Override
	public void processRule(SaxNode saxNode) throws Exception 
	{
		//Do things here
	}
}
```

Setup for Rule Usage

```java
String testXML = "<bookstore>ExcludeText<test>test</test><book>testingText<testing123></testing123></book></bookstore>";
XMLProcessor test = new XMLProcessor();
//Your XPATH expression to trigger this rule on goes here
test.addRule(new RuleImpl("/bookstore/book"));
test.process(new ByteArrayInputStream(testXML.getBytes(StandardCharsets.UTF_8)));
```

**Process Rules Asynchronously**

```java
import io.laniakia.rule.Rule;
...
test.process(new ByteArrayInputStream(testXML.getBytes(StandardCharsets.UTF_8)), true);
```

**Query SaxNode with XPATH Expression**

```java
import io.laniakia.rule.Rule;
...
@Override
public void processRule(SaxNode saxNode) throws Exception 
{
	SaxNode resultNode = saxNode.xPathQueryNode("/location/subTree");
}
```

**Query SaxNode LIST with XPATH Expression**

```java
import io.laniakia.rule.Rule;
...
@Override
public void processRule(SaxNode saxNode) throws Exception 
{
	List<SaxNode> resultNode = saxNode.xPathQueryNodeList("/location/subTree");
}
```

**Query Text of SaxNode**

This method of extracting text uses getTextContent() of the resulting Node

```java
import io.laniakia.rule.Rule;
...
@Override
public void processRule(SaxNode saxNode) throws Exception 
{
	String text = saxNode.getAllNodeText("/textpath");
}
```

**Query Text of SaxNode V2**

This method of extracting text uses the XPath method of extracting Node text, rather than getting the Node value of each node.

```java
import io.laniakia.rule.Rule;
...
@Override
public void processRule(SaxNode saxNode) throws Exception 
{
	String text = saxNode.getXPathNodeText("/textpath");
}
```


## Install from Source

Clone from remote repository then `mvn install`. All of the modules will be installed to your local maven repository.

~~~bash
git clone https://github.com/scriptkittie/saxPath.git
cd saxPath
mvn install
~~~

## Issues/Forks
Please report any issues to the issues section & as always if you have any functionality requests go ahead and open an issue containing your suggestions.

If you have an addition to the project, fork it and submit a pull request. Any type of contributions are welcome.

## Credits
Package written by [StCypher](https://twitter.com/yo_scriptkittie/with_replies)
