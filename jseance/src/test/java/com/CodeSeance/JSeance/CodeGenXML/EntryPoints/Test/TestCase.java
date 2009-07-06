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

package com.CodeSeance.JSeance.CodeGenXML.EntryPoints.Test;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class TestCase
{
    private void createModelFile() throws IOException
    {
        StringBuilder model = new StringBuilder();
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");
        saveXMLFile(model.toString(), modelsDir, "model.xml");
    }

    private void createTemplateFile()  throws IOException
    {
        StringBuilder template = new StringBuilder();
        template.append(com.CodeSeance.JSeance.CodeGenXML.XMLElements.Test.TestCase.TEMPLATE_HEADER_OPEN);
        template.append(" <FileOutput fileName=\"out.txt\">");
        template.append("  <Model fileName=\"model.xml\"/>");
        template.append("  <Include fileName=\"include.xml\"/>");
        template.append(" </FileOutput>");
        template.append(com.CodeSeance.JSeance.CodeGenXML.XMLElements.Test.TestCase.TEMPLATE_HEADER_CLOSE);
        saveXMLFile(template.toString(), templatesDir, "template.xml");
    }

    private void createIncludeFile()  throws IOException
    {
        StringBuilder include = new StringBuilder();
        include.append(com.CodeSeance.JSeance.CodeGenXML.XMLElements.Test.TestCase.INCLUDE_HEADER_OPEN);
        include.append(" <Template>");
        include.append("  <Text>@JavaScript{Models['default'].currentNode.A.@val;}@</Text>");
        include.append(" </Template>");
        include.append(com.CodeSeance.JSeance.CodeGenXML.XMLElements.Test.TestCase.INCLUDE_HEADER_CLOSE);
        saveXMLFile(include.toString(), includesDir, "include.xml");
    }

    private void saveXMLFile(String content, File dir, String fileName) throws IOException
    {
        File file = new File(dir, fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bufferedWriter.write(content);
        bufferedWriter.close();
        file.deleteOnExit();
    }

    protected File includesDir = null;
    protected File modelsDir = null;
    protected File targetDir = null;
    protected File templatesDir = null;

    protected File getInfoLogFile()
    {
        File result = new File(targetDir, "jseance-errors.log");
        result.deleteOnExit();
        return result;
    }

    protected File createStandardLayout() throws IOException
    {
        return createDirStructure("includes", "models", "templates", "target");
    }

    private File createDirStructure(String includesDirName, String modelsDirName, String templatesDirName, String targetDirName) throws IOException
    {
        File rootDir = createTempDirectory();
        includesDir = createTempSubDir(rootDir, includesDirName);
        createIncludeFile();

        modelsDir = createTempSubDir(rootDir, modelsDirName);
        createModelFile();

        templatesDir = createTempSubDir(rootDir, templatesDirName);
        createTemplateFile();

        targetDir = createTempSubDir(rootDir, targetDirName);

        return rootDir;
    }

    private File createTempSubDir(File parentDir, String name)
    {
        File newDir = new File(parentDir, name);
        boolean result = newDir.mkdir();
        assert result;
        newDir.deleteOnExit();
        return newDir;
    }

    private File createTempDirectory() throws IOException
    {
        File newFile = File.createTempFile("EntryPointTest", "dir");
        newFile.deleteOnExit();
        boolean result = newFile.delete();
        assert result;
        result = newFile.mkdir();
        assert result;
        return newFile;
    }
}
