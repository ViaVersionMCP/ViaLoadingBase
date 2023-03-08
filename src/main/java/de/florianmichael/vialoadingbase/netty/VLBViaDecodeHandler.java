/*
 * This file is part of ViaLoadingBase - https://github.com/FlorianMichael/ViaLoadingBase
 * Copyright (C) 2022-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelDecoderException;
import com.viaversion.viaversion.exception.InformativeException;
import com.viaversion.viaversion.util.PipelineUtil;
import de.florianmichael.vialoadingbase.event.PipelineReorderEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@ChannelHandler.Sharable
public class VLBViaDecodeHandler extends MessageToMessageDecoder<ByteBuf> {
    private final UserConnection info;

    public VLBViaDecodeHandler(UserConnection info) {
        this.info = info;
    }

    public UserConnection getInfo() {
        return info;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        if (!info.checkIncomingPacket()) throw CancelDecoderException.generate(null);
        if (!info.shouldTransformPacket()) {
            out.add(bytebuf.retain());
            return;
        }

        ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
        try {
            info.transformIncoming(transformedBuf, CancelDecoderException::generate);

            out.add(transformedBuf.retain());
        } finally {
            transformedBuf.release();
        }
    }

    private void reorder(final ChannelHandlerContext ctx) {
        final int decoderIndex = ctx.pipeline().names().indexOf(NettyConstants.COMPRESSION_HANDLER_NAMES[0]);
        if (decoderIndex == -1) return;

        if (decoderIndex > ctx.pipeline().names().indexOf(NettyConstants.HANDLER_DECODER_NAME)) {
            ChannelHandler encoder = ctx.pipeline().get(NettyConstants.HANDLER_ENCODER_NAME);
            ChannelHandler decoder = ctx.pipeline().get(NettyConstants.HANDLER_DECODER_NAME);

            ctx.pipeline().remove(encoder);
            ctx.pipeline().remove(decoder);

            ctx.pipeline().addAfter(NettyConstants.COMPRESSION_HANDLER_NAMES[1], NettyConstants.HANDLER_ENCODER_NAME, encoder);
            ctx.pipeline().addAfter(NettyConstants.COMPRESSION_HANDLER_NAMES[0], NettyConstants.HANDLER_DECODER_NAME, decoder);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelCodecException.class)) return;

        if ((PipelineUtil.containsCause(cause, InformativeException.class)
                && info.getProtocolInfo().getState() != State.HANDSHAKE)
                || Via.getManager().debugHandler().enabled()) {
            cause.printStackTrace();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof PipelineReorderEvent) {
            reorder(ctx);
        }
        super.userEventTriggered(ctx, evt);
    }
}