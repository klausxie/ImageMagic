package cn.mklaus.tools;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片拼接
 *
 * @author klaus
 * @date 2018/8/23 下午5:10
 */
public class Combiner {

    public static BufferedImage combineBlank(BufferedImage im, Position position, int length, Color bgColor) {
        int w = im.getWidth();
        int h = im.getHeight();

        int[] images = new int[w * h];
        im.getRGB(0, 0, w, h, images, 0, w);

        int nw = 0;
        int nh = 0;
        if (position.isVertical()) {
            nh = h + length;
            nw = w;
        } else {
            nh = h;
            nw = w + length;
        }

        BufferedImage re = new BufferedImage(nw, nh, im.getType());
        Graphics2D g = re.createGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, nw, nh);

        int x = 0;
        int y = 0;

        if (position.isVertical()) {
            y = (Position.BOTTOM == position) ? 0 : length;
        } else {
            x = (Position.RIGHT == position) ? 0 : length;
        }

        re.setRGB(x, y, w, h, images, 0, w);
        return re;
    }

}
