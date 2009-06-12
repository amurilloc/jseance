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
    * Constructor for creating child context managers
    */
    public Context(ContextManager manager, Context parent)
    {
        this.manager = manager;
        this.parent = parent;

        if (parent != null)
        {
            jsDefinitions = parent.jsDefinitions.deepClone();
            jsModels = parent.jsModels.deepClone();

            logSpacing = parent.logSpacing + " ";
        }
        else
        {
            jsDefinitions = new JSDefinitions();
            jsModels = new JSModels();
        }

        manager.setCurrentDefinitions(jsDefinitions);
        manager.setCurrentModels(jsModels);
    }

    private ContextManager manager = null;

    private Context parent = null;

    private String logSpacing = "";

    public void LogInfoMessage(Log log, String tagName, String message)
    {
        if (log.isInfoEnabled())
        {
            log.info(String.format("%s<%s> - %s", logSpacing, tagName, message));
        }
    }

    public void addModel(String name, JSModel model)
    {
        name = setDefaultModelNameIfEmpty(name);
        jsModels.setModel(name, model);
    }

    private final static String UNNAMED_MODEL_NAME = "default";

    // Returns the default model name if the specified string is empty
    private String setDefaultModelNameIfEmpty(String name)
    {
        return ("".equals(name) || name == null) ? UNNAMED_MODEL_NAME : name;
    }

    //private final Hashtable<String, JSModel> models = new Hashtable<String, JSModel>();
    JSModels jsModels = null;

    /*
    * Returns the specified JavaScript representation of a model
    */
    public JSModel getModel(String name)
    {
        name = setDefaultModelNameIfEmpty(name);
        // Obtain the model locally, a deep copy is performend on new context creation
        return jsModels.getModel(name);
    }

    /*
    * Sets the current text sink on this context, this will cause text created by children elements to bubble up
    * to the closest time sink
    */
    public void setTextSink(StringBuffer sink)
    {
        // only one call to this method should be performed within a context
        assert textSink == null;

        textSink = sink;
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
            // Cannot find a valid text sink in context, the top Template element should always have one
            assert false : textSink;
        }
    }

    /*
    * Sets a definition in the current context, note that definitions are inherited from parents but if changed only
    * affect the current context and its children
     */
    public void setDefinition(String name, Object val)
    {
        jsDefinitions.setDefinition(name, val);
    }

    /*
    * Returns the value of the spefified definition, transversing through parent nodes until found
     */
    public Object getDefinition(String name)
    {
        return jsDefinitions.getDefinition(name);
    }

    JSDefinitions jsDefinitions = null;

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