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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * JSeance CodeGenXML Maven Mojo.
 *
 * @goal generate
 *
 * @phase process-sources
 */
public class MavenMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

     /**
     * Location of the error log file.
     * @parameter expression="${jseance.errorLogFile}" default-value="${project.build.directory}/jseance-errors.log"
     */
    private File errorLogFile;

    /**
     * Location of the info log file.
     * @parameter expression="${jseance.infoLogFile}" default-value="${project.build.directory}/jseance-info.log"
     */
    private File infoLogFile;

    /**
     * Location of the debug log file.
     * @parameter expression="${jseance.debugLogFile}" default-value="${project.build.directory}/jseance-debug.log"
     */
    private File debugLogFile;

    /**
     * Outputs debug info to the console during build.
     * @parameter expression="${jseance.consoleDebugLog}" default-value="false"
     */
    private boolean consoleDebugLog;

    /**
     * Location of the models.
     * @parameter expression="${jseance.modelsDir}"
     * @required
     */
    private File modelsDir;

    /**
     * Location of the templates directory.
     * @parameter expression="${jseance.templatesDir}"
     * @required
     */
    private File templatesDir;

   /**
     * Location of the output directory.
     * @parameter expression="${jseance.targetDir}"
     * @required
     */
    private File targetDir;

    /**
     * Skips production of ouput files with readonly flag.
     * @parameter expression="${jseance.ignoreReadOnlyOuputFiles}" default-value="false"
     */
    private boolean ignoreReadOnlyOuputFiles;

    public void execute()
        throws MojoExecutionException
    {
        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }
}
