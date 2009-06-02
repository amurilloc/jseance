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

package com.CodeSeance.JSeance.CodeGenXML.XMLElements.Test;

import java.io.*;
import java.util.Hashtable;

public class TestCase
{
    protected Hashtable<String, StringBuilder> xmlContent = new Hashtable<String, StringBuilder>();
    Hashtable<String, File> xmlFiles = new Hashtable<String, File>();
    protected Hashtable<String, File> ouputFiles = new Hashtable<String, File>();
    protected StringBuilder template = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

    protected void reset()
    {
        xmlContent = new Hashtable<String, StringBuilder>();
        xmlFiles = new Hashtable<String, File>();
        ouputFiles = new Hashtable<String, File>();
        template = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    public static final String TEMPLATE_HEADER_OPEN = "<Template xmlns:JSeance=\"http://www.codeseance.com/JSeance\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.codeseance.com/JSeance\">";
    public static final String TEMPLATE_HEADER_CLOSE = "</Template>";

    public static final String INCLUDE_HEADER_OPEN = "<Include xmlns:JSeance=\"http://www.codeseance.com/JSeance\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.codeseance.com/JSeance\">";
    public static final String INCLUDE_HEADER_CLOSE = "</Include>";

    protected StringBuilder createXMLFile(String name)
    {
        StringBuilder model = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlContent.put(name, model);
        try
        {
            File file = File.createTempFile(name, ".xml");
            file.deleteOnExit();
            xmlFiles.put(name, file);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Test Failed:" + this.getClass().toString(), e);
        }
        return model;
    }

    protected void createOutputFile(String name)
    {
        try
        {
            File file = File.createTempFile(name, ".txt");
            file.deleteOnExit();
            ouputFiles.put(name, file);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Test Failed:" + this.getClass().toString(), e);
        }
    }

    protected void expectResult(String result)
    {
        expectResult(result, true);
    }

    protected void expectResult(String result, boolean reset)
    {
        String outcome;
        try
        {
            // Write the XML files
            for (String fileName : xmlContent.keySet())
            {
                String contents = resolvePlaceholders(xmlContent.get(fileName).toString());
                writeStringToFile(contents, xmlFiles.get(fileName));
            }

            String templateContents = resolvePlaceholders(template.toString());
            File templateFile = convertStringToTempFile("template", ".xml", templateContents);

            String projectBasedir = System.getProperty("PROJECT_BASEDIR");
            System.out.println("HERE:" + projectBasedir);

            String parentPath = templateFile.getParentFile().toString();
            String[] args = {"-consoleDebugLog",
                             "-templatesDir", parentPath,
                             "-modelsDir", parentPath,
                             "-targetDir", parentPath,
                             "-errorLogFile", "target/jseance-errors.log",
                             "-infoLogFile", "target/jseance-info.log",
                             "-debugLogFile", "target/jseance-debug.log",
                             templateFile.getName()
                            };
            com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime();
            outcome = runtime.run(args);

            //outcome = com.CodeSeance.JSeance.CodeGenXML.Runtime.run(templateFile.getParentFile(), templateFile.getName());

            if (!result.equals(outcome))
            {
                throw new RuntimeException("Test Failed: Was expecting:[" + result + "] and obtained:[" + outcome + "]");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Test Failed:" + this.getClass().toString(), e);

        }
        finally
        {
            if (reset)
            {
                reset();
            }
        }

    }

    protected void expectFileOutput(String filePlaceholder, String result)
    {
        File file = ouputFiles.get(filePlaceholder);
        String outcome = convertFileToString(file);

        if (!result.equals(outcome))
        {
            throw new RuntimeException("Test Failed: Was expecting:[" + result + "] and obtained:[" + outcome + "]");
        }
    }

    private String resolvePlaceholders(String contents)
    {
        for (String replacementName : xmlFiles.keySet())
        {
            contents = contents.replace("URL{" + replacementName + "}", xmlFiles.get(replacementName).toURI().toString());
            contents = contents.replace("{" + replacementName + "}", xmlFiles.get(replacementName).getName());
        }

        for (String replacementName : ouputFiles.keySet())
        {
            contents = contents.replace("URL{" + replacementName + "}", ouputFiles.get(replacementName).toURI().toString());
            contents = contents.replace("{" + replacementName + "}", ouputFiles.get(replacementName).getName());
        }
        return contents;
    }

    private void writeStringToFile(String fileContents, File file) throws Exception
    {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(fileContents);
        bufferedWriter.close();
    }

    private File convertStringToTempFile(String prefix, String suffix, String fileContents) throws Exception
    {
        File resultFile = File.createTempFile(prefix, suffix);
        resultFile.deleteOnExit();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resultFile));
        bufferedWriter.write(fileContents);
        bufferedWriter.close();
        return resultFile;
    }

    private String convertFileToString(File file)
    {
        StringBuffer stringBuffer;
        try
        {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            stringBuffer = new StringBuffer(1000);
            char[] buffer = new char[1024];
            int length;
            while ((length = bufferedReader.read(buffer)) > 0)
            {
                stringBuffer.append(buffer, 0, length);
            }
            bufferedReader.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Test Failed", e);
        }

        return stringBuffer.toString();
    }
}
