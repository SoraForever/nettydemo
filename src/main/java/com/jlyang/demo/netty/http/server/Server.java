package com.jlyang.demo.netty.http.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Server {

    public void run() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(7088)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline()
                                .addLast(new HttpResponseEncoder())
                                .addLast(new HttpRequestDecoder())
                                .addLast(new HttpObjectAggregator(65536))
                                .addLast(new ServerHandler());
                    }
                });
        try {
            ChannelFuture future = serverBootstrap.bind().sync();
            System.out.println("服务器已启动");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private Map<String, Function<FullHttpRequest, String>> handler =
                new HashMap<String, Function<FullHttpRequest, String>>() {{
                    put(HttpMethod.GET.name(), request -> handleGet(request));
                    put(HttpMethod.POST.name(), request -> handlePost(request));
                }};

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
            HttpMethod method = request.method();
            String responseBody = handler.getOrDefault(method.name(), this::handleErrorMethod).apply(request);

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(responseBody.getBytes(StandardCharsets.UTF_8)));
            response.headers().add("Content-Type", request.headers().get("Accept"));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
        }

        private String handleGet(FullHttpRequest request) {
            String simple = byUri(request.uri());
            return byAccept(request.headers().get("Accept", "text/html"), simple);
        }

        private String handlePost(FullHttpRequest request) {
            ByteBuf readBuf = request.content();
            byte[] bytes = new byte[readBuf.readableBytes()];
            readBuf.readBytes(bytes);
            String requestBody = new String(bytes, StandardCharsets.UTF_8);
            return byAccept(request.headers().get("Accept", "text/html"),
                    byContentType(request.headers().get("Content-Type", "text/html"), requestBody));
        }

        private String handleErrorMethod(FullHttpRequest request) {
            String simple = "UNKNOWN METHOD";
            return byAccept(request.headers().get("Accept", "text/html"), simple);
        }

        private Object byContentType(String contentType, String s) {
            if ("application/json".equalsIgnoreCase(contentType)) {
                return JSONObject.parse(s);
            } else {
                return s;
            }
        }

        private <T> String byAccept(String accept, T body) {
            if ("application/json".equalsIgnoreCase(accept)) {
                JSONObject json = new JSONObject();
                json.put("retMsg", body);
                return json.toJSONString();
            } else {
                return body.toString();
            }
        }

        private String byUri(String uri) {
            if ("/time".equalsIgnoreCase(uri)) {
                return new Date(System.currentTimeMillis()).toString();
            } else {
                return "UNKNOWN RESOURCES";
            }
        }
    }
}
