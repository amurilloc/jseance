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

import com.CodeSeance.JSeance.CodeGenXML.*;
import org.mozilla.javascript.xml.XMLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * Class for loading an XML model into the parent context
 *
 * @author Andres Murillo
  * @version 1.0
 */
class Model extends Node
{

    public Model(Element element)
    {
        super(element);
    }

    @XMLAttribute
    String fileName;

    @XMLAttribute
    String name;

    @XMLAttribute
    String e4XPath;

    @XMLAttribute
    boolean validate;

    @XMLAttribute
    String xsdFileName;

    @Override
    public void onContextEnter(Context context)
    {
        context.LogInfoMessage(log, "Model", String.format("Loading model: fileName:[%s], name:[%s], e4XPath:[%s], validate:[%s], xsdFileName[%s]", fileName, name, e4XPath, validate, xsdFileName));

        File modelsDir = context.getManager().modelsDir;

        File modelFile = new File(modelsDir, fileName);
        context.LogInfoMessage(log, "Include", String.format("Loading model fileName:[%s], name:[%s], e4XPath:[%s], validate:[%s], xsdFileName[%s]\", fileName, name, e4XPath, validate, xsdFileName",  fileName, name, e4XPath, validate, xsdFileName));

        if (!modelsDir.exists())
        {
            throw new RuntimeException(ExecutionError.INVALID_MODELS_DIR.getMessage(modelsDir));
        }

        if (!modelFile.canRead())
        {
            throw new RuntimeException(String.format(ExecutionError.INVALID_MODEL_FILE.getMessage(modelFile)));
        }

        // Load the XML File
        XMLLoader xmlLoader;

        if ("".equals(xsdFileName) || xsdFileName == null || !validate)
        {
            xmlLoader = XMLLoader.build(validate);
        }
        else
        {
            xmlLoader = XMLLoader.buildFromXSDFileName(context.getManager().modelsDir, xsdFileName);
        }
        Document xmlDoc = xmlLoader.loadXML(context.getManager().modelsDir, fileName);

        // Add the dependency to the file
        context.getManager().templateDependencies.addInputFile(new File(context.getManager().modelsDir, fileName));

        XMLObject jsXML = context.getManager().createXMLObject(xmlDoc);

        // Evaluate the path if required
        Object jsCurrentNodeObj = context.getManager().evaluateE4XPath(jsXML, e4XPath);
        if (!(jsCurrentNodeObj instanceof XMLObject))
        {
            throw new RuntimeException(ExecutionError.INVALID_MODEL_E4X_EXPRESSION.getMessage(e4XPath, jsCurrentNodeObj.getClass()));
        }
        XMLObject jsCurrentNode = (XMLObject) jsCurrentNodeObj;

        // Create and add the new model
        JSModel model = new JSModel();
        model.SetRootNode(jsXML);
        model.SetCurrentNode(jsCurrentNode);
        context.getParent().addModel(name, model);
    }
}