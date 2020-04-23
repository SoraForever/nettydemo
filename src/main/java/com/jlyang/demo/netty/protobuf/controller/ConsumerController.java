package com.jlyang.demo.netty.protobuf.controller;

import com.jlyang.demo.netty.protobuf.client.NettyClient;
import com.jlyang.demo.netty.protobuf.proto.MessageBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ConsumerController {

    @Autowired
    private NettyClient nettyClient;

    @GetMapping("/send")
    public String send() {
        MessageBase.Message message = MessageBase.Message.newBuilder()
                .setCmd(MessageBase.Message.CommandType.NORMAL)
                .setContent("hello server")
                .setRequestId(UUID.randomUUID().toString())
                .build();
        nettyClient.sendMsg(message);
        return "send ok";
    }

}
