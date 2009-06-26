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
 * User: Administrator Date: May 3, 2009 Time: 4:28:34 PM To change this template use File | Settings | File Templates.
 */
public class TextTest extends TestCase
{
    @Test
    public void textTest_Basic()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>A.</Text>");
        template.append(" <Text>B</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);
        expectResult("A.B");
    }

    @Test
    public void textTest_EscapingAttributeCoverage()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text escaping=\"xml-attribute\">A.</Text>");
        template.append(" <Text escaping=\"xml-value\">B.</Text>");
        template.append(" <Text escaping=\"html\">C.</Text>");
        template.append(" <Text escaping=\"java\">D.</Text>");
        template.append(" <Text escaping=\"javascript\">E.</Text>");
        template.append(" <Text escaping=\"sql\">F</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);
        expectResult("A.B.C.D.E.F");
    }

    @Test
    public void textTest_EscapingJavaScriptCoverage()
    {
        template.append(TEMPLATE_HEADER_OPEN);
        template.append(" <Text>@JavaScript{EscapeXMLAttribute('A.')}@</Text>");
        template.append(" <Text>@JavaScript{EscapeXMLValue('B.')}@</Text>");
        template.append(" <Text>@JavaScript{EscapeHTML('C.')}@</Text>");
        template.append(" <Text>@JavaScript{EscapeJava('D.')}@</Text>");
        template.append(" <Text>@JavaScript{EscapeJavaScript('E.')}@</Text>");
        template.append(" <Text>@JavaScript{EscapeSQL('F')}@</Text>");
        template.append(TEMPLATE_HEADER_CLOSE);
        expectResult("A.B.C.D.E.F");
    }
}
