package com.jlyang.demo.netty.server;

import com.jlyang.demo.netty.constant.Command;

public class HeartbeatResponsePacket {

    /**
     * 版本
     */
    private Byte version = 1;

    public Byte getCommand() {
        return Command.HEARTBEAT_RESPONSE;
    }
}
