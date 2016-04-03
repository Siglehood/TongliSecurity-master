package com.sig.tonglisecurity.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Application介绍
 * 一.
 * Application和Act,Service一样是android框架的一个系统组件，当android程序启动时系统会创建一个 application对象，用来
 * 存储系统的一些信息。通常我们是不需要指定一个Application的，这时系统会自动帮我们创建，如果需要创建自己 的Application，也很简
 * 单创建一个类继承 Application并在manifest的application标签中进行注册(只需要给Application标签增加个name属性把自己
 * 的 Application的名字定入即可)。
 * android系统会为每个程序运行时创建一个Application类的对象且仅创建一个，所以Application可以说是单例 (singleton)模式的一个类.且
 * application对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期。因为它是全局 的单例的，所以在不同的Activity,Service中
 * 获得的对象都是同一个对象。所以通过Application来进行一些，数据传递，数据共享 等,数据缓存等操作。
 * <p/>
 * 二.程序的入口
 * Android使用Google Dalvik VM，相对于传统Java VM而言有着很大的不同，在Sun的Java体系中入口点和标准c语言一样是main()，
 * 而每个Android程序都包含着一个Application实例，一个Application实例中有多个Activity、 Service、ContentProvider或
 * Broadcast Receiver。
 * 其实在android.app.Application这个包的onCreate才是真正的Android入口点，只不过大多数开发者无需重写该类。
 * <p/>
 * 第一步、写一个全局的单例模式的MyApplication继承自Application 重写onCreate
 * 第二步、配置全局的Context
 * <application android:name="com.appstore.service.MyApplication" ></application>
 */

@SuppressLint("HandlerLeak")
public class GFFApp extends Application {
    private static GFFApp instance;
    private List<Activity> activities = new LinkedList<Activity>();

    public static synchronized GFFApp getInstance() {
        if (null == instance) {
            instance = new GFFApp();
        }
        return instance;

    }

    /**
     * 添加 Activity 到集合
     *
     * @param activity Activity 对象
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 结束所有的 Activity
     */
    public void exit() {
        for (Activity activity : activities) {
            activity.finish();
        }
        System.exit(0);
    }
}
