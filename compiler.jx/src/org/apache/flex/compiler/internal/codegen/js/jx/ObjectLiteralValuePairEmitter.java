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

package org.apache.flex.compiler.internal.codegen.js.jx;

import org.apache.flex.compiler.codegen.ISubEmitter;
import org.apache.flex.compiler.codegen.js.IJSEmitter;
import org.apache.flex.compiler.common.ISourceLocation;
import org.apache.flex.compiler.internal.codegen.as.ASEmitterTokens;
import org.apache.flex.compiler.internal.codegen.js.JSSubEmitter;
import org.apache.flex.compiler.tree.as.IExpressionNode;
import org.apache.flex.compiler.tree.as.ILiteralNode;
import org.apache.flex.compiler.tree.as.IObjectLiteralValuePairNode;

public class ObjectLiteralValuePairEmitter extends JSSubEmitter implements
        ISubEmitter<IObjectLiteralValuePairNode>
{
    public ObjectLiteralValuePairEmitter(IJSEmitter emitter)
    {
        super(emitter);
    }

    @Override
    public void emit(IObjectLiteralValuePairNode node)
    {
        ISourceLocation sourceLocationNode = (ISourceLocation) node;

        IExpressionNode nameNode = node.getNameNode();
        if (!(nameNode instanceof ILiteralNode))
        {
            startMapping(nameNode);
        }
        getWalker().walk(node.getNameNode());
        if (!(nameNode instanceof ILiteralNode))
        {
            endMapping(nameNode);
        }

        startMapping(sourceLocationNode, nameNode);
        write(ASEmitterTokens.COLON);
        endMapping(sourceLocationNode);

        IExpressionNode valueNode = node.getValueNode();
        getWalker().walk(valueNode);
    }
}