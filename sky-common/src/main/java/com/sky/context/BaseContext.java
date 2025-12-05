package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //设置存储当前线程的用户ID值
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //获取当前线程存储的用户ID值
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
