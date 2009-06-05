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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the entrypoint for Ant builds
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class AntTask extends org.apache.tools.ant.Task
{
    public AntTask()
    {
        setTaskName("JSeance.CodeGenXML");    
    }

    private com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime();

    public void setErrorLogFile(File file)
    {
        runtime.errorLogFile = file;
    }

    public void setInfoLogFile(File file)
    {
        runtime.infoLogFile = file;
    }

    public void setDebugLogFile(File file)
    {
        runtime.debugLogFile = file;
    }

    public void setConsoleDebugLog(boolean val)
    {
        runtime.consoleDebugLog = val;
    }

    public void setIncludesDir(File file)
    {
        runtime.includesDir = file;
    }

    public void setModelsDir(File file)
    {
        runtime.modelsDir = file;
    }

    public void setTargetDir(File file)
    {
        runtime.targetDir = file;
    }

    public void setIgnoreReadOnlyOuputFiles(boolean val)
    {
        runtime.ignoreReadOnlyOuputFiles = val;
    }

    public void addFileset(FileSet fileset)
    {
        filesets.add(fileset);
    }

    private List<FileSet> filesets = new ArrayList<FileSet>();

    @Override
    public void execute() throws BuildException
    {
        for(FileSet fileSet : filesets)
        {
            DirectoryScanner dirScanner = fileSet.getDirectoryScanner(getProject());
            runtime.templatesDir =  dirScanner.getBasedir();

            String[] includedFiles = dirScanner.getIncludedFiles();
            runtime.arguments = new ArrayList<String>();

            // Replace windows-only slashes with platform independant representation and populate the files
            for(String fileName : includedFiles)
            {
                runtime.arguments.add(fileName.replace('\\','/'));
            }

            try
            {
                runtime.run();
            }
            catch (Exception ex)
            {
                //log("", Project.MSG_ERR);
                throw new BuildException(ex);
            }
        }
    }

}
