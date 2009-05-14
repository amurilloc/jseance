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

import org.apache.commons.logging.Log;
import org.mozilla.javascript.xml.XMLObject;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Hashtable;

/**
 * In JSeance, each level of an XML template has a context which is inherited from its parent
 * This is used to keep track of local values as execution advances while maintaining isolation between sibling nodes
 *
 * A context keeps track of:
 * - The current executing template parent path: To allow included templates to define resources by local reference
 * - The set of active models with the current nodes
 * - The current textSink: allows children to write text outputs that bubble up to the closest sink
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class Context
{

    /*
    * Constructor for creating the root context manager
    */
    public Context(ContextManager manager, File parentPath)
    {
        this.manager = manager;
        this.parentPath = parentPath;
    }

    /*
    * Constructor for creating child context managers
    */
    public Context(ContextManager manager, Context parent)
    {
        this.manager = manager;
        this.parent = parent;

        // Create a deep copy of the models
        for (String key : parent.models.keySet())
        {
            JSModel modelCopy = parent.models.get(key).DeepClone();
            models.put(key, modelCopy);
        }

        parentPath = parent.parentPath;
        logSpacing = parent.logSpacing + " ";
    }

    private ContextManager manager = null;

    private Context parent = null;

    private File parentPath;

    private String logSpacing = "";

    public void LogInfoMessage(Log log, String tagName, String message)
    {
        if (log.isInfoEnabled())
        {
            log.info(String.format("%s<%s> - %s", logSpacing, tagName, message));
        }
    }

    /*
    * Returns the current parent path, that is the directory where all resources should be loaded relative to
     */
    public File getParentPath()
    {
        return parentPath;
    }

    /*
    * Sets thge current parent path, this shuold be called from included templates to change the current context
    * reference directory
     */
    public void setParentPath(File parentPath)
    {
        this.parentPath = parentPath;
    }

    /**
     * Adds an XML Model to the current context, these are available in JavaScript context as: For Named Models:
     * Models['<name>'].rootNode or Models['<name>'].currentNode For unnamed or default models:
     * Models['default'].rootNode or Models['default'].currentNode
     *
     * @param fileName    The Model fileName to add
     * @param name        The name to assign to this model, if empty or null 'default' will be used
     * @param e4XPath     The E4X Path to use for the currentNode element, if empty or null the root node will be used
     * @param validate    Specifies if the XML Parser should validate the XML file against a schema (specified in the
     *                    file or in the next parameter)
     * @param xsdFileName Optional, the fileName of a schema file to use for XML validation
     */
    public void addModel(String fileName, String name, String e4XPath, boolean validate, String xsdFileName)
    {
        name = setDefaultModelNameIfEmpty(name);
        // Load the XML File
        XMLLoader xmlLoader;

        if ("".equals(xsdFileName) || xsdFileName == null || !validate)
        {
            xmlLoader = XMLLoader.build(validate);
        }
        else
        {
            xmlLoader = XMLLoader.buildFromXSDFileName(parentPath, xsdFileName);
        }
        Document xmlDoc = xmlLoader.loadXML(parentPath, fileName);

        XMLObject jsXML = manager.createXMLObject(xmlDoc);

        // Evaluate the path if required
        Object jsCurrentNodeObj = manager.evaluateE4XPath(jsXML, e4XPath);
        if (!(jsCurrentNodeObj instanceof XMLObject))
        {
            throw new RuntimeException("Invalid e4XPath Expression:[" + e4XPath + "], was expecting XMLObject instance");
        }
        XMLObject jsCurrentNode = (XMLObject) jsCurrentNodeObj;

        // Create and add the new model
        JSModel model = new JSModel();
        model.SetRootNode(jsXML);
        model.SetCurrentNode(jsCurrentNode);
        models.put(name, model);
    }

    private final static String UNNAMED_MODEL_NAME = "default";

    private String setDefaultModelNameIfEmpty(String name)
    {
        return ("".equals(name) || name == null) ? UNNAMED_MODEL_NAME : name;
    }

    private final Hashtable<String, JSModel> models = new Hashtable<String, JSModel>();

    /*
    * Returns the specified JavaScript representation of a model
    */
    public JSModel getModel(String name)
    {
        name = setDefaultModelNameIfEmpty(name);
        // Obtain the model locally, a deep copy is performend on new context creation
        return models.get(name);
    }

    /*
    * Sets the current text sink on this context, this will cause text created by children elements to bubble up
    * to the closest time sink
    */
    public void setTextSink(StringBuffer sink)
    {
        if (textSink == null)
        {
            textSink = sink;
        }
        else
        {
            throw new RuntimeException("Current context already has a TextSink");
        }
    }

    private StringBuffer textSink = null;

    /*
    * writes text to the closes textSink, transversing parents until a textSink is found. Note that the Template node
    * defines the default textSink
     */
    public void writeText(String text)
    {
        if (textSink != null)
        {
            textSink.append(text);
        }
        else if (parent != null)
        {
            parent.writeText(text);
        }
        else
        {
            throw new RuntimeException("Cannot find valid TextSink in context");
        }
    }

    /*
    * Sets a definition in the current context, note that definitions are inherited from parents but if changed only
    * affect the current context and its children
     */
    public void setDefinition(String name, Object content)
    {
        definitions.put(name, content);
    }

    /*
    * Returns the value of the spefified definition, transversing through parent nodes until found
     */
    public Object getDefinition(String name)
    {
        if (definitions.containsKey(name))
        {
            return definitions.get(name);
        }
        else if (parent != null)
        {
            return parent.getDefinition(name);
        }
        return null;
    }

    private final Hashtable<String, Object>definitions = new Hashtable<String, Object>();

    /*
    * Returns the parent context if exisiting
     */
    public Context getParent()
    {
        return parent;
    }

    public ContextManager getManager()
    {
        return manager;
    }
}