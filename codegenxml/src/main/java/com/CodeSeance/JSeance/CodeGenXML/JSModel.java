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

package com.CodeSeance.JSeance.CodeGenXML;

import org.mozilla.javascript.Scriptable;

public class JSModel implements Scriptable
{

    public JSModel DeepClone()
    {
        JSModel result = new JSModel();
        result.rootNode = this.rootNode;
        result.currentNode = this.currentNode;
        return result;
    }

    public org.mozilla.javascript.xml.XMLObject GetRootNode()
    {
        return rootNode;
    }

    public org.mozilla.javascript.xml.XMLObject GetCurrentNode()
    {
        return currentNode;
    }

    public void SetRootNode(org.mozilla.javascript.xml.XMLObject rootNode)
    {
        this.rootNode = rootNode;
    }

    public void SetCurrentNode(org.mozilla.javascript.xml.XMLObject currentNode)
    {
        this.currentNode = currentNode;
    }

    private org.mozilla.javascript.xml.XMLObject rootNode = null;
    private org.mozilla.javascript.xml.XMLObject currentNode = null;

    public String getClassName()
    {
        return "ModelClass";
    }

    public boolean has(String name, Scriptable start)
    {
        return (name.equals("rootNode") || name.equals("currentNode"));
    }

    public boolean has(int index, Scriptable start)
    {
        return false;
    }

    public Object get(String name, Scriptable start)
    {
        if (name.equals("rootNode"))
        {
            return rootNode;
        }
        else if (name.equals("currentNode"))
        {
            return currentNode;
        }
        else
        {
            return NOT_FOUND;
        }
    }

    public Object get(int index, Scriptable start)
    {
        return null;
    }

    public void put(String name, Scriptable start, Object value)
    {
        if (name.equals("currentNode"))
        {
            if (!(value instanceof org.mozilla.javascript.xml.XMLObject))
            {
                throw new RuntimeException("Expected type not found: org.mozilla.javascript.xml.XMLObject");
            }
            currentNode = (org.mozilla.javascript.xml.XMLObject) value;
        }
    }

    public void put(int index, Scriptable start, Object value)
    {
    }

    public void delete(String id)
    {
    }

    public void delete(int index)
    {
    }

    public Scriptable getPrototype()
    {
        return prototype;
    }

    public void setPrototype(Scriptable prototype)
    {
        this.prototype = prototype;
    }

    public Scriptable getParentScope()
    {
        return parent;
    }

    public void setParentScope(Scriptable parent)
    {
        this.parent = parent;
    }

    public Object[] getIds()
    {
        return new Object[0];
    }

    public Object getDefaultValue(Class typeHint)
    {
        return "[object Model]";
    }

    public boolean hasInstance(Scriptable value)
    {
        Scriptable proto = value.getPrototype();
        while (proto != null)
        {
            if (proto.equals(this))
            {
                return true;
            }
            proto = proto.getPrototype();
        }

        return false;
    }

    private Scriptable prototype, parent;
}