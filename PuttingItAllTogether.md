# Introduction & Preparation #

This example is based on a template found in [OpenDesigns.org](http://www.opendesigns.org/), it shows how to use a model, template and includes to generate a static web page. More examples are under construction, this should at least show the general concepts.

  1. Make sure you have JDK 5 or greater installed and Ant or Maven if you plan to use for building. For CommandLine java.exe needs to be in the path.
  1. Download the [Examples File](http://jseance.googlecode.com/files/examples.zip) and unzip, this should create the following directory structure:
```
examples
|-- jseance-2.0-beta-2-SNAPSHOT-jar-with-dependencies.jar
'-- ant
    command-line
    common
     |-- includes
     |-- models
     |-- target
     '-- templates
    maven2
```

# Example Details #

## The Model ##
The XML model to use as input data to the template can be found in home/examples/common/models
```
<?xml version="1.0" encoding="UTF-8"?>
<WebPage>
 <FileName>home.html</FileName>
 <Header>
  <Name>My Website Name</Name>
  <SubName>Website sub-name</SubName>
  <Menu>
   <Item name="Home" href="#"/>
   <Item name="Blogs" href="#"/>
   <Item name="Photos" href="#"/>
   <Item name="About" href="#"/>
   <Item name="Contact" href="#"/>
  </Menu>
 </Header>
 <Sidebar>
  <Section name="Categories">
   <Category name="Category A" href="#"/>
   <Category name="Category B" href="#"/>
   <Category name="Category C" href="#"/>
   <Category name="Category D" href="#"/>
  </Section>
  <Section name="Archives">
   <Category name="September" href="#"/>
   <Category name="August" href="#"/>
   <Category name="July" href="#"/>
   <Category name="June" href="#"/>
   <Category name="May" href="#"/>
  </Section>
 </Sidebar>
 <Posts>
  <Post title="First Post" date="June 27, 2009"><![CDATA[<p>Lorem ...</p>]]></Post>
 </Posts>
</WebPage>
```

## The Template ##
The XML template splits the page generation into subsections, each with an include for readability. The main template can be found in home/examples/common/templates:
```
@!XMLModel("WebPage.xml")!
@!Output(Models['default'].rootNode.FileName)!
  @!Include("Header.jseance")!
  @!Include("Content.jseance")!
  @!Include("Sidebar.jseance")!
  @!Include("Footer.jseance")!
@!End!
```

The template performs 3 main steps:

1. Loads the default XML model into the context, see [XMLModel](Functions#XMLModel.md) for details:
```
@!XMLModel("WebPage.xml")!
```

2. Declares an output file, whose children produced text will be written to, see [Output](Functions#Output.md) for details:
```
@!Output(Models['default'].rootNode.FileName)!
```

> Note that the file name is determined at run time from a model attribute:
```
<WebPage>                       <-- rootNode
 <FileName>home.html</FileName> <-- FileName.toString()
...
```

3. Includes the top, body, side and bottom sections, see [Include](NodeTypes#Include.md) for details:
```
  @!Include("Header.jseance")!
  @!Include("Content.jseance")!
  @!Include("Sidebar.jseance")!
  @!Include("Footer.jseance")!
```

## The Includes ##
The includes can be found in the home/common/includes directory. The full template text can be found in the corresponding XML files, the following are the key sections from each one:

### Header.xml ###

This include is responsible for generating the top section of the page, it needs to draw the menus:
```
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
...
@!For("Header.Menu.Item")!
     <li><a href="!Eval(Models['default'].currentNode.@href, "html")!">!Eval(Models['default'].currentNode.@name, "html")!</a></li>
@!End!
...
```

To iterate through the menu nodes of the model, it uses the following expression (see [For-IfEmpty](Functions#For-IfEmpty.md) for reference):
```
@!For("Header.Menu.Item")!
```

This will change the `Models['default'].currentNode` context variable for children accordignly, thus the following section can be used to generate the menu text and link:
```
     <li><a href="!Eval(Models['default'].currentNode.@href, "html")!">!Eval(Models['default'].currentNode.@name, "html")!</a></li>
```

Which use /WebPage/Menu/Item href and name XML attributes accordingly. Notice the use of the `html` escaping functionality, see [Eval](Functions#Eval.md) for a list of available escaping options.

### Content.xml ###

Notice the use of For and Eval nodes:
```
<!-- start page -->
 <div id="page">
	<!-- start content -->
	<div id="content">
@!For("Posts.Post")!
		<div class="post">
			<h1 class="title">!Eval(Models['default'].currentNode.@title, "html")!</h1>
			<p class="meta"><small>!Eval(Models['default'].currentNode.@date, "html")!</small></p>
		  <div class="entry">!Eval(Models['default'].currentNode, "html")!</div>
		</div>
@!End!
	</div>
<!-- end content -->

```

### Sidebar.xml ###

Besides using iterators, the sidebar needs to render the following HTML:
```
				<ul class="back_title">
					<li class="top"><a href="#">Category A</a></li>
					<li><a href="#">Category B</a></li>
					<li><a href="#">Category C</a></li>
					<li><a href="#">Category D</a></li>
				</ul>
```

Where the first `<li>` element requires a special attribute. To accomplish this we can embed JavaScript code in combination with an [If-EiseIf-Else](Functions#If-EiseIf-Else.md) expression:

```
...
  @!Code!
  var first = true;
  @!End!
  @!For("Category")!
    @!If(first)!
    @!Code!
    first = false;
    @!End!    
	   <li class="top">
    @!Else!
		 <li>
    @!End!
      <a href="!Eval(Models['default'].currentNode.@href)!">!Eval(Models['default'].currentNode.@name, "html")!</a>
     </li>
  @!End!
...
```

[Prev: JavaScript Support](JavaScriptSupport.md)