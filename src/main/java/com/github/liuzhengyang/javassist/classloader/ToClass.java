package com.github.liuzhengyang.javassist.classloader;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * If what classes must be modified is known in advance, the easiest way for modifying the classes is as follows:
 *
 * 1. Get a CtClass object by calling ClassPool.get(),
 * 2. Modify it, and
 * 3. Call writeFile() or toBytecode() on that CtClass object to obtain a modified class file.
 */
public class ToClass {
    public static void main(String[] args) throws Exception {
        toClass();
    }

    /**
     * If whether a class is modified or not is determined at load time, the users must make Javassist collaborate with a class loader.
     * Javassist can be used with a class loader so that bytecode can be modified at load time.
     * The users of Javassist can define their own version of class loader but they can also use a class loader provided by Javassist.
     *
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void toClass() throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
//        Class<Hello> helloClass = Hello.class;
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.github.liuzhengyang.javassist.classloader.ToClass$Hello");
        CtMethod method = ctClass.getDeclaredMethod("say");
        method.insertBefore("System.out.println(\"Hello.say()\");");
        // by default, use Thread.currentThread().getContextClassLoader();
        Class clazz = ctClass.toClass();
        Hello h = (Hello) clazz.newInstance();
        h.say();

        // the program above depends on the fact that the Hello class is never loaded before toClass() is invoked.
        Class clazzByClassLoader = ctClass.toClass(ToClass.class.getClassLoader());
    }

    public static class Hello {
        public void say() {
            System.out.println("Hello");
        }
    }
}
