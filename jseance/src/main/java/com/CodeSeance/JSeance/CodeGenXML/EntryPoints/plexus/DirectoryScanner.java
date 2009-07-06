/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Ant" and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */

package com.CodeSeance.JSeance.CodeGenXML.EntryPoints.plexus;

import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for scanning a directory for files/directories which match certain criteria. Heavily modified version of the
 * original plexus Directory Scanner to avoid SNAPSHOT dependency Changed by Andres Murillo, retained original Apache
 * license
 *
 * @author Arnout J. Kuiper <a href="mailto:ajkuiper@wxs.nl">ajkuiper@wxs.nl</a>
 * @author Magesh Umasankar
 * @author <a href="mailto:bruce@callenish.com">Bruce Atherton</a>
 * @author <a href="mailto:levylambert@tiscali-dsl.de">Antoine Levy-Lambert</a>
 */
public class DirectoryScanner
{

    /**
     * Patterns which should be excluded by default, like SCM files
     * <ul>
     * <li>Misc: &#42;&#42;/&#42;~, &#42;&#42;/#&#42;#, &#42;&#42;/.#&#42;, &#42;&#42;/%&#42;%, &#42;&#42;/._&#42; </li>
     * <li>CVS: &#42;&#42;/CVS, &#42;&#42;/CVS/&#42;&#42;, &#42;&#42;/.cvsignore</li>
     * <li>RCS: &#42;&#42;/RCS, &#42;&#42;/RCS/&#42;&#42;</li>
     * <li>SCCS: &#42;&#42;/SCCS, &#42;&#42;/SCCS/&#42;&#42;</li>
     * <li>VSSercer: &#42;&#42;/vssver.scc</li>
     * <li>SVN: &#42;&#42;/.svn, &#42;&#42;/.svn/&#42;&#42;</li>
     * <li>GNU: &#42;&#42;/.arch-ids, &#42;&#42;/.arch-ids/&#42;&#42;</li>
     * <li>Bazaar: &#42;&#42;/.bzr, &#42;&#42;/.bzr/&#42;&#42;</li>
     * <li>SurroundSCM: &#42;&#42;/.MySCMServerInfo</li>
     * <li>Mac: &#42;&#42;/.DS_Store</li>
     * <li>Serena Dimension: &#42;&#42;/.metadata, &#42;&#42;/.metadata/&#42;&#42;</li>
     * <li>Mercurial: &#42;&#42;/.hg, &#42;&#42;/.hg/&#42;&#42;</li>
     * <li>GIT: &#42;&#42;/.git, &#42;&#42;/.git/&#42;&#42;</li>
     * <li>Bitkeeper: &#42;&#42;/BitKeeper, &#42;&#42;/BitKeeper/&#42;&#42;, &#42;&#42;/ChangeSet, &#42;&#42;/ChangeSet/&#42;&#42;</li>
     * <li>Darcs: &#42;&#42;/_darcs, &#42;&#42;/_darcs/&#42;&#42;, &#42;&#42;/.darcsrepo, &#42;&#42;/.darcsrepo/&#42;&#42;&#42;&#42;/-darcs-backup&#42;, &#42;&#42;/.darcs-temp-mail
     * </ul>
     */
    public static final String[] DEFAULTEXCLUDES = {
            // Miscellaneous typical temporary files
            "**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*",

            // CVS
            "**/CVS", "**/CVS/**", "**/.cvsignore",

            // RCS
            "**/RCS", "**/RCS/**",

            // SCCS
            "**/SCCS", "**/SCCS/**",

            // Visual SourceSafe
            "**/vssver.scc",

            // Subversion
            "**/.svn", "**/.svn/**",

            // Arch
            "**/.arch-ids", "**/.arch-ids/**",

            //Bazaar
            "**/.bzr", "**/.bzr/**",

            //SurroundSCM
            "**/.MySCMServerInfo",

            // Mac
            "**/.DS_Store",

            // Serena Dimensions Version 10
            "**/.metadata", "**/.metadata/**",

            // Mercurial
            "**/.hg", "**/.hg/**",

            // git
            "**/.git", "**/.git/**",

            // BitKeeper
            "**/BitKeeper", "**/BitKeeper/**", "**/ChangeSet", "**/ChangeSet/**",

            // darcs
            "**/_darcs", "**/_darcs/**", "**/.darcsrepo", "**/.darcsrepo/**", "**/-darcs-backup*", "**/.darcs-temp-mail"};

    public DirectoryScanner(List<String> includes, List<String> excludes)
    {
        // Normalize the includes and add a default search pattern if required
        this.includes = NormalizeInclusionExclusionList(includes);
        if (includes.isEmpty())
        {
            includes.add("**");
        }

        // Normalize and assign excludes
        this.excludes = NormalizeInclusionExclusionList(excludes);

        // Add all the default excludes
        excludes.addAll(Arrays.asList(DEFAULTEXCLUDES));
    }

    private List<String> NormalizeInclusionExclusionList(List<String> list)
    {
        List<String> result = new ArrayList<String>();
        for (String item : list)
        {
            String pattern = item.trim();
            if (pattern.endsWith(File.separator))
            {
                pattern += "**";
            }
            result.add(pattern);
        }
        return result;
    }

    private final List<String> includes;
    private final List<String> excludes;

    public List<File> scan(File sourceDir) throws MojoFailureException
    {
        // Validate the sourceDir
        if (!(sourceDir.exists() && sourceDir.isDirectory()))
        {
            throw new MojoFailureException(String.format("Invalid sourceDirectory :[%s]", sourceDir));
        }

        return scandir(sourceDir, "");
    }

    private List<File> scandir(File dir, String vpath)
    {
        List<File> filesIncluded = new ArrayList<File>();

        String[] files = dir.list();

        List<String> newfiles = (files != null) ? Arrays.asList(dir.list()) : new ArrayList<String>();

        for (String newfile : newfiles)
        {
            String name = vpath + newfile;
            File file = new File(dir, newfile);
            if (file.isDirectory())
            {
                if (isIncluded(name))
                {
                    if (!isExcluded(name))
                    {
                        scandir(file, name + File.separator);
                    }
                    else
                    {
                        if (couldHoldIncluded(name))
                        {
                            scandir(file, name + File.separator);
                        }
                    }
                }
                else
                {
                    if (couldHoldIncluded(name))
                    {
                        scandir(file, name + File.separator);
                    }
                }
            }
            else if (file.isFile())
            {
                if (isIncluded(name))
                {
                    if (!isExcluded(name))
                    {
                        filesIncluded.add(file);
                    }
                }

            }
        }
        return filesIncluded;
    }

    /**
     * Tests whether or not a name matches against at least one include
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         include pattern, or <code>false</code> otherwise.
     */
    private boolean isIncluded(String name)
    {
        for (String include : includes)
        {
            if (SelectorUtils.matchPath(include, name))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a name matches the start of at least one include
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against the start of at
     *         least one include pattern, or <code>false</code> otherwise.
     */
    private  boolean couldHoldIncluded(String name)
    {
        for (String include : includes)
        {

            if (SelectorUtils.matchPatternStart(include, name))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether or not a name matches against at least one exclude
     * pattern.
     *
     * @param name The name to match. Must not be <code>null</code>.
     * @return <code>true</code> when the name matches against at least one
     *         exclude pattern, or <code>false</code> otherwise.
     */
    private  boolean isExcluded(String name)
    {
        for (String exclude : excludes)
        {
            if (SelectorUtils.matchPath(exclude, name))
            {
                return true;
            }
        }
        return false;
    }
   
}
