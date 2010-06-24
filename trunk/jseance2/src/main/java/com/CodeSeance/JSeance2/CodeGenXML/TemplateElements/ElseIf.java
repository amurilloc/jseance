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

/**
 Created by IntelliJ IDEA.
 User: amurillo
 Date: Jun 17, 2010
 Time: 9:57:01 AM
 To change this template use File | Settings | File Templates.
 */
class ElseIf extends HierarchicalNode
{
    public ElseIf(String arguments, Template.Position position)
    {
        super(arguments, position);
    }

    @TagParameter
    boolean condition;

    @Override
    public void loadChildren(Template template, Node parent)
    {
        // Clear the children, this is needed to avoid double addition if the tag is within a loop
        children.clear();

        if (parent == null || !parent.getClass().equals(If.class))
        {
            template.throwError("ElseIf statement needs to be a direct child of a If statement");
        }

        Class nextNodeClass = template.peekNodeClass();
        while (!nextNodeClass.equals(ElseIf.class) && !nextNodeClass.equals(Else.class) && !nextNodeClass.equals(End.class))
        {
            children.add(template.parseNode(this));
            nextNodeClass = template.peekNodeClass();
        }
    }
}
