package com.jlyang.demo.netty.server;

import com.jlyang.demo.netty.proto.MessageBase;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message msg) {
        if (msg.getCmd().equals(MessageBase.Message.CommandType.HEARTBEAT_REQUEST)) {
            System.out.println("收到客户端发来的心跳消息：" + msg.toString());
            //回应pong
            ctx.writeAndFlush(new HeartbeatResponsePacket());
        } else if (msg.getCmd().equals(MessageBase.Message.CommandType.NORMAL)) {
            System.out.println("收到客户端的业务消息：" + msg.toString());
        }
    }
}
