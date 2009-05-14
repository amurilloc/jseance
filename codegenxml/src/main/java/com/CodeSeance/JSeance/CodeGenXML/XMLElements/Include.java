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

import com.CodeSeance.JSeance.CodeGenXML.XMLAttribute;
import com.CodeSeance.JSeance.CodeGenXML.XMLLoader;
import com.CodeSeance.JSeance.CodeGenXML.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * Class for including other script files at runtime
 *
 * @author Andres Murillo
 * @version 1.0
 */
class Include extends HierarchicalNode
{
    public Include(Element element)
    {
        super(element);
    }

    @XMLAttribute
    String fileName;

    @Override
    public void onContextEnter(Context context)
    {
        context.LogInfoMessage(log, "Include", String.format("Loading file:[%s]", context.getParentPath() + File.pathSeparator + fileName));

        XMLLoader xmlLoader = XMLLoader.buildFromCodeTemplateSchema();
        Document document = xmlLoader.loadXML(context.getParentPath(), fileName);
        context.LogInfoMessage(log, "Include", "XMLSchema validated, processing children");

        LoadChildren(document.getDocumentElement().getChildNodes());

        // Set the new current path
        File file = new File(context.getParentPath(), fileName);
        File newParentPath = file.getParentFile();
        if (!context.getParentPath().equals(newParentPath))
        {
            context.LogInfoMessage(log, "Include", String.format("Included resources will be loaded relative to path:[%s]", newParentPath.toString()));
            context.setParentPath(newParentPath);
        }

        ExecuteChildren(context);
    }
}