It's possible to embed JavaScript code in any attribute or text node of a JSeance template. For example, the following template will write the current date to a file:

```
@!Output('test.txt')!
    @!Eval(Date())!
@!End!
```

Function attributes can also include JavaScript code, the following example declares a var in JavaScript and then uses it in the fileName parameter to specify the file to write. This can be extended to generate fileNames based on XML model data and to generate a variable number of files from a single template.

```
<?xml version="1.0" encoding="UTF-8"?>
@!Code!
var fileNameVar = 'file.txt';
@!End!
@!Output(fileNameVar)!
TestOutput
@!End!

```

> # JSeance Object Model #

During a template execution, the JSeance engine defines arbitrary JavaScript objects to make certain context information available to scripting.

> ## XML Models ##

The following example illustrates the use of the 'Models' JavaScript Object:

XML Model file (model.xml):
```
<?xml version="1.0" encoding="UTF-8"?>
<RootNode>
 <A attribute1="first ">
  <B attribute2="second ">
    <C attribute3="third"/>
  </B>
</A>
</RootNode>
```

The JSeance  template:
```
@!XMLModel('model.xml', null, 'A.B')!
@!Output('test.txt')!
    @!Eval(Models['default'].rootNode.A.@attribute1)!
    @!Eval(Models['default'].currentNode.@attribute2)!
    @!Eval(Model.currentNode.C.@attribute2)!
@!End!
```

Output file contents (test.txt):
```
first second third
```

The 'Models' object contains all the models available in the context. Each model has 'currentNode' and 'rootNode' attributes. The 'currentNode' attribute can change depending on the model e4XPath attribute or when an [For-IfEmpty](Functions#For-IfEmpty.md) node is being used. Note that the 'Model' object is short-hand for Models['default'].

> ## Built-In Encoding Functions ##

The following JavaScript functions are available by default:
| EscapeHTML | Escapes HTML text |
|:-----------|:------------------|
| EscapeXMLValue | Escapes the content of an XML node |
| EscapeXMLAttribute | Escapes the text of an xml attribute |
| EscapeJava | Escapes a Java String |
| EscapeJavaScript | Escapes a JavaScript string |
| EscapeSQL | Escapes a SQL string |

> # E4X Reference #

Example XML:
```
var myXML = <RootNode>
 <Child attribute="child attribute">
  <A attribute="first">Text Value</A>
  <B attribute="second" />
  <C attribute="third"> Concatenated</C>
 </Child>
</RootNode>;
```

| **Expression** | **Value** | **Description** |
|:---------------|:----------|:----------------|
| `myXML.Child[0].@attribute` | `first` | Child nodes can be referenced by index |
| `myXML.Child.B.@attribute` | `second` | Child nodes can be referenced by tagName|
| `myXML['Child']['B'].attribute['attribute']` | `second` | Explicit navigation|
| `myXML.Child[0].toString()` | `Text Value` | Node to string conversion |
| `myXML.Child[0].toXMLString()` | `<A attribute="first">Text Value</A>` | Node to XML string conversion |
| `myXML.Child.*.(@attribute == "third").toXMLString()` | `<C attribute="third" />` | filter by attribute value |
| `myXML.Child.B.localName()` | `B` | Node local node name |
| `myXML.Child.B.name()` | `B` | Node full name|
| `myXML.Child.*.length()` | `3` | Node size |
| `myXML.Child.B.parent().name()` | `Child` | Accessing node parents |
| `myXML.Child.text()` | `Text Value Concatenated` | Concatenate child text values|
| `for each(var child in myXML.Child.chindren())` | `N/A` | Iteration through child nodes |

Full specification: [ECMA-357](http://www.ecma-international.org/publications/standards/Ecma-357.htm)

A great guide can be found [here](http://wso2.org/project/mashup/1.5.2/docs/e4xquickstart.html).
[Prev: Template Functions](Functions.md) [Next: Putting it all together: Example](PuttingItAllTogether.md)