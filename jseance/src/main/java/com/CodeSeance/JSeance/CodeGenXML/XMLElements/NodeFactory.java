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

import org.w3c.dom.Element;

/**
 * This is a factory class to create XML Node types, is more efficient than scanning all available classes and creating
 * new instances from discovered types
 *
 * @author Andres Murillo
 * @version 1.0
 */
class NodeFactory
{
    private NodeFactory()
    {
    }

    /**
     * SingletonHolder is loaded on the first execution of Runtime.getInstance() or the first access to
     * SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder
    {
        private final static NodeFactory INSTANCE = new NodeFactory();
    }

    public static NodeFactory getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    public Node createNode(Element element)
    {
        String localName = element.getLocalName();
        if (localName.equals("Template"))
        {
            return new Template(element);
        }
        else if (localName.equals("Model"))
        {
            return new Model(element);
        }
        else if (localName.equals("Definition"))
        {
            return new Definition(element);
        }
        else if (localName.equals("FileOutput"))
        {
            return new FileOutput(element);
        }
        else if (localName.equals("Include"))
        {
            return new Include(element);
        }
        else if (localName.equals("RequiredDefine"))
        {
            return new IncludeRequiredDefine(element);
        }
        else if (localName.equals("OutputIterator"))
        {
            return new OutputIterator(element);
        }
        else if (localName.equals("Switch"))
        {
            return new Switch(element);
        }
        else if (localName.equals("Conditional"))
        {
            return new Conditional(element);
        }
        else if (localName.equals("JavaScript"))
        {
            return new JavaScript(element);
        }
        else if (localName.equals("Text"))
        {
            return new Text(element);
        }
        else if (localName.equals("Case"))
        {
            return new SwitchCase(element);
        }
        else if (localName.equals("Default"))
        {
            return new SwitchDefault(element);
        }
        else if (localName.equals("If"))
        {
            return new ConditionalIf(element);
        }
        else if (localName.equals("ElseIf"))
        {
            return new ConditionalElseIf(element);
        }
        else if (localName.equals("Else"))
        {
            return new ConditionalElse(element);
        }
        else
        {
            throw new RuntimeException("Unknown XML Node Type:" + localName);
        }
    }
}
