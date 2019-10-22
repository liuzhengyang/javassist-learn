package com.github.liuzhengyang.javassist.introspection;

import com.github.liuzhengyang.javassist.testdata.Foo;
import com.github.liuzhengyang.javassist.testdata.Profile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class Methods {
    public static void main(String[] args) throws Exception {
//        insertSourceText();
        printMethodTimeCost();
    }

    /**
     * The statement and the block can refer to fields and methods.
     * They can also refer to the parameters to the method that they are inserted into if that method
     * was compiled with the -g option (to include a local variable attribute in the class file).
     * Otherwise, they must access the method parameters through the special variables $0, $1, $2, ... described below.
     * Accessing local variables declared in the method is not allowed although declaring a new local variable in the block is allowed.
     * However, insertAt() allows the statement and the block to access local variables if these variables are available at the specified line number and the target method was compiled with the -g option.
     *
     * $0, $1, $2, ...    	this and actual parameters
     * $args	An array of parameters. The type of $args is Object[].
     * $$	All actual parameters.
     * For example, m($$) is equivalent to m($1,$2,...)
     *
     * $cflow(...)	cflow variable
     * $r	The result type. It is used in a cast expression.
     * $w	The wrapper type. It is used in a cast expression.
     * $_	The resulting value
     * $sig	An array of java.lang.Class objects representing the formal parameter types.
     * $type	A java.lang.Class object representing the formal result type.
     * $class	A java.lang.Class object representing the class currently edited.
     *
     */
    private static void insertSourceText() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.github.liuzhengyang.javassist.testdata.Foo");
        CtMethod method = ctClass.getDeclaredMethod("method");

        // $0, $1, $2, ...    	this and actual parameters
        // The types of those variables are identical to the parameter types.
        // $0 is equivalent to this. If the method is static, $0 is not available.
        method.insertBefore("System.out.println(\"Param 1 is \" + $1);");

        // $args	An array of parameters. The type of $args is Object[].
        // Note that $args[0] is not equivalent to $0; $0 represents this.
        method.insertBefore("System.out.println(\"Params is \" + $args);");

        // $r represents the result type (return type) of the method.
        // It must be used as the cast type in a cast expression.
        // For example, this is a typical use:
        // Object result = ... ;
        // $_ = ($r)result;

        // $class The value of $class is an java.lang.Class object representing the class in which the edited method is declared. This represents the type of $0.
        method.insertBefore("System.out.println(\"Class is \" + $class);");

        method.insertAfter("System.out.println(\"Result is \" + $_);", true);
        method.insertAfter("System.out.println(\"Result type is \" + $type);", true);

        // Note that the inserted code fragment must end with a throw or return statement.
        method.addCatch("{System.out.println($e); throw $e;}", classPool.get("java.lang.Exception"));

        ctClass.toClass();
        Foo.method("invoke");
    }


    /**
     * Accessing local variables declared in the method is not allowed although declaring a new local variable in the block is allowed.
     * @throws Exception
     */
    private static void printMethodTimeCost() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("com.github.liuzhengyang.javassist.testdata.Foo");
        CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
        for (CtMethod declaredMethod : declaredMethods) {
            if (declaredMethod.hasAnnotation(Profile.class)) {
                String name = declaredMethod.getName();
                // 创建新的本地变量前，要声明一个addLocalVariable
                declaredMethod.addLocalVariable("start", CtClass.longType);
                declaredMethod.addLocalVariable("end", CtClass.longType);
                declaredMethod.insertBefore("{long start = System.currentTimeMillis();}");
                declaredMethod.insertAfter("{long end = System.currentTimeMillis();\n" +
                        "System.out.println(\"" + ctClass.getName() + "." + name +  " cost \" + (end - start));}");
                // 注意这里当目标代码有catch exception时，会报Bad local variable type异常, StackMap计算不对

            }
        }
        ctClass.toClass();
        ctClass.debugWriteFile("/tmp/javassist");
        Foo.method("hello");
    }
}
