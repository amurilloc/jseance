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
import com.CodeSeance.JSeance.CodeGenXML.XMLAttribute;
import org.w3c.dom.Element;

/**
 * Class for implementing "Else If" functionality
 *
 * @author Andres Murillo
 * @version 1.0
 */
class ConditionalElseIf extends HierarchicalNode
{
    public ConditionalElseIf(Element element)
    {
        super(element);
    }

    @XMLAttribute
    String jsExpression;

    @Override
    public void onContextEnter(Context context)
    {
        if (context.getParent().getDefinition(Conditional.CONDITIONAL_MATCH).equals(false))
        {
            context.LogInfoMessage(log, "ElseIf", String.format("Processing jsExpression:[%s]", jsExpression));

            boolean expression = ("true".equals(context.getManager().evaluateJS(jsExpression, "ConditionalElseIf", 0).toString().toLowerCase()));

            context.LogInfoMessage(log, "ElseIf", String.format("jsExpression:[%s] evaluates to:[%b]", jsExpression, expression));

            context.getParent().setDefinition(Conditional.CONDITIONAL_MATCH, expression);
            if (expression)
            {
                context.LogInfoMessage(log, "ElseIf", "Processing children");
                ExecuteChildren(context);
            }
        }
    }
}