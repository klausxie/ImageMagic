package cn.mklaus.tools.image;

import lombok.Data;
import lombok.ToString;

/**
 * @author klaus
 * Created on 2019/4/1 3:41 PM
 */
@Data
@ToString
public class Padding {

    public int top = 0;

    public int bottom = 0;

    public int left = 0;

    public int right = 0;

    public Padding(int top, int right, int bottom, int left) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public static Padding create(int top, int right, int bottom, int left) {
        return new Padding(top, right, bottom, left);
    }

    public static Padding create(int top, int horizon, int bottom) {
        return new Padding(top, horizon, bottom, horizon);
    }

    public static Padding create(int vertical, int horizon) {
        return new Padding(vertical, horizon, vertical, horizon);
    }

    public static Padding create(int padding) {
        return new Padding(padding, padding, padding, padding);
    }

}
