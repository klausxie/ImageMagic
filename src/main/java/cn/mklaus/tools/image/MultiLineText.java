package cn.mklaus.tools.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.awt.*;
import java.util.List;

/**
 * @author klaus
 * @date 2019/4/1 3:35 PM
 */
@Data
@ToString
@AllArgsConstructor
@Builder
public class MultiLineText {

    public static final float DEFAULT_LINE_HEIGHT_RATIO = 1.3f;

    private String content;

    @Builder.Default
    private Font font = new Font("Songti", Font.PLAIN, 32);

    @Builder.Default
    private Color color = Color.BLACK;

    @Builder.Default
    private float alpha = 1.0f;

    @Builder.Default
    private boolean breakWord = true;

    @Builder.Default
    private float lineHeightRatio = DEFAULT_LINE_HEIGHT_RATIO;

    @Builder.Default
    private int lineWidth = Integer.MAX_VALUE;

    /**
     * 直接调用 g.drawString 渲染出来的文本不是上下居中的。
     * 需要计算 offsetY 偏移居中。
     * @return  int
     */

    /**
     * PADDING_TOP_RATIO. 是当你渲染一行文本的时候。文字离顶部有字体大小的0.3倍的空隙。
     * 例如：font-size: 40 的文本渲染出来的行高是 40 * 1 + 40 * 0.3 = 52;
     *
     * 估计出来了行高，就可以通过计算 Y 轴方向的偏移来使得文本上下居中。
     */
    public static final Float PADDING_TOP_RATIO = 0.3f;
    public int getOffsetY() {
        if (lineHeightRatio < 1.0f) {
            throw new IllegalArgumentException("lineHeightRatio must not less than 1.0, given " + lineHeightRatio);
        }
        return Math.round(((lineHeightRatio - 1)/2 - PADDING_TOP_RATIO) * font.getSize());
    }
    public int getPadding() {
        return Math.round((lineHeightRatio - 1)/2 * font.getSize());
    }


    public List<String> splitTextMatchLineWidth() {
        if (content == null || content.trim().length() == 0) {
            throw new RuntimeException("content is not set");
        }
        return TextPrinter.splitTextMatchLineWidth(content, lineWidth, font);
    }

}
