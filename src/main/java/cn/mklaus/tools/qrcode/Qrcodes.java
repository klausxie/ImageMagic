package cn.mklaus.tools.qrcode;

import cn.mklaus.tools.image.ImageMagic;
import cn.mklaus.tools.image.Location;
import cn.mklaus.tools.image.Transformer;
import com.beust.jcommander.Strings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Objects;

/**
 * @author klaus
 * Created on 2018/9/17 下午5:19
 */
public class Qrcodes {

    private static int DEFAULT_CONTENT_WIDTH = 200;

    public static Builder newBuilder() {
        return new Builder();
    }

    public static BufferedImage create(Builder builder) throws Exception {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter()
                .encode(builder.content, BarcodeFormat.QR_CODE,  DEFAULT_CONTENT_WIDTH, DEFAULT_CONTENT_WIDTH, hints);


        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width , height, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                int rgb = matrix.get(x, y) ? builder.foregroundColor.getRGB() : builder.backgroundColor.getRGB();
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }
    
    public static class Builder {
        private String content;
        private Color backgroundColor = Color.WHITE;
        private Color foregroundColor = Color.BLACK;
        private int padding = 10;
        private int width = 200;
        private String format = "png";
        private String filePath;

        public Builder() {
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder foregroundColor(Color foregroundColor) {
            this.foregroundColor = foregroundColor;
            return this;
        }

        public Builder padding(int padding) {
            this.padding = padding;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            try {
                this.format = filePath.substring(filePath.lastIndexOf(".") + 1);
            } catch (Exception e) {
                e.printStackTrace();
                this.format = "png";
            }

            return this;
        }

        public int getContentWidth() {
            return this.width - (2 * this.padding);
        }

        private BufferedImage scalaAndMergeBlank(BufferedImage bufferedImage) {
            // 缩放到指定大小
            bufferedImage = Transformer.scale(bufferedImage, width - 2 * padding, width - 2 * padding);

            // 白底
            BufferedImage blank = new BufferedImage(width, width, bufferedImage.getType());
            Graphics2D g = blank.createGraphics();
            g.setColor(backgroundColor);
            g.fillRect(0, 0, width, width);

            // 合并白底
            return ImageMagic.newMagic(blank)
                    .mergeInside(bufferedImage, Location.builder().x(padding).y(padding).build())
                    .getBufferedImage();
        }

        public BufferedImage build() throws Exception {
            if (Objects.isNull(content) || content.isEmpty()) {
                throw new IllegalArgumentException("content must not be empty");
            }

            // 二维码
            BufferedImage bufferedImage = Qrcodes.create(this);

            // 缩放二维码并加入 padding。
            // 为什么要这样做？ 原因是zxing 工具包并不能准确的处理 padding
            bufferedImage = scalaAndMergeBlank(bufferedImage);

            if (!Strings.isStringEmpty(filePath)) {
                File file = new File(filePath);
                ImageIO.write(bufferedImage, format, file);
            }
            return bufferedImage;
        }

        public File buildToTempFile() throws Exception {
            BufferedImage bufferedImage = build();
            File file = File.createTempFile("qrcodes",".png");
            ImageIO.write(bufferedImage, format, file);
            return file;
        }
    }
    
}
