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
import org.apache.flex.compiler.common.IMetaInfo;
import org.apache.flex.compiler.internal.codegen.as.ASEmitterTokens;
import org.apache.flex.compiler.internal.codegen.js.JSSubEmitter;
import org.apache.flex.compiler.internal.codegen.js.flexjs.JSFlexJSEmitter;
import org.apache.flex.compiler.internal.tree.as.SetterNode;
import org.apache.flex.compiler.tree.as.IAccessorNode;
import org.apache.flex.compiler.tree.as.IParameterNode;

public class DefinePropertyFunctionEmitter extends JSSubEmitter implements
        ISubEmitter<IAccessorNode>
{

    public DefinePropertyFunctionEmitter(IJSEmitter emitter)
    {
        super(emitter);
    }

    @Override
    public void emit(IAccessorNode node)
    {
        // TODO (mschmalle) will remove this cast as more things get abstracted
        JSFlexJSEmitter fjs = (JSFlexJSEmitter) getEmitter();

        boolean isBindableSetter = false;
        if (node instanceof SetterNode)
        {
            IMetaInfo[] metaInfos = null;
            metaInfos = node.getMetaInfos();
            for (IMetaInfo metaInfo : metaInfos)
            {
                String name = metaInfo.getTagName();
                if (name.equals("Bindable")
                        && metaInfo.getAllAttributes().length == 0)
                {
                    isBindableSetter = true;
                    break;
                }
            }
        }
        if (isBindableSetter)
        {
            //write(ASEmitterTokens.FUNCTION);
            //emitParameters(node.getParameterNodes());
            write(ASEmitterTokens.SPACE);
            writeNewline(ASEmitterTokens.BLOCK_OPEN);

            write(ASEmitterTokens.VAR);
            write(ASEmitterTokens.SPACE);
            write("oldValue");
            write(ASEmitterTokens.SPACE);
            write(ASEmitterTokens.EQUAL);
            write(ASEmitterTokens.SPACE);
            write(ASEmitterTokens.THIS);
            write(ASEmitterTokens.MEMBER_ACCESS);
            write(node.getName());
            //write(ASEmitterTokens.PAREN_OPEN);
            //write(ASEmitterTokens.PAREN_CLOSE);
            writeNewline(ASEmitterTokens.SEMICOLON);

            // add change check
            write(ASEmitterTokens.IF);
            write(ASEmitterTokens.SPACE);
            write(ASEmitterTokens.PAREN_OPEN);
            write("oldValue");
            write(ASEmitterTokens.SPACE);
            write(ASEmitterTokens.STRICT_EQUAL);
            write(ASEmitterTokens.SPACE);
            IParameterNode[] params = node.getParameterNodes();
            write(params[0].getName());
            write(ASEmitterTokens.PAREN_CLOSE);
            write(ASEmitterTokens.SPACE);
            write(ASEmitterTokens.RETURN);
            writeNewline(ASEmitterTokens.SEMICOLON);

            write(ASEmitterTokens.THIS);
            write(ASEmitterTokens.MEMBER_ACCESS);
            write("__bindingWrappedSetter__" + node.getName());
            write(ASEmitterTokens.PAREN_OPEN);
            write(params[0].getName());
            write(ASEmitterTokens.PAREN_CLOSE);
            writeNewline(ASEmitterTokens.SEMICOLON);

            // add dispatch of change event
            writeNewline("    this.dispatchEvent(org_apache_flex_events_ValueChangeEvent.createUpdateEvent(");
            writeNewline("         this, \"" + node.getName()
                    + "\", oldValue, " + params[0].getName() + "));");
            write(ASEmitterTokens.BLOCK_CLOSE);
            //writeNewline(ASEmitterTokens.SEMICOLON);
            writeNewline();
            writeNewline();
        }
        else
        {
            fjs.emitMethodScope(node.getScopedNode());
        }
    }
}