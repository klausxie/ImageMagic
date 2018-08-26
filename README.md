# ImageMagic Java图片编辑工具

### 安装 Maven

```
<dependency>
   <groupId>org.projectlombok</groupId>
   <artifactId>lombok</artifactId>
   <version>1.18.2</version>
</dependency>
```

### 使用

```java

//从文件中读取
File img = new File("path/to/img.png");
ImageMagic magic = ImageMagic.newMagic(img);

//从网络中读取
String imgUrl = "http://img.xx.com/hash.jpg";
ImageMagic magic = ImageMagic.newMagic(imgUrl);


```

##### 图片圆角
<style>
    .box {text-align:center}
    img {display: block; width=160px}
</style>
<div align=center style="display: flex; ">
    <div class="box">
        <img src="./src/test/resources/avatar.jpg">
        <label>原图</label>
    </div>
    <div class="box">
        <img src="./src/test/resources/avatar_round_100.jpg">
        <label>圆角100像素</label>
    </div>
    <div class="box">
        <img src="./src/test/resources/avatar_round_circle.jpg">
        <label>圆角100%</label>
    </div>
</div>



```java

// 圆100个像素点
magic.roundCorner(100); 

// 圆100%最小长度边
// 等同于 magic.roundCorner(Math.min(magic.height(), magic.width()))
magic.roundCornerRadio(100);

```

##### 图片缩放

```java

// 全覆盖
magic.scale(300, 300);

// 等比覆盖
magic.zoomScale(300, 300, Color.GRAY);

// 剪切覆盖
magic.clipScale(300, 300);

```

##### 透明

```java
// 透明区间： 看不见 0 - 透明程度 - 1.0 等于没操作
magic.alpha(0.6f)

```