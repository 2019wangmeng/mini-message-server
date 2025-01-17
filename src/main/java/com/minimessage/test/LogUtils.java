package com.minimessage.test;

import io.netty.buffer.ByteBuf;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class LogUtils {

    public static void log(ByteBuf byteBuf){
        int length = byteBuf.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(byteBuf.readerIndex())
                .append(" write index:").append(byteBuf.writerIndex())
                .append(" capacity:").append(byteBuf.capacity())
                        .append(NEWLINE);
        System.out.println(buf.toString());
    }
}
