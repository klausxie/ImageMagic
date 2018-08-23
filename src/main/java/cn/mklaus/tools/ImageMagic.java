package cn.mklaus.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by klaus on 8/5/17.
 */
public class ImageMagic {

    /**
     * 当前操作图片的 BufferedImage
     */
    private BufferedImage bufferedImage;

    public ImageMagic(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
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

    public static ImageMagic newMagic(String link) {
        try {
            URL url = new URL(link);
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
        File temp = File.createTempFile("ImageMagic", ".png");
        ImageIO.write(this.bufferedImage, "png", temp);
        return temp;
    }

    public File toFile(File file) throws IOException {
        ImageIO.write(this.bufferedImage, "png", file);
        return file;
    }

}
