package com.github.liuzhengyang.javassist.introspection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 *
 */
public class AlteringMethodBody {
    public static void main(String[] args) throws Exception {

    }

    private static void instrumentMethod() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.github.liuzhengyang.javassist.testdata.Foo");
        CtMethod method = ctClass.getDeclaredMethod("hello");
        method.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                super.edit(m);
            }
        });
    }
}
