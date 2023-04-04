package com.zhouj.rpc.protocol;

import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**分隔符编码
 *
 * @author zhouj
 * @since 2020-08-04
 */
public class SplitEncode extends MessageToByteEncoder {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        byte[] bytes = ProtostuffUtil.serialize(o);
        byteBuf.writeBytes(bytes);
        //添加分割字符
        byteBuf.writeBytes(Constant.SPLIT.getBytes());
    }
}
