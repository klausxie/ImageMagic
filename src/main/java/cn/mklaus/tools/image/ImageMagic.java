package cn.mklaus.tools.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author klaus
 * Created on 2018-08-16 下午12:03
 */
public class ImageMagic {

    private static final String TEMP_PREFIX = "ImageMagic";

    private static final String PNG = "png";
    private static final String PNG_SUFFIX = ".png";
    private static final String JPRG = "jpeg";
    private static final String JPRG_SUFFIX = ".jpeg";

    /**
     * 当前操作图片的 BufferedImage
     */
    private BufferedImage bufferedImage;

    private String format;
    private String formatSuffix;

    public ImageMagic(BufferedImage bufferedImage) {
        this.format = JPRG;
        this.formatSuffix = JPRG_SUFFIX;
        this.bufferedImage = bufferedImage;
        checkImageType(bufferedImage);
    }

    public static ImageMagic newMagic(Object img) {
        BufferedImage bufferedImage = read(img);
        return new ImageMagic(bufferedImage);
    }

    private static BufferedImage read(Object img) {
        try {
            if (img instanceof BufferedImage) {
                return (BufferedImage)img;
            } else if (img instanceof File) {
                return ImageIO.read((File)img);
            } else if (img instanceof URL) {
                return ImageIO.read((URL) img);
            } else {
                if (img instanceof CharSequence) {
                    File file = new File(img.toString());
                    return ImageIO.read(file);
                } else {
                    throw new IllegalArgumentException("Can't load bufferedImage: " + img.toString());
                }
            }
        } catch (IOException e) {
            e.fillInStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static ImageMagic newMagic(BufferedImage image) {
        return new ImageMagic(image);
    }

    public static ImageMagic newMagic(File image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image);
            return new ImageMagic(bufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ImageMagic newMagic(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage bufferedImage = ImageIO.read(url);
            return new ImageMagic(bufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ImageMagic createBlank(int width, int height, Color color) {
        BufferedImage blank = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = blank.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        return ImageMagic.newMagic(blank);
    }

    public static ImageMagic createTransparent(int width, int height) {
        BufferedImage blank = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                blank.setRGB( i,j,0x01000000);
            }
        }
        return ImageMagic.newMagic(blank);
    }

    public int height() {
        return this.bufferedImage.getHeight();
    }

    public int width() {
        return this.bufferedImage.getWidth();
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public File toFile() throws IOException {
        File temp = File.createTempFile(TEMP_PREFIX, this.formatSuffix);
        return toFile(temp);
    }

    public File toFile(File file) throws IOException {
        ImageIO.write(this.bufferedImage, this.format, file);
        return file;
    }

    public ImageMagic png() {
        this.format = PNG;
        this.formatSuffix = PNG_SUFFIX;
        return this;
    }

    public void checkImageType(BufferedImage bi) {
        // 带有 Alpha信息的图片，使用 PNG 格式输出文件
        if (bi.getType() == BufferedImage.TYPE_INT_ARGB
                || bi.getType() == BufferedImage.TYPE_INT_ARGB_PRE
                || bi.getType() == BufferedImage.TYPE_4BYTE_ABGR
                || bi.getType() == BufferedImage.TYPE_4BYTE_ABGR_PRE) {
            png();
        }
    }

    /**
     * Builder
     */

    /**
     * 全覆盖伸缩图片
     * @param w     图片宽度
     * @param h     图片高度
     * @return      ImageMagic
     */
    public ImageMagic scale(int w, int h) {
        this.bufferedImage = Transformer.scale(this.getBufferedImage(), w, h);
        return this;
    }

    /**
     * 等比覆盖伸缩图片
     * @param w         图片宽度
     * @param h         图片高度
     * @param bgColor   背景颜色
     * @return          ImageMagic
     */
    public ImageMagic zoomScale(int w, int h, Color bgColor) {
        this.bufferedImage = Transformer.zoomScale(this.getBufferedImage(), w, h, bgColor);
        return this;
    }

    /**
     * 剪切覆盖伸缩图片
     * @param w     图片宽度
     * @param h     图片高度
     * @return      ImageMagic
     */
    public ImageMagic clipScale(int w, int h) {
        this.bufferedImage = Transformer.clipScale(this.getBufferedImage(), w, h);
        return this;
    }

    /**
     * 图片圆角
     * @param corner    圆角像素值
     * @return          ImageMagic
     */
    public ImageMagic roundCorner(int corner) {
        this.bufferedImage = Transformer.roundCorner(this.bufferedImage, corner);
        png();
        return this;
    }

    /**
     * 图片圆角
     * @param percent   圆角百分比
     * @return          ImageMagic
     */
    public ImageMagic roundCornerRadio(int percent) {
        this.bufferedImage = Transformer.roundedCornerRadio(this.bufferedImage, percent);
        png();
        return this;
    }

    /**
     * 图片透明
     * @param alpha     透明度0.0-1.0f
     * @return          ImageMagic
     */
    public ImageMagic alpha(float alpha) {
        this.bufferedImage = Transformer.alpha(this.bufferedImage, alpha);
        return this;
    }

    /**
     * 图片合并
     * @param im            被合并图片
     * @param direction     合并方向
     * @return              ImageMagic
     */
    public ImageMagic merge(BufferedImage im, Direction direction) {
        checkImageType(im);
        this.bufferedImage = Combiner.merge(this.bufferedImage, im, direction);
        return this;
    }

    /**
     * 合并空图
     * @param length        空图宽或高（根据方向而定）
     * @param color         空图颜色
     * @param direction     合并方向
     * @return              ImageMagic
     */
    public ImageMagic mergeBlank(int length, Color color, Direction direction) {
        this.bufferedImage = Combiner.mergeBlank(this.bufferedImage, length, color, direction);
        return this;
    }

    /**
     * 合并图片到内部
     * @param im            被合并图片
     * @param location      定位信息
     * @return              ImageMagic
     */
    public ImageMagic mergeInside(BufferedImage im, Location location) {
        this.bufferedImage = Combiner.mergeInside(this.bufferedImage, im, location);
        return this;
    }

    /**
     * 水印文字
     * @param text          文字信息
     * @param location      定位信息
     * @return              ImageMagic
     */
    public ImageMagic printText(Text text, Location location) {
        this.bufferedImage = TextPrinter.printText(this.bufferedImage, text, location);
        return this;
    }

    /**
     * 水印文字
     * @param text          文字信息
     * @param location      定位信息
     * @return              ImageMagic
     */
    public ImageMagic printMultiLineText(MultiLineText text, Location location) {
        this.bufferedImage = TextPrinter.printMultiLineText(this.bufferedImage, text, location);
        return this;
    }

}
