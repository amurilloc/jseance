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
 Date: Jun 15, 2010
 Time: 6:02:32 PM
 To change this template use File | Settings | File Templates.
 */
class NodeFactory
{
    private static class SingletonHolder
    {
        private final static NodeFactory INSTANCE = new NodeFactory();
    }

    public static NodeFactory getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    public Node createNode(String tag, String arguments, Template.Position position)
    {
        tag = tag.toLowerCase();
        if (tag.equals("case"))
        {
            return new Case(arguments, position);
        }
        else if (tag.equals("code"))
        {
            return new Code();
        }
        else if (tag.equals("default"))
        {
            return new Default();
        }
        else if (tag.equals("else"))
        {
            return new Else();
        }
        else if (tag.equals("elseif"))
        {
            return new ElseIf(arguments, position);
        }
        else if (tag.equals("end"))
        {
            return new End();
        }
        else if (tag.equals("eval"))
        {
            return new Eval(arguments, position);
        }
        else if (tag.equals("for"))
        {
            return new For(arguments, position);
        }
        else if (tag.equals("if"))
        {
            return new If(arguments, position);
        }
        else if (tag.equals("ifempty"))
        {
            return new IfEmpty();
        }
        else if (tag.equals("include"))
        {
            return new Include(arguments, position);
        }
        else if (tag.equals("output"))
        {
            return new Output(arguments, position);
        }
        else if (tag.equals("switch"))
        {
            return new Switch(arguments, position);
        }
        else if (tag.equals("text"))
        {
            return new Text(arguments);
        }
        else if (tag.equals("xmlmodel"))
        {
            return new XMLModel(arguments, position);
        }
        else
        {
            // this should never happen since the XML Document is validated by the regular expression
            assert false : tag;
            return null;
        }

    }

    public Class getNodeClass(String tag)
    {
        tag = tag.toLowerCase();
        if (tag.equals("case"))
        {
            return Case.class;
        }
        else if (tag.equals("code"))
        {
            return Code.class;
        }
        else if (tag.equals("default"))
        {
            return Default.class;
        }
        else if (tag.equals("else"))
        {
            return Else.class;
        }
        else if (tag.equals("elseif"))
        {
            return ElseIf.class;
        }
        else if (tag.equals("end"))
        {
            return End.class;
        }
        else if (tag.equals("eval"))
        {
            return Eval.class;
        }
        else if (tag.equals("for"))
        {
            return For.class;
        }
        else if (tag.equals("if"))
        {
            return If.class;
        }
        else if (tag.equals("ifempty"))
        {
            return IfEmpty.class;
        }
        else if (tag.equals("include"))
        {
            return Include.class;
        }
        else if (tag.equals("output"))
        {
            return Output.class;
        }
        else if (tag.equals("switch"))
        {
            return Switch.class;
        }
        else if (tag.equals("text"))
        {
            return Text.class;
        }
        else if (tag.equals("xmlmodel"))
        {
            return XMLModel.class;
        }
        else
        {
            // this should never happen since the XML Document is validated by the regular expression
            assert false : tag;
            return null;
        }

    }
}
