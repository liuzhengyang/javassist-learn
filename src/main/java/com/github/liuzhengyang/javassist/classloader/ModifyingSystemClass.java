package com.github.liuzhengyang.javassist.classloader;

/**
 * The system classes like java.lang.String cannot be loaded by a class loader other than the system class loader. Therefore, SampleLoader or javassist.Loader shown above cannot modify the system classes at loading time.
 */
public class ModifyingSystemClass {

}
