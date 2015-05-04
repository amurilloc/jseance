# JSeance is an Structured Code Generator based on XML, Java<sup>TM</sup> and JavaScript<sup>TM</sup> #

## The Code Generation Process ##
Using input XML Models (any schema) and a JSeance template, the code generation engine produces text output of any type, supporting complex transformations and customizations through native rich-features and an embedded JavaScript<sup>TM</sup> engine:

![http://jseance.googlecode.com/svn/wiki/images/process.jpg](http://jseance.googlecode.com/svn/wiki/images/process.jpg)

## Key Features ##

JSeance philosophy is to enable developers to produce highly-complex code while keeping emphasis on template readability and maintainability. It follows a structured approach to solve 90% of the challenges of code generation, and integrates a full-featured JavaScript engine ([Rhino](http://www.mozilla.org/rhino/)) for solving the other 10% within the same template XML file.

![http://jseance.googlecode.com/svn/wiki/images/features.jpg](http://jseance.googlecode.com/svn/wiki/images/features.jpg)

## Feature Description ##
  * **Embedded JavaScript<sup>TM</sup> Engine** ([Rhino](http://www.mozilla.org/rhino/)): While the general features of the engine support most common cases, there will be situations where specialized code is required. The embedded full-featured JavaScript Engine allows developers and template designers to achieve specialized code generation behaviors and transformations without requiring external code files.


  * **ECMAScript for XML ([E4X](http://en.wikipedia.org/wiki/ECMAScript_for_XML)) Support and Iterators**: Built-in iterators with E4X expressions allow for easy navigation between input model nodes, making cross-model queries possible. E4X provides a simple and expressive syntax for selecting, filtering and locating relevant node information.

  * **Multiple XML Input Models**: Allows the template designer to load several XML files, query and cross-reference each separately. This makes it possible to split domain model aspects into separate files, increasing maintainability and readability. For example, you could separate business object information into a Relations.xml and Attributes.xml XML files, each referring to the same entities but expressing different aspects. A template could access both XML files to generate a database schema or [Hibernate](https://www.hibernate.org/) back end.

  * **Conditional Expressions**: Part of the template XML schema includes If/ElseIf/Else and Switch/Case functionality with rich JavaScript expression syntax. This provides a standard mechanism for expressing choices and conditional clauses within the same template structure, which improves maintainability and readability.

  * **External Includes**: This allows template designers to split large template files into smaller, semi-independent sections which can later be re-used by other templates.

  * **Multiple Output Files**: A template can generate multiple files; the name, location and contents can be customized at runtime based on model data, context information or embedded JavaScript. This allows simultaneous generation of files related to a feature or functionality from a single template.

  * **Runtime Template Parametrization**: Every attribute and text content of a JSeance template element can be embedded with JavaScript to be evaluated at runtime. Input and output file names can be decided based on context and input data. This complements the structured XML template approach with added flexibility for cases where the concrete inputs, outputs and actions need to be discovered at runtime.