package com.jlyang.demo.netty.client;

import com.jlyang.demo.netty.proto.MessageBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

    /**
     * 如果服务端发送消息给客户端，下面方法进行接收消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message msg) {
        System.out.println("客户端收到消息：" + msg.toString() );
    }

    /**
     * 处理异常, 一般将实现异常处理逻辑的Handler放在ChannelPipeline的最后
     * 这样确保所有入站消息都总是被处理，无论它们发生在什么位置，下面只是简单的关闭Channel并打印异常信息
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
