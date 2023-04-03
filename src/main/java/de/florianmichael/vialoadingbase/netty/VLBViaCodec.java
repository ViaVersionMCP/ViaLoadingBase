/*
 * This file is part of ViaLoadingBase - https://github.com/FlorianMichael/ViaLoadingBase
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.vialoadingbase.netty;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelDecoderException;
import com.viaversion.viaversion.exception.CancelEncoderException;
import com.viaversion.viaversion.util.PipelineUtil;
import de.florianmichael.vialoadingbase.netty.event.CompressionReorderEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class VLBViaCodec extends ByteToMessageCodec<ByteBuf> {

    private final UserConnection info;

    public VLBViaCodec(final UserConnection info) {
        this.info = info;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        if (!this.info.checkOutgoingPacket()) throw CancelEncoderException.generate(null);

        out.writeBytes(in);
        if (this.info.shouldTransformPacket()) {
            this.info.transformOutgoing(out, CancelEncoderException::generate);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!this.info.checkIncomingPacket()) throw CancelDecoderException.generate(null);

        final ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(in);
        try {
            if (this.info.shouldTransformPacket()) {
                this.info.transformIncoming(transformedBuf, CancelDecoderException::generate);
            }
            out.add(transformedBuf.retain());
        } finally {
            transformedBuf.release();
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            super.write(ctx, msg, promise);
        } catch (Throwable e) {
            if (!PipelineUtil.containsCause(e, CancelCodecException.class)) {
                throw e;
            } else {
                promise.setSuccess();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            super.channelRead(ctx, msg);
        } catch (Throwable e) {
            if (!PipelineUtil.containsCause(e, CancelCodecException.class)) {
                throw e;
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt.equals(CompressionReorderEvent.INSTANCE)) {
            final ChannelPipeline pipeline = ctx.pipeline();

            if (pipeline.names().indexOf(NettyConstants.MINECRAFT_DECOMPRESSION_NAME) > pipeline.names().indexOf(NettyConstants.VIA_CODEC_NAME)) {
                pipeline.addAfter(NettyConstants.MINECRAFT_DECOMPRESSION_NAME, NettyConstants.VIA_CODEC_NAME, pipeline.remove(NettyConstants.VIA_CODEC_NAME));
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
