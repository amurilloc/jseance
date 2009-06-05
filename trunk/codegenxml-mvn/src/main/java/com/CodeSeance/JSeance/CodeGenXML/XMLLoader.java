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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * This is a helper class for loading and validating XML files. It wraps the complexity of the Java XML API into a
 * single class. To obtain an instance, use any of the static builder methods:
 * <pre>
 *    XMLLoader loader = XMLLoader.BuildFromCodeTemplateSchema();
 *    XMLLoader loader = XMLLoader.BuildFromXSDFileName(parentPath, fileName);
 *    XMLLoader loader = XMLLoader.Build(true);
 * </pre>
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class XMLLoader
{
    /*
    * Builds an XML Builder loading the XSD embedded into the project
     */
    public static XMLLoader buildFromCodeTemplateSchema()
    {
        InputStream xsdFile = XMLLoader.class.getClassLoader().getResourceAsStream(SCHEMA_FILE);
        try
        {
            return new XMLLoader(xsdFile);
        }
        catch (ParserConfigurationException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            Log log = LogFactory.getLog(XMLLoader.class.getName().replace("com.CodeSeance.JSeance.", ""));
            log.fatal("Cannot create XMLLoader instance for parsing a Template XML file");
            throw new RuntimeException(String.format("ParserConfigurationException:[%s]", ex.getMessage()), ex);
        }
    }

    /*
    * Builds an XMLLoader with optional validation. if validating, the xsd file location needs to be
    * specified in the xml file
    */
    public static XMLLoader build(boolean validate)
    {
        try
        {
            return new XMLLoader(validate);
        }
        catch (ParserConfigurationException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            Log log = LogFactory.getLog(XMLLoader.class.getName().replace("com.CodeSeance.JSeance.", ""));
            log.fatal("Cannot create XMLLoader instance from XMLLoader build(boolean validate)");
            throw new RuntimeException(String.format("ParserConfigurationException:[%s]", ex.getMessage()), ex);
        }
    }

    /*
    * Builds an XMLLoader with XSD validation from the file specified
    */
    public static XMLLoader buildFromXSDFileName(File parentPath, String xsdFileName)
    {
        try
        {
            return new XMLLoader(parentPath, xsdFileName);
        }
        catch (ParserConfigurationException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            Log log = LogFactory.getLog(XMLLoader.class.getName().replace("com.CodeSeance.JSeance.", ""));
            log.fatal("Cannot create XMLLoader instance from XMLLoader buildFromXSDFileName(File parentPath, String xsdFileName)");
            throw new RuntimeException(String.format("ParserConfigurationException:[%s]", ex.getMessage()), ex);
        }

    }

    // The name of the embedded xsd for COdeTemplate
    public static final String SCHEMA_FILE = "JSeance1.0.xsd";

    // The instance logger
    private final Log log = LogFactory.getLog(XMLLoader.class.getName().replace("com.CodeSeance.JSeance.", ""));

    /*
   * Provate constructor with no specific schema file and validation option
    */
    private XMLLoader(boolean validate) throws ParserConfigurationException
    {
        createDocumentBuilder(validate, null);
    }

    /*
    * Private constructor with optional validation and InputStream xsdFile
    */
    private XMLLoader(InputStream xsdFile) throws ParserConfigurationException
    {
        createDocumentBuilder(true, xsdFile);
    }

    /*
    * Private constructor with optional validation and xsd File
    */
    private XMLLoader(File parentPath, String xsdFileName) throws ParserConfigurationException
    {
        File xsdFile = new File(parentPath, xsdFileName);
        createDocumentBuilder(true, xsdFile);
    }

    /*
    * Wrapper for JAXP API DocumentBuilder creation, initializes required members
    */
    private void createDocumentBuilder(boolean validate, Object xsdFile) throws ParserConfigurationException
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setIgnoringComments(true);
        docFactory.setNamespaceAware(true);
        docFactory.setValidating(validate);
        if (validate)
        {
            docFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            if (xsdFile != null)
            {
                docFactory.setAttribute(JAXP_SCHEMA_SOURCE, xsdFile);
            }
        }

        documentBuilder = docFactory.newDocumentBuilder();

        ErrorHandler errorHandler = new ErrorHandler();
        documentBuilder.setErrorHandler(errorHandler);
    }

    /*
     * Loads the specified XML stream and returns the document object
     */
    public Document loadXML(File parentPath, String fileName)
    {
        try
        {
            File file = new File(parentPath, fileName);
            InputStream inputStream = new FileInputStream(file);
            return loadXML(inputStream);
        }
        catch (FileNotFoundException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException("Unexpected Exception: " + ex.getClass(), ex);
        }
    }

    /*
    * Loads the specified XML file and returns the document object
    */
    public Document loadXML(InputStream inputStream)
    {
        try
        {
            return documentBuilder.parse(inputStream);
        }
        catch (IOException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException("Unexpected Exception: " + ex.getClass(), ex);
        }
        catch (SAXException ex)
        {
            // Wrap Exception with RuntimeException since caller won't be able to handle it
            throw new RuntimeException("Unexpected Exception: " + ex.getClass(), ex);
        }
    }

    // The instance document builder
    private DocumentBuilder documentBuilder = null;

    // Static declarations for XML validation
    static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    // the name and path of the XSD schema for XML Validation
    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    // Internal class for error handling
    private class ErrorHandler extends DefaultHandler
    {
        private String FormatMessage(SAXParseException ex)
        {
            return String.format("XML Parsing Error - LineNumber:[%d], ColumnNumber:[%d], Message:[%s]", ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
        }

        public void warning(SAXParseException ex)
        {
            log.warn(FormatMessage(ex));
        }

        public void error(SAXParseException ex) throws SAXParseException
        {
            log.error(FormatMessage(ex));
            throw ex;
        }

        public void fatalError(SAXParseException ex) throws SAXParseException
        {
            log.fatal(FormatMessage(ex));
            throw ex;
        }
    }
}
