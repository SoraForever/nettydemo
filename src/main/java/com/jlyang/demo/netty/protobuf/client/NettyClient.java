package com.jlyang.demo.netty.protobuf.client;

import com.jlyang.demo.netty.protobuf.proto.MessageBase;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class NettyClient {
    private EventLoopGroup group = new NioEventLoopGroup();
    @Value("${netty.port}")
    private int port;
    @Value("${netty.host}")
    private String host;
    private SocketChannel socketChannel;

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ClientHandlerInitializer());
        ChannelFuture future = bootstrap.connect();
        //客户端断线重连逻辑
        future.addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                System.out.println("连接Netty服务端成功");
            } else {
                System.out.println("连接失败，进行断线重连");
                f.channel().eventLoop().schedule(this::start, 20, TimeUnit.SECONDS);
            }
        });
        socketChannel = (SocketChannel) future.channel();
    }

    public void sendMsg(MessageBase.Message message) {
        socketChannel.writeAndFlush(message);
    }
}
