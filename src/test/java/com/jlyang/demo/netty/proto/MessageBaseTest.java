package com.jlyang.demo.netty.proto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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