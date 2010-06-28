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

package com.CodeSeance.JSeance2.CodeGenXML.XMLElements.Test;

import com.CodeSeance.JSeance2.CodeGenXML.ExecutionError;
import org.testng.annotations.Test;

import java.io.File;

public class OutputTest extends TestCase
{
    @Test
    public void fileOutputTest_Basic()
    {
        createOutputFile("FILE");

        template.append("!Output(\"{FILE}\")!");
        template.append("Test");
        template.append("!End!");
        expectResult("", false, false);

        expectFileOutput("FILE", "Test");
    }

    @Test
    public void fileOutputTest_AutoCreateParentDirs()
    {
        CleanupAutoCreateParentDirs();

        try
        {
            File outputFile = new File("A" + File.separator + "B" + File.separator + "C" + File.separator + "Output.txt");
            template.append(String.format("!Output(\"%s\")!", org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(outputFile.toString())));
            template.append("Test");
            template.append("!End!");
            expectResult("", false, false);

            File fullPath = new File(System.getProperty("java.io.tmpdir") + File.separator + outputFile.toString());
            String outcome = convertFileToString(fullPath);

            if (!"Test".equals(outcome))
            {
                throw new RuntimeException("Test Failed: Was expecting:[Test] and obtained:[" + outcome + "]");
            }
            reset();
        }
        finally
        {
            CleanupAutoCreateParentDirs();
        }
    }

    @Test
    public void fileOutputTest_AutoCreateParentDirsError()
    {
        CleanupAutoCreateParentDirs();
        ExecutionError.simulate_CANNOT_CREATE_PARENT_DIRS = true;

        File outputFile = new File("A" + File.separator + "B" + File.separator + "C" + File.separator + "Output.txt");
        template.append(String.format("!Output(\"%s\")!", org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(outputFile.toString())));
        template.append("Test");
        template.append("!End!");
        expectError(ExecutionError.CANNOT_WRITE_TARGET_FILE, true, true, true, false, null, false);
        CleanupAutoCreateParentDirs();
    }

    private void CleanupAutoCreateParentDirs()
    {
        String tempFileDir = System.getProperty("java.io.tmpdir");
        // Cleanup if needed
        File cleanupDir = new File(tempFileDir + File.separator + "A" + File.separator + "B" + File.separator + "C" + File.separator + "Output.txt");
        if (cleanupDir.exists())
        {
            cleanupDir.delete();
        }

        cleanupDir = new File(tempFileDir + File.separator + "A" + File.separator + "B" + File.separator + "C");
        if (cleanupDir.exists())
        {
            cleanupDir.delete();
        }

        cleanupDir = new File(tempFileDir + File.separator + "A" + File.separator + "B");
        if (cleanupDir.exists())
        {
            cleanupDir.delete();
        }

        cleanupDir = new File(tempFileDir + File.separator + "A");
        if (cleanupDir.exists())
        {
            cleanupDir.delete();
        }
    }

    @Test
    public void fileOutputTest_UTF16()
    {
        createOutputFile("FILE");

        template.append("!Output(\"{FILE}\",\"UTF-16\")!");
        template.append("Test");
        template.append("!End!");
        expectResult("", false, false);

        expectFileOutput("FILE", "þÿ\u0000T\u0000e\u0000s\u0000t");
    }

    @Test
    public void fileOutputTest_CannotWriteTargetFile()
    {
        String simulatedFile = "SimulatedFile.txt";

        template.append("!Output(\"");
        template.append(simulatedFile);
        template.append("\")!");
        template.append("Test");
        template.append("!End!");

        ExecutionError.simulate_CANNOT_WRITE_TARGET_FILE = true;
        expectError(ExecutionError.CANNOT_WRITE_TARGET_FILE, true, true, true, false, simulatedFile, false);
    }

    @Test
    public void fileOutputTest_TargetFileReadonly()
    {
        File outputFile = createOutputFile("FILE");
        boolean result = outputFile.setReadOnly();
        assert result;

        template.append("!Output(\"{FILE}\")!");
        template.append("Test");
        template.append("!End!");

        expectResult("", true, true);
    }

    @Test
    public void fileOutputTest_TargetFileReadonlyFail()
    {
        File outputFile = createOutputFile("FILE");
        boolean result = outputFile.setReadOnly();
        assert result;

        template.append("!Output(\"{FILE}\")!");
        template.append("Test");
        template.append("!End!");

        expectError(ExecutionError.TARGET_FILE_READONLY, true, true, true, false, outputFile.getName(), false);
    }

    @Test
    public void fileOutputTest_XML()
    {
        createOutputFile("FILE");

        template.append("!Output(\"{FILE}\", \"UTF-8\", true)!");
        template.append("<Root><Node attribute=\"!Eval(EscapeXMLAttribute('\"<&\\u000A\\u000D\\u0009'))!\">!Eval(EscapeXMLValue('<>&'))!</Node></Root>");
        template.append("!End!");

        expectResult("", false, false);

        expectFileOutput("FILE", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<Root><Node attribute=\"&quot;&lt;&amp;&#xA;&#xD;&#x9;\">&lt;&gt;&amp;</Node></Root>");
    }

    @Test
    public void fileOutputTest_HTML()
    {
        createOutputFile("FILE");

        template.append("!Output(\"{FILE}\", \"UTF-8\", true)!");
        template.append("<Root><Node attribute=\"!Eval(EscapeHTML('<conversion>'))!\"></Node></Root>");
        template.append("!End!");

        expectResult("", false, false);

        expectFileOutput("FILE", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<Root><Node attribute=\"&lt;conversion&gt;\"></Node></Root>");
    }
}
