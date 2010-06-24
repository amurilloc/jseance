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

public class ForTest extends TestCase
{
    @Test
    public void outputIteratorTest_Simple()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A.\"/>");
        model.append(" <A val=\"B.\"/>");
        model.append(" <A val=\"C\"/>");
        model.append("</Model>");


        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!For(\"A\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");
        template.append("!End!");


        expectResult("A.B.C");
    }

    @Test
    public void outputIteratorTest_IfEmpty()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A.\"/>");
        model.append(" <A val=\"B.\"/>");
        model.append(" <A val=\"C\"/>");
        model.append("</Model>");

        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!For(\"A.B\")!");
        template.append("ExecutionError");
        template.append("!IfEmpty!");
        template.append("Ok");
        template.append("!End!");

        expectResult("Ok");
    }

    @Test
    public void outputIteratorTest_Namespace()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model xmlns:XNamespace=\"http://www.xnamespace.com/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.xnamespace.com/\">");
        model.append(" <A val=\"A.\"/>");
        model.append(" <A val=\"B.\"/>");
        model.append(" <A val=\"C\"/>");
        model.append("</Model>");


        template.append("!XMLModel(\"{MODEL}\")!");
        //template.append("<JavaScript>default xml namespace = \"http://www.xnamespace.com/\"</JavaScript>");
        // Ignore namespace
        template.append("!For(\"*::A\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");
        template.append("!End!");

        // Specific namespace
        template.append("!For(\"child(QName('http://www.xnamespace.com/', 'A'))\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");
        template.append("!End!");

        // Default namespace
        template.append("!Code!default xml namespace = 'http://www.xnamespace.com/';!End!");
        template.append("!For(\"A\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");
        template.append("!End!");


        expectResult("A.B.CA.B.CA.B.C");
    }

    // TODO: Test if empty

    @Test
    public void outputIteratorTest_NamedModel()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A.\"/>");
        model.append(" <A val=\"B.\"/>");
        model.append(" <A val=\"C\"/>");
        model.append("</Model>");


        template.append("!XMLModel(\"{MODEL}\", \"TestModel\")!");
        template.append("!For(\"A\", \"TestModel\")!");
        template.append("!Eval(Models['TestModel'].currentNode.@val)!");
        template.append("!End!");


        expectResult("A.B.C");
    }

    @Test
    public void outputIteratorTest_Separator()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append(" <A val=\"B\"/>");
        model.append(" <A val=\"C\"/>");
        model.append("</Model>");


        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!For(\"A\", null, \".\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");
        template.append("!End!");


        expectResult("A.B.C");
    }

    @Test
    public void outputIteratorTest_InvalidOutputIteratorE4XExpression()
    {
        StringBuilder model = createXMLFile("MODEL");
        model.append("<Model>");
        model.append(" <A val=\"A\"/>");
        model.append(" <A val=\"B\"/>");
        model.append(" <A val=\"C\"/>");
        model.append("</Model>");

        String e4XPath = "A.toString()";

        template.append("!XMLModel(\"{MODEL}\")!");
        template.append("!For(\"");
        template.append(e4XPath);
        template.append("\", null, \".\")!");
        template.append("!Eval(Models['default'].currentNode.@val)!");
        template.append("!End!");


        expectError(ExecutionError.INVALID_OUTPUT_ITERATOR_E4X_EXPRESSION, true, true, true, false, e4XPath, false);
    }

}
