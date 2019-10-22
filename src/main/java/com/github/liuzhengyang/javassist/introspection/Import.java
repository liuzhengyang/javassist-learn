package com.github.liuzhengyang.javassist.introspection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

/**
 * All the class names in source code must be fully qualified (they must include package names).
 * However, the java.lang package is an exception;
 * for example, the Javassist compiler can resolve Object as well as java.lang.Object.
 *
 * To tell the compiler to search other packages when resolving a class name, call importPackage() in ClassPool.
 *
 * Note that importPackage() does not affect the get() method in ClassPool.
 * Only the compiler considers the imported packages.
 * The parameter to get() must be always a fully qualified name.
 */
public class Import {
    public static void main(String[] args) throws Exception {
        useImport();
    }

    private static void useImport() throws CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        classPool.importPackage("java.util");
        CtClass ctClass = classPool.makeClass("Test");
        CtField make = CtField.make("private Map map;", ctClass);
        ctClass.addField(make);
    }
}
