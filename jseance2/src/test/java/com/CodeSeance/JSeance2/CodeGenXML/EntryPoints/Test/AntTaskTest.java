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

import com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.AntTask;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class AntTaskTest
{
    @Test
    public void AntTaskStdTest() throws IOException
    {
        com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.Test.TestHelper testHelper = new com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.Test.TestHelper();
        try
        {
            File testDir = testHelper.createTempDirectory();
            AntTask antTask = new AntTask();
            antTask.setSourcesDir(new File(testDir, "jseance"));
            antTask.setTargetDir(new File(testDir, "target"));
            antTask.setErrorLogFile(File.createTempFile("jseance-errors", ".log"));
            antTask.setInfoLogFile(File.createTempFile("jseance-info", ".log"));
            antTask.setDebugLogFile(File.createTempFile("jseance-debug", ".log"));
            File outputFile = testHelper.createStandardLayout(antTask.getSourcesDir(), antTask.getTargetDir());

            FileSet fileSet = new FileSet();
            fileSet.setDir(new File(testDir, "jseance/templates"));
            fileSet.setIncludes("**/*.jseance");
            antTask.addFileset(fileSet);
            Project project = new Project();
            project.setBasedir(".");
            antTask.setProject(project);
            antTask.execute();
            testHelper.validateSucess(outputFile,
                    antTask.getErrorLogFile(),
                    antTask.getInfoLogFile(),
                    antTask.getDebugLogFile());
        }
        finally
        {
            testHelper.disposeFiles();
        }
    }
}
