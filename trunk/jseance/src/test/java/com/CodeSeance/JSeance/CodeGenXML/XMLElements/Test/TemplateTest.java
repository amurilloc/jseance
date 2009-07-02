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

public class TemplateTest extends TestCase
{
    @Test
    public void templateTest_Basic()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>A.</Text>");
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A.B");
    }

    @Test
    public void templateTest_InvalidTargetDir()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>A.</Text>");
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_TARGET_DIR, true, true, false, false, null, false);
    }

    @Test
    public void templateTest_InvalidTemplateFile()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>A.</Text>");
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_TEMPLATE_FILE, true, true, true, false, null, true);
    }

    @Test
    public void templateTest_ContextManagerInitializeError()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>A.</Text>");
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        ExecutionError.simulate_CONTEXTMANAGER_INITIALIZE_ERROR = true;

        expectError(ExecutionError.CONTEXTMANAGER_INITIALIZE_ERROR, true, true, true, false, null, false);
    }

    @Test
    public void templateTest_XMLParserConfigError()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>A.</Text>");
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        ExecutionError.simulate_XML_PARSER_CONFIG_ERROR = true;

        expectError(ExecutionError.XML_PARSER_CONFIG_ERROR, true, true, true, false, null, false);
    }

    @Test
    public void templateTest_XMLIOError()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>A.</Text>");
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        ExecutionError.simulate_XML_PARSER_IO_ERROR = true;

        expectError(ExecutionError.XML_PARSER_IO_ERROR, true, true, true, false, null, false);
    }

    @Test
    public void templateTest_InvalidXML()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <TextA.</Text>"); // Missing >
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_TEMPLATE_XML, true, true, true, false, null, false);
    }
}
