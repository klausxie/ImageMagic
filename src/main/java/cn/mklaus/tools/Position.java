package cn.mklaus.tools;

/**
 * @author klaus
 * @date 2018/8/23 下午5:16
 */
public enum Position {

    /**
     * 上
     */
    TOP,

    /**
     * 右
     */
    RIGHT,

    /**
     * 下
     */
    BOTTOM,

    /**
     * 左
     */
    LEFT

    ;

    public boolean isVertical() {
        return TOP == this || BOTTOM == this;
    }

}
