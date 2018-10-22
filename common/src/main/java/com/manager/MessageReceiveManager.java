package com.manager;

import com.BaseReceiver;
import com.util.ContextUtil;
import com.util.SerializeUtil;
import com.util.SpringUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageReceiveManager extends AbstractVerticle {

    private static Vertx vertx;
    private BaseReceiver receiver = SpringUtils.getBeansOfType(BaseReceiver.class).values().iterator().next();

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        MessageReceiveManager.vertx = vertx;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        vertx.eventBus().consumer(ContextUtil.id,
                msg -> receiver.onReceive(SerializeUtil.stm((byte[]) msg.body())));
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }
}