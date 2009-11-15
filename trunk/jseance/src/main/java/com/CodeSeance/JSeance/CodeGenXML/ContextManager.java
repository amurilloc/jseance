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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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
    public ContextManager(File includesDir, File modelsDir, File targetDir, boolean ignoreReadOnlyOuputFiles, TemplateDependencies templateDependencies)
    {
        this.includesDir = includesDir;
        this.modelsDir = modelsDir;
        this.targetDir = targetDir;
        this.ignoreReadOnlyOuputFiles = ignoreReadOnlyOuputFiles;
        this.templateDependencies = templateDependencies;
        initializeJavaScriptEngine();
        contextStack.push(new Context(this, null));
    }

    // The working directories and runtime configuration
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
            if (ExecutionError.simulate_CONTEXTMANAGER_INITIALIZE_ERROR)
            {
                ExecutionError.simulate_CONTEXTMANAGER_INITIALIZE_ERROR = false;
                throw new Exception("Simulated exception for log testing");
            }

            jsScope = jsContext.initStandardObjects();

            // Declare the java classes that implement models and definitions in the js engine
            ScriptableObject.defineClass(jsScope, JSModels.class);
            ScriptableObject.defineClass(jsScope, JSModel.class);
            ScriptableObject.defineClass(jsScope, JSDefinitions.class);
        }
        catch (Exception ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException(ExecutionError.CONTEXTMANAGER_INITIALIZE_ERROR.getMessage(ex.getMessage()));
        }

        evaluateJSPrivate("function " + XML_CREATE_FN + "(xmlText){return new XML(xmlText);};", "ContextManager.java", 123);
        evaluateJSPrivate("function " + XML_EVAL_PATH_FN + "(xml, path){return eval('xml.' + path);};", "ContextManager.java", 124);
        evaluateJSPrivate("function " + XML_LENGTH_FN + "(xml){return xml.length();};", "ContextManager.java", 125);
        evaluateJSPrivate("function " + XML_GET_NODE_AT_FN + "(xml, index){return xml[index];};", "ContextManager.java", 126);
        evaluateJSPrivate("function " + XML_NODE_TO_STRING + "(xml){return xml.toXMLString();};", "ContextManager.java", 127);
        
        declareStringConversionFunction("EscapeXMLValue", "com.CodeSeance.JSeance.CodeGenXML.XMLElements.Text.escapeXMLValue");
        declareStringConversionFunction("EscapeXMLAttribute", "com.CodeSeance.JSeance.CodeGenXML.XMLElements.Text.escapeXMLAttribute");
        declareStringConversionFunction("EscapeHTML", "org.apache.commons.lang.StringEscapeUtils.escapeHtml");
        declareStringConversionFunction("EscapeJava", "org.apache.commons.lang.StringEscapeUtils.escapeJava");
        declareStringConversionFunction("EscapeJavaScript", "org.apache.commons.lang.StringEscapeUtils.escapeJavaScript");
        declareStringConversionFunction("EscapeSQL", "org.apache.commons.lang.StringEscapeUtils.escapeSql");
    }

    private void declareStringConversionFunction(String name, String method)
    {
        evaluateJSPrivate(String.format("function %s (val){return String(%s(val));};", name, method), "ContextManager.java", 139);
    }

    public void setCurrentDefinitions(JSDefinitions jsDefinitions)
    {
        ScriptableObject.putProperty(jsScope, "Definitions", jsDefinitions);
    }

    public void setCurrentModels(JSModels jsModels)
    {
        ScriptableObject.putProperty(jsScope, "Models", jsModels);
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
        StreamResult result;
        try
        {
            if (ExecutionError.simulate_CONTEXTMANAGER_CREATEXMLOBJECT_ERROR)
            {
                ExecutionError.simulate_CONTEXTMANAGER_CREATEXMLOBJECT_ERROR = false;
                throw new TransformerException("Simulated exception for log testing");
            }
            transformer = TransformerFactory.newInstance().newTransformer();
            // The next section is required in order for the JS engine to parse the XML
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
        }
        catch (TransformerException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException(ExecutionError.CONTEXTMANAGER_CREATEXMLOBJECT_ERROR.getMessage(ex.getMessage()));
        }

        String xmlString = result.getWriter().toString();

        // Create the JS Objects
        Object[] args = {xmlString};
        Object jsXMLObj = callJSFunction(XML_CREATE_FN, args);
        assert jsXMLObj instanceof XMLObject;
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
        assert result instanceof Integer;
        return (Integer) result;
    }

    /*
    * Returns a JavaScript XML Node at the specified place in the NodeList
    */
    public XMLObject getXMLItemAt(XMLObject xmlObj, int index)
    {
        Object[] args = {xmlObj, index};
        Object result = callJSFunction(XML_GET_NODE_AT_FN, args);
        assert result instanceof XMLObject;
        return (XMLObject) result;
    }

    // JSEngine context variables
    org.mozilla.javascript.Context jsContext = null;
    org.mozilla.javascript.Scriptable jsScope = null;

    // Pushes a new Context into the stack
    public void push()
    {
        Context newContext = new Context(this, contextStack.peek());
        contextStack.push(newContext);
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

    // Evaluates the specified JavaScript text - Public version useful for logging & breakpoints
    public Object evaluateJS(String script, String sourceName, int lineNo)
    {
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Evaluating JavaScript:[%s]", script));
        }
        return evaluateJSPrivate(script, sourceName, lineNo);
    }

    // Evaluates the specified JavaScript text - Private version, not logged
    private Object evaluateJSPrivate(String script, String sourceName, int lineNo)
    {
        try
        {
            return jsContext.evaluateString(jsScope, script, sourceName, lineNo, null);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ExecutionError.JAVASCRIPT_EVAL_ERROR.getMessage(script, ex.getMessage()));
        }
    }

    // Calls the specified Javascript function
    public Object callJSFunction(String functionName, Object[] fxArgs)
    {
        Object fxObj = jsScope.get(functionName, jsScope);
        assert fxObj instanceof org.mozilla.javascript.Function;
        org.mozilla.javascript.Function fx = (org.mozilla.javascript.Function) fxObj;
        return fx.call(jsContext, jsScope, jsScope, fxArgs);
    }

    // Prefix to be used when embedding JavaScript code into text
    public final static String[] CODE_PREFIXES = {"@JavaScript{", "@JS{"};
    // Suffix to be used when embedding JavaScript code into text
    public final static String CODE_SUFFIX = "}@";

    // Resolves the embedded script fragments within a piece of texts
    public String resolveCodeFragments(String input)
    {
        String result = input;
        for (String codePrefix : CODE_PREFIXES)
        {
            int locStart = input.indexOf(codePrefix);
            if (locStart >= 0)
            {
                StringBuilder builder = new StringBuilder();
                int locEnd = input.indexOf(CODE_SUFFIX, locStart);
                if (locEnd < 0)
                {
                    throw new RuntimeException(ExecutionError.JAVASCRIPT_NOT_CLOSED.getMessage(input, CODE_SUFFIX));
                }
                String subExpression = input.substring(locStart + codePrefix.length(), locEnd);
                builder.append(input.substring(0, locStart));
                builder.append(evaluateJS(subExpression, input, 0));
                builder.append(input.substring(locEnd + CODE_SUFFIX.length()));
                result = resolveCodeFragments(builder.toString());
            }
        }
        return result;
    }

    // The stack used to maintain state as the XML Template document gets explored
    private Stack<Context> contextStack = new Stack<Context>();
}
