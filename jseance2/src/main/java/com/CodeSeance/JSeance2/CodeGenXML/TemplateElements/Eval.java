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

package com.CodeSeance.JSeance2.CodeGenXML.TemplateElements;

import com.CodeSeance.JSeance2.CodeGenXML.Context;

/**
 Created by IntelliJ IDEA.
 User: amurillo
 Date: Jun 16, 2010
 Time: 6:03:43 PM
 To change this template use File | Settings | File Templates.
 */
public class Eval extends Node
{
    public Eval(String arguments, Template.Position position)
    {
        super(arguments, position);
    }

    @TagParameter
    Object code;

    @TagParameter(required = false)
    String escaping;

    @Override
    public void onExecutionStart(Context context)
    {
        String text = code.toString();
        context.LogInfoMessage(log, "Eval", String.format("Output:[%s]", text));
        if (escaping != null && !"".equals(escaping))
        {
            if (escaping.equals("xml-attribute"))
            {
                text = escapeXMLAttribute(text);
            }
            else if (escaping.equals("xml-value"))
            {
                text = escapeXMLValue(text);
            }
            else if (escaping.equals("html"))
            {
                text = org.apache.commons.lang.StringEscapeUtils.escapeHtml(text);
            }
            else if (escaping.equals("java"))
            {
                text = org.apache.commons.lang.StringEscapeUtils.escapeJava(text);
            }
            else if (escaping.equals("javascript"))
            {
                text = org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(text);
            }
            else if (escaping.equals("sql"))
            {
                text = org.apache.commons.lang.StringEscapeUtils.escapeSql(text);
            }
        }

        String[] fragments = text.split("\\\\");
        String result = "";
        for (String fragment : fragments)
        {
            if (!result.equals(""))
            {
                result += "\\";
            }
            result += fragment.replace("\\n", "\n").replace("\\r", "\r");
        }

        context.writeText(text);
    }

    public static String escapeXMLValue(String text)
    {
        return text.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    public static String escapeXMLAttribute(String text)
    {
        return text.replaceAll("&", "&amp;")
                .replaceAll("\"", "&quot;")
                .replaceAll("<", "&lt;")
                .replaceAll("\n", "&#xA;")
                .replaceAll("\r", "&#xD;")
                .replaceAll("\u0009", "&#x9;");
    }
}
