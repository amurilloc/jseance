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

import com.CodeSeance.JSeance.CodeGenXML.DependencyTracking.DependencyManager;
import com.CodeSeance.JSeance.CodeGenXML.DependencyTracking.TemplateDependencies;
import com.CodeSeance.JSeance.CodeGenXML.EntryPoints.Logger;
import com.CodeSeance.JSeance.CodeGenXML.XMLElements.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * This is the args4j class to define command line options for the executable Jar execution method also is the entry
 * point for functional tests
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class Runtime
{
    public Runtime(String errorLogFileName, String infoLogFileName, String debugLogFileName, boolean ignoreReadOnlyOuputFiles, boolean forceRebuild)
    {
        this.errorLogFileName = errorLogFileName;
        this.infoLogFileName = infoLogFileName;
        this.debugLogFileName = debugLogFileName;
        this.ignoreReadOnlyOuputFiles = ignoreReadOnlyOuputFiles;
        this.forceRebuild = forceRebuild;

        ConfigureLogger();
    }

    //Uses the specified filename for error logging
    private final String errorLogFileName;

    //Uses the specified filename for info logging
    private final String infoLogFileName;

    //Uses the specified filename for debugging
    private final String debugLogFileName;

    //Skips production of ouput files with readonly flag
    private final boolean ignoreReadOnlyOuputFiles;

    //Skips dependency checks and forces a rebuild
    private final boolean forceRebuild;

    // The dependency manager used to avoid unnecessary builds
    //private final DependencyManager dependencyManager;

    private void ConfigureLogger(Properties properties, String logName, String threshold, String fileName)
    {
        properties.setProperty(String.format("log4j.appender.%s", logName), "org.apache.log4j.FileAppender");
        properties.setProperty(String.format("log4j.appender.%s.File", logName), fileName);
        properties.setProperty(String.format("log4j.appender.%s.Append", logName), "false");
        properties.setProperty(String.format("log4j.appender.%s.layout", logName), "org.apache.log4j.PatternLayout");
        properties.setProperty(String.format("log4j.appender.%s.layout.ConversionPattern", logName), "%-5p %30c - %m%n");
        properties.setProperty(String.format("log4j.appender.%s.threshold", logName), threshold);
    }

    private void ConfigureLogger()
    {
        if (!logConfigured)
        {
            Properties log4Jproperties = new Properties();

            // override the logger config
            log4Jproperties.setProperty("log4j.rootLogger", "DEBUG" + (errorLogFileName != null ? ", ErrorLog" : "") + (infoLogFileName != null ? ", InfoLog" : "") + (debugLogFileName != null ? ", DebugLog" : ""));

            // override the log filenames and append mode
            if (errorLogFileName != null)
            {
                ConfigureLogger(log4Jproperties, "ErrorLog", "WARN", errorLogFileName);
            }

            if (infoLogFileName != null)
            {
                ConfigureLogger(log4Jproperties, "InfoLog", "INFO", infoLogFileName);
            }

            if (debugLogFileName != null)
            {
                ConfigureLogger(log4Jproperties, "DebugLog", "DEBUG", debugLogFileName);
            }

            // Configure log4j
            PropertyConfigurator.configure(log4Jproperties);
            logConfigured = true;
        }
    }

    private static boolean logConfigured = false;

    public String run(File sourcesDir, File targetDir, List<File> templateFiles, Logger externalLog)
    {
         // Directory from where to load include files (relative to)
        File includesDir = new File(sourcesDir, "/includes");

        //Directory from where to load model(xml) files (relative to)
        File modelsDir = new File(sourcesDir, "/models");
        return run(includesDir, modelsDir, targetDir, templateFiles, externalLog);
    }

    private boolean errors = false;

    public boolean hasErrors()
    {
        return errors;
    }

    public String run(File includesDir, File modelsDir, File targetDir, List<File> templateFiles, Logger externalLog)
    {
        errors = false;
        Log log = LogFactory.getLog("Runtime");

        StringBuffer buffer = new StringBuffer();

        if (!((targetDir.exists() && targetDir.isDirectory()) || targetDir.mkdirs()))
        {
            String message = ExecutionError.INVALID_TARGET_DIR.getMessage(targetDir);
            externalLog.errorMessage(message);
            log.error(message);
            return null;
        }

        DependencyManager dependencyManager = new DependencyManager(targetDir);

        // access non-option arguments and generate the templates
        for (File templateFile : templateFiles)
        {
            TemplateDependencies templateDependencies = dependencyManager.getTemplateDependencies(templateFile);

            // Track the processing time
            externalLog.infoMessage(String.format("Processing template file:[%s]", templateFile.toString()));
            long startMillis = System.currentTimeMillis();

            if (!dependencyManager.getTemplateDependencies(templateFile).isUpToDate() || forceRebuild)
            {
                dependencyManager.clearTemplateDependencies(templateFile);
                try
                {
                    String result = Template.run(templateFile, includesDir, modelsDir, targetDir, ignoreReadOnlyOuputFiles, templateDependencies);
                    buffer.append(result);
                    dependencyManager.commit();
                }
                catch (Exception ex)
                {
                    errors = true;
                    externalLog.errorMessage(ex.getMessage());
                    log.error(ex.getMessage());
                }
            }
            else
            {
                String message = String.format("File dependencies are up to date, skipping template generation:[%s]", templateFile);
                externalLog.infoMessage(message);
                log.info(message);
            }

            long elapsedMillis = System.currentTimeMillis() - startMillis;
            externalLog.infoMessage(String.format("Completed in :[%s] ms", elapsedMillis));
        }
        return buffer.toString();
    }

    public static Log CreateLogger(Class classObj)
    {
        return LogFactory.getLog(classObj.getName().replace("com.CodeSeance.JSeance.CodeGenXML.", ""));
    }
}
