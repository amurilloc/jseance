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

import com.CodeSeance.JSeance.CodeGenXML.XMLElements.Template;
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

/**
 * This is the args4j class to define command line options for the executable Jar execution method
 *
 * @author Andres Murillo
 * @version 1.0
 */
public class CommandLineExecution
{
    @Option(name="-rebuild",usage="forces output files to be re-created regadless of dependency state")
    private boolean forceRebuild = false;

    @Option(name="-outputDir",usage="root output directory, if not specified same as template file")
    private File outputDir = new File(".");

    @Option(name="-modelsDir",usage="models root directory, if not specified same as template file")
    private File modelsDir = new File(".");

    @Argument
    private List<String> arguments = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
            new CommandLineExecution().doMain(args);
        }

        public void doMain(String[] args) throws IOException
        {
            CmdLineParser parser = new CmdLineParser(this);

            // if you have a wider console, you could increase the value;
            // here 80 is also the default
            parser.setUsageWidth(80);

            try
            {
                parser.parseArgument(args);
            }
            catch( CmdLineException e ) {
                System.err.println(e.getMessage());
                System.err.println("java -jar codegenxml-1.0.jar [options...] arguments...");
                parser.printUsage(System.err);
                System.err.println();
                return;
            }

            // access non-option arguments
            for(String fileName : arguments)
            {
                File file = new File(fileName);
                if (file.canRead())
                {
                    URL log4Jresource = Runtime.class.getClassLoader().getResource("log4j.properties");
                    PropertyConfigurator.configure(log4Jresource);
                    String result = Template.run(file.getParentFile(), fileName);
                    System.out.print(result);
                }
                else
                {
                    throw new RuntimeException(String.format("Cannot read file:[%s]", file));
                }
            }
        }

}
