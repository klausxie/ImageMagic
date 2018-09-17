package cn.mklaus.tools;

import cn.mklaus.tools.qrcode.Qrcodes;

import java.awt.*;
import java.io.File;

/**
 * @author klaus
 * @date 2018/9/17 下午5:26
 */
public class App {

    public static void main(String[] args) throws Exception {
        File file = Qrcodes.newBuilder()
                .content("http://chuangcifang.com")
                .filePath("/Users/klaus/Desktop/qrcode.png")
                .width(400)
                .backgroundColor(Color.WHITE)
                .foregroundColor(Color.GREEN)
                .padding(20)
                .buildToTempFile();
        System.out.println(file.getAbsolutePath());
    }

}
