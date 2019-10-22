package com.baidu.eduai.sdk.touping.data;

/**
 * Created by tianhouchao on 2019/6/4.
 * 相册拷贝过来的
 */
public class ForScreenConstants {
    // 要修改
    public static final String SKILL_ID = "0354818e-6eb5-578b-c251-25334a6e2a81";
    public static final String SCHEME = "dueros://";
    public static final String URL_PRE = SCHEME + SKILL_ID;

    public static final class Namespace {
        public static final String FORSCREEN = "ai.dueros.device_interface.extensions.forscreen";
        public static final String NAMESPACE_SCREEN = "ai.dueros.device_interface.screen";
    }

    // 指令名字
    public static class Name {
        public static final String OPEN_FORSCREEN_TO_HOME = "open_forscreen_home";
    }

    public static class ParamKey {
    }

    public static class Event {
    }

    public static class Action {
        public static final String ACTION_OPEN_FOR_SCREEN_ACTIVITY = "com.baidu.duer.forscreen.activity";
        public static final String ACTION_OPEN_FOR_SCREEN_BROADCAST = "com.baidu.duer.forscreen.broadcast";
        public static final String ACTION_FOR_SCREEN_SERVE_SWITCH_BROADCAST =
                "com.baidu.duer.forscreen.serve.switch.broadcast";
    }

    public static class Extras {
        public static final String EXTRAS_FORSCREEN_SWITCH = "extrans_forscreen_switch";
    }

    public static class RandomPort {
        public static final String RANDOM_PORT_KEY = "random_port_key";
        public static final int RANDOM_PORT_DEFAULT = 88;
    }
}