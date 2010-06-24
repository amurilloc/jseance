function JSeanceUtils_CreateXML(xmlText)
{
    return new XML(xmlText);
};

function JSeanceUtils_EvalXMLPath(xml, path)
{
    return eval('xml.' + path);
}

function JSeanceUtils_XMLLength(xml)
{
    return xml.length();
}

function JSeanceUtils_XMLGetNodeAt(xml, index)
{
    return xml[index];
}

function JSeanceUtils_XMLNodeToString(xml)
{
    return xml.toXMLString();
}

function JSeanceUtils_ConvertArgsToArray()
{
    return arguments;
}

// Escape Functions
function EscapeXMLValue(val)
{
    return String(com.CodeSeance.JSeance2.CodeGenXML.TemplateElements.Eval.escapeXMLValue(val));
}

function EscapeXMLAttribute(val)
{
    return String(com.CodeSeance.JSeance2.CodeGenXML.TemplateElements.Eval.escapeXMLAttribute(val));
}

function EscapeHTML(val)
{
    return String(org.apache.commons.lang.StringEscapeUtils.escapeHtml(val));
}

function EscapeJava(val)
{
    return String(org.apache.commons.lang.StringEscapeUtils.escapeJava(val));
}

function EscapeJavaScript(val)
{
    return String(org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(val));
}

function EscapeSQL(val)
{
    return String(org.apache.commons.lang.StringEscapeUtils.escapeSql(val));
}