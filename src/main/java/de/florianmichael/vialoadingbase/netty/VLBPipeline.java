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
import de.florianmichael.vialoadingbase.netty.event.CompressionReorderEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

public class VLBPipeline extends ChannelInboundHandlerAdapter {

    public static final String VIA_CODEC_NAME = "via-codec";

    protected final UserConnection user;

    public VLBPipeline(final UserConnection user) {
        this.user = user;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        ctx.pipeline().addBefore(getDecoderName(), VIA_CODEC_NAME, new VLBViaCodec(this.user));
    }

    public String getDecoderName() {
        return "decoder";
    }

    public String getCompressionHandlerName() {
        return "compress";
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt.equals(CompressionReorderEvent.INSTANCE)) {
            final ChannelPipeline pipeline = ctx.pipeline();

            if (pipeline.names().indexOf(this.getCompressionHandlerName()) > pipeline.names().indexOf(VIA_CODEC_NAME)) {
                pipeline.addAfter(this.getCompressionHandlerName(), VIA_CODEC_NAME, pipeline.remove(VIA_CODEC_NAME));
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
