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
import com.CodeSeance.JSeance2.CodeGenXML.JSModel;
import org.mozilla.javascript.xml.XMLObject;

/**
 Created by IntelliJ IDEA.
 User: amurillo
 Date: Jun 16, 2010
 Time: 11:58:48 AM
 To change this template use File | Settings | File Templates.
 */
class For extends HierarchicalNode
{
    public For(String arguments, Template.Position position)
    {
        super(arguments, position);
    }

    @TagParameter
    String e4XPath;

    @TagParameter(required = false, defaultValue="default")
    String modelName;

    @TagParameter(required = false)
    String separator;

    private org.mozilla.javascript.xml.XMLObject currentNode;

    @Override
    public void onExecutionStart(Context context)
    {
        context.LogInfoMessage(log, "For", String.format("Evaluating e4XPath:[%s] on model:[%s]", e4XPath, modelName));

        JSModel jsModel = context.getModel(modelName);

        // Save the current node to restore it afterwards
        currentNode = jsModel.getCurrentNode();

        Object elementsObj = context.evaluateE4XPath(jsModel.getCurrentNode(), e4XPath);
        if (!(elementsObj instanceof XMLObject))
        {
            throw new RuntimeException(ExecutionError.INVALID_OUTPUT_ITERATOR_E4X_EXPRESSION.getMessage(e4XPath, elementsObj.getClass()));
        }
        XMLObject elements = (XMLObject) elementsObj;
        int xmlCollectionSize = context.getXMLCollectionSize(elements);
        if (xmlCollectionSize == 0)
        {
            context.LogInfoMessage(log, "For", "e4XPath expression returned no children");
            if (ifEmpty != null)
            {
                ifEmpty.loadAttributes(context);
                ifEmpty.onExecutionStart(context);
                ifEmpty.onExecutionEnd(context);
            }
        }
        else
        {
            for (int i = 0; i < xmlCollectionSize; i++)
            {
                XMLObject currentNode = context.getXMLItemAt(elements, i);
                context.LogInfoMessage(log, "For", String.format("Processing children, step:[%s]/[%s], currentNode:[%s]", i + 1, xmlCollectionSize, context.xmlNodeToString(currentNode)));

                jsModel.setCurrentNode(currentNode);

                super.onExecutionStart(context);

                if ((!"".equals(separator)) && i < (xmlCollectionSize - 1))
                {
                    context.writeText(separator);
                }
            }
        }
    }

    @Override
    public void onExecutionEnd(Context context)
    {
        // Restore the current node to the initial state
        JSModel jsModel = context.getModel(modelName);
        jsModel.setCurrentNode(currentNode);
    }

    private Node ifEmpty = null;

    @Override
    public void loadChildren(Template template, Node parent)
    {
        // Clear the children, this is needed to avoid double addition if the tag is within a loop
        children.clear();

        Node child;
        while (!(child = template.parseNode(this)).getClass().equals(End.class))
        {
            if (child.getClass().equals(IfEmpty.class))
            {
                if (ifEmpty != null)
                {
                    template.throwError("IfEmpty statement needs to be the last child of a For statement");
                }
                ifEmpty = child;
            }
            else
            {
                children.add(child);
            }
        }
    }
}
