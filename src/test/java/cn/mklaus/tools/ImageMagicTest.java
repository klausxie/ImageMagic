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

    private static File CURRY = getTestFile("/curry.jpg");
    private static File AVATAR = getTestFile("/avatar.jpg");

    private static File getTestFile(String path) {
        return new File(ImageMagicTest.class.getResource(path).getFile());
    }

    @Test
    public void read() {
        ImageMagic magic = ImageMagic.newMagic(CURRY);
        Assert.assertEquals(400, magic.width());
        Assert.assertEquals(598, magic.height());
    }

    @Test
    public void roundScaleTest() {
        ImageMagic magic = ImageMagic.newMagic(CURRY)
                .clipScale(400, 400);
        save(magic);
    }

    @Test
    public void roundCornerTest() {
        ImageMagic magic = ImageMagic.newMagic(AVATAR)
                .roundCorner(100);
        save(magic);
    }

    @Test
    public void roundCornerRadioTest() {
        ImageMagic magic = ImageMagic.newMagic(AVATAR)
                .roundCornerRadio(100);
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

        ImageMagic avatar = ImageMagic.newMagic(AVATAR).roundCornerRadio(100);

        ImageMagic magic = ImageMagic.newMagic(CURRY)
                .mergeInside(avatar.getBufferedImage(), location);

        save(magic);
    }

    @Test
    public void testPrintText() {
        Location location = Location.builder()
                .absolute(true)
                .bottom(50)
                .right(50)
                .build();

        Text text = Text.builder()
                .color(Color.GREEN)
                .font(new Font("Songti", Font.ITALIC , 32))
                .content("MVP MVP MVP")
                .build();


        ImageMagic magic = ImageMagic.newMagic(CURRY)
                .printText(text, location);
        save(magic);
    }

    @Test
    public void testAlpha() {
        ImageMagic curry = ImageMagic.newMagic(CURRY);
        ImageMagic avater = ImageMagic.newMagic(AVATAR);
        Location location = Location.builder().horizonCenter(true).verticalCenter(true).build();
        curry.mergeInside(avater.getBufferedImage(), location);
        save(curry);
    }

    @Test
    public void testFulifm() {
        File img1 = getTestFile("/img1.png");
        File img2 = getTestFile("/img2.png");

        ImageMagic merge = ImageMagic.newMagic(img1)
                .merge(ImageMagic.newMagic(img2).getBufferedImage(), Direction.LEFT);

        int height = merge.height();
        int width = merge.width();
        int blankHeight = Math.min(width / 10, Math.max(height / 5, 140));
        int blankHorizalPadding = blankHeight / 3;
        int textFontSize = blankHeight / 2;
        int textBottomPadding = (blankHeight - textFontSize) / 2;


        merge.mergeBlank(blankHeight, Color.WHITE, Direction.BOTTOM);

        Text text = Text.builder()
                .content("最美录取通知书")
                .font(new Font("Songti", Font.PLAIN, textFontSize))
                .build();

        Location location = Location.builder()
                .absolute(true)
                .left(blankHorizalPadding)
                .bottom(textBottomPadding)
                .build();

        merge.printText(text, location);


        int qrcodeHeight = (int)(blankHeight * 0.9);
        int qrcodeBottomPadding = (blankHeight - qrcodeHeight) / 2;

        File qrcode = getTestFile("/qrcode.png");
        ImageMagic qrMagic = ImageMagic.newMagic(qrcode)
                .scale(qrcodeHeight, qrcodeHeight);

        Location qrLocation = Location.builder()
                .absolute(true)
                .right(blankHorizalPadding)
                .bottom(qrcodeBottomPadding)
                .build();


        merge.mergeInside(qrMagic.getBufferedImage(), qrLocation);

        save(merge);
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
