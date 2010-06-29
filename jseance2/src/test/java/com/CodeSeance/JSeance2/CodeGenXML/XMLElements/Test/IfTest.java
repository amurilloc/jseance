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

import org.testng.annotations.Test;
import com.CodeSeance.JSeance2.CodeGenXML.ExecutionError;

public class IfTest extends TestCase
{
    @Test
    public void conditionalTest_If()
    {
        template.append("!If(1 > 0)!");
        template.append("Ok");   // << Executed Statement
        template.append("!ElseIf(1 == 1)!");
        template.append("ExecutionError");
        template.append("!Else!");
        template.append("ExecutionError");
        template.append("!End!");

        expectResult("Ok");
    }

    @Test
    public void conditionalTest_DoubleElse()
    {
        template.append("!If(1 > 0)!");
        template.append("Ok");   // << Executed Statement
        template.append("!Else!");
        template.append("ExecutionError");
        template.append("!Else!");
        template.append("ExecutionError");
        template.append("!End!");

        expectError(ExecutionError.INVALID_TEMPLATE_FORMAT, true, true, true, false, null, false );
    }

    @Test
    public void switchTest_OrphanElseIf()
    {
        template.append("!ElseIf!");
        template.append("!End!");
        expectError(ExecutionError.INVALID_TEMPLATE_FORMAT, true, true, true, false, null, false );
    }

    @Test
    public void switchTest_OrphanElse()
    {
        template.append("!Else!");
        template.append("!End!");
        expectError(ExecutionError.INVALID_TEMPLATE_FORMAT, true, true, true, false, null, false );
    }

    @Test
    public void conditionalTest_ElseIf()
    {
        template.append("!If(1 > 2)!");
        template.append("ExecutionError");
        template.append("!ElseIf(1 > 0)!");
        template.append("Ok");   // << Executed Statement
        template.append("!Else!");
        template.append("ExecutionError");
        template.append("!End!");
        expectResult("Ok");
    }

    @Test
    public void conditionalTest_Else()
    {
        template.append("!If(1 > 2)!");
        template.append("ExecutionError");
        template.append("!ElseIf(1 > 2)!");
        template.append("ExecutionError");   // << Executed Statement
        template.append("!Else!");
        template.append("Ok");
        template.append("!End!");
        expectResult("Ok");
    }

    @Test
    public void conditionalTest_NestedIfs()
    {
        template.append("!If(1 > 0)!");

            template.append("       @!If(1 < 0)!\n");
            template.append("ExecutionError\n");
            template.append("       @!ElseIf(1 == 1)!\n");
            template.append("Ok\n");
            template.append("       @!Else!\n");
            template.append("ExecutionError\n");
            template.append("       @!End!\n");

        template.append("!ElseIf(1 == 1)!");
        template.append("ExecutionError");
        template.append("!Else!");
        template.append("ExecutionError");
        template.append("!End!");

        expectResult("Ok\n");
    }

    @Test
    public void conditionalTest_NestedIfsIncomplete()
    {
        template.append("!If(1 > 0)!");

            template.append("       @!If(1 < 0)!\n");
            template.append("ExecutionError\n");
            template.append("       @!ElseIf(1 == 1)!\n");
            template.append("Ok\n");

        expectError(ExecutionError.INVALID_TEMPLATE_MISSING_END, true, true, true, false, null, false );
    }
}
