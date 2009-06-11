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

import org.testng.annotations.Test;
import com.CodeSeance.JSeance.CodeGenXML.ExecutionError;

public class ModelTest extends TestCase
{
    @Test
    public void modelTest_Default()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\"/>");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test
    public void modelTest_Named()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" name=\"TestModel\"/>");
        template.append(" <Text>@JavaScript{Models['TestModel'].currentNode.A.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test
    public void modelTest_E4XPath()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A>");
        model.append("  <B val=\"A\"/>");
        model.append(" </A>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" e4XPath=\"A.B\"/>");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test
    public void modelTest_Validation_SchemaInFileOK()
    {
        StringBuilder xsd = createXMLFile("XSD");
        xsd.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">");
        xsd.append(" <xs:element name=\"Model\">");
        xsd.append("  <xs:complexType>");
        xsd.append("   <xs:sequence>");
        xsd.append("    <xs:element name=\"A\">");
        xsd.append("     <xs:complexType>");
        xsd.append("      <xs:sequence>");
        xsd.append("       <xs:element name=\"B\"/>");
        xsd.append("      </xs:sequence>");
        xsd.append("     </xs:complexType>");
        xsd.append("    </xs:element>");
        xsd.append("   </xs:sequence>");
        xsd.append("  </xs:complexType>");
        xsd.append(" </xs:element>");
        xsd.append("</xs:schema>");

        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"URL{XSD}\">");
        model.append(" <A>");
        model.append("  <B val=\"A\"/>");
        model.append(" </A>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" validate=\"true\"/>");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.B.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test(expectedExceptions = {RuntimeException.class})
    public void modelTest_Validation_SchemaInFileFail()
    {
        StringBuilder xsd = createXMLFile("XSD");
        xsd.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">");
        xsd.append(" <xs:element name=\"Model\">");
        xsd.append("  <xs:complexType>");
        xsd.append("   <xs:sequence>");
        xsd.append("    <xs:element name=\"A\">");
        xsd.append("     <xs:complexType>");
        xsd.append("      <xs:sequence>");
        xsd.append("       <xs:element name=\"B\"/>");
        xsd.append("      </xs:sequence>");
        xsd.append("     </xs:complexType>");
        xsd.append("    </xs:element>");
        xsd.append("   </xs:sequence>");
        xsd.append("  </xs:complexType>");
        xsd.append(" </xs:element>");
        xsd.append("</xs:schema>");

        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"URL{XSD}\">");
        model.append(" <A>");
        //Missing element <B/> to make validation fail
        model.append(" </A>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" validate=\"true\"/>");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.B.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test(expectedExceptions = {RuntimeException.class})
    public void modelTest_Validation_SchemaInFileMissingFail()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"NotFound.xsd\">");
        model.append(" <A/>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" validate=\"true\"/>");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.B.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test
    public void modelTest_NoValidation_SchemaInFileMissing()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"NotFound.xsd\">");
        model.append(" <A val=\"A\" />");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" validate=\"false\"/>");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test
    public void modelTest_Validation_ExternalSchemaOK()
    {
        StringBuilder xsd = createXMLFile("XSD");
        xsd.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">");
        xsd.append(" <xs:element name=\"Model\">");
        xsd.append("  <xs:complexType>");
        xsd.append("   <xs:sequence>");
        xsd.append("    <xs:element name=\"A\">");
        xsd.append("     <xs:complexType>");
        xsd.append("      <xs:sequence>");
        xsd.append("       <xs:element name=\"B\"/>");
        xsd.append("      </xs:sequence>");
        xsd.append("     </xs:complexType>");
        xsd.append("    </xs:element>");
        xsd.append("   </xs:sequence>");
        xsd.append("  </xs:complexType>");
        xsd.append(" </xs:element>");
        xsd.append("</xs:schema>");

        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"NotFound.xsd\">");
        model.append(" <A>");
        model.append("  <B val=\"A\"/>");
        model.append(" </A>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" validate=\"true\" xsdFileName=\"{XSD}\" />");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.B.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test
    public void modelTest_NoValidation_ExternalSchemaOK()
    {
        StringBuilder xsd = createXMLFile("XSD");
        xsd.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">");
        xsd.append(" <xs:element name=\"Model\">");
        xsd.append("  <xs:complexType>");
        xsd.append("   <xs:sequence>");
        xsd.append("    <xs:element name=\"A\">");
        xsd.append("     <xs:complexType>");
        xsd.append("      <xs:sequence>");
        xsd.append("       <xs:element name=\"B\"/>");
        xsd.append("      </xs:sequence>");
        xsd.append("     </xs:complexType>");
        xsd.append("    </xs:element>");
        xsd.append("   </xs:sequence>");
        xsd.append("  </xs:complexType>");
        xsd.append(" </xs:element>");
        xsd.append("</xs:schema>");

        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"NotFound.xsd\">");
        model.append(" <A val=\"A\">");
        //Missing element <B/> to make validation fail
        model.append(" </A>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\" validate=\"false\" xsdFileName=\"{XSD}\" />");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("A");
    }

    @Test
    public void modelTest_InvalidModelsDir()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"{MODEL}\"/>");
        template.append(" <Text>@JavaScript{Models['default'].currentNode.A.@val;}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_MODELS_DIR, true, false, true, true, false, null, false);
    }

    @Test
    public void modelTest_InvalidModelFile()
    {
        String invalidModelFile = "InvalidModelFile.xml";
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Model fileName=\"");
        template.append(invalidModelFile);
        template.append("\"/>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectError(ExecutionError.INVALID_MODEL_FILE, true, true, true, true, false, invalidModelFile, false);
    }
}
