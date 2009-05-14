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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.net.URL;

/**
 * Main library entry point
 *
 * @author Andres Murillo
 * @version %I%, %G%
 */
public class Runtime
{
    public static void main(String[] args)
    {
        // Create a local logger for the static context
        Log log = CreateLogger(Runtime.class);

        for(String fileName : args)
        {
            File file = new File(fileName);
            if (file.canRead())
            {
                System.out.print(run(file.getParentFile(), file.getName()));
            }
            else
            {
                throw new RuntimeException(String.format("Cannot read file:[%s]", file));
            }
        }
    }

    public static final String run(File parentPath, String fileName)
    {
        configureLog();
        return Template.run(parentPath, fileName);
    }

    public static void configureLog()
    {
        URL log4Jresource = Runtime.class.getClassLoader().getResource("log4j.properties");
        PropertyConfigurator.configure(log4Jresource);
    }

    public static Log CreateLogger(Class classObj)
    {
        return LogFactory.getLog(classObj.getName().replace("com.CodeSeance.JSeance.CodeGenXML.", ""));
    }
}
