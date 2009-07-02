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

import com.CodeSeance.JSeance.CodeGenXML.ExecutionError;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;

import java.io.File;
import java.util.*;

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
     * @parameter expression="${jseance.errorLogFile}" default-value="${project.build.outputDirectory}/jseance-errors.log"
     */
    private File errorLogFile;

    /**
     * Location of the info log file.
     *
     * @parameter expression="${jseance.infoLogFile}" default-value="${project.build.outputDirectory}/jseance-info.log"
     */
    private File infoLogFile;

    /**
     * Location of the debug log file.
     *
     * @parameter expression="${jseance.debugLogFile}"
     */
    private File debugLogFile = null;

    /**
     * Location of the error log file.
     *
     * @parameter expression="${jseance.sourcesDir}" default-value="${project.build.sourceDirectory}/jseance"
     */
    private File sourcesDir;

    /**
     * Location of the output directory.
     *
     * @parameter expression="${jseance.targetDir}"  default-value="${project.build.outputDirectory}/jseance"
     */
    private File targetDir;

    /**
     * Skips production of ouput files with readonly flag.
     *
     * @parameter expression="${jseance.ignoreReadOnlyOuputFiles}" default-value="false"
     */
    private boolean ignoreReadOnlyOuputFiles;

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
    private Set<String> includes = new HashSet<String>();

    /**
     * List of files to exclude
     *
     * @parameter
     */
    private Set<String> excludes = new HashSet<String>();


    private SourceInclusionScanner buildInclusionScanner()
    {
        SourceInclusionScanner inclusionScanner;

        if (includes.isEmpty() && excludes.isEmpty())
        {
            includes = Collections.singleton("**/*.xml");
            inclusionScanner = new SimpleSourceInclusionScanner(includes, Collections.EMPTY_SET);
        }
        else
        {
            if (includes.isEmpty())
            {
                includes.add("**/*.xml");
            }
            inclusionScanner = new SimpleSourceInclusionScanner(includes, excludes);
        }

        // This is a workaround to a limitation in SimpleSourceInclusionScanner
        inclusionScanner.addSourceMapping(new SuffixMapping("foobar", "foobar"));

        return inclusionScanner;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime(errorLogFile!= null? errorLogFile.toString() : null, infoLogFile!=null ? infoLogFile.toString() : null, debugLogFile!= null ? debugLogFile.toString() : null, ignoreReadOnlyOuputFiles, forceRebuild);

        SourceInclusionScanner scanner = buildInclusionScanner();
        File templatesDir = new File(sourcesDir, "/templates");
        if (!templatesDir.exists())
        {
            throw new MojoExecutionException(ExecutionError.INVALID_TEMPLATES_DIR.getMessage(templatesDir));
        }

        try
        {
            Set files = scanner.getIncludedSources(templatesDir, null);

            List<File> templateFiles = new ArrayList<File>();

            // Add files to List obj
            for (Object source : files)
            {
                File file = (File) source;
                templateFiles.add(file);
            }

            runtime.run(sourcesDir, targetDir, templateFiles, this);
        }
        catch (InclusionScanException ex)
        {
            throw new MojoExecutionException(String.format("Cannot load XML Templates from:[%s]", templatesDir), ex);
        }
        catch (Exception ex)
        {
            throw new MojoExecutionException(ex.getMessage(), ex);
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

    private void setErrorLogFile(File errorLogFile)
    {
        this.errorLogFile = errorLogFile;
    }

    private void setInfoLogFile(File infoLogFile)
    {
        this.infoLogFile = infoLogFile;
    }

    private void setDebugLogFile(File debugLogFile)
    {
        this.debugLogFile = debugLogFile;
    }

     private void setSourcesDir(File sourcesDir)
    {
        this.sourcesDir = sourcesDir;
    }

    private void setTargetDir(File targetDir)
    {
        this.targetDir = targetDir;
    }

    private void setIgnoreReadOnlyOuputFiles(boolean ignoreReadOnlyOuputFiles)
    {
        this.ignoreReadOnlyOuputFiles = ignoreReadOnlyOuputFiles;
    }

    private void setForceRebuild(boolean forceRebuild)
    {
        this.forceRebuild = forceRebuild;
    }
}
