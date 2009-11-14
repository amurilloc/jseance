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

package com.CodeSeance.JSeance.CodeGenXML.XMLElements;

import com.CodeSeance.JSeance.CodeGenXML.Context;
import com.CodeSeance.JSeance.CodeGenXML.XMLAttribute;
import com.CodeSeance.JSeance.CodeGenXML.XMLTextContent;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for writing text to the text sink in the current context
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class Text extends Node
{

    public Text(Element element)
    {
        super(element);
    }

    @XMLAttribute
    String escaping;

    @XMLTextContent
    String text;

    @Override
    public void onContextEnter(Context context)
    {
        context.LogInfoMessage(log, "Text", String.format("Output:[%s]", text));
        if (escaping!= null && !"".equals(escaping))
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
        text = escapeNewLine(text);

        context.writeText(text);
    }

    private String escapeNewLine(String text)
    {
        StringBuffer result = new StringBuffer();
        List<String>fragments = split(text, "\\\\");
        for (String fragment : fragments)
        {
            String escapedText = fragment.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t");
            result.append("".equals(fragment) ? "\\" : escapedText);
         }
        return result.toString();
    }

    // This method is required due to extrange behavior from String.split
    public List<String> split(String text, String separator)
    {
        List<String> result = new ArrayList<String>();

        int start = text.indexOf(separator);
        if (start == -1)
        {
            result.add(text);
        }
        else
        {
            while (start > -1)
            {
                result.add(text.substring(0, start));
                text = text.substring(start == 0 ? separator.length() : start);
                start = text.indexOf(separator);
            }
        }
        return result;
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