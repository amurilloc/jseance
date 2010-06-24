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

import java.util.LinkedList;
import java.util.List;

/**
 Created by IntelliJ IDEA.
 User: amurillo
 Date: Jun 17, 2010
 Time: 9:56:14 AM
 To change this template use File | Settings | File Templates.
 */
class If extends HierarchicalNode
{

    public If(String arguments, Template.Position position)
    {
        super(arguments, position);
    }

    @TagParameter
    boolean condition;

    private final List<ElseIf> elseIfs = new LinkedList<ElseIf>();
    private Else elseTag = null;

    @Override
    public void loadChildren(Template template, Node parent)
    {
        // Clear the children, this is needed to avoid double addition if the tag is within a loop
        children.clear();
        elseIfs.clear();
        elseTag = null;

        Node child;
        while (!(child = template.parseNode(this)).getClass().equals(End.class))
        {
            if (elseTag != null)
            {
                template.throwError("Else statement needs to be the last child of an If statement");
            }

            if (child.getClass().equals(ElseIf.class))
            {
                elseIfs.add((ElseIf) child);
            }
            else if (child.getClass().equals(Else.class))
            {
                elseTag = (Else) child;
            }
            else
            {
                children.add(child);
            }
        }
    }

    @Override
    public void onExecutionStart(Context context)
    {
        context.LogInfoMessage(log, "If", String.format("Evaluates to:[%b]", condition));

        boolean clauseFound = condition;
        if (condition)
        {
            super.onExecutionStart(context);
        }
        else
        {
            for (ElseIf elseIf : elseIfs)
            {
                elseIf.loadAttributes(context);
                context.LogInfoMessage(log, "ElseIf", String.format("Evaluates to:[%b]", elseIf.condition));
                if (elseIf.condition)
                {
                    elseIf.onExecutionStart(context);
                    elseIf.onExecutionEnd(context);
                    clauseFound = true;
                    break;
                }
            }
        }
        if (!clauseFound && elseTag != null)
        {
            elseTag.loadAttributes(context);
            elseTag.onExecutionStart(context);
            elseTag.onExecutionEnd(context);
        }
    }
}
