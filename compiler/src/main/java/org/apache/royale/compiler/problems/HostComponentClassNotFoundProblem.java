/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.royale.compiler.problems;

import org.apache.royale.compiler.definitions.metadata.IMetaTag;

/**
 * Created when the specified class name could not be resolved.
 */
public final class HostComponentClassNotFoundProblem extends CompilerProblem
{
    public static final String DESCRIPTION =
        "[${HOST_COMPONENT}] class '${className}' not found.";

    public static final int errorCode = 1459;
    public HostComponentClassNotFoundProblem(IMetaTag site, String className)
    {
        super(site);
        this.className = className;
    }

    public final String className;
    
    // Prevent these from being localized.
    public final String HOST_COMPONENT = "HostComponent";
}
