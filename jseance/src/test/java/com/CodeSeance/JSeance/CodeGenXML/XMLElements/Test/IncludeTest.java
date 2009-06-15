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

public class IncludeTest extends TestCase
{
    @Test
    public void includeTest_Simple()
    {
        StringBuilder include = createXMLFile("INCLUDE");
        include.append(INCLUDE_HEADER_OPEN);
        include.append(" <Template>");
        include.append("  <Text>Ok</Text>");
        include.append(" </Template>");
        include.append(INCLUDE_HEADER_CLOSE);

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Include fileName=\"{INCLUDE}\"/>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("Ok");
    }

    @Test
    public void includeTest_RequiredDefineOk()
    {
        StringBuilder include = createXMLFile("INCLUDE");
        include.append(INCLUDE_HEADER_OPEN);
        include.append(" <RequiredDefine name=\"TestDefinition\"/>");
        include.append(" <Template>");
        include.append("  <Text>@JavaScript{Definitions['TestDefinition'];}@</Text>");
        include.append(" </Template>");
        include.append(INCLUDE_HEADER_CLOSE);

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Definition name=\"TestDefinition\">");
        template.append("  <Text>Ok</Text>");
        template.append(" </Definition>");
        template.append(" <Include fileName=\"{INCLUDE}\"/>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("Ok");
    }

    @Test
    public void includeTest_RequiredDefineFail()
    {
        StringBuilder include = createXMLFile("INCLUDE");
        include.append(INCLUDE_HEADER_OPEN);
        include.append(" <RequiredDefine name=\"TestDefinition\"/>");
        include.append(" <Template>");
        include.append("  <Text>@JavaScript{Definitions['TestDefinition'];}@</Text>");
        include.append(" </Template>");
        include.append(INCLUDE_HEADER_CLOSE);

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Include fileName=\"{INCLUDE}\"/>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.MISSING_INCLUDE_DEFINITION, true, true, true, true, false, "TestDefinition", false);
    }

    @Test
    public void includeTest_InvalidIncludesDir()
    {
        StringBuilder include = createXMLFile("INCLUDE");
        include.append(INCLUDE_HEADER_OPEN);
        include.append(" <Template>");
        include.append("  <Text>Ok</Text>");
        include.append(" </Template>");
        include.append(INCLUDE_HEADER_CLOSE);

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Include fileName=\"{INCLUDE}\"/>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_INCLUDES_DIR, false, true, true, true, false, null, false);
    }

    @Test
    public void includeTest_InvalidIncludeFile()
    {
        String invalidIncludeFile = "InvalidIncludeFile.xml";
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Include fileName=\"");
        template.append(invalidIncludeFile);
        template.append("\"/>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_INCLUDE_FILE, true, true, true, true, false, invalidIncludeFile, false);
    }

    @Test
    public void includeTest_InvalidXML()
    {
        StringBuilder include = createXMLFile("INCLUDE");
        include.append(INCLUDE_HEADER_OPEN);
        include.append(" <Template");   // Missing >
        include.append("  <Text>Ok</Text>");
        include.append(" </Template>");
        include.append(INCLUDE_HEADER_CLOSE);

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Include fileName=\"{INCLUDE}\"/>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_INCLUDE_XML, true, true, true, true, false, null, false);
    }
}
