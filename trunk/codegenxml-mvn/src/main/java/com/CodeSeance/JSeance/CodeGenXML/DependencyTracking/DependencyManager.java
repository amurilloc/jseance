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

package com.CodeSeance.JSeance.CodeGenXML.DependencyTracking;

import com.CodeSeance.JSeance.CodeGenXML.XMLLoader;
import org.apache.commons.logging.Log;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Hashtable;

/**
 * This class is responsible for loading and saving file dependencies. This is an optimization so template generation
 * can be skipped if all dependencies are up to date.
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class DependencyManager
{
    Log log = com.CodeSeance.JSeance.CodeGenXML.Runtime.CreateLogger(DependencyManager.class);

    public DependencyManager(File targetDir)
    {
        this.outputDirectory = targetDir;

        // Load the dependencies from disk
        dependencyFile = new File(targetDir, DEPENDENCY_FILENAME);
        if (dependencyFile.exists())
        {
            boolean deleteFile = false;
            try
            {
                XMLLoader xmLoader = XMLLoader.build(false);
                Document document = xmLoader.loadXML(targetDir, DEPENDENCY_FILENAME);
                Element rootNode = document.getDocumentElement();
                NodeList templateChildren = rootNode.getChildNodes();
                for (int i = 0; i < templateChildren.getLength(); i++)
                {
                    if (templateChildren.item(i) instanceof Element)
                    {
                        Element templateChild = (Element) templateChildren.item(i);

                        File templateFile = new File(templateChild.getAttribute("fileName"));
                        TemplateDependencies templateDependency = new TemplateDependencies(templateFile);
                        templateDependencies.put(dependencyFile.toString(), templateDependency);

                        NodeList dependencyNodes = templateChild.getChildNodes();
                        for (int j = 0; j < dependencyNodes.getLength(); j++)
                        {
                            if (dependencyNodes.item(j) instanceof Element)
                            {
                                Element dependencyChild = (Element) dependencyNodes.item(j);
                                String localName = dependencyChild.getLocalName();
                                File dependencyChildFile = new File(dependencyChild.getAttribute("fileName"));
                                if (localName.equals("Input"))
                                {
                                    templateDependency.addInputFile(dependencyChildFile);
                                }
                                else if (localName.equals("Output"))
                                {
                                    templateDependency.addOutputFile(dependencyChildFile);
                                }
                                else
                                {
                                    throw new RuntimeException(String.format("Unexpected dependency node:[%s] in file:[%s]", localName, dependencyFile));
                                }
                            }
                        }
                    }
                }

            }
            catch (Exception ex)
            {
                log.warn(String.format("Cannot parse dependency file:[%s], deleting. Cause:[%s]", dependencyFile, ex.getMessage()));
                deleteFile = true;
            }

            if (deleteFile)
            {
                if (!dependencyFile.delete())
                {
                    throw new RuntimeException(String.format("Cannot delete corrupt dependency file:[%s]", dependencyFile));
                }
            }
        }
    }

    private final File dependencyFile;

    private final String DEPENDENCY_FILENAME = ".jseance-dependencies.xml";
    private final File outputDirectory;

    public TemplateDependencies getTemplateDependencies(File templateFile)
    {
        String key = templateFile.toString();
        if (!templateDependencies.containsKey(key))
        {
            templateDependencies.put(key, new TemplateDependencies(templateFile));
        }
        return templateDependencies.get(key);
    }

    Hashtable<String, TemplateDependencies> templateDependencies = new Hashtable<String, TemplateDependencies>();

    public void cleanup()
    {
        if (dependencyFile.exists() && !dependencyFile.delete())
        {
            throw new RuntimeException(String.format("Cannot delete dependencies file:[%s]", dependencyFile));
        }
    }

    public void commit()
    {
        try
        {
            // Build the XML representation
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation domImplementation = documentBuilder.getDOMImplementation();
            Document document = domImplementation.createDocument(null, "JSeanceDependencies", null);
            Element rootNode = document.getDocumentElement();
            for (String fileName : templateDependencies.keySet())
            {
                Element templateChild = templateDependencies.get(fileName).serialize(document);
                rootNode.appendChild(templateChild);
            }

            // Write the XML Document
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(dependencyFile);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, streamResult);
        }
        catch (Exception ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException("Unexpected Exception while building dependencies document: " + ex.getClass(), ex);
        }
    }
}
