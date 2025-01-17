package com.minimessage.test;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutionException;

public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = eventLoopGroup.next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(() -> {
            System.out.println("开始计算.....");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            promise.setSuccess(80);
        }).start();
        System.out.println(promise.get());
    }
}
