package cn.navclub.fishpond.server.util;


import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class KaptUtil {

    private static final int FONT_SIZE = 30;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 50;


    public static Future<String> create(String text, int lineNum) {
        var future = CompletableFuture.supplyAsync(() -> {
            var image = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            var ctx = image.createGraphics();
            ctx.setColor(Color.WHITE);
            ctx.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            //绘制文字
            drawStr(text, image.getGraphics());
            //绘制随机干扰线条
            drawRDLine(ctx, lineNum);

            try {
                var outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "PNG", outputStream);
                var array = outputStream.toByteArray();
                return Base64.getEncoder().encodeToString(array);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        var promise = Promise.<String>promise();
        future.whenComplete((rs, t) -> {
            if (t != null) {
                promise.fail(t);
            } else {
                promise.complete(rs);
            }
        });
        return promise.future();
    }

    private static final Random RANDOM = new Random();

    private static void drawRDLine(Graphics2D ctx, int lineNum) {
        ctx.setColor(Color.green);
        ctx.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
        for (int i = 0; i < lineNum; i++) {
            var x = RANDOM.nextInt(DEFAULT_WIDTH);
            var y = RANDOM.nextInt(DEFAULT_HEIGHT);
            var l = RANDOM.nextInt(DEFAULT_WIDTH * DEFAULT_HEIGHT);
            ctx.drawLine(x, 0, x + l, y + l);
        }
    }

    private static void drawStr(String text, Graphics g) {
        var font = new Font(
                "TimesRoman",
                Font.ITALIC,
                FONT_SIZE
        );

        g.setFont(font);
        g.setColor(Color.red);

        var w = g.getFontMetrics().stringWidth(text);
        var h = g.getFontMetrics().getHeight();
        var x = Math.abs((DEFAULT_WIDTH - w) / 2);
        var y = Math.abs((DEFAULT_HEIGHT - h) / 2) + FONT_SIZE;

        g.drawString(text, x, y);
    }
}
