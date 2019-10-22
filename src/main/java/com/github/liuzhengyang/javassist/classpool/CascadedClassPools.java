package com.github.liuzhengyang.javassist.classpool;

import javassist.ClassPool;
import javassist.NotFoundException;

/**
 * If a program is running on a web application server, creating multiple instances of ClassPool might be necessary;
 * an instance of ClassPool should be created for each class loader (i.e. container).
 * The program should create a ClassPool object by not calling getDefault() but a constructor of ClassPool.
 */
public class CascadedClassPools {
    public static void main(String[] args) throws Exception {
        createParentChild();
    }

    private static void createParentChild() throws NotFoundException {
        ClassPool parent = ClassPool.getDefault();
        ClassPool child = new ClassPool(parent);
        child.insertClassPath("./classes");
    }
}
