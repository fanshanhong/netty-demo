package com.fanshanhong.nettydemo.netty;

import java.lang.reflect.Method;

/**
 * @Description: 探究一下Java中的继承
 * @Author: fan
 * @Date: 2020-08-15 15:57
 * @Modify:
 */
public class test {

    public static void main(String[] args) {
        C c = new C();

        c.fun1();
        Method[] methods = C.class.getMethods();
        Method[] declaredMethods = C.class.getDeclaredMethods();


        // 1、getMethods返回一个包含某些 Method 对象的数组，这些对象反映此 Class 对象所表示的类或接口的公共 member 方法。
        // 2、getDeclaredMethods返回 Method 对象的一个数组，这些对象反映此 Class 对象表示的类或接口声明的所有方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法。
        //
        // 也就是说getDeclaredMethods能拿到所有（不包括继承的方法），而getMethods只能拿到public方法（包括继承的类或接口的方法）
        //
        //
        //
        //还有只得注意的是这两个方法返回的数组中的元素的顺序是无序的，它和你在类中定义方法的顺序无关
        for (Method m : methods) {
            System.out.println(m.getName());
        }
        System.out.println("---------------");
        for (Method m : declaredMethods) {
            System.out.println(m.getName());
        }
    }
}


abstract class Base {

    public void fun1() {
        fun2();
    }

    public int fun2() {
        return 3 + config();
    }

    abstract public int config();

}


class C extends Base {

    @Override
    public int config() {
        return 2;
    }
}
