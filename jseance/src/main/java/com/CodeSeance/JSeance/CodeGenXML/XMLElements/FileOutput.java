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

package com.CodeSeance.JSeance.CodeGenXML.XMLElements;

import com.CodeSeance.JSeance.CodeGenXML.Context;
import com.CodeSeance.JSeance.CodeGenXML.ExecutionError;
import com.CodeSeance.JSeance.CodeGenXML.XMLAttribute;
import org.w3c.dom.Element;

import java.io.*;
import java.util.Hashtable;

/**
 * Class for creating a TextSink with file destination
 *
 * @author Andres Murillo
 * @version 1.0
 */
class FileOutput extends HierarchicalNode
{

    public FileOutput(Element element)
    {
        super(element);
        if (fileEncoding == null)
        {
            fileEncoding = new FileEncoding();
        }
    }

    // Text sink to use in current context
    private StringBuffer buffer = null;

    @XMLAttribute
    String fileName;

    @XMLAttribute
    String encoding;

    @XMLAttribute
    boolean writeXMLHeader;

    // The file to write to
    private File file = null;

    private boolean skipFile = false;

    @Override
    public void onContextEnter(Context context)
    {
        file = new File(context.getManager().targetDir, fileName);

        skipFile = file.exists() && !file.canWrite() && context.getManager().ignoreReadOnlyOuputFiles;
        if (skipFile)
        {
            context.LogInfoMessage(log, "FileOutput", String.format("Readonly flag set, skipping output fileName:[%s]", fileName));
        }
        else
        {
            context.LogInfoMessage(log, "FileOutput", String.format("Processing children and writing to fileName:[%s]", fileName));

            if (file.exists() && !file.canWrite())
            {
                throw new RuntimeException(ExecutionError.TARGET_FILE_READONLY.getMessage(file));
            }

            // Change the sink of the current context
            buffer = new StringBuffer();
            context.setTextSink(buffer);
            ExecuteChildren(context);
        }
    }

    @Override
    public void onContextExit(Context context)
    {
        if (!skipFile)
        {
            String text = buffer.toString();
            context.LogInfoMessage(log, "FileOutput", String.format("Children produced:[%s]", text));

            try
            {
                if (ExecutionError.simulate_CANNOT_WRITE_TARGET_FILE)
                {
                    ExecutionError.simulate_CANNOT_WRITE_TARGET_FILE = false;
                    throw new IOException("Simulated exception for log testing");
                }

                OutputStream fileOutputStream = new FileOutputStream(file);
                OutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

                assert (encoding != null && !"".equals(encoding));
                String javaEncoding = fileEncoding.getJavaEncoding(encoding);
                assert (javaEncoding != null && !"".equals(javaEncoding));
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bufferedOutputStream, javaEncoding);

                if (writeXMLHeader)
                {
                    String header = String.format("<?xml version=\"1.0\" encoding=\"%s\"?>\r\n", encoding);
                    outputStreamWriter.write(header);
                }

                outputStreamWriter.write(text);

                outputStreamWriter.flush();
                outputStreamWriter.close();

                // Add the dependency to the file
                context.getManager().templateDependencies.addOutputFile(file);

            }
            catch (IOException ex)
            {
                throw new RuntimeException(ExecutionError.CANNOT_WRITE_TARGET_FILE.getMessage(file, ex.getMessage()));
            }

        }
    }

    private class FileEncoding
    {
        private Hashtable<String, String> encodings = new Hashtable<String, String>();

        public String getJavaEncoding(String xmlEncoding)
        {
            return encodings.get(xmlEncoding);
        }

        public FileEncoding()
        {
            encodings.put("ISO-8859-1", "8859_1");
            encodings.put("ISO-8859-2", "8859_2");
            encodings.put("ISO-8859-3", "8859_3");
            encodings.put("ISO-8859-4", "8859_4");
            encodings.put("ISO-8859-5", "8859_5");
            encodings.put("ISO-8859-6", "8859_6");
            encodings.put("ISO-8859-7", "8859_7");
            encodings.put("ISO-8859-8", "8859_8");
            encodings.put("ISO-8859-9", "8859_9");

            encodings.put("ISO-8859-13", "ISO8859_13");
            encodings.put("ISO-8859-15", "ISO8859_15_FDIS");
            encodings.put("UTF-8", "UTF8");
            encodings.put("UTF-16", "UnicodeBig");
            encodings.put("ISO-2022-JP", "JIS");
            encodings.put("Shift_JIS", "SJIS");
            encodings.put("EUC-JP", "EUCJIS");
            encodings.put("US-ASCII", "ASCII");
            encodings.put("GBK", "GBK");
            encodings.put("Big5", "Big5");
            encodings.put("ISO-2022-CN", "ISO2022CN");
            encodings.put("ISO-2022-KR", "ISO2022KR");
        }
    }

    private static FileEncoding fileEncoding = null;
}