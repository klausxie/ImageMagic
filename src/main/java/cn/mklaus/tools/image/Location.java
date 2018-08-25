package cn.mklaus.tools.image;

import lombok.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * 定位类，描述图片或文字在图片中的定位
 *
 * @author klaus
 * @date 2018/8/25 上午10:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Location {
    private BufferedImage backgroundImage;
    private BufferedImage overImage;
    private Font overFont;
    private String overText;
    private int bgHeight;
    private int bgWidth;
    private int overHeight;
    private int overWidth;

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    @Builder.Default
    private float alpha = 1.0f;

    private boolean verticalCenter;
    private boolean horizonCenter;

    /**
     * padding
     **/
    private boolean absolute;
    @Builder.Default
    private int top = -1;
    @Builder.Default
    private int bottom = -1;
    @Builder.Default
    private int left = -1;
    @Builder.Default
    private int right = -1;

    public void setup(BufferedImage backgroundImage, BufferedImage overImage) {
        this.backgroundImage = backgroundImage;
        this.overImage = overImage;
        this.bgHeight = backgroundImage.getHeight();
        this.bgWidth = backgroundImage.getWidth();
        this.overHeight = overImage.getHeight();
        this.overWidth = overImage.getWidth();
    }

    public void setup(BufferedImage backgroundImage, Font font, String text) {
        this.backgroundImage = backgroundImage;
        this.overFont = font;
        this.overText = text;
        this.bgHeight = backgroundImage.getHeight();
        this.bgWidth = backgroundImage.getWidth();

        BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = temp.createGraphics();
        FontRenderContext context = g.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(text, context);
        this.overWidth = (int)bounds.getWidth();
        this.overHeight = (int)bounds.getHeight();
    }


    public int getCenterX() {
        return (bgWidth - overWidth) / 2;
    }

    public int getCenterY() {
        return (bgHeight - overHeight) / 2;
    }

    public int getX() {
        int startX = x;
        if (horizonCenter) {
            startX = getCenterX();
        }
        if (absolute) {
            if (left > -1) {
                startX = left;
            } else if (right > -1) {
                startX = bgWidth - overWidth - right;
            }
        }
        return startX + offsetX;
    }

    public int getY() {
        int startY = y;
        if (verticalCenter) {
            startY = getCenterY();
        }
        if (absolute) {
            if (top > -1) {
                startY = top;
            } else if (bottom > -1) {
                startY = bgHeight - overHeight - bottom;
            }
        }
        return startY + offsetY;
    }

    public Location verticalCenter() {
        this.verticalCenter = true;
        return this;
    }

}
