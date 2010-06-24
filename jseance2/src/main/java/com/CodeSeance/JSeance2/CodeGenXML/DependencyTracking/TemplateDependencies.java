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

package com.CodeSeance.JSeance2.CodeGenXML.DependencyTracking;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 This class is responsible validation dependencies for a specific template. It can also serealize itself to XML via
 commit()

 @author Andres Murillo
 @version 1.0 */
public class TemplateDependencies
{
    public TemplateDependencies(File templateFile)
    {
        this.templateFile = templateFile;
    }

    private final File templateFile;

    public void addInputFile(File file)
    {
        inputs.add(file);
    }

    private Set<File> inputs = new HashSet<File>();

    public void addOutputFile(File file)
    {
        outputs.add(file);
    }

    private Set<File> outputs = new HashSet<File>();

    public boolean isUpToDate()
    {

        long outputEarliestModificationDate = outputs.size() > 0 ? Long.MAX_VALUE : 0L;

        for (File outputFile : outputs)
        {
            if (outputFile.exists())
            {
                // Check the output file last modification date and update the newest file dat if needed
                long outputDate = outputFile.lastModified();
                if (outputDate < outputEarliestModificationDate)
                {
                    outputEarliestModificationDate = outputDate;
                }
            }
            else
            {
                // output file missing, better rebuild
                return false;
            }
        }

        long inputLastModifiedDate = templateFile.exists() ? templateFile.lastModified() : Long.MAX_VALUE;

        // Optimization: Check if the template file is newer than the newest output file
        if (inputLastModifiedDate > outputEarliestModificationDate)
        {
            return false;
        }

        for (File inputFile : inputs)
        {
            if (inputFile.exists())
            {
                // Check the file date and if older than current, set it as the newest dependency
                // change date
                long inputDate = inputFile.lastModified();
                if (inputDate > inputLastModifiedDate)
                {
                    inputLastModifiedDate = inputDate;
                }
            }
            else
            {
                // Dependency missing, better rebuild
                return false;
            }
        }

        //Check if any of the input files is newer than any of the output files
        return inputLastModifiedDate <= outputEarliestModificationDate;
    }

    public Element serialize(Document document) throws IOException
    {
        Element result = document.createElement("Template");
        result.setAttribute("fileName", templateFile.getCanonicalPath());
        for (File file : inputs)
        {
            Element child = document.createElement("Input");
            child.setAttribute("fileName", file.getCanonicalPath());
            result.appendChild(child);
        }

        for (File file : outputs)
        {
            Element child = document.createElement("Output");
            child.setAttribute("fileName", file.getCanonicalPath());
            result.appendChild(child);
        }

        return result;
    }
}
