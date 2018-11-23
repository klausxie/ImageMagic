# ImageMagic Java图片编辑工具

### 安装 Maven

```
<dependency>
   <groupId>cn.mklaus.tools</groupId>
   <artifactId>tools</artifactId>
   <version>0.3.0</version>
</dependency>
```

### 0.2.0更新，添加二维码生成功能

![image](http://download.chuangcifang.com/a1a5f502ba6011e8ba8ff218982a9b2e)

```java
 File file = Qrcodes.newBuilder()
                .content("http://chuangcifang.com")
                .filePath("/Users/klaus/Desktop/qrcode.png") // 可选
                .width(400)
                .backgroundColor(Color.WHITE)
                .foregroundColor(Color.YELLOW)
                .padding(20)
                .buildToTempFile();

```


### 使用

```java

//从文件中读取
File img = new File("path/to/img.png");
ImageMagic magic = ImageMagic.newMagic(img);

//从网络中读取
String imgUrl = "http://img.xx.com/hash.jpg";
ImageMagic magic = ImageMagic.newMagic(imgUrl);

// 保存到临时文件
File temp = magic.toFile();

// 保存到指定文件
File save = new File();
magic.toFile(save);

```

##### 图片圆角

![image](http://download.chuangcifang.com/d813d238a94d11e8a153f218982a9b2e)

```java

// 圆100个像素点
magic.roundCorner(100); 

// 圆100%最小长度边
// 等同于 magic.roundCorner(Math.min(magic.height(), magic.width()))
magic.roundCornerRadio(100);

```

##### 图片缩放

![image](http://download.chuangcifang.com/ef44a28ca94d11e8bb81f218982a9b2e)

```java

// 全覆盖
magic.scale(400, 400);

// 等比覆盖
magic.zoomScale(400, 400, Color.GRAY);

// 剪切覆盖
magic.clipScale(400, 400);

```

##### 透明

```java
// 透明区间： 看不见 0 - 透明程度 - 1.0 等于没操作
magic.alpha(0.6f)

```

##### 图片水印

![image](http://download.chuangcifang.com/fd9a0e6ea94d11e898f2f218982a9b2e?imageView2/2/h/400)

```java

File curry = new File("curry.jpg");
File avatar = new File("avatar.jpg");

// 将头像先缩放，圆角，透明处理
ImageMagic avatarMagic = ImageMagic.newMagic(avatar)
                .scale(60, 60)
                .roundCornerRadio(100)
                .alpha(0.8f);

// 定位信息
Location location = Location.builder()
                .absolute(true)
                .left(20)
                .verticalCenter(true)
                .build();

// 将头像放入背景图中
ImageMagic.newMagic(curry)
                .mergeInside(avatarMagic.getBufferedImage(), location);


```

##### 文字水印

![image](http://download.chuangcifang.com/0d10543da94e11e8bb63f218982a9b2e?imageView2/2/h/400)

```java
Location location = Location.builder()
                .absolute(true)
                .bottom(20)
                .right(20)
                .build();

Text text = Text.builder()
            .color(Color.GREEN)
            .font(new Font("Songti", Font.PLAIN , 32))
            .alpha(0.7f)
            .content("MVP MVP MVP")
            .build();

File curry = new File("curry.jpg");
ImageMagic.newMagic(curry)
                .printText(text, location);


```

### 定位类 Location 和 方向枚举 Direction

```java

// 覆盖优先级： absolute 定位 > center 定位 > 坐标x,y定位
// 另外有： left > right, top > bottom 

Location.builder()
    .x(x)
    .y(y)
    .verticalCenter(true)
    .horizonCenter(true)
    .absolute(true)
    .top(top)
    .right(right)
    .bottom(bottom)
    .left(left)
    .offsetX(offsetX)
    .offsetY(offsetY)
    .build();



// 获取 X 坐标源码
public int getX() {
    int startX = x;
    if (horizonCenter) {
        startX = getCenterX();
    }
    if (absolute) {
        if (left > -1) {
            startX = left;
        } else if (right > -1) {
            startX = bgWidth - overWidth - right;
        }
    }
    return startX + offsetX;
}

// 方向枚举用于合并两张图片时，指明合并方向
Direction { TOP, RIGHT, BOTTOM, LEFT }

```


### 一个项目的需求例子

需要将两个用户上传的图片合并，写上活动文字和二维码。

![image](http://download.chuangcifang.com/1bb05bcca94e11e897d9f218982a9b2e?imageView2/2/h/400)


```java
File img1 = new File("img1.png");
File img2 = new File("img2.png");

// 合并两张图片
ImageMagic merge = ImageMagic.newMagic(img1)
        .merge(ImageMagic.newMagic(img2).getBufferedImage(), Direction.LEFT);

int height = merge.height();
int width = merge.width();
int blankHeight = Math.min(width / 10, Math.max(height / 5, 140));

// 加入白底
merge.mergeBlank(blankHeight, Color.WHITE, Direction.BOTTOM);


int blankHorizalPadding = blankHeight / 3;
int textFontSize = blankHeight / 2;
int textBottomPadding = (blankHeight - textFontSize) / 2;

Text text = Text.builder()
        .content("最美录取通知书")
        .font(new Font("Songti", Font.PLAIN, textFontSize))
        .build();

Location location = Location.builder()
        .absolute(true)
        .left(blankHorizalPadding)
        .bottom(textBottomPadding)
        .build();

// 水印文字
merge.printText(text, location);


int qrcodeHeight = (int)(blankHeight * 0.9);
int qrcodeBottomPadding = (blankHeight - qrcodeHeight) / 2;

File qrcode = new File("qrcode.png");
ImageMagic qrMagic = ImageMagic.newMagic(qrcode)
        .scale(qrcodeHeight, qrcodeHeight);

Location qrLocation = Location.builder()
        .absolute(true)
        .right(blankHorizalPadding)
        .bottom(qrcodeBottomPadding)
        .build();

// 合并二维码
merge.mergeInside(qrMagic.getBufferedImage(), qrLocation);

```

输出结果：

![image](http://download.chuangcifang.com/295b9111a94e11e89383f218982a9b2e?imageView2/2/h/400)

### 注意

目前图片透明有一个问题。如果一张图片先调用 alpha(), 再后面调用 roundCorner()就会出现透明失效问题。
而先调用 roundCorner(), 再后调用 alpha() 则功能正常。
