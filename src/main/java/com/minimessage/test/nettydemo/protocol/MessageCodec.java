package com.minimessage.test.nettydemo.protocol;

import com.minimessage.test.nettydemo.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MessageCodec extends ByteToMessageCodec<Message> {
    private final static Logger log = LoggerFactory.getLogger(MessageCodec.class);

    /**
     * 自定义协议要素：
     * 1.魔数
     * 2.版本号
     * 3.序列化方式
     * 4.指令类型
     * 5.请求序号
     * 6.正文长度
     * 7.消息正文
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //定义魔数 4个字节
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //版本
        out.writeByte(1);
        //序列化方式 0.jdk 1.json
        out.writeByte(0);
        //消息类型
        out.writeByte(msg.getMessageType());
        //请求序号
        out.writeInt(msg.getSequenceId());
        //对齐填充
        out.writeByte(0xff);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] byteArray = bos.toByteArray();
        //正文长度
        out.writeInt(byteArray.length);
        //消息正文
        out.writeBytes(byteArray);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{},{},{},{},{},{}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}",message);
        out.add(message);
    }
}
