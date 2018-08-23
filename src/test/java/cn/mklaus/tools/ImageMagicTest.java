package cn.mklaus.tools;


import org.junit.Assert;
import org.junit.Test;
import org.nutz.img.Images;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author klaus
 * @date 2018/8/23 下午3:01
 */
public class ImageMagicTest {

    private static File IMG = new File(ImageMagicTest.class.getResource("/curry.jpg").getFile());


    @Test
    public void read() {
        ImageMagic magic = ImageMagic.newMagic(IMG);
        Assert.assertEquals(750, magic.width());
        Assert.assertEquals(1122, magic.height());
    }

    @Test
    public void toFile() throws IOException {
        ImageMagic magic = ImageMagic.newMagic(IMG);
        File file = null;
        try {
            file = magic.toFile();
            Assert.assertNotNull(file);
        } catch (IOException e) {
            Assert.fail();
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    public void rotate() throws IOException {
        ImageMagic magic = ImageMagic.newMagic(IMG);
//        BufferedImage scale = Transformer.roundCorner(magic.getBufferedImage(), 7500);
//        File target = new File("target.jpg");
//        Images.clipScale(magic.getBufferedImage(), target, 1000, 1000);

        BufferedImage c = Combiner.combineBlank(magic.getBufferedImage(), Position.RIGHT, 200, Color.BLUE);
        save(c);
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
