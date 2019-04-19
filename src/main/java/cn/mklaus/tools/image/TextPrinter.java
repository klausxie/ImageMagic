package cn.mklaus.tools.image;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author klaus
 * Created on 2019/4/1 3:41 PM
 */
public class TextPrinter {


    private static FontRenderContext FONT_RENDER_CONTEXT = ImageMagic.createBlank(10, 10, Color.WHITE)
            .getBufferedImage()
            .createGraphics()
            .getFontRenderContext();


    /**
     * 将一段文本内容分成多行
     *
     * @param content       文本内容
     * @param lineWidth     每行最多宽度
     * @param font          Font.class 带有字体大小信息。
     * @return              List
     */
    public static List<String> splitTextMatchLineWidth(String content, int lineWidth, Font font) {
        List<String> textList = new ArrayList<>();
        while (content.length() > 0) {
            int position = splitOnOneLine(content, lineWidth, font);

            String line = content.substring(0, position);
            content = content.substring(position);
            textList.add(line.trim());
        }
        return textList;
    }

    /**
     * 从文本中选取满足一行的第一个结尾点。
     *
     * @param content       文本内容
     * @param lineWidth     每行最多宽度
     * @param font          Font.class 带有字体大小信息。
     * @return
     */
    private static int splitOnOneLine(String content, int lineWidth, Font font) {
        boolean prevCharIsLetter = false;
        boolean currentCharIsLetter;
        int englishWordStartIndex = -1;

        int pos = 1;
        while (pos <= content.length()) {
            String text = content.substring(0, pos);

            // 换行
            if (text.contains("\n")) {
                return pos;
            }

            currentCharIsLetter = isEndWithLetter(text);
            if (calculateTextWidth(text, font) > lineWidth) {
                if (prevCharIsLetter && currentCharIsLetter) {
                    pos = englishWordStartIndex;
                }
                break;
            }

            if (!prevCharIsLetter && currentCharIsLetter) {
                englishWordStartIndex = pos - 1;
            }
            prevCharIsLetter = currentCharIsLetter;

            pos++;
        }
        return pos - 1;
    }

    /**
     * 计算在指定字体下的文本所占长度
     * @param text  文本
     * @param font  字体
     * @return  Double
     */
    private static int calculateTextWidth(String text, Font font) {
        return (int)Math.round(font.getStringBounds(text, FONT_RENDER_CONTEXT).getWidth());
    }

    /**
     * 判断字符串是否以[a-zA-Z]结尾
     * @param text  字符串
     * @return  Boolean
     */
    private static boolean isEndWithLetter(String text) {
        char c = text.charAt(text.length() - 1);
        return  (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }


    /**
     * 水印文字
     * @param bg        背景图
     * @param text      文字
     * @param location  位置信息
     * @return          处理后的图片 Buffer
     */
    public static BufferedImage printText(BufferedImage bg, Text text, Location location) {
        location.setup(bg, text);
        Graphics2D g = bg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, text.getAlpha()));
        g.setColor(text.getColor());
        g.setFont(text.getFont());
        g.drawString(text.getContent(), location.getX(), location.getY());
        g.dispose();
        return bg;
    }

    public static BufferedImage printMultiLineText(BufferedImage bg, MultiLineText multiLineText, Location location) {
        BufferedImage mutliLineImage = createMultiLineImage(multiLineText);
        return Combiner.mergeInside(bg, mutliLineImage, location);
    }

    /**
     * 创建多行文本图片。【透明底】
     * 图片总高度 = 文本高度 + 上下留空
     *  文本高度：lines.size() * lineHeightRatio * font.size
     *  上下留空：(lineHeightRatio - 1) * font.size
     *
     * @param multiLineText 多行文本模型
     * @return  BufferedImage
     */
    public static BufferedImage createMultiLineImage(MultiLineText multiLineText) {
        Font font = multiLineText.getFont();
        int fontSize = font.getSize();

        List<String> lines = multiLineText.splitTextMatchLineWidth();

        int allTextHeight = (int) (lines.size() * fontSize * multiLineText.getLineHeightRatio());
        int imageHeight = allTextHeight + multiLineText.getPadding() * 2;
        int offsetY = multiLineText.getOffsetY();

        ImageMagic bg = ImageMagic.createTransparent(multiLineText.getLineWidth(), imageHeight);
        Text text;
        Location location;
        for (int i = 0; i < lines.size(); i++) {
            int paddingTop = Math.round( i * (fontSize * multiLineText.getLineHeightRatio())) + multiLineText.getPadding();
            text = Text.builder()
                    .color(multiLineText.getColor())
                    .font(font)
                    .content(lines.get(i))
                    .build();
            location = Location.builder()
                    .absolute(true)
                    .top(paddingTop)
                    .offsetY(offsetY)
                    .build();
            bg.printText(text, location);
        }
        return bg.getBufferedImage();
    }

    /**
     * 创建多行文本图片。
     *
     * @param multiLineText     多行文本模型
     * @param backgroundColor   背景颜色
     * @param padding           Padding，填充背景颜色
     * @return  BufferedImage
     */
    public static BufferedImage createMultiLineImage(MultiLineText multiLineText, Color backgroundColor, Padding padding) {
        Font font = multiLineText.getFont();
        int fontSize = font.getSize();

        List<String> lines = multiLineText.splitTextMatchLineWidth();
        int allTextHeight = (int) (lines.size() * fontSize * multiLineText.getLineHeightRatio());
        int imageHeight = padding.top + padding.bottom + allTextHeight;
        int imageWidth = padding.left + padding.right + multiLineText.getLineWidth();
        ImageMagic bg = ImageMagic.createBlank(imageWidth, imageHeight, backgroundColor);

        int offsetY = -(int)((multiLineText.getLineHeightRatio() - 1) / 2 * fontSize);
        Text text;
        Location location;
        for (int i = 0; i < lines.size(); i++) {
            int paddingTop = padding.top + (int)( i * (fontSize * multiLineText.getLineHeightRatio()));
            text = Text.builder()
                    .color(multiLineText.getColor())
                    .font(font)
                    .content(lines.get(i))
                    .build();
            location = Location.builder()
                    .absolute(true)
                    .top(paddingTop)
                    .left(padding.left)
                    .offsetY(offsetY)
                    .build();
            bg.printText(text, location);
        }
        return bg.getBufferedImage();
    }

    public static void main(String[] args) throws IOException {
        MultiLineText contentMultiLineText = MultiLineText.builder()
                .lineWidth(800)
                .content("动：八年初心不改，诠释中国创新力量")
                .font(new Font("Source Han Sans SC Heavy", Font.PLAIN, 50))
                .build();
        BufferedImage contentBI = TextPrinter.createMultiLineImage(contentMultiLineText, Color.WHITE, Padding.create(0,  0, 0, 0));
        ImageMagic.newMagic(contentBI).toFile(new File("/Users/klaus/Desktop/tools.png"));

    }

}
