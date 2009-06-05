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
 * @goal generate-sources
 * @phase generate-sources
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
     * @parameter expression="${jseance.debugLogFile}" default-value="${project.build.directory}/jseance-debug.log"
     */
    private File debugLogFile;

    /**
     * Outputs debug info to the console during build.
     *
     * @parameter expression="${jseance.consoleDebugLog}" default-value="false"
     */
    private boolean consoleDebugLog;

    /**
     * Location of the models.
     *
     * @parameter expression="${jseance.includesDir}"  default-value="${project.build.sourceDirectory}/jseance/includes"
     * @required
     */
    private File includesDir;

    /**
     * Location of the models.
     *
     * @parameter expression="${jseance.modelsDir}" default-value="${project.build.sourceDirectory}/jseance/models"
     */
    private File modelsDir;

    /**
     * Location of the templates directory.
     *
     * @parameter expression="${jseance.templatesDir}" default-value="${project.build.sourceDirectory}/jseance/templates"
     */
    private File templatesDir;

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
        com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime(
            errorLogFile.toString(),
            infoLogFile.toString(),
            debugLogFile.toString(),
            consoleDebugLog,
            false,
            includesDir,
            modelsDir,
            targetDir,
            ignoreReadOnlyOuputFiles,
            forceRebuild);

        SourceInclusionScanner scanner = buildInclusionScanner();

        try
        {
            Set files = scanner.getIncludedSources(templatesDir, null);

            Hashtable<File, List<String>> fileGroups = new Hashtable<File, List<String>>();

            // Group the files by parent dir
            for (Object source : files)
            {
                File sourceFile = (File)source;
                File parentFile = sourceFile.getParentFile();
                if (!fileGroups.containsKey(parentFile))
                {
                    List<String> fileList = new ArrayList<String>();
                    fileGroups.put(parentFile, fileList);
                }
                fileGroups.get(parentFile).add(sourceFile.getName());
            }

            // Execute the teplates
            for (File parentFile : fileGroups.keySet())
            {
                runtime.run(parentFile, fileGroups.get(parentFile), this);
            }
        }
        catch (InclusionScanException ex)
        {
            throw new MojoExecutionException(String.format("Cannot load XML Templates from:[%s]", templatesDir), ex);
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
}
