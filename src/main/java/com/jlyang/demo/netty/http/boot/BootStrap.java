package com.jlyang.demo.netty.http.boot;

import com.jlyang.demo.netty.http.server.Server;

public class BootStrap {
    public static void main(String[] args) {
        new Server().run();
    }
}
