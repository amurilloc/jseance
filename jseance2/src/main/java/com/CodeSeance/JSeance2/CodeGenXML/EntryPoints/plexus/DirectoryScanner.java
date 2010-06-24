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

package com.CodeSeance.JSeance2.CodeGenXML.EntryPoints.plexus;

import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 Class for scanning a directory for files/directories which match certain criteria. Heavily modified version of the
 original plexus Directory Scanner to avoid SNAPSHOT dependency Changed by Andres Murillo, retained original Apache
 license

 @author Arnout J. Kuiper <a href="mailto:ajkuiper@wxs.nl">ajkuiper@wxs.nl</a>
 @author Magesh Umasankar
 @author <a href="mailto:bruce@callenish.com">Bruce Atherton</a>
 @author <a href="mailto:levylambert@tiscali-dsl.de">Antoine Levy-Lambert</a> */
public class DirectoryScanner
{

    /**
     Patterns which should be excluded by default, like SCM files
     <ul>
     <li>Misc: &#42;&#42;/&#42;~, &#42;&#42;/#&#42;#, &#42;&#42;/.#&#42;, &#42;&#42;/%&#42;%, &#42;&#42;/._&#42; </li>
     <li>CVS: &#42;&#42;/CVS, &#42;&#42;/CVS/&#42;&#42;, &#42;&#42;/.cvsignore</li>
     <li>RCS: &#42;&#42;/RCS, &#42;&#42;/RCS/&#42;&#42;</li>
     <li>SCCS: &#42;&#42;/SCCS, &#42;&#42;/SCCS/&#42;&#42;</li>
     <li>VSSercer: &#42;&#42;/vssver.scc</li>
     <li>SVN: &#42;&#42;/.svn, &#42;&#42;/.svn/&#42;&#42;</li>
     <li>GNU: &#42;&#42;/.arch-ids, &#42;&#42;/.arch-ids/&#42;&#42;</li>
     <li>Bazaar: &#42;&#42;/.bzr, &#42;&#42;/.bzr/&#42;&#42;</li>
     <li>SurroundSCM: &#42;&#42;/.MySCMServerInfo</li>
     <li>Mac: &#42;&#42;/.DS_Store</li>
     <li>Serena Dimension: &#42;&#42;/.metadata, &#42;&#42;/.metadata/&#42;&#42;</li>
     <li>Mercurial: &#42;&#42;/.hg, &#42;&#42;/.hg/&#42;&#42;</li>
     <li>GIT: &#42;&#42;/.git, &#42;&#42;/.git/&#42;&#42;</li>
     <li>Bitkeeper: &#42;&#42;/BitKeeper, &#42;&#42;/BitKeeper/&#42;&#42;, &#42;&#42;/ChangeSet, &#42;&#42;/ChangeSet/&#42;&#42;</li>
     <li>Darcs: &#42;&#42;/_darcs, &#42;&#42;/_darcs/&#42;&#42;, &#42;&#42;/.darcsrepo, &#42;&#42;/.darcsrepo/&#42;&#42;&#42;&#42;/-darcs-backup&#42;, &#42;&#42;/.darcs-temp-mail
     </ul>
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
     Tests whether or not a name matches against at least one include
     pattern.

     @param name The name to match. Must not be <code>null</code>.
     @return <code>true</code> when the name matches against at least one
     include pattern, or <code>false</code> otherwise.
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
     Tests whether or not a name matches the start of at least one include
     pattern.

     @param name The name to match. Must not be <code>null</code>.
     @return <code>true</code> when the name matches against the start of at
     least one include pattern, or <code>false</code> otherwise.
     */
    private boolean couldHoldIncluded(String name)
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
     Tests whether or not a name matches against at least one exclude
     pattern.

     @param name The name to match. Must not be <code>null</code>.
     @return <code>true</code> when the name matches against at least one
     exclude pattern, or <code>false</code> otherwise.
     */
    private boolean isExcluded(String name)
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
