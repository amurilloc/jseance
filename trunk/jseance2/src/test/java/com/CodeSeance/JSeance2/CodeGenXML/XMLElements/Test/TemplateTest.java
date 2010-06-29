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
import com.CodeSeance.JSeance2.CodeGenXML.TemplateElements.Node;
import com.CodeSeance.JSeance2.CodeGenXML.TemplateElements.NodeFactory;
import org.testng.annotations.Test;

public class TemplateTest extends TestCase
{
    @Test
    public void templateTest_Basic()
    {
        template.append("A.");
        template.append("B");
        expectResult("A.B");
    }

    @Test
    public void templateTest_InlineTag()
    {
        template.append("!Eval('A.')!");
        template.append("!Eval('B')!");
        expectResult("A.B");
    }

    @Test
    public void templateTest_SingleLineTag()
    {
        template.append("   @!Eval('A.')!   \r\n");
        template.append("   @!Eval('B.')!\n");
        template.append("   @!Eval('C')!\n");
        expectResult("A.B.C");
    }

    // Experimental code, so far not needed
    /*
    @Test
    public void templateTest_RemovePrecedingNewline()
    {
        template.append("A.\r\n");
        template.append("   @!Eval('B.')!\n");
        template.append("C.\n");
        template.append("   @!Eval('D')!\n");
        expectResult("A.B.C.D");
    }
    */
    
    @Test
    public void templateTest_MixedLineTag()
    {
        template.append("!Eval('0.')!");
        template.append("   @!Eval('A.')!   \r\n");
        template.append("!Eval('B.')!");
        template.append("   @!Eval('C')!\n");
        expectResult("0.A.B.C");
    }

    @Test
    public void templateTest_InvalidTargetDir()
    {
        template.append("A.");
        template.append("B");
        expectError(ExecutionError.INVALID_TARGET_DIR, true, true, false, false, null, false);
    }

    @Test
    public void templateTest_InvalidTemplateFile()
    {
        template.append("A.");
        template.append("B");
        expectError(ExecutionError.INVALID_TEMPLATE_FILE, true, true, true, false, null, true);
    }

    @Test
    public void templateTest_ContextManagerInitializeError()
    {
        template.append("A.");
        template.append("B");
        ExecutionError.simulate_CONTEXTMANAGER_INITIALIZE_ERROR = true;

        expectError(ExecutionError.CONTEXTMANAGER_INITIALIZE_ERROR, true, true, true, false, null, false);
    }

    @Test
    public void templateTest_MissingArguments()
    {
        template.append("!If!");
        template.append("Ok");
        template.append("!End!");

        expectError(ExecutionError.MISSING_TAG_ARGUMENTS, true, true, true, false, null, false);
    }
    
    @Test
    public void templateTest_MissingEnd()
    {
        template.append("!If!");
        template.append("Ok");
        expectError(ExecutionError.INVALID_TEMPLATE_MISSING_END, true, true, true, false, null, false);
    }

    @Test
    public void templateTest_InvalidArguments()
    {
        template.append("!If(askhjaksdhajshd asdjashdkjashd asdjhakjdha)!");
        template.append("Ok");
        template.append("!End!");

        expectError(ExecutionError.INVALID_TAG_ARGUMENTS, true, true, true, false, null, false);
    }

    @Test
    public void templateText_InvalidNodeFactoryTag()
    {
        Node result = NodeFactory.getInstance().createNode("ErrorTag", null, null);
        assert result == null;
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void templateTest_SimulateMemoryError()
    {
        ExecutionError.simulate_MEMORY_IO_ERROR = true;
        template.append("A.\n");
        template.append("B.\n");
        template.append("C\n");
        expectResult("A.\n.B.\nC\n");
    }
}
