package com.jlyang.demo.netty.proto;

import com.jlyang.demo.netty.protobuf.proto.MessageBase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class MessageBaseTest {

    @Test
    void testMessageBase1(){
        MessageBase.Message message = MessageBase.Message.newBuilder()
                .setRequestId(UUID.randomUUID().toString())
                .setContent("hello world")
                .build();
        System.out.println("message: "+ message.toString());
    }
}