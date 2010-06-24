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
import com.CodeSeance.JSeance2.CodeGenXML.ExecutionError;

import java.io.File;
import java.io.IOException;

/**
 Created by IntelliJ IDEA.
 User: amurillo
 Date: Jun 8, 2010
 Time: 6:16:31 PM
 To change this template use File | Settings | File Templates.
 */
class Include extends Node
{
    public Include(String arguments, Template.Position position)
    {
        super(arguments, position);
    }

    @TagParameter
    String fileName;

    @Override
    public void onExecutionStart(Context context)
    {
        File includesDir = context.includesDir;

        File includeFile = new File(includesDir, fileName);
        context.LogInfoMessage(log, "Include", String.format("Loading include fileName:[%s]", fileName));

        if (!includesDir.exists())
        {
            throw new RuntimeException(ExecutionError.INVALID_INCLUDES_DIR.getMessage(includesDir));
        }

        try
        {
            if (fileName.toLowerCase().endsWith(".js"))
            {
                context.loadJavaScriptInclude(includeFile);
            }
            else if (fileName.toLowerCase().endsWith(".jseance"))
            {
                Template template = new Template(includeFile);
                template.loadChildren(template, this);
                template.loadAttributes(context);
                template.onExecutionStart(context);
                template.onExecutionEnd(context);
            }
            else
            {
                throw new RuntimeException(ExecutionError.INVALID_INCLUDE_TERMINATION.getMessage(fileName));
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ExecutionError.INVALID_INCLUDE_FILE.getMessage(includeFile), ex);
        }
    }
}
