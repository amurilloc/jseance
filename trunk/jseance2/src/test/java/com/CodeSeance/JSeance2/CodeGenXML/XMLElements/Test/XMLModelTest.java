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

public class XMLModelTest extends TestCase
{
    @Test
    public void modelTest_Default()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!Eval(Models['default'].currentNode.A.@val)!");

        expectResult("A");
    }

    @Test
    public void modelTest_Named()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\", \"TestModel\")!");
        template.append("!Eval(Models['TestModel'].currentNode.A.@val)!");

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

        template.append("!XMLModel(\"{MODEL}\", null, \"A.B\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");

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

        template.append("!XMLModel(\"{MODEL}\", null, null, true)!");
        template.append("!Eval(Models['default'].currentNode.A.B.@val)!");


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

        template.append("!XMLModel(\"{MODEL}\", null, null, true)!");
        template.append("!Eval(Models['default'].currentNode.A.B.@val)!");

        expectResult("A");
    }

    @Test(expectedExceptions = {RuntimeException.class})
    public void modelTest_Validation_SchemaInFileMissingFail()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"NotFound.xsd\">");
        model.append(" <A/>");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\", null, null, true)!");
        template.append("!Eval(Models['default'].currentNode.A.B.@val)!");

        expectResult("A");
    }

    @Test
    public void modelTest_NoValidation_SchemaInFileMissing()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"NotFound.xsd\">");
        model.append(" <A val=\"A\" />");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\", null, null, false)!");
        template.append("!Eval(Models['default'].currentNode.A.@val)!");

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

        template.append("!XMLModel(\"{MODEL}\", null, null, true, \"{XSD}\")!");
        template.append("!Eval(Models['default'].currentNode.A.B.@val)!");

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

        template.append("!XMLModel(\"{MODEL}\", null, null, false, \"{XSD}\")!");
        template.append("!Eval(Models['default'].currentNode.A.@val)!");

        expectResult("A");
    }

    @Test
    public void modelTest_InvalidModelsDir()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!Eval(Models['default'].currentNode.A.@val)!");

        expectError(ExecutionError.INVALID_MODELS_DIR, true, false, true, false, null, false);
    }

    @Test
    public void modelTest_InvalidModelFile()
    {
        String invalidModelFile = "InvalidModelFile.xml";

        template.append("!XMLModel(\"");
        template.append(invalidModelFile);
        template.append("\")!");

        expectError(ExecutionError.INVALID_MODEL_FILE, true, true, true, false, invalidModelFile, false);
    }

    @Test
    public void modelTest_InvalidModelE4XExpression()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A>");
        model.append("  <B val=\"A\"/>");
        model.append(" </A>");
        model.append("</Model>");

        String e4XPath = "A.B.toString()";

        template.append("!XMLModel(\"{MODEL}\", null, \"");
        template.append(e4XPath);
        template.append("\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");

        expectError(ExecutionError.INVALID_MODEL_E4X_EXPRESSION, true, true, true, false, e4XPath, false);
    }

    @Test
    public void modelTest_ContextManagerCreateXMLObject_Error()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!Eval(Models['default'].currentNode.A.@val)!");

        ExecutionError.simulate_CONTEXTMANAGER_CREATEXMLOBJECT_ERROR = true;

        expectError(ExecutionError.CONTEXTMANAGER_CREATEXMLOBJECT_ERROR, true, true, true, false, null, false);
    }

    @Test
    public void modelTest_InvalidModelXML()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model");  // Missing closing brace
        model.append(" <A val=\"A\"/>");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!Eval(Models['default'].currentNode.A.@val)!");

        expectError(ExecutionError.INVALID_MODEL_XML, true, true, true, false, null, false);
    }
}
