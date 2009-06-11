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

package com.CodeSeance.JSeance.CodeGenXML.XMLElements;

import com.CodeSeance.JSeance.CodeGenXML.ContextManager;
import com.CodeSeance.JSeance.CodeGenXML.XMLAttribute;
import com.CodeSeance.JSeance.CodeGenXML.XMLTextContent;
import com.CodeSeance.JSeance.CodeGenXML.Context;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;

import java.lang.reflect.Field;

/**
 * This is the base class for objects in the hierarchy of XML nodes that compose a code generation template. It defines
 * the common behavior for all subclasses of the composite. Note that the base class makes no attempt to validate
 * attributes or XML structure since that is the responsibility of the XSD validator used in XMLLoader
 *
 * @author Andres Murillo
  * @version 1.0
 */
abstract class Node
{
    // The common logger for this class and derived classes
    protected final Log log;

    /**
     * Derived classes are expected to call the super constructor with the XMLElement that corresponds to the node
     *
     * @param element the XML Element corresponding to the concrete class
     */
    public Node(Element element)
    {
        // Create the logger for the concrete type
        log = com.CodeSeance.JSeance.CodeGenXML.Runtime.CreateLogger(this.getClass());

        if (log.isTraceEnabled())
        {
            log.trace(String.format("Loading XMLElement:[%s]", element.getLocalName()));
        }

        this.element = element;
    }

    private final Element element;

    /*
    * Loads the concrete type properties with attributes from the xml node, substitutes javascript when needed
     */
    public void loadAttributes(Context context)
    {
        ContextManager contextManager = context.getManager();
        for (Field field : this.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(XMLAttribute.class))
            {
                Class type = field.getType();

                String attributeName = field.getAnnotation(XMLAttribute.class).attributeName();
                if ("".equals(attributeName) || attributeName == null)
                {
                    attributeName = field.getName();
                }

                String stringValue = element.getAttribute(attributeName);
                try
                {
                    field.set(this, replaceJSAndConvert(contextManager, stringValue, type));
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException("Programming ExecutionError: IllegalAccessException during Node sttribute construction:");
                }
            }
            else if (field.isAnnotationPresent(XMLTextContent.class))
            {
                Class type = field.getType();
                try
                {
                    field.set(this, replaceJSAndConvert(contextManager, element.getTextContent(), type));
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException("Programming ExecutionError: IllegalAccessException during Node sttribute construction:");
                }
            }
        }
    }

    /*
   *  Converts a string to the specified type, only boolean and string conversions are allowed to avoid introducing
   *  code that is not in use
    */
    private Object replaceJSAndConvert(ContextManager contextManager, String value, Class type)
    {
        value = contextManager.resolveCodeFragments(value);
        if (Boolean.TYPE.equals(type) || Boolean.class.equals(type))
        {
            return "true".equals(value.toLowerCase());
        }
        else if (String.class.equals(type))
        {
            return value;
        }
        else
        {
            throw new RuntimeException("Programming ExecutionError: Unsupported class attribute type during Node construction:" + type.toString());
        }
    }

    /*
    * Method will be called on runtime (after construction) to signal
    * context execution entering the current node
    */
    public abstract void onContextEnter(Context context);

    /*
    * Method will be called on runtime (after construction) to signal
    * context execution leaving the current node
    */
    public void onContextExit(Context context)
    {
        // No action by default
    }
}