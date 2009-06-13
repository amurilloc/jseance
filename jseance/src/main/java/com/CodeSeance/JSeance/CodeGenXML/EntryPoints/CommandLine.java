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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the args4j class to define command line options for the executable Jar point for functional tests
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class CommandLine implements Logger
{
    @Option(name = "-errorLogFile", usage = "uses the specified filename for error logging, default is 'jseance-errors.log'")
    String errorLogFileName = "./jseance-errors.log";

    @Option(name = "-infoLogFile", usage = "uses the specified filename for info logging, default is 'jseance-info.log'")
    String infoLogFileName = "./jseance-info.log";

    @Option(name = "-debugLogFile", usage = "uses the specified filename for debugging, default is off")
    String debugLogFileName = null;

    @Option(name = "-consoleInfoLog", usage = "outputs status info to the console")
    boolean consoleInfoLog = false;

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

    // The list of files to process
    @Argument
    List<String> arguments = new ArrayList<String>();

    // static entry point for executable jar
    public static void main(String[] args)
    {
        try
        {
            CommandLine commandLine = new CommandLine();
            String result = commandLine.run(args);
            if (commandLine.consoleTemplateOut)
            {
                System.out.println(result);
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            System.exit(2);
        }
    }

    // Parses the command line arguments and executes the specified templates
    public String run(String[] args)
    {
        com.CodeSeance.JSeance.CodeGenXML.Runtime runtime = new com.CodeSeance.JSeance.CodeGenXML.Runtime(errorLogFileName, infoLogFileName, debugLogFileName, includesDir, modelsDir, targetDir, ignoreReadOnlyOuputFiles, forceRebuild);

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

        return runtime.run(templatesDir, arguments, this);
    }

    public void infoMessage(String message)
    {
        if (consoleInfoLog)
        {
            System.out.println(message);
        }
    }

    public void errorMessage(String message)
    {
        if (consoleInfoLog)
        {
            System.out.println(message);
        }
    }
}
