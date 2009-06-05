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
import com.CodeSeance.JSeance.CodeGenXML.ContextManager;
import com.CodeSeance.JSeance.CodeGenXML.XMLLoader;
import com.CodeSeance.JSeance.CodeGenXML.DependencyTracking.TemplateDependencies;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * Top element of a CodeSeance Template, provides a loader to parse and execute a template
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class Template extends HierarchicalNode
{
    public Template(Element element)
    {
        super(element);
    }

    // Default buffer for text accumulation
    private StringBuffer buffer = new StringBuffer();


    @Override
    public void onContextEnter(Context context)
    {
        context.LogInfoMessage(log, "Template", "Processing children");

        // Sets the top text sink
        context.setTextSink(buffer);

        // Execute child nodes
        ExecuteChildren(context);
    }

    @Override
    public void onContextExit(Context context)
    {
        String text = buffer.toString();
        context.LogInfoMessage(log, "Template", String.format("Children produced:[%s]", text));

        Context parentContext = context.getParent();
        if (parentContext != null)
        {
            parentContext.writeText(text);
        }
    }

    public static String run(File templatesDir, File includesDir, File modelsDir, File targetDir, String fileName,
                             boolean ignoreReadOnlyOuputFiles, TemplateDependencies templateDependencies)
    {
        // Create a local logger for the static context
        Log log = com.CodeSeance.JSeance.CodeGenXML.Runtime.CreateLogger(Template.class);

        if (log.isInfoEnabled())
        {
            log.info(String.format("Loading Template:[%s]", templatesDir + File.separator + fileName));
        }

        // Load the default schema validator
        XMLLoader xmlLoader = XMLLoader.buildFromCodeTemplateSchema();

        // Loads the XML document
        Document document = xmlLoader.loadXML(templatesDir, fileName);

        // Load the object hierarchy from the XMLDocument
        Template template = new Template(document.getDocumentElement());

        if (log.isInfoEnabled())
        {
            log.info("XMLSchema validated");
        }

        // Create a new ContextManager
        ContextManager contextManager = new ContextManager(templatesDir, includesDir, modelsDir, targetDir,
                                                           ignoreReadOnlyOuputFiles, templateDependencies);

        try
        {
            // Enter and leave the context on the new template element
            template.onContextEnter(contextManager.getCurrentContext());
            template.onContextExit(contextManager.getCurrentContext());
        }
        finally
        {
            // dispose the context manager to release used resources
            contextManager.dispose();
        }

        return template.buffer.toString();
    }
}