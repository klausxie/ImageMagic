package cn.mklaus.tools.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.awt.*;

/**
 * @author klaus
 * Created on 2018/8/25 下午5:40
 */
@Data
@ToString
@AllArgsConstructor
@Builder
public class Text {

    private String content;

    @Builder.Default
    private Font font = new Font("Songti", Font.PLAIN, 32);

    @Builder.Default
    private Color color = Color.BLACK;

    @Builder.Default
    private float alpha = 1.0f;

}
