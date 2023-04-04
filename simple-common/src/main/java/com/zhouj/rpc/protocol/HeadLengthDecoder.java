package com.zhouj.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * head指定body长度解码
 * @author zhouj
 * @since 2023-04-04
 */
public class HeadLengthDecoder extends LengthFieldBasedFrameDecoder {

    private Class<?> generateClass;


    Logger logger = LoggerFactory.getLogger(HeadLengthDecoder.class);

    public HeadLengthDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, Class generateClass) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.generateClass = generateClass;
    }


    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            //转换为byteBuffer
            ByteBuffer byteBuffer = frame.nioBuffer();
            return HeadProtocol.decode(byteBuffer, generateClass);
        } catch (Exception e) {
            logger.error("decode exception, " + e.getMessage(), e);
        } finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }


}
