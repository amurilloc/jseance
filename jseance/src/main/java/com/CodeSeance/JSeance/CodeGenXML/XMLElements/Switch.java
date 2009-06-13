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
 * Class for Switch functionality root node
 *
 * @author Andres Murillo
 * @version 1.0
 */
class Switch extends HierarchicalNode
{

    public Switch(Element element)
    {
        super(element);
    }

    // Using GUIDS to define reserved definition names
    public static final String SWITCH_EXPRESSION = "3786330d-a758-44d7-a39c-afe41fd63e48";
    public static final String SWITCH_MATCH = "f90167f5-f598-414a-8fbc-8049312b78dd";

    @XMLAttribute
    String jsExpression;

    @Override
    public void onContextEnter(Context context)
    {
        context.LogInfoMessage(log, "Switch", String.format("Processing jsExpression:[%s]", jsExpression));

        String expression = context.getManager().evaluateJS(jsExpression, "Switch", 0).toString();

        context.LogInfoMessage(log, "Switch", String.format("jsExpression:[%s] evaluates to:[%s]", jsExpression, expression));

        context.setDefinition(SWITCH_EXPRESSION, expression);
        context.setDefinition(SWITCH_MATCH, false);

        context.LogInfoMessage(log, "Switch", "Processing children");

        ExecuteChildren(context);
    }
}