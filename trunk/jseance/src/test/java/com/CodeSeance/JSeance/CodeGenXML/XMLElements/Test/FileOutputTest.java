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

import com.CodeSeance.JSeance.CodeGenXML.ExecutionError;
import org.testng.annotations.Test;

import java.io.File;

public class FileOutputTest extends TestCase
{
    @Test
    public void fileOutputTest_Basic()
    {
        createOutputFile("FILE");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <FileOutput fileName=\"{FILE}\">");
        template.append("  <Text>Test</Text>");
        template.append(" </FileOutput>");
        template.append(TEMPLATE_HEADER_CLOSE);
        expectResult("", false, false);

        expectFileOutput("FILE", "Test");
    }

    @Test
    public void fileOutputTest_NoAppend()
    {
        createOutputFile("FILE");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <FileOutput fileName=\"{FILE}\">");
        template.append("  <Text>A.</Text>");
        template.append(" </FileOutput>");
        template.append(" <FileOutput fileName=\"{FILE}\">");
        template.append("  <Text>B</Text>");
        template.append(" </FileOutput>");
        template.append(TEMPLATE_HEADER_CLOSE);
        expectResult("", false, false);

        expectFileOutput("FILE", "B");
    }

    @Test
    public void fileOutputTest_CannotWriteTargetFile()
    {
        String simulatedFile = "SimulatedFile.txt";
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <FileOutput fileName=\"");
        template.append(simulatedFile);
        template.append("\">");
        template.append("  <Text>Test</Text>");
        template.append(" </FileOutput>");
        template.append(TEMPLATE_HEADER_CLOSE);
        ExecutionError.simulate_CANNOT_WRITE_TARGET_FILE = true;
        expectError(ExecutionError.CANNOT_WRITE_TARGET_FILE, true, true, true, true, false, simulatedFile, false);
    }

    @Test
    public void fileOutputTest_TargetFileReadonly()
    {
        File outputFile = createOutputFile("FILE");
        boolean result = outputFile.setReadOnly();
        assert result;

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <FileOutput fileName=\"{FILE}\">");
        template.append("  <Text>Test</Text>");
        template.append(" </FileOutput>");
        template.append(TEMPLATE_HEADER_CLOSE);
        expectResult("", true, true);
    }

    @Test
    public void fileOutputTest_TargetFileReadonlyFail()
    {
        File outputFile = createOutputFile("FILE");
        boolean result = outputFile.setReadOnly();
        assert result;

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <FileOutput fileName=\"{FILE}\">");
        template.append("  <Text>Test</Text>");
        template.append(" </FileOutput>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.TARGET_FILE_READONLY, true, true, true, true, false, outputFile.getName(), false);

    }
}
