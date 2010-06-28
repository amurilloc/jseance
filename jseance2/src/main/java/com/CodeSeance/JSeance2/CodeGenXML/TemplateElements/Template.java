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

package com.CodeSeance.JSeance2.CodeGenXML.TemplateElements;

import com.CodeSeance.JSeance2.CodeGenXML.Context;
import com.CodeSeance.JSeance2.CodeGenXML.ExecutionError;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 Created by IntelliJ IDEA.
 User: amurillo
 Date: Jun 15, 2010
 Time: 5:27:40 PM
 To change this template use File | Settings | File Templates.
 */
public class Template extends HierarchicalNode
{
    public Template(File templateFile) throws IOException
    {
        StringBuffer buffer = new StringBuffer(1024);
        BufferedReader reader = new BufferedReader(new FileReader(templateFile));

        try
        {
            char[] chars = new char[1024];
            int readChars;
            while ((readChars = reader.read(chars)) > -1)
            {
                buffer.append(String.valueOf(chars, 0, readChars));
            }
        }
        finally
        {
            reader.close();
        }

        this.text = buffer.toString();
        this.pendingText = this.text;
        lineReader = new BufferedReader(new StringReader(this.text));
        currentLine = lineReader.readLine();
        charPos = 0;
        position = new Position(templateFile, 1, 0);
    }

    private final String text;
    private String pendingText;
    private int charPos;
    private final Position position;
    private final BufferedReader lineReader;
    private String currentLine;

    public Position getPosition()
    {
        return position;
    }

    private String getPendingText()
    {
        return pendingText;
    }

    private void advanceText(int characters)
    {
        if ((position.col + characters) > currentLine.length())
        {
            position.col = (characters - currentLine.length());
            try
            {
                currentLine = lineReader.readLine();
                if (ExecutionError.simulate_MEMORY_IO_ERROR)
                {
                    ExecutionError.simulate_MEMORY_IO_ERROR = false;
                    throw new IOException("Simulated Exception for Error testing");    
                }
            }
            catch (IOException ex)
            {
                // This should never happen since we are reading from memory
                assert false : ex.getMessage();
            }
            position.line++;
        }
        charPos += characters;
        pendingText = text.substring(charPos);
    }

    public void throwError(String message)
    {
        throw new RuntimeException(ExecutionError.INVALID_TEMPLATE_FORMAT.getMessage(position.templateFile.getName(), position.line, position.col, message));
    }

    public boolean isDone()
    {
        return (charPos >= text.length());
    }

    public String peekNodeTag()
    {
        String pendingText = getPendingText();

        Matcher matcher = inlinePattern.matcher(pendingText);

        if (matcher.find())
        {
            // Try to see if its possible to match the whole line
            Matcher singleLineMatcher = singleLinePattern.matcher(pendingText);
            if (singleLineMatcher.find() && singleLineMatcher.start() < matcher.start())
            {
                matcher = singleLineMatcher;
            }

            // If the match was not at the beginning of the template, create a node at the beginning and return it, advance the template
            int startMatch = matcher.start();
            if (startMatch > 0)
            {
                return "text";
            }

            // Determine the tag
            String tag = matcher.group(1);
            return tag.toLowerCase();
        }
        else
        {
            return "text";
        }

    }

    // Default buffer for text accumulation
    private StringBuffer textSink = null;

    public String getText()
    {
        return textSink.toString();
    }

    private Node parent;

    @Override
    public void loadChildren(Template template, Node parent)
    {
        this.parent = parent;

        while (!isDone())
        {
            Node node = parseNode(this);
            children.add(node);
        }
    }

    @Override
    public void onExecutionStart(Context context)
    {
        textSink = new StringBuffer();
        // Sets the top text sink
        context.pushTextSink(textSink);

        // Add the dependency
        context.templateDependencies.addInputFile(position.templateFile);

        for (Node child : children)
        {
            child.loadAttributes(context);
            child.onExecutionStart(context);
            child.onExecutionEnd(context);
        }
    }

    @Override
    public void onExecutionEnd(Context context)
    {
        context.popTextSink();
        if (parent != null)
        {
            context.writeText(textSink.toString());    
        }
    }

    public Node parseNode(Node parent)
    {
        String pendingText = getPendingText();
        if (pendingText.equals(""))
        {
            throw new RuntimeException(ExecutionError.INVALID_TEMPLATE_MISSING_END.getMessage(position.templateFile.getName()));
        }

        Matcher matcher = inlinePattern.matcher(pendingText);

        if (matcher.find())
        {
            // Try to see if its possible to match the whole line
            Matcher singleLineMatcher = singleLinePattern.matcher(pendingText);
            if (singleLineMatcher.find() && singleLineMatcher.start() < matcher.start())
            {
                matcher = singleLineMatcher;
            }

            // If the match was not at the beginning of the template, create a node at the beginning and return it, advance the template
            int startMatch = matcher.start();
            if (startMatch > 0)
            {
                advanceText(startMatch);
                String text = pendingText.substring(0, startMatch);
                if (matcher == singleLineMatcher)
                {
                    // Special case when we want to supress the endline of a text section if the following line
                    // is a full line tag, for example in:
                    // @!Output('file.txt')!
                    // text
                    // @!End!
                    // text will be writtend to the file without the trailing end
                    if (text.endsWith("\r\n"))
                    {
                        text = text.substring(0, text.length() - 2);
                    }
                    else if (text.endsWith("\n"))
                    {
                        text = text.substring(0, text.length() - 1);
                    }
                }
                return new Text(text);
            }

            advanceText(matcher.end());

            // Determine the tag and arguments (if any)
            String tag = matcher.group(1);
            String arguments = matcher.groupCount() > 1 ? matcher.group(2) : null;

            // Create the node
            Node node = NodeFactory.getInstance().createNode(tag, arguments, position);
            node.loadChildren(this, parent);
            return node;
        }
        else
        {
            advanceText(pendingText.length());

            return new Text(pendingText);
        }
    }

    /*
     Pattern match documentation
     \s*        Any whitespace at the beginning
     @           Initial ampersand
     (XMLModel|Include|Eval|Output|If|ElseIf|Else|Switch|Case|Default|End|For|IfEmpty|Code|End)           Tag with capture group 0
     (            Start capture group
     \(          Opening parenthesis for parameters
     .*          Parameter data
     \)          Closing parenthesis
     )?          End capture group 1, whole group is optional
    @           End ampersand
    \s*         Any whitespace at the end
    */
    private static final Pattern inlinePattern = Pattern.compile("!(XMLModel|Include|Eval|Output|If|ElseIf|Else|Switch|Case|Default|End|For|IfEmpty|Code|End)(\\(.*?\\))?!", Pattern.CASE_INSENSITIVE);
    private static final Pattern singleLinePattern = Pattern.compile("^[ \\t\\x0B\\f]*@" + inlinePattern + "[ \\t\\x0B\\f]*(?:\\r?\\n)?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    public class Position
    {
        Position(File templateFile, int line, int col)
        {
            this.templateFile = templateFile;
            this.line = line;
            this.col = col;
        }

        private final File templateFile;
        private int line;
        private int col;

        public String getFileName()
        {
            return templateFile.getName();
        }

        public int getLine()
        {
            return line;
        }

        public int getCol()
        {
            return col;
        }
    }
}
