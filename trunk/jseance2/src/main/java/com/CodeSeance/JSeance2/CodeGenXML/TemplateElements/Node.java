/*
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
 */

package com.CodeSeance.JSeance2.CodeGenXML.TemplateElements;

import com.CodeSeance.JSeance2.CodeGenXML.Context;
import com.CodeSeance.JSeance2.CodeGenXML.ExecutionError;
import org.apache.commons.logging.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 Created by IntelliJ IDEA.
 User: amurillo
 Date: Jun 8, 2010
 Time: 4:43:42 PM
 To change this template use File | Settings | File Templates.
 */
public class Node
{
    // The common logger for this class and derived classes
    protected final Log log;

    Node()
    {
        this(null, null);
    }

    private final Template.Position position;

    Node(String arguments, Template.Position position)
    {
        this.position = position;
        // Create the logger for the concrete type
        log = com.CodeSeance.JSeance2.CodeGenXML.Runtime.CreateLogger(this.getClass());

        if (log.isDebugEnabled())
        {
            String className = this.getClass().getName();
            if (arguments != null)
            {
                log.debug(String.format("Creating Node:[%s] with arguments:[%s]", className.substring(className.lastIndexOf('.') + 1), arguments));
            }
            else
            {
                log.debug(String.format("Creating Node:[%s]", className.substring(className.lastIndexOf('.') + 1)));
            }

        }

        this.arguments = arguments;
    }

    private final String arguments;

    /*
   * Loads the concrete type properties with attributes from the xml node, substitutes javascript when needed
    */

    public void loadAttributes(Context context)
    {
        StringBuilder missingParameters = new StringBuilder();
        try
        {
            Object[] params = evaluateParams(context, arguments);
            int paramNumber = 0;
            for (Field field : this.getClass().getDeclaredFields())
            {
                if (field.isAnnotationPresent(TagParameter.class))
                {
                    TagParameter annotation = field.getAnnotation(TagParameter.class);

                    if (paramNumber < params.length)
                    {
                        Object paramValue = params[paramNumber++];
                        setFieldValue(field, paramValue == null ? annotation.defaultValue() : paramValue);
                    }
                    else if (!annotation.required())
                    {
                        setFieldValue(field, annotation.defaultValue());
                    }
                    else
                    {
                        missingParameters.append( missingParameters.length() > 0 ? ", " + field.getName() : field.getName());
                    }
                }

            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ExecutionError.INVALID_TAG_ARGUMENTS.getMessage(arguments, position.getLine(), position.getCol(), ex.getMessage()));
        }

        if (missingParameters.length() > 0)
        {
            throw new RuntimeException(ExecutionError.MISSING_TAG_ARGUMENTS.getMessage(missingParameters, position.getLine(), position.getCol()));
        }
    }

    private void setFieldValue(Field field, Object value) throws IllegalAccessException
    {
        // TODO: Detect when value types match and throw more descriptive error
        field.setAccessible(true);
        Type type = field.getType();
        if (type.equals(boolean.class))
        {
            field.set(this, value.toString().toLowerCase().equals("true"));
        }
        else if (type.equals(String.class))
        {
            field.set(this, value.toString());
        }
        else
        {
            field.set(this, value);
        }
    }

    private Object[] evaluateParams(Context context, String elementText) throws NoSuchFieldException, IllegalAccessException
    {
        if (arguments == null || "".equals(arguments))
        {
            return new Object[0];
        }
        else
        {
            String evalCode = "JSeanceUtils_ConvertArgsToArray" + elementText;
            Object result = context.evaluateJS(evalCode, position.getFileName(), position.getLine());
            Field argsField = result.getClass().getDeclaredField("args");
            argsField.setAccessible(true);
            return (Object[]) argsField.get(result);
        }
    }


    /*
    * Method will be called on runtime (after construction) to signal
    * context execution entering the current node
    */

    public void onExecutionStart(Context context)
    {

    }


    public void loadChildren(Template template, Node parent)
    {
    }

    /*
    * Method will be called on runtime (after construction) to signal
    * context execution leaving the current node
    */

    public void onExecutionEnd(Context context)
    {
        // No action by default
    }

    /**
     Annotation for class fields which should be extracted from the JSeance template
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TagParameter
    {
        boolean required() default true;

        String defaultValue() default "";
    }
}
