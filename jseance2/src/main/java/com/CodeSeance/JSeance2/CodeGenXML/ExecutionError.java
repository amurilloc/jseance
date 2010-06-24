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

package com.CodeSeance.JSeance2.CodeGenXML;

/**
 Class used to represent all possible application errors

 @author Andres Murillo
 @version 1.0 */
public enum ExecutionError
{
    // 100 - General Errors
    INVALID_TARGET_DIR(100, "Cannot read or create target directory:[%s]"),
    CANNOT_WRITE_TARGET_FILE(101, "Cannot write to target file:[%s], Error:[%s]"),
    TARGET_FILE_READONLY(102, "Cannot write to target file:[%s] because of readonly flag, use the 'ignoreReadOnlyOuputFiles' option to ignore readonly target files"),

    // 200 - Template Errors
    INVALID_TEMPLATE_FILE(200, "Cannot read template file:[%s]"),
    INVALID_TEMPLATES_DIR(201, "Cannot read templates directory:[%s]"),
    INVALID_OUTPUT_ITERATOR_E4X_EXPRESSION(202, "Invalid output iterator e4XPath expression:[%s], expecting XMLObject type, found:[%s]"),
    JAVASCRIPT_EVAL_ERROR(203, "JavaScript evaluation error on expression:[%s], message:[%s]"),
    JAVASCRIPT_NOT_CLOSED(204, "Embedded JavaScript code in text:[%s] missing termination marker:[%s]"),
    INVALID_TEMPLATE_XML(205, "Error parsing template XML file:[%s] - Message:[%s]"),
    INVALID_TEMPLATE_FORMAT(206, "Error parsing template file:[%s], line:[%d], col:[%d], error:[%s]"),
    INVALID_TAG_ARGUMENTS(206, "Error parsing template tag arguments:[%s], line:[%d], col:[%d], error:[%s]"),
    INVALID_TEMPLATE_END_STATEMENT(207, "Template has an extra @End@ statement"),
    INVALID_TEMPLATE_MISSING_END(207, "Template has a missing End statement, unexpected end of file while parsing"),

    // 300 Model Errors
    INVALID_MODELS_DIR(300, "Cannot read models directory:[%s]"),
    INVALID_MODEL_FILE(301, "Cannot read model file:[%s]"),
    INVALID_MODEL_XML(302, "Error parsing model XML file:[%s] - Message:[%s]"),
    INVALID_MODEL_E4X_EXPRESSION(303, "Invalid model e4XPath expression:[%s], expecting XMLObject type, found:[%s]"),

    // 400 Include Errors
    INVALID_INCLUDES_DIR(400, "Cannot read includes directory:[%s]"),
    INVALID_INCLUDE_FILE(401, "Cannot read include file:[%s]"),
    MISSING_INCLUDE_DEFINITION(402, "Missing include definition:[%s]"),
    INVALID_INCLUDE_XML(403, "Error parsing include XML file:[%s] - Message:[%s]"),
    INVALID_INCLUDE_TERMINATION(404, "Cannot determine file type for:[%s] - Supported file name terminations are .js and .jseance"),

    // 900 - Internal errorrs
    CONTEXTMANAGER_INITIALIZE_ERROR(900, "Internal configuration error:[Context.initializeJavaScriptEngine], please verify that the correct dependencies are included. Exception:[%s]"),
    CONTEXTMANAGER_CREATEXMLOBJECT_ERROR(901, "Error converting XML to JavaScript Object:[Context.createXMLObject]. Exception:[%s]"),
    XML_PARSER_CONFIG_ERROR(902, "Error configuring XML Parser:[%s]"),
    XML_PARSER_IO_ERROR(903, "IO Error while parsing XML:[%s]");

    // Exception simulation, for cases where throwing is too complex
    public static boolean simulate_CANNOT_WRITE_TARGET_FILE = false;
    public static boolean simulate_CONTEXTMANAGER_INITIALIZE_ERROR = false;
    public static boolean simulate_CONTEXTMANAGER_CREATEXMLOBJECT_ERROR = false;
    public static boolean simulate_XML_PARSER_IO_ERROR = false;
    public static boolean simulate_XML_PARSER_CONFIG_ERROR = false;

    private final int id;
    private final String message;

    ExecutionError(int id, String message)
    {
        this.id = id;
        this.message = message;
    }

    public String getMessage(Object... args)
    {
        return getErrorCode() + " - " + String.format(message, args);
    }

    public String getErrorCode()
    {
        return String.format("Error[%05d]", id);
    }
}
