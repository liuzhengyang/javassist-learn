package com.github.liuzhengyang.javassist.readwrite;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

/**
 * To define a new class from scratch, makeClass() must be called on a ClassPool.
 * This program defines a class Point including no members. Member methods of Point can be created with factory methods declared in CtNewMethod and appended to Point with addMethod() in CtClass.
 *
 * makeClass() cannot create a new interface; makeInterface() in ClassPool can do. Member methods in an interface can be created with abstractMethod() in CtNewMethod. Note that an interface method is an abstract method.
 */
public class DefineNewClass {
    public static void main(String[] args) throws Exception {
        createNewClass();
    }

    private static void createNewClass() throws CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass newClass = classPool.makeClass("MakeNewClass");
        newClass.writeFile("/tmp/");
        CtClass newInterface = classPool.makeInterface("MakeNewInterface");
        newInterface.writeFile("/tmp");
    }
}
