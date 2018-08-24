package cn.mklaus.tools.image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片拼接
 *
 * @author klaus
 * @date 2018/8/23 下午5:10
 */
public class Combiner {

    /**
     * 扩充图片区域
     * @param im        需要扩充的图片
     * @param position  扩充方向
     * @param length    扩充长度
     * @param bgColor   填充的颜色
     * @return          扩充后的 BufferedImage
     */
    public static BufferedImage mergeBlank(BufferedImage im,  int length, Color bgColor, Position position) {
        int w = im.getWidth();
        int h = im.getHeight();
        int bw = 0;
        int bh = 0;
        if (position.isVertical()) {
            bw = w;
            bh = length;
        } else {
            bw = length;
            bh = h;
        }

        BufferedImage blank = new BufferedImage(bw, bh, im.getType());
        Graphics2D g = blank.createGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, bw, bh);

        return merge(im, blank, position);
    }

    /**
     * 合并两张图片
     * @param img1      图1
     * @param img2      图2
     * @param position  合并方向
     * @return          合并后的 BufferedImage
     */
    public static BufferedImage merge(BufferedImage img1, BufferedImage img2, Position position) {
        if (position == Position.TOP || position == Position.LEFT) {
            BufferedImage temp = img1; img1 = img2; img2 = temp;
        }

        int w1 = img1.getWidth();
        int h1 = img1.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        BufferedImage re;
        if (position.isVertical()) {
            // 垂直合并

            if (w1 > w2) {
                img1 = Transformer.scale(img1, w2, (int)(h1 * (1.0 * w2 / w1)) );
            } else {
                img2 = Transformer.scale(img2, w1, (int)(h2 * (1.0 * w1 / w2)));
            }

            int[] img1RgbArray = readRgbArray(img1);
            int[] img2RgbArray = readRgbArray(img2);
            h1 = img1.getHeight();
            h2 = img2.getHeight();
            // After scale: img1.width = img2.width
            int w = img1.getWidth();

            re = new BufferedImage(w, h1 + h2, img1.getType());
            re.setRGB(0, 0, w, h1, img1RgbArray, 0, w);
            re.setRGB(0, h1, w, h2, img2RgbArray, 0, w);
            return re;

        } else {
            // 水平合并

            if (h1 > h2) {
                img1 = Transformer.scale(img1, (int)(w1 * (1.0 * h2 / h1)), h2);
            } else {
                img2 = Transformer.scale(img2, (int)(w2 * (1.0 * h1 / h2)), h1);
            }

            int[] img1RgbArray = readRgbArray(img1);
            int[] img2RgbArray = readRgbArray(img2);
            w1 = img1.getWidth();
            w2 = img2.getWidth();
            // After scale: img1.height = img2.height
            int h = img1.getHeight();

            re = new BufferedImage( w1 + w2, h, img1.getType());
            re.setRGB(0, 0,  w1, h, img1RgbArray, 0, w1);
            re.setRGB(w1, 0, w2, h, img2RgbArray, 0, w2);
            return re;

        }

    }


    public static BufferedImage mergeInside(BufferedImage bg, BufferedImage im, Config config) {
        Graphics2D g = bg.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, config.getAlpha()));
        g.drawImage(im, config.getX(), config.getY(), null);
        g.dispose();
        return bg;
    }

    /**
     * 将图片信息读取到数组
     * @param img   图片
     * @return      int[]
     */
    private static int[] readRgbArray(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] rgbArray = new int[w * h];
        img.getRGB(0, 0, w, h, rgbArray, 0, w);
        return rgbArray;
    }

    public static class Config {
        private int x;
        private int y;
        private float alpha;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public float getAlpha() {
            return alpha;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int x;
            private int y;
            private float alpha;

            public Builder x(int x) {
                this.x = x;
                return this;
            }

            public Builder y(int y) {
                this.y = y;
                return this;
            }

            public Builder alpha(float alpha) {
                this.alpha = alpha;
                return this;
            }

            public Config build() {
                Config config = new Config();
                config.x = this.x;
                config.y = this.y;
                config.alpha = this.alpha;
                return config;
            }
        }


    }

}
