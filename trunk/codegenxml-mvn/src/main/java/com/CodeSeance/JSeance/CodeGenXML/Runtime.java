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
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    @Option(name = "-errorLogFile", usage = "uses the specified filename for error logging, default is disabled")
    File errorLogFile = null;

    @Option(name = "-infoLogFile", usage = "uses the specified filename for info logging, default is disabled")
    File infoLogFile = null;

    @Option(name = "-debugLogFile", usage = "uses the specified filename for debugging, default is disabled")
    File debugLogFile = null;

    @Option(name = "-consoleDebugLog", usage = "outputs debug info to the console")
    boolean consoleDebugLog = false;

    @Option(name = "-consoleTemplateOut", usage = "outputs Template resulting text to the console")
    boolean consoleTemplateOut = false;

    @Option(name = "-templatesDir", usage = "Directory from where to load template files (relative to), default is './templates'")
    File templatesDir = new File("./templates");

    @Option(name = "-includesDir", usage = "Directory from where to load include files (relative to), default is './includes'")
    File includesDir = new File("./includes");

    @Option(name = "-modelsDir", usage = "Directory from where to load model(xml) files (relative to), default is './models'")
    File modelsDir = new File("./models");

    @Option(name = "-targetDir", usage = "Directory from where to load model(xml) files (relative to), default is './target'")
    File targetDir = new File("./target");

    @Option(name = "-ignoreReadOnlyOuputFiles", usage = "Skips production of ouput files with readonly flag")
    boolean ignoreReadOnlyOuputFiles = false;

    @Option(name = "-forceRebuild", usage = "Skips dependency checks and forces a rebuild")
    boolean forceRebuild = false;

    @Argument
    // The list of files to process
    List<String> arguments = new ArrayList<String>();

    // static entry point for executable jar
    public static void main(String[] args)
    {
        try
        {
            new Runtime().run(args);
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            System.exit(2);
        }
    }

    // Parses the command line arguments and executes the specified templates
    public String run(String[] args) throws Exception
    {
        CmdLineParser parser = new CmdLineParser(this);

        // if you have a wider console, you could increase the value;
        // here 80 is also the default
        parser.setUsageWidth(80);

        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            System.err.println(e.getMessage());
            System.err.println("java -jar codegenxml-1.0.jar [options...] templatefiles...");
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
        }

        return run();
    }

    private void ConfigureLogger() throws IOException
    {
        if (!logConfigured)
        {
            // Configure the logger
            URL log4Jresource = Runtime.class.getClassLoader().getResource("log4j.properties");
            Properties log4Jproperties = new Properties();
            log4Jproperties.load(log4Jresource.openStream());

            // override the logger config
            log4Jproperties.setProperty("log4j.rootLogger", "DEBUG" + (errorLogFile != null ? ", ErrorLog" : "") + (infoLogFile != null ? ", InfoLog" : "") + (debugLogFile != null ? ", DebugLog" : "") + (consoleDebugLog ? ", Console" : ""));

            // override the log filenames and append mode
            if (errorLogFile != null)
            {
                log4Jproperties.setProperty("log4j.appender.ErrorLog.File", errorLogFile.toString());
                log4Jproperties.setProperty("log4j.appender.ErrorLog.Append", "true");
            }

            if (infoLogFile != null)
            {
                log4Jproperties.setProperty("log4j.appender.InfoLog.File", infoLogFile.toString());
                log4Jproperties.setProperty("log4j.appender.InfoLog.Append", "true");
            }

            if (debugLogFile != null)
            {
                log4Jproperties.setProperty("log4j.appender.DebugLog.File", debugLogFile.toString());
                log4Jproperties.setProperty("log4j.appender.DebugLog.Append", "true");
            }

            // Configure log4j
            PropertyConfigurator.configure(log4Jproperties);
            logConfigured = true;
        }
    }

    private static boolean logConfigured = false;

    public String run() throws Exception
    {
        Log log = LogFactory.getLog("Runtime");
        try
        {
            ConfigureLogger();

            StringBuffer buffer = new StringBuffer();
            // access non-option arguments and generate the templates

            DependencyManager dependencyManager =  new DependencyManager(targetDir);
            
            for (String fileName : arguments)
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
