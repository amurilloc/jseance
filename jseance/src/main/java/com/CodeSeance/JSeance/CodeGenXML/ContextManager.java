/* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1/GPL 2.0
*
* The contents of this file are subject to the Mozilla Public License Version
* 1.1 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS IS" basis,
* WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
* for the specific language governing rights and limitations under the
* License.
*
* The Original Code is JSeance code, released
* May 6, 2009.
*
* The Initial Developer of the Original Code is
* Andres Murillo.
* Portions created by the Initial Developer are Copyright (C) 2009
* the Initial Developer. All Rights Reserved.
*
* Alternatively, the contents of this file may be used under the terms of
* the GNU General Public License Version 2 or later (the "GPL"), in which
* case the provisions of the GPL are applicable instead of those above. If
* you wish to allow use of your version of this file only under the terms of
* the GPL and not to allow others to use your version of this file under the
* MPL, indicate your decision by deleting the provisions above and replacing
* them with the notice and other provisions required by the GPL. If you do
* not delete the provisions above, a recipient may use your version of this
* file under either the MPL or the GPL.
*
* ***** END LICENSE BLOCK ***** */

package com.CodeSeance.JSeance.CodeGenXML;

import com.CodeSeance.JSeance.CodeGenXML.DependencyTracking.TemplateDependencies;
import org.apache.commons.logging.Log;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.xml.XMLObject;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.Stack;

/**
 * This is class serves a dual purpose, it encapsulates the functionality of the JavaScript engine (Rhino) and manages
 * the stack of Context objets that become active as Template scopes execute.
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class ContextManager
{
    /*
    * Constructor initialized the JavaScript engine and pushes a new context on the stack,
    * note that the thread that created the context manager should be the same calling any
    * action on it or its children (rhino js engine requirement).
    * dispose needs to be called when the ContextManager is no longer needed to release
    * resources
    */
    public ContextManager(File templatesDir, File includesDir, File modelsDir, File targetDir,
                          boolean ignoreReadOnlyOuputFiles, TemplateDependencies templateDependencies)
    {
        this.templatesDir = templatesDir;
        this.includesDir = includesDir;
        this.modelsDir = modelsDir;
        this.targetDir = targetDir;
        this.ignoreReadOnlyOuputFiles = ignoreReadOnlyOuputFiles;
        this.templateDependencies = templateDependencies;
        initializeJavaScriptEngine();
        contextStack.push(new Context(this));
    }

    // The working directories and runtime configuration
    public final File templatesDir;
    public final File includesDir;
    public final File modelsDir;
    public final File targetDir;
    public final boolean ignoreReadOnlyOuputFiles;

    // Dependency manager, readers and writers need to use it
    public final TemplateDependencies templateDependencies;

    // The context factory to be used across all instances
    final static ContextFactory factory = new ContextFactory();

    private final static String XML_CREATE_FN = "JSeance_CreateXML";
    private final static String XML_EVAL_PATH_FN = "JSeance_EvalXMLPath";
    private final static String XML_LENGTH_FN = "JSeance_XMLLength";
    private final static String XML_GET_NODE_AT_FN = "JSeance_XMLGetNodeAt";
    private final static String XML_NODE_TO_STRING = "JSeance_XMLNodeToString";

    //Initializes the JavaScript engine (Rhino) with the required context objects and instances
    private void initializeJavaScriptEngine()
    {
        jsContext = factory.enterContext();
        try
        {
            jsScope = jsContext.initStandardObjects();

            // Create the instances for models and definitions
            JSModels jsModels = new JSModels();
            jsModels.SetContextManager(this);
            JSDefinitions jsDefinitions = new JSDefinitions();
            jsDefinitions.SetContextManager(this);

            // Declare the java classes that implement models and definitions in the js engine
            org.mozilla.javascript.ScriptableObject.defineClass(jsScope, JSModels.class);
            org.mozilla.javascript.ScriptableObject.defineClass(jsScope, JSModel.class);
            org.mozilla.javascript.ScriptableObject.defineClass(jsScope, JSDefinitions.class);

            // Declare the global instances available within the js scope
            ScriptableObject.putProperty(jsScope, "Models", jsModels);
            ScriptableObject.putProperty(jsScope, "Definitions", jsDefinitions);

            evaluateJS("function " + XML_CREATE_FN + "(xmlText){return new XML(xmlText);};", "Context.java", 25);
            evaluateJS("function " + XML_EVAL_PATH_FN + "(xml, path){return eval('xml.' + path);};", "Context.java", 26);
            evaluateJS("function " + XML_LENGTH_FN + "(xml){return xml.length();};", "Context.java", 26);
            evaluateJS("function " + XML_GET_NODE_AT_FN + "(xml, index){return xml[index];};", "Context.java", 26);
            evaluateJS("function " + XML_NODE_TO_STRING + "(xml){return xml.toXMLString();};", "Context.java", 26);
        }
        catch (Exception ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException("Unexpected Internal Exception", ex);
        }
    }

    // Class logger
    private Log log = Runtime.CreateLogger(this.getClass());

    /*
    * Transforms a Java XML Document into a JavaScript XMLObject that can be used within a script context
     */
    public XMLObject createXMLObject(Document document)
    {
        //Transform to String
        Transformer transformer;
        try
        {
            transformer = TransformerFactory.newInstance().newTransformer();
        }
        catch (TransformerConfigurationException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException(String.format("Unexpected Exception:[%s], Error:[%s] ",  ex.getClass(), ex.getMessage()), ex);
        }

        // The next section is required in order for the JS engine to parse the XML
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        try
        {
            transformer.transform(source, result);
        }
        catch (TransformerException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException(String.format("Unexpected Exception:[%s], Error:[%s] ",  ex.getClass(), ex.getMessage()), ex);
        }

        String xmlString = result.getWriter().toString();

        // Create the JS Objects
        Object[] args = {xmlString};
        Object jsXMLObj = callJSFunction(XML_CREATE_FN, args);
        if (!(jsXMLObj instanceof XMLObject))
        {
            throw new RuntimeException("Invalid XMLObject, was expecting XMLObject instance");
        }
        return (XMLObject) jsXMLObj;
    }

    /*
    * Evaluates the specified E4X Path on a JavaScript XML Node
     */
    public Object evaluateE4XPath(XMLObject xmlObj, String e4XPath)
    {
        Object result = xmlObj;
        if (!"".equals(e4XPath) && e4XPath != null)
        {
            Object[] args = {xmlObj, e4XPath};
            result = callJSFunction(XML_EVAL_PATH_FN, args);
        }
        return result;
    }

   /*
    * Evaluates the specified E4X Path on a JavaScript XML Node
     */
    public String xmlNodeToString(XMLObject xmlObj)
    {
        Object[] args = {xmlObj};
        return callJSFunction(XML_NODE_TO_STRING, args).toString();
    }

    /*
    * returns the collection size of a Javascript NodeList
    */
    public int getXMLCollectionSize(XMLObject xmlObj)
    {
        Object[] args = {xmlObj};
        Object result = callJSFunction(XML_LENGTH_FN, args);
        if (!(result instanceof Integer))
        {
            throw new RuntimeException("Invalid XMLObject, was expecting Integer result");
        }
        return (Integer) result;
    }

    /*
    * Returns a JavaScript XML Node at the specified place in the NodeList
    */
    public XMLObject getXMLItemAt(XMLObject xmlObj, int index)
    {
        Object[] args = {xmlObj, index};
        Object result = callJSFunction(XML_GET_NODE_AT_FN, args);
        if (!(result instanceof XMLObject))
        {
            throw new RuntimeException("Invalid result, was expecting XMLObject result");
        }
        return (XMLObject) result;
    }

    // JSEngine context variables
    org.mozilla.javascript.Context jsContext = null;
    org.mozilla.javascript.Scriptable jsScope = null;

    // Pushes a new Context into the stack
    public void push()
    {
        contextStack.push(new Context(this, contextStack.peek()));
    }

    // Pops a context from the stack
    public Context pop()
    {
        return contextStack.pop();
    }

    // Returns the current context from the top of the stack without changing it
    public Context getCurrentContext()
    {
        return contextStack.peek();
    }

    // Releases the resources assciated with the JSEngine attached to the current thread
    public void dispose()
    {
        org.mozilla.javascript.Context.exit();
    }

    // Evaluates the specified JavaScript text
    public Object evaluateJS(String script, String sourceName, int lineNo)
    {
        if (log.isTraceEnabled())
        {
            log.trace(String.format("Evaluating JavaScript:[%s]", script));
        }
        try
        {
            return jsContext.evaluateString(jsScope, script, sourceName, lineNo, null);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot Evaluate JavaScript:[" + script + "] " + sourceName + "Line: " + lineNo, e);
        }
    }

    // Calls the specified Javascript function
    public Object callJSFunction(String functionName, Object[] fxArgs)
    {
        Object fxObj = jsScope.get(functionName, jsScope);
        if (!(fxObj instanceof org.mozilla.javascript.Function))
        {
            throw new RuntimeException(functionName + " is undefined or not a JavaScript function.");
        }
        else
        {
            org.mozilla.javascript.Function fx = (org.mozilla.javascript.Function) fxObj;
            return fx.call(jsContext, jsScope, jsScope, fxArgs);
        }
    }

    // Prefix to be used when embedding JavaScript code into text
    public final static String CODE_PREFIX = "@JavaScript{";
    // Suffix to be used when embedding JavaScript code into text
    public final static String CODE_SUFFIX = "}@";

    // Resolves the embedded script fragments within a piece of texts
    public String resolveCodeFragments(String input)
    {
        int locStart = input.indexOf(CODE_PREFIX);
        if (locStart >= 0)
        {
            StringBuilder result = new StringBuilder();
            int locEnd = input.indexOf(CODE_SUFFIX, locStart);
            if (locEnd < 0)
            {
                throw new RuntimeException("Unterminated " + CODE_PREFIX + " String Fragment:[" + input + "]");
            }
            String subExpression = input.substring(locStart + CODE_PREFIX.length(), locEnd);
            result.append(input.substring(0, locStart));
            result.append(evaluateJS(subExpression, input, 0));
            result.append(input.substring(locEnd + CODE_SUFFIX.length()));
            return resolveCodeFragments(result.toString());
        }
        else
        {
            return input;
        }
    }

    // The stack used to maintain state as the XML Template document gets explored
    private Stack<Context> contextStack = new Stack<Context>();
}
