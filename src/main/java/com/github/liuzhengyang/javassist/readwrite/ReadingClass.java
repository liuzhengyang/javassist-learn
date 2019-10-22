package com.github.liuzhengyang.javassist.readwrite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import com.github.liuzhengyang.javassist.testdata.Foo;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.MethodInfo;
import net.bytebuddy.agent.ByteBuddyAgent;

public class ReadingClass {

    public static void main(String[] args) throws Exception {
        read();
        readFromBytes();
    }

    public static void read() throws NotFoundException, CannotCompileException, IOException, IllegalAccessException, InstantiationException {
        ClassPool classPool = ClassPool.getDefault();
        // get ctClass (compile time class)
        CtClass fooClass = classPool.get("com.github.liuzhengyang.javassist.testdata.Foo");
        // modify class
        fooClass.setSuperclass(classPool.get("com.github.liuzhengyang.javassist.testdata.Bar"));
        // write to some path
        fooClass.writeFile("/tmp/");
        Class fooClazz = fooClass.toClass();
        Foo o = (Foo) fooClazz.newInstance();
        o.print();
    }

    public static void readFromBytes() {
        Instrumentation instrumentation = ByteBuddyAgent.install();
        instrumentation.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                ClassPool classPool = ClassPool.getDefault();
                try {
                    CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
                    for (CtMethod declaredMethod : declaredMethods) {
                        MethodInfo methodInfo = declaredMethod.getMethodInfo();
                        if (methodInfo.getCodeAttribute() == null) {
                            continue;
                        }
                        declaredMethod.insertBefore("System.out.println(\"Before Method\");");
                        declaredMethod.insertAfter("System.out.println(\"After Method\");");
                    }
                    return ctClass.toBytecode();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
                return classfileBuffer;
            }
        });
        Foo.hello();
    }
}
