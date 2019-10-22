# Javassist learn

Java bytecode engineering tookit [http://www.javassist.org](http://www.javassist.org)

## Use Case

像ASM、cglib、bytebuddy等框架一样，javassist是操作bytecode的工具，所以常见的bytecode enginer的场景都适用。
例如监控profiling、AOP、动态代理等。javassist不同于其他框架的一点是，其他框架一般需要掌握java bytecode instruction，使用字节码编程就像用汇编写代码，但是javassist可以直接注入Java代码，在很多情况下用起来很方便。相比起使用ASM，你不需要计算max local、max stack, 不需要用label编写各种if控制语句，只需要用熟悉的Java语言。

## 引入依赖

maven依赖

```xml
<dependency>
    <groupId>javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.12.1.GA</version>
</dependency>
```

## 基本功能点

### 读取类、修改、返回修改的类

`ClassPool`可以理解为CtClass(Compile time class)的一个map。
我们可以用classname(带包名的全称）从ClassPool中读取（通过classpath寻找)到一个CtClass，
CtClass又能找到类的方法、构造器、字段等（类似反射API)，然后对CtClass里的内容进行各种修改，
最终通过CtClass.makeFile、CtClass.toByteArray等方法把修改完成的类写入到一个文件或者返回一个byte数组

下面是一个读取一个类，修改它继承的父类，然后把修改好的class写入到一个文件夹里，并且用`Thread.currentThread().contextClassLoader()`加载这个类、生成一个实例并调用的例子

```java
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
```

CtClass也可以通过传入一个byte数组来生成，例如下面是一个常见的`Instrumentation`的实例

```java
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
```

可以创建一个新的类
```java
ClassPool classPool = ClassPool.getDefault();
CtClass newClass = classPool.makeClass("MakeNewClass");
newClass.writeFile("/tmp/");
CtClass newInterface = classPool.makeInterface("MakeNewInterface");
newInterface.writeFile("/tmp");
```

#### ClassSearchPool
ClassPool需要知道在哪里去找到传入的classname所在的class文件，默认情况是通过当前线程的contextClassLoader来寻找的。但是在一些web项目中，可能存在比较复杂的classloader继承关系，所以经常需要手动传入一个classloader来告诉ClassPool如何找到对应的类。如果知道class的目录，可以直接传入目录名
```java
ClassPool classPool = ClassPool.getDefault();
// this statement registers the classpath that was used to loading the `ClassSearchPath.class`.
classPool.insertClassPath(new ClassClassPath(ClassSearchPath.class));

// You can register a directory name as the class search path
classPool.insertClassPath("/tmp/");

// add a byte array
classPool.insertClassPath(new ByteArrayClassPath("foo", new byte[]{1, 2, 3}));
```

### 修改方法

`CtMethod`提供了insertBefore、insertAfter方法，顾名思义就是在方法最前面和最后面添加代码。
在修改方法时，经常还需要获取一些方法的上下文信息，例如方法的参数值、类型、返回值返回类型等。javassist也
提供了一些特殊变量来方便获取
```java
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
```

### 增加删除方法、字段

可以通过CtNewMethod.make创建方法，CtNewConstructor.make、CtField.make创建字段，然后用CtClass.addMethod, addField，addContructor等方法添加到这个类中。

```java
ClassPool classPool = ClassPool.getDefault();
CtClass ctClass = classPool.get("com.github.liuzhengyang.javassist.testdata.Foo");
CtMethod method = CtNewMethod.make("public void newMethod() {\n" +
        "        System.out.println(\"this is new method\");\n" +
        "    }", ctClass);
ctClass.addMethod(method);

CtConstructor constructor = CtNewConstructor.make(new CtClass[]{classPool.get("java.lang.String")},
        new CtClass[]{}, "{System.out.println(\"Adding a constructor\");}", ctClass);
ctClass.addConstructor(constructor);

CtField field = CtField.make("private String name;", ctClass);
ctClass.setModifiers(ctClass.getModifiers() | Modifier.PUBLIC);
ctClass.setModifiers(ctClass.getModifiers() & ~Modifier.ABSTRACT);
ctClass.addField(field, "init values");

System.out.println(Arrays.toString(ctClass.getDeclaredConstructors()));
System.out.println(Arrays.toString(ctClass.getDeclaredMethods()));
```

### 一个简单的应用，javaagent和javassist结合使用

[javaagent-example](https://github.com/liuzhengyang/javaagent-example)

### 另一个简单应用，打印方法的耗时，aop的最常见用法
和jdk proxy、cglib的区别在于，这里是修改原有的类，而不是创建出一个代理

```java
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
```

### Javassist compiler的限制
javassist在执行的时候，会把外面上面用到的Java源代码在内存中编译成对应的字节码信息。不过javassist的编译器目前还有一些使用上的限制。

- 不支持Java5里面的枚举
- 不支持内部类。javassist可以读取、修改内部类匿名类
- labeled continue、break不支持（这个用法很少）
- Java的方法分派不能正确分派。




