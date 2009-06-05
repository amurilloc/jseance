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
import com.CodeSeance.JSeance.CodeGenXML.XMLElements.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    public Runtime(String errorLogFileName,
                   String infoLogFileName,
                   String debugLogFileName,
                   boolean consoleDebugLog,
                   boolean consoleTemplateOut,
                   File includesDir,
                   File modelsDir,
                   File targetDir,
                   boolean ignoreReadOnlyOuputFiles,
                   boolean forceRebuild)
    {
        this.errorLogFileName = errorLogFileName;
        this.infoLogFileName = infoLogFileName;
        this.debugLogFileName = debugLogFileName;
        this.consoleDebugLog = consoleDebugLog;
        this.consoleTemplateOut = consoleTemplateOut;
        this.includesDir = includesDir;
        this.modelsDir = modelsDir;
        this.targetDir = targetDir;
        this.ignoreReadOnlyOuputFiles = ignoreReadOnlyOuputFiles;
        this.forceRebuild = forceRebuild;

        ConfigureLogger();

        dependencyManager =  new DependencyManager(targetDir);
    }
    //Uses the specified filename for error logging
    private final String errorLogFileName;

    //Uses the specified filename for info logging
    private final String infoLogFileName;

    //Uses the specified filename for debugging
    private final String debugLogFileName;

    //Outputs debug info to the console
    private final boolean consoleDebugLog;

    //outputs Template resulting text to the console
    private final boolean consoleTemplateOut;

    // Directory from where to load include files (relative to)
    private final File includesDir;

    //Directory from where to load model(xml) files (relative to)
    private final File modelsDir;

    //Directory from where to load model(xml) files (relative to)
    private final File targetDir;

   //Skips production of ouput files with readonly flag
    private final boolean ignoreReadOnlyOuputFiles;

    //Skips dependency checks and forces a rebuild
    private final boolean forceRebuild;

    // The dependency manager used to avoid unnecessary builds
    public final DependencyManager dependencyManager;

    private void ConfigureLogger()
    {
        if (!logConfigured)
        {
            // Configure the logger
            URL log4Jresource = Runtime.class.getClassLoader().getResource("log4j.properties");
            Properties log4Jproperties = new Properties();
            try
            {
                log4Jproperties.load(log4Jresource.openStream());
            }
            catch (IOException ex)
            {
                // The caller wont be able to handle the exception anyway, wrap in a RuntimeException and rethrow
                throw new RuntimeException(ex);
            }

            // override the logger config
            log4Jproperties.setProperty("log4j.rootLogger", "DEBUG" + (errorLogFileName != null ? ", ErrorLog" : "") + (infoLogFileName != null ? ", InfoLog" : "") + (debugLogFileName != null ? ", DebugLog" : "") + (consoleDebugLog ? ", Console" : ""));

            // override the log filenames and append mode
            if (errorLogFileName != null)
            {
                log4Jproperties.setProperty("log4j.appender.ErrorLog.File", errorLogFileName);
                log4Jproperties.setProperty("log4j.appender.ErrorLog.Append", "true");
            }

            if (infoLogFileName != null)
            {
                log4Jproperties.setProperty("log4j.appender.InfoLog.File", infoLogFileName);
                log4Jproperties.setProperty("log4j.appender.InfoLog.Append", "true");
            }

            if (debugLogFileName != null)
            {
                log4Jproperties.setProperty("log4j.appender.DebugLog.File", debugLogFileName);
                log4Jproperties.setProperty("log4j.appender.DebugLog.Append", "true");
            }

            // Configure log4j
            PropertyConfigurator.configure(log4Jproperties);
            logConfigured = true;
        }
    }

    private static boolean logConfigured = false;

    public String run(File templatesDir, List<String> templateFileNames) throws Exception
    {
        Log log = LogFactory.getLog("Runtime");
        try
        {
            StringBuffer buffer = new StringBuffer();
            // access non-option arguments and generate the templates
            
            for (String fileName : templateFileNames)
            {
                if (!targetDir.exists())
                {
                    if (!targetDir.mkdirs())
                    {
                        throw new RuntimeException(String.format("Cannot create ouput directory:[%s]", targetDir));
                    }
                }

                File file = new File(templatesDir, fileName);
                if (file.canRead())
                {
                    TemplateDependencies templateDependencies =  dependencyManager.getTemplateDependencies(file);

                    if (!dependencyManager.getTemplateDependencies(file).isUpToDate() || forceRebuild)
                    {
                        String result = Template.run(templatesDir, includesDir, modelsDir, targetDir, fileName, ignoreReadOnlyOuputFiles, templateDependencies);
                        buffer.append(result);
                        if (consoleTemplateOut)
                        {
                            System.out.print(result);
                        }
                        dependencyManager.commit();
                    }
                    else
                    {
                        log.info(String.format("File dependencies are up to date, skipping template generation:[%s]", file));
                    }
                }
                else
                {
                    throw new RuntimeException(String.format("Cannot read file:[%s]", file));
                }
            }
            return buffer.toString();
        }
        catch(Exception ex)
        {
            log.fatal(ex);
            throw ex;
        }
    }

    public static Log CreateLogger(Class classObj)
    {
        return LogFactory.getLog(classObj.getName().replace("com.CodeSeance.JSeance.CodeGenXML.", ""));
    }
}
