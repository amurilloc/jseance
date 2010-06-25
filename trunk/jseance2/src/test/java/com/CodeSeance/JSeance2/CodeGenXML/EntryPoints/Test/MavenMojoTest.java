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

package com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.Test;

import com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.MavenMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class MavenMojoTest
{
    @Test
    public void MavenMojoStdTest() throws IOException, MojoExecutionException, MojoFailureException
    {
        com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.Test.TestHelper testHelper = new com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.Test.TestHelper();
        try
        {
            File testDir = testHelper.createTempDirectory();
            File sourcesDir = new File(testDir, "jseance");
            File targetDir = new File(testDir, "target");
            File errorLogFile = File.createTempFile("jseance-errors", ".log");
            File infoLogFile = File.createTempFile("jseance-info", ".log");
            MavenMojo mavenMojo = new MavenMojo();
            mavenMojo.setErrorLogFile(errorLogFile);
            mavenMojo.setInfoLogFile(infoLogFile);
            mavenMojo.setDebugLogFile(null);
            mavenMojo.setSourcesDir(sourcesDir);
            mavenMojo.setTargetDir(targetDir);
            mavenMojo.setIgnoreReadOnlyOuputFiles(false);
            mavenMojo.setForceRebuild(false);

            File outputFile = testHelper.createStandardLayout(sourcesDir, targetDir);

            mavenMojo.execute();

            testHelper.validateSucess(outputFile,
                    errorLogFile,
                    infoLogFile,
                    null);
        }
        finally
        {
            testHelper.disposeFiles();
        }
    }
}
