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

package com.CodeSeance.JSeance.CodeGenXML.EntryPoints;

import com.CodeSeance.JSeance.CodeGenXML.EntryPoints.plexus.DirectoryScanner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * JSeance CodeGenXML Maven Mojo.
 *
 * @author <a href="mailto:andres.murillo@gmail.com">Andres Murillo</a>
 * @goal jseance
 */
public class MavenMojo extends AbstractMojo implements Logger
{
    /**
     * Location of the error log file.
     *
     * @parameter expression="${jseance.errorLogFile}" default-value="${project.build.directory}/jseance-errors.log"
     */
    private File errorLogFile;

    /**
     * Location of the info log file.
     *
     * @parameter expression="${jseance.infoLogFile}" default-value="${project.build.directory}/jseance-info.log"
     */
    private File infoLogFile;

    /**
     * Location of the debug log file.
     *
     * @parameter expression="${jseance.debugLogFile}"
     */
    private File debugLogFile = null;

    /**
     * Location of the sources log file.
     *
     * @parameter expression="${jseance.sourcesDir}" default-value="${basedir}/src/jseance"
     */
    private File sourcesDir;

    /**
     * Location of the output directory.
     *
     * @parameter expression="${jseance.targetDir}"  default-value="${project.build.directory}/jseance"
     */
    private File targetDir;

    /**
     * Skips production of ouput files with readonly flag.
     *
     * @parameter expression="${jseance.ignoreReadOnlyOuputFiles}" default-value="false"
     */
    public boolean ignoreReadOnlyOuputFiles;

    /**
     * Skips dependency checks and forces a rebuild
     *
     * @parameter expression="${jseance.forceRebuild}" default-value="false"
     */
    private boolean forceRebuild;

    /**
     * List of files to include, default is all .xml files in the templates dir
     *
     * @parameter
     */
    private List<String> includes = new ArrayList<String>();

    /**
     * List of files to exclude
     *
     * @parameter
     */
    private List<String> excludes = new ArrayList<String>();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime(errorLogFile!= null? errorLogFile.toString() : null, infoLogFile!=null ? infoLogFile.toString() : null, debugLogFile!= null ? debugLogFile.toString() : null, ignoreReadOnlyOuputFiles, forceRebuild);

        if (includes.isEmpty())
        {
            includes.add("**/*.xml");
        }
        DirectoryScanner scanner = new DirectoryScanner(includes, excludes);

        File templatesDir = new File(sourcesDir, "/templates");
        try
        {
            List<File> templateFiles = scanner.scan(templatesDir);           
            runtime.run(sourcesDir, targetDir, templateFiles, this);
        }
        catch (Exception ex)
        {
            throw new MojoFailureException(ex.getMessage());
        }
    }

    public void infoMessage(String message)
    {
        getLog().info(message);
    }

    public void errorMessage(String message)
    {
        getLog().error(message);
    }

    public void setErrorLogFile(File errorLogFile)
    {
        this.errorLogFile = errorLogFile;
    }

    public void setInfoLogFile(File infoLogFile)
    {
        this.infoLogFile = infoLogFile;
    }

    public void setDebugLogFile(File debugLogFile)
    {
        this.debugLogFile = debugLogFile;
    }

     public void setSourcesDir(File sourcesDir)
    {
        this.sourcesDir = sourcesDir;
    }

    public void setTargetDir(File targetDir)
    {
        this.targetDir = targetDir;
    }

    public void setIgnoreReadOnlyOuputFiles(boolean ignoreReadOnlyOuputFiles)
    {
        this.ignoreReadOnlyOuputFiles = ignoreReadOnlyOuputFiles;
    }

    public void setForceRebuild(boolean forceRebuild)
    {
        this.forceRebuild = forceRebuild;
    }
}
