package cn.mklaus.tools;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by klaus on 8/5/17.
 */
public class ImageMagic {
    private BufferedImage bufferedImage;

    public static ImageMagic newMagic(BufferedImage image) {
        ImageMagic imageMagic = new ImageMagic();
        imageMagic.bufferedImage = image;
        return imageMagic;
    }

    public static ImageMagic newMagic(File image) {
        ImageMagic imageMagic = new ImageMagic();
        try {
            imageMagic.bufferedImage = ImageIO.read(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return imageMagic;
    }

    public static ImageMagic newMagic(String link) {
        ImageMagic imageMagic = new ImageMagic();
        try {
            URL url = new URL(link);
            imageMagic.bufferedImage = ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return imageMagic;
    }

    public ImageMagic() {
    }

    public ImageMagic setImageInsideOnCenter(BufferedImage inside, float alpha, int y) {
        int x = (width() - inside.getWidth()) / 2;
        return setImageInside(inside, alpha, x, y);
    }

    public ImageMagic setImageInsideOnCenterOffset(BufferedImage inside, float alpha, int y, int offset) {
        int x = (width() - inside.getWidth()) / 2 + offset;
        return setImageInside(inside, alpha, x, y);
    }

    public ImageMagic setImageInside(BufferedImage inside, float alpha, int x, int y) {
        Graphics2D gs = this.bufferedImage.createGraphics();
        gs.setComposite(AlphaComposite.getInstance(3, alpha));
        gs.drawImage(inside, x, y, null);
        gs.dispose();
        return this;
    }

    private enum XType {
        LEFT, CENTER, RIGHT, NONE;
    }

    public ImageMagic pressTextOnRight(String text, int y, int size, Color color, int offsetX) {
        return pressText(XType.RIGHT, text, 0, y, size, color, offsetX);
    }

    public ImageMagic pressTextOnCenter(String text, int y, int size, Color color) {
        return pressText(XType.CENTER, text, 0, y, size, color, 0);
    }

    public ImageMagic pressText(String text, int x, int y, int size, Color color) {
        return pressText(XType.NONE, text, x, y, size, color, 0);
    }

    private ImageMagic pressText(XType type, String text, int x, int y, int size, Color color, int offsetX) {
        BufferedImage image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
        Font font = new Font("Hiragino Sans GB W6", XType.RIGHT.equals(type) || XType.NONE.equals(type) ? Font.PLAIN : Font.BOLD, size);
        Graphics2D g = this.newGraphics2D(image, font, size, color);
        x = this.calculateX(g, font, type, x, text) + offsetX;
        g.drawString(text, x, y);
        g.dispose();
        this.bufferedImage = image;
        return this;
    }

    private int calculateX(Graphics2D g, Font font, XType type, int x, String text) {
        if (XType.CENTER.equals(type)) {
            FontRenderContext context = g.getFontRenderContext();
            Rectangle2D bounds = font.getStringBounds(text, context);
            return (int)((width() - bounds.getWidth()) / 2);
        } else if (XType.LEFT.equals(type)) {
            return 0;
        } else if (XType.RIGHT.equals(type)) {
            FontRenderContext context = g.getFontRenderContext();
            Rectangle2D bounds = font.getStringBounds(text, context);
            return (int)(width() - bounds.getWidth());
        } else {
            return x;
        }
    }

    private Graphics2D newGraphics2D(BufferedImage image, Font font, int size, Color color) {
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width(), height());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawImage(this.bufferedImage, 0, 0, width(), height(), null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g.setFont(font);
        g.setColor(color);
        return g;
    }

    public ImageMagic pressTextOnBox(String text, int x, int y, int width, int height, int size, Color color) {
        BufferedImage image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
        Font font = new Font("Hiragino Sans GB W6", Font.BOLD, size);
        Graphics2D g = this.newGraphics2D(image, font, size, color);
        FontRenderContext context = g.getFontRenderContext();
        List<String> lines = this.splitStringLines(text, width, height, font, context);
        for (int i = 0; i < lines.size(); i++) {
            int xValue = calculateX(g, font, XType.CENTER, x, lines.get(i));
            g.drawString(lines.get(i), xValue, centerY(i, size, lines.size()));
        }
        g.dispose();
        this.bufferedImage = image;
        return this;
    }

    private int centerY(int i, int size, int lineSize) {
        System.out.println("size = " + size);
        int height = height() - 90;
        int lineSpace = (int)(size * 0.3);
        int start = (height - lineSize * size - (lineSize - 1) * lineSpace) / 2;
        return start + (i * (size + lineSpace)) + size;
    }

    private List<String> splitStringLines(String text, int width, int height, Font font, FontRenderContext context) {
        List<String> lines = new ArrayList<>(8);
        int textWidth = this.getTextBoundWidth(text, font, context);
        if (textWidth < width) {
            lines.add(text);
        } else {
            int lineCount = 1 + (textWidth / width);
            int charCountOfEachLine = text.length() / lineCount;
            String useText = text;
            while (useText.length() > 0) {
                int endIndex = getLineEndIndex(useText, charCountOfEachLine, width, context, font);
                lines.add(useText.substring(0, endIndex));
                useText = useText.substring(endIndex);
            }
        }
        return lines;
    }

    private int getLineEndIndex(String text, int defaultLength, int width, FontRenderContext context, Font font) {
        String temp = text.length() > defaultLength ? text.substring(0, defaultLength) : text;
        int textWidth = this.getTextBoundWidth(temp, font, context);
        if (textWidth > width) {
            do {
                defaultLength--;
                temp = text.length() > defaultLength ? text.substring(0, defaultLength) : text;
            } while (this.getTextBoundWidth(temp, font, context) > width);
            return defaultLength;
        } else {
            do {
                if (defaultLength > text.length()) {
                    return text.length();
                }
                defaultLength++;
                temp = text.length() > defaultLength ? text.substring(0, defaultLength) : text;
            } while (this.getTextBoundWidth(temp, font, context) < width && defaultLength < text.length());
            return defaultLength - 1;
        }
    }

    private int getTextBoundWidth(String text, Font font, FontRenderContext context) {
        Rectangle2D bounds = font.getStringBounds(text, context);
        return (int)bounds.getWidth();
    }

    public int getTextWidth(String text, int size) {
        BufferedImage image = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
        Font font = new Font("Hiragino Sans GB W6",Font.BOLD, size);
        Graphics2D g = this.newGraphics2D(image, font, size, Color.BLACK);
        FontRenderContext context = g.getFontRenderContext();
        return this.getTextBoundWidth(text, font, context);
    }

    public ImageMagic roundedCornerRadio(int percent) {
        if (percent < 1 || percent > 100) {
            throw new IllegalArgumentException("percent value between 1 and 100");
        }
        int corner = Math.min(bufferedImage.getHeight(), bufferedImage.getWidth());
        corner = corner * percent / 100;
        return roundedCorner(corner);
    }

    public ImageMagic roundedCorner(int corner) {
        int w = width();
        int h = height();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, corner,
                corner));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(this.bufferedImage, 0, 0, null);

        g2.dispose();
        this.bufferedImage = output;
        return this;
    }
    
    public File toFile() throws IOException {
        File temp = File.createTempFile("images", ".png");
        ImageIO.write(this.bufferedImage, "png", temp);
        return temp;
    }

    public File toFile(File file) throws IOException {
        ImageIO.write(this.bufferedImage, "png", file);
        return file;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int height() {
        return this.bufferedImage.getHeight();
    }

    public int width() {
        return this.bufferedImage.getWidth();
    }

}
