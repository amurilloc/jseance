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

/**
 * User: Administrator Date: May 3, 2009 Time: 5:23:01 PM To change this template use File | Settings | File Templates.
 */
public class SwitchTest extends TestCase
{
    @Test
    public void switchTest_Break()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Switch jsExpression=\"2*3\">");
        template.append("  <Case value=\"1\">");
        template.append("   <Text>ExecutionError</Text>");
        template.append("  </Case>");
        template.append("  <Case value=\"true\">");
        template.append("   <Text>ExecutionError</Text>");
        template.append("  </Case>");
        template.append("  <Case value=\"6\">");
        template.append("   <Text>Ok</Text>");  // << Executed Statement
        template.append("  </Case>");
        template.append("  <Case value=\"6\">");
        template.append("   <Text>ExecutionError</Text>");
        template.append("  </Case>");
        template.append("  <Case value=\"7\">");
        template.append("   <Text>ExecutionError</Text>");
        template.append("  </Case>");
        template.append(" </Switch>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("Ok");
    }

    @Test
    public void switchTest_Default()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Switch jsExpression=\"2*3\">");
        template.append("  <Case value=\"1\">");
        template.append("   <Text>ExecutionError</Text>");
        template.append("  </Case>");
        template.append("  <Case value=\"true\">");
        template.append("   <Text>ExecutionError</Text>");
        template.append("  </Case>");
        template.append("  <Case value=\"5\">");
        template.append("   <Text>A.</Text>");
        template.append("  </Case>");
        template.append("  <Case value=\"7\">");
        template.append("   <Text>B</Text>");
        template.append("  </Case>");
        template.append("  <Default>");
        template.append("   <Text>Ok</Text>");  // << Executed Statement
        template.append("  </Default>");
        template.append(" </Switch>");
        template.append(TEMPLATE_HEADER_CLOSE);

        expectResult("Ok");
    }
}
