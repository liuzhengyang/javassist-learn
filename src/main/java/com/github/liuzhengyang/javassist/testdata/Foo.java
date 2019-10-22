package com.github.liuzhengyang.javassist.testdata;

import java.util.concurrent.TimeUnit;

import javassist.CtClass;

public class Foo {

    Foo () {
        System.out.println("Constructor of foo");
    }

    static {
        CtClass.debugDump = "/tmp/javassist";
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            hello();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void hello() {
        System.out.println("Hello");
    }

    public void print() {
        System.out.println("Print...");
    }

    @Profile
    public static int method(String input) {
        System.out.println(input);
        return 1;
    }
}
