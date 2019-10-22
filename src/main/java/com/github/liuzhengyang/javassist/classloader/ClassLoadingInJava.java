package com.github.liuzhengyang.javassist.classloader;

/**
 * In Java, multiple class loaders can coexist and each class loader creates its own name space.
 * Different class loaders can load different class files with the same class name.
 * The loaded two classes are regarded as different ones.
 * This feature enables us to run multiple application programs on a single JVM even if these programs include different classes with the same name.
 * The JVM does not allow dynamically reloading a class.
 * Once a class loader loads a class, it cannot reload a modified version of that class during runtime.
 * Thus, you cannot alter the definition of a class after the JVM loads it.
 * However, the JPDA (Java Platform Debugger Architecture) provides limited ability for reloading a class.
 */
public class ClassLoadingInJava {

    /**
     * If the same class file is loaded by two distinct class loaders,
     * the JVM makes two distinct classes with the same name and definition.
     * The two classes are regarded as different ones. Since the two classes are not identical,
     * an instance of one class is not assignable to a variable of the other class.
     * The cast operation between the two classes fails and throws a ClassCastException.
     *
     */

    /**
     * Next, let's consider a slightly modified example.
     *
     * public class Point {
     *     private int x, y;
     *     public int getX() { return x; }
     *         :
     * }
     *
     * public class Box {      // the initiator is L but the real loader is PL
     *     private Point upperLeft, size;
     *     public Point getSize() { return size; }
     *         :
     * }
     *
     * public class Window {    // loaded by a class loader L
     *     private Box box;
     *     public boolean widthIs(int w) {
     *         Point p = box.getSize();
     *         return w == p.getX();
     *     }
     * }
     * Now, the definition of Window also refers to Point. In this case, the class loader L must also delegate to PL if it is requested to load Point. You must avoid having two class loaders doubly load the same class. One of the two loaders must delegate to the other.
     */
}
