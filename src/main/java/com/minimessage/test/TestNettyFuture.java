package com.minimessage.test;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.Callable;

public class TestNettyFuture {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();
        eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return null;
            }
        });
    }
}
