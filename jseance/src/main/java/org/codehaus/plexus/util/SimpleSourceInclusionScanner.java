package org.codehaus.plexus.util;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jdcasey
 * @version $Id: AbstractSourceInclusionScanner.java 2408 2005-08-18 13:39:41Z trygvis $
 */
public class SimpleSourceInclusionScanner
{
    protected Set<String> sourceIncludes;
    protected Set<String> sourceExcludes;

    public SimpleSourceInclusionScanner(Set<String> sourceExcludes, Set<String> sourceIncludes)
    {
        this.sourceExcludes = sourceExcludes;
        this.sourceIncludes = sourceIncludes;
    }

    protected List<String> scanForSources( File sourceDir, Set<String> sourceIncludes, Set<String> sourceExcludes )
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setFollowSymlinks( true );
        ds.setBasedir( sourceDir );

        String[] includes;
        if ( sourceIncludes.isEmpty() )
        {
            includes = new String[0];
        }
        else
        {
            includes = (String[]) sourceIncludes.toArray( new String[sourceIncludes.size()] );
        }

        ds.setIncludes( includes );

        String[] excludes;
        if ( sourceExcludes.isEmpty() )
        {
            excludes = new String[0];
        }
        else
        {
            excludes = (String[]) sourceExcludes.toArray( new String[sourceExcludes.size()] );
        }

        ds.setExcludes( excludes );
        ds.addDefaultExcludes();

        ds.scan();

        return ds.getIncludedFiles();
    }

    public Set<File> getIncludedSources( File sourceDir)
    {
        List<String> potentialSources = scanForSources( sourceDir, sourceIncludes, sourceExcludes );

        Set<File> matchingSources = new HashSet<File>();

        if ( potentialSources != null )
        {
            for (String potentialSource :  potentialSources)
            {
                matchingSources.add( new File( sourceDir, potentialSource ) );
            }
        }

        return matchingSources;
    }
}
