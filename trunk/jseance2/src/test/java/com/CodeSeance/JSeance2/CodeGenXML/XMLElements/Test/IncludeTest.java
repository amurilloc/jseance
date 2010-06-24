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

public class IncludeTest extends TestCase
{
    @Test
    public void includeTest_SimpleJSeance()
    {
        StringBuilder include = createIncludeFile("INCLUDE", ".jseance");
        include.append("Ok");

        template.append("!Include(\"{INCLUDE}\")!");

        expectResult("Ok");
    }

    @Test
    public void includeTest_SimpleJS()
    {
        StringBuilder include = createIncludeFile("INCLUDE", ".js");
        include.append("var testVar = 'Ok';");

        template.append("!Include(\"{INCLUDE}\")!");
        template.append("!Eval(testVar)!");

        expectResult("Ok");
    }

    @Test
    public void includeTest_InvalidIncludesDir()
    {
        StringBuilder include = createIncludeFile("INCLUDE", ".jseance");
        include.append("Ok");

        template.append("!Include(\"{INCLUDE}\")!");

        expectError(ExecutionError.INVALID_INCLUDES_DIR, false, true, true, false, null, false);
    }

    @Test
    public void includeTest_InvalidIncludeFile()
    {
        String invalidIncludeFile = "InvalidIncludeFile.jseance";

        template.append("!Include(\"");
        template.append(invalidIncludeFile);
        template.append("\")!");


        expectError(ExecutionError.INVALID_INCLUDE_FILE, true, true, true, false, invalidIncludeFile, false);
    }


}
