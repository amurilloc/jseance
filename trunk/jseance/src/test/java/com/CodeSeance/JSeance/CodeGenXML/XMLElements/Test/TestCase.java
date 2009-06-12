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

import com.CodeSeance.JSeance.CodeGenXML.EntryPoints.Logger;
import com.CodeSeance.JSeance.CodeGenXML.ExecutionError;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TestCase implements Logger
{
    protected Hashtable<String, StringBuilder> xmlContent = new Hashtable<String, StringBuilder>();
    Hashtable<String, File> xmlFiles = new Hashtable<String, File>();
    protected Hashtable<String, File> ouputFiles = new Hashtable<String, File>();
    public StringBuilder template = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

    private List<String> errors = new ArrayList<String>();

    private final String errorLogFile =  "./target/jseance-errors.log";

    public void reset()
    {
        xmlContent = new Hashtable<String, StringBuilder>();
        xmlFiles = new Hashtable<String, File>();
        ouputFiles = new Hashtable<String, File>();
        template = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        errors = new ArrayList<String>();
    }

    public static final String TEMPLATE_HEADER_OPEN = "<Template xmlns:JSeance=\"http://www.codeseance.com/JSeance\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.codeseance.com/JSeance\">";
    public static final String TEMPLATE_HEADER_CLOSE = "</Template>";

    public static final String INCLUDE_HEADER_OPEN = "<Include xmlns:JSeance=\"http://www.codeseance.com/JSeance\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.codeseance.com/JSeance\">";
    public static final String INCLUDE_HEADER_CLOSE = "</Include>";

    public StringBuilder createXMLFile(String name)
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

    protected File createOutputFile(String name)
    {
        try
        {
            File file = File.createTempFile(name, ".txt");
            file.deleteOnExit();
            ouputFiles.put(name, file);
            return file;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Test Failed:" + this.getClass().toString(), e);
        }
    }

    protected void expectResult(String result)
    {
        expectResult(result, true, false);
    }

    public File persist()
    {
        try
        {
            // Write the XML files
            for (String fileName : xmlContent.keySet())
            {
                String contents = resolvePlaceholders(xmlContent.get(fileName).toString());
                writeStringToFile(contents, xmlFiles.get(fileName));
            }

            String templateContents = resolvePlaceholders(template.toString());
            return convertStringToTempFile("template", ".xml", templateContents);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }


    protected void expectResult(String result, boolean reset, boolean ignoreReadOnlyOuputFiles)
    {
        String outcome;
        try
        {
            File templateFile = persist();

            File parentPath = templateFile.getParentFile();
            com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime(errorLogFile, null, null, parentPath, parentPath, parentPath, ignoreReadOnlyOuputFiles, false);
            List<String> templateFileNames = new ArrayList<String>();
            templateFileNames.add(templateFile.getName());
            outcome = runtime.run(parentPath, templateFileNames, this);

            // Cleanup the dependencies file
            runtime.dependencyManager.cleanup();

            if (!result.equals(outcome))
            {
                throw new RuntimeException("Test Failed: Was expecting:[" + result + "] and obtained:[" + outcome + "]");
            }
        }
        finally
        {
            if (reset)
            {
                reset();
            }
        }

    }

    protected void expectError(ExecutionError error, boolean validIncludesDir, boolean validModelsDir, boolean validTargetDir, boolean validTemplatesDir, boolean ignoreReadOnlyOuputFiles, String invalidFile, boolean deleteTemplateFile)
    {
        try
        {
            File templateFile = persist();
            if (deleteTemplateFile)
            {
                boolean result = templateFile.delete();
                assert result;
            }

            File parentPath = templateFile.getParentFile();
            File incorrectDir = new File("YYZ:\\incorrectDir\\//\\");

            com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime(errorLogFile, null, null, validIncludesDir ? parentPath : incorrectDir, validModelsDir ? parentPath : incorrectDir, validTargetDir ? parentPath : incorrectDir, ignoreReadOnlyOuputFiles, false);

            List<String> templateFileNames = new ArrayList<String>();
            templateFileNames.add(templateFile.getName());
            runtime.run(validTemplatesDir ? parentPath : incorrectDir, templateFileNames, this);

            // Cleanup the dependencies file
            runtime.dependencyManager.cleanup();

            if (errors.size() == 0)
            {
                throw new RuntimeException("ExecutionError: No error message logged");
            }
            else if (!validIncludesDir || !validModelsDir || !validTargetDir)
            {
                if (errors.size() == 0 || !errors.get(0).contains(incorrectDir.getName()))
                {
                    throw new RuntimeException("ExecutionError: Message does not contain dir");
                }
            }
            else if (invalidFile != null)
            {

                if (!errors.get(0).contains(invalidFile))
                {
                    throw new RuntimeException("ExecutionError: Message does not contain file");
                }
            }
            else if (deleteTemplateFile)
            {
                if (errors.size() == 0 || !errors.get(0).contains(templateFile.getName()))
                {
                    throw new RuntimeException("ExecutionError: Message does not contain file");
                }
            }
            if (errors.size() == 0 || !errors.get(0).contains(error.getErrorCode()))
            {
                throw new RuntimeException("ExecutionError: Message does not contain correct error code");
            }
        }
        finally
        {
            reset();
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
        reset();
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

    public static File convertStringToTempFile(String prefix, String suffix, String fileContents) throws Exception
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

    public void infoMessage(String message)
    {
        System.out.println(message);
    }

    public void errorMessage(String message)
    {
        errors.add(message);
        System.out.println(message);
    }
}
