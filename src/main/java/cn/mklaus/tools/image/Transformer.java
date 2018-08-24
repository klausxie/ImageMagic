package cn.mklaus.tools.image;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * 图片转换变形工具类
 *
 * @author klaus
 * @date 2018/8/23 下午2:42
 */
public class Transformer {

    public static BufferedImage scale(BufferedImage im, int w, int h) {
        BufferedImage re = new BufferedImage(w, h, im.getType());
        Graphics2D g = re.createGraphics();
        g.drawImage(im, 0, 0, w, h, null);
        g.dispose();
        return re;
    }

    public static BufferedImage zoomScale(BufferedImage im, int w, int h, Color bgColor) {
        int ow = im.getWidth();
        int oh = im.getHeight();

        float or = (float)ow / (float)oh;
        float nr = (float)w / (float)h;

        int x = 0;
        int y = 0;
        int nw = 0;
        int nh = 0;

        if (or > nr) {
            nw = w;
            nh = (int)((float)w / or);
            y = (h - nh) / 2;
        } else if (or < nr) {
            nh = h;
            nw = (int)((float)h * or);
            x = (w - nw) / 2;
        } else {
            nw = w;
            nh = h;
        }

        BufferedImage re = new BufferedImage(w, h, im.getType());
        Graphics2D g = re.createGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, w, h);

        g.drawImage(im, x, y, nw, nh, null);
        g.dispose();

        return re;
    }

    public static BufferedImage clipScale(BufferedImage im, int w, int h) {
        int oW = im.getWidth();
        int oH = im.getHeight();
        float oR = (float)oW / (float)oH;
        float nR = (float)w / (float)h;

        int nW;
        int nH;
        int x;
        int y;

        if (oR > nR) {
            nW = h * oW / oH;
            nH = h;
            x = (w - nW) / 2;
            y = 0;
        } else if (oR < nR) {
            nW = w;
            nH = w * oH / oW;
            x = 0;
            y = (h - nH) / 2;
        } else {
            nW = w;
            nH = h;
            x = 0;
            y = 0;
        }

        BufferedImage re = new BufferedImage(w, h, im.getType());
        re.createGraphics().drawImage(im, x, y, nW, nH,null);
        return re;
    }

    public static BufferedImage roundedCornerRadio(BufferedImage im, int percent) {
        if (percent < 1 || percent > 100) {
            throw new IllegalArgumentException("percent value between 1 and 100");
        }
        int corner = Math.min(im.getHeight(), im.getWidth());
        corner = corner * percent / 100;
        return roundCorner(im, corner);
    }

    public static BufferedImage roundCorner(BufferedImage im, int corner) {
        int w = im.getWidth();
        int h = im.getHeight();

        BufferedImage re = new BufferedImage(w, h,  BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = re.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, corner, corner));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(im, 0, 0, null);

        g2.dispose();
        return re;
    }

}
