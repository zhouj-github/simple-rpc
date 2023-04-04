package com.zhouj.rpc.protocol;

import com.zhouj.rpc.util.ProtostuffUtil;

import java.nio.ByteBuffer;

/**
 * 基于header 指定长度的解码
 *
 * @author zhouj
 * @since 2023-04-04
 */
public class HeadProtocol {

    /**
     *
     * @param byteBuffer
     * @param generateClass
     * @return
     */
    public static Object decode(ByteBuffer byteBuffer, Class generateClass) {
        int length = byteBuffer.limit();
        byte[] headerBytes = new byte[4];
        byte[] bodyBytes = new byte[length - 4];
        byteBuffer.get(headerBytes);
        byteBuffer.get(bodyBytes);
        return ProtostuffUtil.deserialize(bodyBytes, generateClass);
    }
}
