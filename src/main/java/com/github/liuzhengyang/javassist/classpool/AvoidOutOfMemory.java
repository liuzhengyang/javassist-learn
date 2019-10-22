package com.github.liuzhengyang.javassist.classpool;

import javassist.ClassPool;
import javassist.CtClass;

public class AvoidOutOfMemory {
    public static void main(String[] args) {
       detach();
    }

    private static void detach() {
        ClassPool classPool = ClassPool.getDefault();
        CtClass test = classPool.makeClass("Test");
        test.detach();
    }
}
