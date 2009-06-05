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

import com.CodeSeance.JSeance.CodeGenXML.XMLAttribute;
import com.CodeSeance.JSeance.CodeGenXML.Context;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for creating a TextSink with file destination
 *
 * @author Andres Murillo
 * @version 1.0
 */
class FileOutput extends HierarchicalNode
{

    public FileOutput(Element element)
    {
        super(element);
    }

    // Text sink to use in current context
    private final StringBuffer buffer = new StringBuffer();

    @XMLAttribute
    String fileName;

    @XMLAttribute
    boolean append;

    // The file to write to
    private File file = null;

    private boolean skipFile = false;

    @Override
    public void onContextEnter(Context context)
    {
        file = new File(context.getManager().targetDir, fileName);



        skipFile = file.exists() && !file.canWrite() && context.getManager().ignoreReadOnlyOuputFiles;
        if (skipFile)
        {
            context.LogInfoMessage(log, "FileOutput", String.format("Readonly flag set, skipping file:[%s]", context.getManager().targetDir + File.pathSeparator + fileName));
        }
        else
        {
            context.LogInfoMessage(log, "FileOutput", String.format("Processing children and writing to file:[%s]", context.getManager().targetDir + File.pathSeparator + fileName));

            // Check if the file is writable before proceeding
            if (file.exists() && !file.canWrite())
            {
                throw new RuntimeException(String.format("Cannot write to file:[%s]", file.toString()));
            }

            // Change the sink of the current context
            context.setTextSink(buffer);
            ExecuteChildren(context);
        }
    }

    @Override
    public void onContextExit(Context context)
    {
        if (!skipFile)
        {
            String text = buffer.toString();
            context.LogInfoMessage(log, "FileOutput", String.format("Children produced:[%s]", text));

            try
            {
                // Write the text to disk
                FileWriter fileWriter = new FileWriter(file, append);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write(text);
                bufferedWriter.close();

                // Add the dependency to the file
                context.getManager().templateDependencies.addOutputFile(file);

            }
            catch (IOException exception)
            {
                log.error(String.format("Cannot write file to disk:[%s]", fileName));
                throw new RuntimeException(exception);
            }
        }
    }

}