package com.github.liuzhengyang.javassist.introspection;

import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

public class AddMethodField {
    public static void main(String[] args) throws Exception {
        addMethod();
    }

    private static void addMethod() throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.github.liuzhengyang.javassist.testdata.Foo");
        CtMethod method = CtNewMethod.make("public void newMethod() {\n" +
                "        System.out.println(\"this is new method\");\n" +
                "    }", ctClass);
        ctClass.addMethod(method);

        CtConstructor constructor = CtNewConstructor.make(new CtClass[]{classPool.get("java.lang.String")},
                new CtClass[]{}, "{System.out.println(\"Adding a constructor\");}", ctClass);
        ctClass.addConstructor(constructor);

        CtField field = CtField.make("private String name;", ctClass);
        ctClass.setModifiers(ctClass.getModifiers() | Modifier.PUBLIC);
        ctClass.setModifiers(ctClass.getModifiers() & ~Modifier.ABSTRACT);
        ctClass.addField(field, "init values");

        System.out.println(Arrays.toString(ctClass.getDeclaredConstructors()));
        System.out.println(Arrays.toString(ctClass.getDeclaredMethods()));
    }

}
