package com.github.liuzhengyang.javassist.readwrite;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * If a CtClass object is converted into a class file by writeFile(), toClass(), or toBytecode(), Javassist freezes that CtClass object.
 * Further modifications of that CtClass object are not permitted.
 * This is for warning the developers when they attempt to modify a class file that has been already loaded since the JVM does not allow reloading a class.
 */
public class DefrostClass {
    public static void main(String[] args) throws Exception {
        froze();
    }

    private static void froze() throws IOException, CannotCompileException, NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.github.liuzhengyang.javassist.testdata.Foo");
        ctClass.setSuperclass(classPool.get("java.lang.Object"));
//        ctClass.writeFile();
        // While debugging, you might want to temporarily stop pruning and freezing and write a modified class file to a disk drive. debugWriteFile() is a convenient method for that purpose. It stops pruning, writes a class file, defrosts it, and turns pruning on again (if it was initially on).
        ctClass.debugWriteFile("/tmp/");
        ctClass.setSuperclass(classPool.get("java.util.HashMap"));
        ctClass.writeFile("/tmp/");
    }
}
