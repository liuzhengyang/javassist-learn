package com.github.liuzhengyang.javassist.readwrite;

import javassist.ByteArrayClassPath;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

/**
 * The default ClassPool returned by a static method ClassPool.getDefault() searches the same path that the underlying JVM (Java virtual machine) has.
 * If a program is running on a web application server such as JBoss and Tomcat, the ClassPool object may not be able to find user classes
 * since such a web application server uses multiple class loaders as well as the system class loader. In that case, an additional class path must be registered to the ClassPool.
 */
public class ClassSearchPath {
    public static void main(String[] args) throws Exception {
        registerClassPath();
    }

    private static void registerClassPath() throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        // this statement registers the classpath that was used to loading the `ClassSearchPath.class`.
        classPool.insertClassPath(new ClassClassPath(ClassSearchPath.class));

        // You can register a directory name as the class search path
        classPool.insertClassPath("/tmp/");

        // add a byte array
        classPool.insertClassPath(new ByteArrayClassPath("foo", new byte[]{1, 2, 3}));
    }
}
