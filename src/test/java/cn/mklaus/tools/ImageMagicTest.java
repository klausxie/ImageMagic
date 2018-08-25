package cn.mklaus.tools;


import cn.mklaus.tools.image.*;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author klaus
 * @date 2018/8/23 下午3:01
 */
public class ImageMagicTest {

    private static File CURRY = new File(ImageMagicTest.class.getResource("/curry.jpg").getFile());
    private static File IMG = new File(ImageMagicTest.class.getResource("/img.png").getFile());
    private static File AVATAR = new File(ImageMagicTest.class.getResource("/avatar.jpg").getFile());

    private static ImageMagic CURRY_MAGIC = ImageMagic.newMagic(CURRY);
    private static ImageMagic IMG_MAGIC = ImageMagic.newMagic(IMG);
    private static ImageMagic AVATAR_MAGIC = ImageMagic.newMagic(AVATAR);

    @Test
    public void read() {
        ImageMagic magic = ImageMagic.newMagic(IMG);
        Assert.assertEquals(750, magic.width());
        Assert.assertEquals(1122, magic.height());
    }

    @Test
    public void roundCornerTest() {
        ImageMagic magic = ImageMagic.newMagic(AVATAR)
                .roundCorner(80);
        save(magic);
    }

    @Test
    public void roundCornerRadioTest() {
        ImageMagic magic = ImageMagic.newMagic(AVATAR)
                .roundCorner(100);
        save(magic);
    }

    @Test
    public void mergeTest() {
        ImageMagic curry = ImageMagic.newMagic(CURRY);
        ImageMagic magic = ImageMagic.newMagic(AVATAR)
                .merge(curry.getBufferedImage(), Direction.BOTTOM);
        save(magic);
    }

    @Test
    public void mergeBlankTest() {
        ImageMagic magic = ImageMagic.newMagic(AVATAR)
                .mergeBlank(200, Color.GREEN, Direction.LEFT);
        save(magic);
    }

    @Test
    public void testMergeInside() {
        Location location = Location.builder()
                .horizonCenter(true)
                .verticalCenter(true)
                .absolute(true)
                .right(20)
                .build();

        AVATAR_MAGIC.roundCornerRadio(100);
        ImageMagic im = ImageMagic.newMagic(CURRY)
                .mergeInside(AVATAR_MAGIC.getBufferedImage(), location);

        save(im);
    }

    @Test
    public void testPrintText() {
        Location location = Location.builder()
                .horizonCenter(true)
//                .offsetY(50)
//                .verticalCenter(true)
                .absolute(true)
                .right(20)
                .bottom(0)
                .left(20)
//                .top(0)
                .build();

        Text text = Text.builder()
                .color(Color.GREEN)
                .font(new Font("Songti", Font.ITALIC , 16))
                .content("MVP MVP MVP")
                .build();

        BufferedImage im = Combiner.printText(CURRY_MAGIC.getBufferedImage(), text, location);
        save(im);
    }

    private void save(BufferedImage bufferedImage) {
        ImageMagic magic = ImageMagic.newMagic(bufferedImage);
        save(magic);
    }

    private void save(ImageMagic imageMagic) {
        File target = new File("target.jpg");
        try {
            imageMagic.toFile(target);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
