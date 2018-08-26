package cn.mklaus.tools.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author klaus
 * @date 2018-08-16 下午12:03
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

    public static BufferedImage read(Object img) {
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

    public ImageMagic scale(int w, int h) {
        this.bufferedImage = Transformer.scale(this.getBufferedImage(), w, h);
        return this;
    }

    public ImageMagic zoomScale(int w, int h, Color bgColor) {
        this.bufferedImage = Transformer.zoomScale(this.getBufferedImage(), w, h, bgColor);
        return this;
    }

    public ImageMagic clipScale(int w, int h) {
        this.bufferedImage = Transformer.clipScale(this.getBufferedImage(), w, h);
        return this;
    }

    public ImageMagic roundCorner(int corner) {
        this.bufferedImage = Transformer.roundCorner(this.bufferedImage, corner);
        png();
        return this;
    }

    public ImageMagic roundCornerRadio(int percent) {
        this.bufferedImage = Transformer.roundedCornerRadio(this.bufferedImage, percent);
        png();
        return this;
    }

    public ImageMagic alpha(float alpha) {
        this.bufferedImage = Transformer.alpha(this.bufferedImage, alpha);
        return this;
    }

    public ImageMagic merge(BufferedImage im, Direction direction) {
        checkImageType(im);
        this.bufferedImage = Combiner.merge(this.bufferedImage, im, direction);
        return this;
    }

    public ImageMagic mergeBlank(int length, Color color, Direction direction) {
        this.bufferedImage = Combiner.mergeBlank(this.bufferedImage, length, color, direction);
        return this;
    }

    public ImageMagic mergeInside(BufferedImage im, Location location) {
        this.bufferedImage = Combiner.mergeInside(this.bufferedImage, im, location);
        return this;
    }

    public ImageMagic printText(Text text, Location location) {
        this.bufferedImage = Combiner.printText(this.bufferedImage, text, location);
        return this;
    }

}
