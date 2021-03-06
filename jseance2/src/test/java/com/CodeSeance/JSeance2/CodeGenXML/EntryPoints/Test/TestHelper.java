/*
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
 */

package com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.Test;

import com.CodeSeance.JSeance2.CodeGenXML.XMLElements.Test.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class TestHelper
{
    public void registerDisposableFile(File item)
    {
        disposableFiles.push(item);
    }

    Stack<File> disposableFiles = new Stack<File>();

    public void disposeFiles() throws IOException
    {
        while (!disposableFiles.isEmpty())
        {
            File file = disposableFiles.pop();
            if (file.exists())
            {
                boolean result;
                if (file.isDirectory())
                {
                    result = deleteDirectoryRecursive(file);
                }
                else
                {
                    result = file.delete();
                }
                if (!result)
                {
                    throw new RuntimeException(String.format("Error deleting file:[%s]", file.getCanonicalPath()));
                }
            }
        }
    }

    public void validateSucess(File outputFile, File errorLogFile, File infoLogFile, File debugLogFile)
    {
        String result = TestCase.convertFileToString(outputFile, "UTF-8");

        if (!"A".equals(result))
        {
            throw new RuntimeException(String.format("Unexpected test result:[%s], was expecting:[A]", result));
        }
        if (errorLogFile != null)
        {
            if (errorLogFile.exists())
            {
                errorLogFile.deleteOnExit();
            }
            else
            {
                throw new RuntimeException("Missing Error Log File");
            }
        }

        if (infoLogFile != null)
        {
            if (infoLogFile.exists())
            {
                infoLogFile.deleteOnExit();
            }
            else
            {
                throw new RuntimeException("Missing Info Log File");
            }
        }

        if (debugLogFile != null)
        {
            if (debugLogFile.exists())
            {
                debugLogFile.deleteOnExit();
            }
            else
            {
                throw new RuntimeException("Missing Debug Log File");
            }
        }
    }

    protected File createStandardLayout(File sourcesDir, File targetDir) throws IOException
    {
        if (!sourcesDir.exists())
        {
            boolean result = sourcesDir.mkdirs();
            assert result;
            registerDisposableFile(sourcesDir);
        }

        return createDirStructure(sourcesDir, "includes", "models", "templates", targetDir);
    }

    private File createDirStructure(File sourcesDir, String includesDirName, String modelsDirName, String templatesDirName, File targetDir) throws IOException
    {
        File includesDir = createTempSubDir(sourcesDir, includesDirName);
        createIncludeFile(includesDir);

        File modelsDir = createTempSubDir(sourcesDir, modelsDirName);
        createModelFile(modelsDir);

        File templatesDir = createTempSubDir(sourcesDir, templatesDirName);
        createTemplateFile(templatesDir);

        if (!targetDir.exists())
        {
            boolean result = targetDir.mkdirs();
            assert result;
            registerDisposableFile(targetDir);
        }

        File outputFile = new File(targetDir, "out.txt");
        registerDisposableFile(outputFile);
        return outputFile;
    }

    public boolean deleteDirectoryRecursive(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectoryRecursive(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    return( path.delete() );
  }

    private File createTempSubDir(File parentDir, String name)
    {
        File newDir = new File(parentDir, name);
        if (!newDir.exists())
        {
            boolean result = newDir.mkdir();
            assert result;
        }
        registerDisposableFile(newDir);
        return newDir;
    }

    public File createTempDirectory() throws IOException
    {
        File newFile = File.createTempFile("EntryPointTest", "dir");
        boolean result = newFile.delete();
        assert result;
        result = newFile.mkdir();
        assert result;
        registerDisposableFile(newFile);
        return newFile;
    }

    private void createModelFile(File modelsDir) throws IOException
    {
        StringBuilder model = new StringBuilder();
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");
        saveXMLFile(model.toString(), modelsDir, "model.xml");
    }

    private void createTemplateFile(File templatesDir) throws IOException
    {
        StringBuilder template = new StringBuilder();
        template.append("!Output(\"out.txt\")!");
        template.append("!XMLModel(\"model.xml\")!");
        template.append("!Include(\"include.jseance\")!");
        template.append("!End!");
        saveTemplateFile(template.toString(), templatesDir, "template.jseance");
    }

    private void createIncludeFile(File includesDir) throws IOException
    {
        StringBuilder include = new StringBuilder();
        include.append("!Eval(Models['default'].currentNode.A.@val)!");
        saveTemplateFile(include.toString(), includesDir, "include.jseance");
    }

    private void saveTemplateFile(String content, File dir, String fileName) throws IOException
    {
        File file = new File(dir, fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(content);
        bufferedWriter.close();
        registerDisposableFile(file);
    }
    private void saveXMLFile(String content, File dir, String fileName) throws IOException
    {
        File file = new File(dir, fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bufferedWriter.write(content);
        bufferedWriter.close();
        registerDisposableFile(file);
    }
}
