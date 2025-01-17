package com.minimessage.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        //在切片过程中没有发生数据复制
        ByteBuf f1 = buf.slice(0, 5);
        ByteBuf f2 = buf.slice(5, 5);
        //buf,f2,f2共用一块内存，如果buf调用release释放后，f1和f2的数据页没有了
    }
}
