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

package com.CodeSeance.JSeance.CodeGenXML;

/**
 * Class used to represent all possible application errors
 *
 * @author Andres Murillo
 * @version 1.0
 */
public enum ExecutionError
{
   INVALID_TARGET_DIR(1, "Cannot read or create target directory:[%s]"),
   CANNOT_WRITE_TARGET_FILE(2, "Cannot write to target file:[%s]"),
   TARGET_FILE_READONLY(3, "Cannot write to target file:[%s] because of readonly flag, use the 'ignoreReadOnlyOuputFiles' option to ignore readonly target files"),

   INVALID_TEMPLATE_FILE(4, "Cannot read template file:[%s]"),
   INVALID_TEMPLATES_DIR(5, "Cannot read templates directory:[%s]"),

   INVALID_INCLUDES_DIR(6, "Cannot read includes directory:[%s]"),
   INVALID_INCLUDE_FILE(7, "Cannot read include file:[%s]"),
   MISSING_INCLUDE_DEFINITION(8, "Missing include definition:[%s]"),

   INVALID_MODELS_DIR(9, "Cannot read models directory:[%s]"),
   INVALID_MODEL_FILE(10, "Cannot read model file:[%s]"),
   INVALID_MODEL_E4X_EXPRESSION(11, "Invalid model e4XPath expression:[%s], expecting XMLObject type, found:[%s]");

    public static boolean simulate_CANNOT_WRITE_TARGET_FILE = false;

    private int id;
    private String message;

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
        return String.format("ExecutionError[%05d]", id);
    }
}
