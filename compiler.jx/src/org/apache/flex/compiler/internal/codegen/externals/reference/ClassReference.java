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

package org.apache.flex.compiler.internal.codegen.externals.reference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.flex.compiler.internal.codegen.externals.utils.DebugLogUtils;
import org.apache.flex.compiler.internal.codegen.externals.utils.JSTypeUtils;

import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.JSDocInfoBuilder;
import com.google.javascript.rhino.JSTypeExpression;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.jstype.JSType;

public class ClassReference extends BaseReference
{

    private boolean isFinal;

    private List<String> imports = new ArrayList<String>();
    private MethodReference constructor;
    private Map<String, FieldReference> fields = new HashMap<String, FieldReference>();
    private Map<String, MethodReference> methods = new HashMap<String, MethodReference>();

    private Node nameNode;

    private Node functionNode;

    @SuppressWarnings("unused")
    private Node paramListNode;

    private boolean isNamespace;

    public void setIsNamespace(boolean isNamespace)
    {
        this.isNamespace = isNamespace;
    }

    public boolean isNamespace()
    {
        return isNamespace;
    }

    public MethodReference getConstructor()
    {
        return constructor;
    }

    public Map<String, FieldReference> getFields()
    {
        return fields;
    }

    public Map<String, MethodReference> getMethods()
    {
        return methods;
    }

    public FieldReference getField(String name)
    {
        return fields.get(name);
    }

    public MethodReference getMethod(String name)
    {
        return methods.get(name);
    }

    public boolean isFinal()
    {
        return isFinal;
    }

    public void setFinal(boolean isFinal)
    {
        this.isFinal = isFinal;
    }

    public final boolean isInterface()
    {
        return getComment().isInterface();
    }

    /**
     * 
     * @param model
     * @param node (FUNCTION [NAME, PARAM_LIST, BLOCK]), or (ASSIGN [FUNCTION
     *        [NAME, PARAM_LIST, BLOCK]])
     * @param qualfiedName
     * @param comment
     */
    public ClassReference(ReferenceModel model, Node node, String qualfiedName)
    {
        super(model, node, qualfiedName, node.getJSDocInfo());

        nameNode = null;
        functionNode = null;
        paramListNode = null;

        if (comment.getTypedefType() != null)
        {
            //System.out.println(node.toStringTree());
            /*
             VAR 727 [jsdoc_info: JSDocInfo] [source_file: [w3c_rtc]] [length: 21]
                NAME MediaConstraints 727 [source_file: [w3c_rtc]] [length: 16]
             */
        }
        else if (comment.isConstant())
        {
            /*
             VAR 882 [jsdoc_info: JSDocInfo]
                NAME Math 
                    OBJECTLIT
             */
            constructor = new NullConstructorReference(model, this, node,
                    getBaseName(), comment);
        }
        else if (node.isFunction())
        {
            /*
             FUNCTION FooVarArgs 43 [jsdoc_info: JSDocInfo]
                NAME FooVarArgs
                PARAM_LIST
                    NAME arg1
                    NAME var_args
                BLOCK 43
             */
            nameNode = node.getChildAtIndex(0);
            functionNode = node;
            paramListNode = functionNode.getChildAtIndex(1);
        }
        else if (node.isVar())
        {
            /*
            VAR 67 [jsdoc_info: JSDocInfo]
                NAME VarAssignFooNoArgs
                    FUNCTION 
                        NAME 
                        PARAM_LIST
                        BLOCK
             */
            nameNode = node.getChildAtIndex(0);
            functionNode = nameNode.getChildAtIndex(0);
            try
            {
                paramListNode = functionNode.getChildAtIndex(1);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if (node.isAssign() && node.getChildAtIndex(1).isFunction())
        {
            /*
             ASSIGN 60 [jsdoc_info: JSDocInfo]
                NAME AssignFooNoArgs
                FUNCTION
                    NAME
                    PARAM_LIST
                    BLOCK
             */
            nameNode = node.getFirstChild();
            functionNode = node.getLastChild();
            // this is an anonymous function assignment, no name
            //functionNameNode = functionNode.getChildAtIndex(0);
            paramListNode = functionNode.getChildAtIndex(1);
        }

        if (functionNode != null)
        {
            constructor = new MethodReference(model, this, functionNode,
                    getBaseName(), comment, false);
        }

    }

    public boolean hasSuperField(String fieldName)
    {
        List<ClassReference> list = getSuperClasses();
        for (ClassReference reference : list)
        {
            if (reference.hasField(fieldName))
                return true;
        }
        return false;
    }

    public boolean hasSuperMethod(String methodName)
    {
        List<ClassReference> list = getSuperClasses();
        for (ClassReference reference : list)
        {
            if (reference.hasMethod(methodName))
                return true;
        }
        return false;
    }

    public MethodReference getSuperMethod(String methodName)
    {
        List<ClassReference> list = getSuperClasses();
        for (ClassReference reference : list)
        {
            if (reference.hasMethod(methodName))
                return reference.getMethod(methodName);
        }

        list = getAllImplInterfaces(); // return all our interfaces and all superclass
        for (ClassReference reference : list)
        {
            if (reference.hasMethod(methodName))
                return reference.getMethod(methodName);
        }

        return null;
    }

    public List<ClassReference> getSuperClasses()
    {
        ArrayList<ClassReference> result = new ArrayList<ClassReference>();
        ClassReference superClass = getSuperClass();
        while (superClass != null)
        {
            result.add(superClass);
            superClass = superClass.getSuperClass();
        }
        //        ClassReference objectReference = getModel().getClassReference("Object");
        //        // TODO tests
        //        if (objectReference != null)
        //            result.add(objectReference);
        return result;
    }

    public List<ClassReference> getAllImplInterfaces()
    {
        ArrayList<ClassReference> result = new ArrayList<ClassReference>();
        for (JSTypeExpression jsTypeExpression : getComment().getImplementedInterfaces())
        {
            String interfaceName = jsTypeExpression.evaluate(null,
                    getModel().getJSCompiler().getTypeRegistry()).getDisplayName();
            ClassReference classReference = getModel().getClassReference(
                    interfaceName);
            if (classReference != null)
                result.add(classReference);
        }

        return result;
    }

    public List<ClassReference> getImplementedInterfaces()
    {
        ArrayList<ClassReference> result = new ArrayList<ClassReference>();
        for (JSTypeExpression jsTypeExpression : getComment().getImplementedInterfaces())
        {
            String interfaceName = jsTypeExpression.evaluate(null,
                    getModel().getJSCompiler().getTypeRegistry()).toAnnotationString();
            //System.out.println("      !!!!!!!! [" + interfaceName + "]");
            ClassReference reference = getModel().getClassReference(
                    interfaceName);
            if (reference != null)
                result.add(reference);
        }
        return result;
    }

    public List<ClassReference> getSuperInterfaces()
    {
        ArrayList<ClassReference> result = new ArrayList<ClassReference>();
        ClassReference superClass = getSuperClass();
        while (superClass != null)
        {
            result.add(superClass);
            superClass = superClass.getSuperClass();
        }
        return result;
    }

    public boolean hasField(String fieldName)
    {
        return fields.containsKey(fieldName);
    }

    public boolean hasInstanceField(String fieldName)
    {
        if (!fields.containsKey(fieldName))
            return false;

        return !fields.get(fieldName).isStatic();
    }

    public boolean hasStaticField(String fieldName)
    {
        if (!fields.containsKey(fieldName))
            return false;

        return fields.get(fieldName).isStatic();
    }

    public boolean hasMethod(String methodName)
    {
        return methods.containsKey(methodName);
    }

    public boolean hasInstanceMethod(String fieldName)
    {
        if (!methods.containsKey(fieldName))
            return false;

        return !methods.get(fieldName).isStatic();
    }

    public boolean hasStaticMethod(String fieldName)
    {
        if (!methods.containsKey(fieldName))
            return false;

        return methods.get(fieldName).isStatic();
    }

    public FieldReference addField(Node node, String fieldName,
            JSDocInfo comment, boolean isStatic)
    {
        if (hasField(fieldName))
        {
            // XXX Warning
            return null;
        }

        if (isNamespace)
            isStatic = false;

        if (comment == null)
        {
            DebugLogUtils.err("Field comment null for; "
                    + node.getQualifiedName());
            //DebugLogUtils.err(node);
            JSDocInfoBuilder b = new JSDocInfoBuilder(true);
            b.recordBlockDescription("Generated doc for missing field JSDoc.");
            comment = b.build();
        }

        FieldReference field = new FieldReference(getModel(), this, node,
                fieldName, comment, isStatic);

        fields.put(fieldName, field);
        return field;
    }

    public MethodReference addMethod(Node node, String functionName,
            JSDocInfo comment, boolean isStatic)
    {
        if (isNamespace)
            isStatic = false;

        if (comment == null)
        {
            DebugLogUtils.err("Method comment null for; "
                    + node.getQualifiedName());
            //DebugLogUtils.err(node);
            JSDocInfoBuilder b = new JSDocInfoBuilder(true);
            b.recordBlockDescription("Generated doc for missing method JSDoc.");
            comment = b.build();
        }

        MethodReference method = new MethodReference(getModel(), this, node,
                functionName, comment, isStatic);
        methods.put(functionName, method);
        return method;
    }

    @Override
    public void emit(StringBuilder sb)
    {
        String packageName = getPackageName();

        sb.append("package ");
        if (!packageName.equals(""))
            sb.append(packageName + " ");
        sb.append("{\n");
        sb.append("\n");

        printImports(sb);

        boolean isInterface = isInterface();

        if (isInterface)
        {
            printInterface(sb);
        }
        else
        {
            printClass(sb);
        }

        sb.append("{\n");
        sb.append("\n");

        if (!isInterface)
        {
            printConstructor(sb);
            sb.append("\n");
        }

        for (Entry<String, FieldReference> fieldSet : getFields().entrySet())
        {
            fieldSet.getValue().emit(sb);
            sb.append("\n");
        }

        for (Entry<String, MethodReference> methodSet : getMethods().entrySet())
        {
            MethodReference method = methodSet.getValue();
            //if (!method.isOverride())
            //{
            method.emit(sb);
            sb.append("\n");
            //}
        }

        sb.append("}\n");
        sb.append("}\n"); // package
    }

    private void printClass(StringBuilder sb)
    {
        boolean isDynamic = false;

        sb.append("public ");
        if (isDynamic)
        {
            sb.append("dynamic ");
        }

        if (isFinal)
        {
            sb.append("final ");
        }

        sb.append("class ");
        sb.append(getBaseName() + " ");

        if (getComment().hasBaseType())
        {
            printSuperClass(sb);
            sb.append(" ");
        }
        else
        {
            // XXX JSObject extends
            //sb.append("extends JSObject ");
        }

        if (!isInterface())
        {
            printImplements(sb);
        }
    }

    private void printInterface(StringBuilder sb)
    {
        sb.append("public interface ");

        sb.append(getQualifiedName() + " ");

        List<JSTypeExpression> extendedInterfaces = getComment().getExtendedInterfaces();
        int len = extendedInterfaces.size();
        int i = 0;
        if (len > 0)
        {
            sb.append("extends ");
            for (JSTypeExpression jsTypeExpression : extendedInterfaces)
            {
                String value = jsTypeExpression.evaluate(null,
                        getModel().getJSCompiler().getTypeRegistry()).toAnnotationString();
                sb.append(value);
                if (i < len - 1)
                    sb.append(", ");
            }
            sb.append(" ");
        }
    }

    private void printSuperClass(StringBuilder sb)
    {
        sb.append("extends ");
        String value = JSTypeUtils.toTypeJsType(getModel().getJSCompiler(),
                getComment().getBaseType()).toString();
        sb.append(value);
    }

    private void printImplements(StringBuilder sb)
    {
        List<JSTypeExpression> implementedInterfaces = getComment().getImplementedInterfaces();
        if (implementedInterfaces.size() == 0)
            return;

        sb.append("implements ");

        int len = implementedInterfaces.size();
        for (int i = 0; i < len; i++)
        {
            String value = implementedInterfaces.get(i).evaluate(null,
                    getModel().getJSCompiler().getTypeRegistry()).getDisplayName();
            sb.append(value);
            if (i < len - 1)
                sb.append(", ");
        }

        sb.append(" ");
    }

    private void printConstructor(StringBuilder sb)
    {
        if (constructor != null)
        {
            constructor.emit(sb);
        }
    }

    private void printImports(StringBuilder sb)
    {
        sb.append("\n");
        for (String imp : imports)
        {
            sb.append("import " + imp + ";\n");
        }
        sb.append("\n");
    }

    public File getFile(File asSourceRoot)
    {
        String packagePath = toPackagePath();
        return new File(asSourceRoot, packagePath + File.separator
                + getBaseName() + ".as");
    }

    private String toPackagePath()
    {
        String packageName = getPackageName();

        String[] cname = packageName.split("\\.");
        String sdirPath = "";
        if (cname.length > 0)
        {
            for (int i = 0; i < cname.length; i++)
            {
                sdirPath += cname[i] + File.separator;
            }

            return sdirPath;
        }

        return "";
    }

    public boolean isMethodOverrideFromInterface(MethodReference reference)
    {
        if (!hasImplementations())
            return false;

        List<JSTypeExpression> implementedInterfaces = getComment().getImplementedInterfaces();
        for (JSTypeExpression jsTypeExpression : implementedInterfaces)
        {
            String interfaceName = jsTypeExpression.evaluate(null,
                    getModel().getJSCompiler().getTypeRegistry()).getDisplayName();
            ClassReference classReference = getModel().getClassReference(
                    interfaceName);
            return classReference.hasSuperMethod(reference.getQualifiedName());
        }

        return false;
    }

    public MethodReference getMethodOverrideFromInterface(
            MethodReference reference)
    {
        // get all super classes, reverse and search top down
        List<ClassReference> superClasses = getSuperClasses();
        superClasses.add(0, this);
        Collections.reverse(superClasses);

        // for each superclass, get all implemented interfaces
        for (ClassReference classReference : superClasses)
        {
            List<ClassReference> interfaces = classReference.getImplementedInterfaces();
            for (ClassReference interfaceReference : interfaces)
            {
                // check for the method on the interface
                MethodReference method = interfaceReference.getMethod(reference.getBaseName());
                if (method != null)
                    return method;
            }
        }

        return null;
    }

    private boolean hasImplementations()
    {
        return getComment().getImplementedInterfaceCount() > 0;
    }

    public boolean _hasImplements(String interfaceName)
    {
        //        boolean hasImplements = TagUtils.hasTags(this, "implements");
        //        if (hasImplements)
        //        {
        //            List<DocletTag> impls = TagUtils.getTags(this, "implements");
        //            for (DocletTag tag : impls)
        //            {
        //                String value = tag.getValue();
        //                value = value.substring(1, value.indexOf("}"));
        //                return value.equals(interfaceName);
        //            }
        //        }
        return false;
    }

    public ClassReference getSuperClass()
    {
        if (getBaseName().equals("Object"))
            return null;

        JSTypeExpression baseType = getComment().getBaseType();
        if (baseType != null)
        {
            JSType jsType = baseType.evaluate(null,
                    getModel().getJSCompiler().getTypeRegistry());
            if (jsType != null)
                return getModel().getClassReference(jsType.getDisplayName());
        }
        else
        {
            return getModel().getObjectReference();
        }

        return null;
    }

    public boolean hasSuperFieldConflict(FieldReference reference)
    {
        //        ClassReference2 superClass = getSuperClass();
        //        if (superClass != null)
        //            return superClass.getInstanceFields().containsKey(
        //                    reference.getName());
        return false;
    }

    public boolean isPropertyInterfaceImplementation(FieldReference reference)
    {
        //        List<ClassReference2> superInterfaces = getSuperInterfaces();
        //        for (ClassReference2 interfaceRef : superInterfaces)
        //        {
        //            if (interfaceRef == null)
        //            {
        //                System.err.println("isPropertyInterfaceImplementation() null");
        //                continue;
        //            }
        //            if (interfaceRef.hasFieldConflict(reference))
        //                return true;
        //        }
        return false;
    }

    public boolean hasLocalMethodConflict(String functionName)
    {
        return methods.containsKey(functionName);
    }

    public boolean hasFieldConflict(FieldReference reference)
    {
        return getFields().containsKey(reference.getQualifiedName());
    }

    public void addImport(String qualifiedName)
    {
        imports.add(qualifiedName);
    }

}
