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

import com.CodeSeance.JSeance.CodeGenXML.Context;
import com.CodeSeance.JSeance.CodeGenXML.JSModel;
import com.CodeSeance.JSeance.CodeGenXML.XMLAttribute;
import org.mozilla.javascript.xml.XMLObject;
import org.w3c.dom.Element;

/**
 * Class for handling nodegroups and iterations in templates
 *
 * @author Andres Murillo
 * @version 1.0
 */
class OutputIterator extends HierarchicalNode
{

    public OutputIterator(Element element)
    {
        super(element);
    }

    private final StringBuffer buffer = new StringBuffer();

    @XMLAttribute
    String e4XPath;

    @XMLAttribute
    String modelName;

    @XMLAttribute
    String separator;

    @Override
    public void onContextEnter(Context context)
    {
        context.LogInfoMessage(log, "OutputIterator", String.format("Evaluating e4XPath:[%s] on model:[%s]", e4XPath, modelName));
        context.setTextSink(buffer);

        JSModel jsModel = context.getModel(modelName);
        Object elementsObj = context.getManager().evaluateE4XPath(jsModel.GetCurrentNode(), e4XPath);
        if (!(elementsObj instanceof XMLObject))
        {
            throw new RuntimeException("Invalid e4XPath Expression:[" + e4XPath + "], was expecting XMLObject instance");
        }
        XMLObject elements = (XMLObject) elementsObj;
        int xmlCollectionSize = context.getManager().getXMLCollectionSize(elements);
        if (xmlCollectionSize == 0)
        {
            context.LogInfoMessage(log, "OutputIterator", "e4XPath expression returned no children");
        }
        for (int i = 0; i < xmlCollectionSize; i++)
        {
            XMLObject currentNode = context.getManager().getXMLItemAt(elements, i);
            context.LogInfoMessage(log, "OutputIterator", String.format("Processing children, step:[%s]/[%s], currentNode:[%s]", i+1, xmlCollectionSize, context.getManager().xmlNodeToString(currentNode)));

            jsModel.SetCurrentNode(currentNode);
            ExecuteChildren(context);
            if ((!"".equals(separator)) && i < (xmlCollectionSize - 1))
            {
                context.writeText(separator);
            }
        }
    }

    @Override
    public void onContextExit(Context context)
    {

        String text = buffer.toString();
        context.LogInfoMessage(log, "OutputIterator", String.format("Children produced:[%s]", text));
        context.getParent().writeText(text);
    }
}