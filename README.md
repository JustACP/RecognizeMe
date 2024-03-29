# RecognizeMe

A Face Detector and Recognizer SDK
一个基于Java的人脸检测和人脸识别SDK

第一次写这种库，各方面都还很有缺陷，写的不是很好。
还是忍不住吐槽 `OpenCV`对 `Java`的支持。

## 结构

1. 识别核心位于 `com.FaceCore.FaceCore`
   * 多数功能位于 `FaceCore`
2. Object 文件
   1. `Person` 类：
      * 储存 name id faceFeature（人脸特征）
   2. `NoFaceFeatureException`
3. 依赖： `OpenCV 4.6.0`（已包含）

## 食用指南

1. 导入作为库
2. FaceCore faceCore = new FaceCore(); //创建识别核心

   1. faceRecognizerByImages(String srcImage,String dstImage);

      1. srcImage 是待比对照片的文件路径 dstImage为参考对象图片路径
      2. 作用：将srcImg中的人脸与dstImg人脸对比判断是否为同一人（src可以是多个人脸）
      3. 返回 真或假
   2. faceRecognizerSimilarityRatioByImages(String srcImage, String dstImage)；

      1. 参数与上述相同
      2. 返回相似度多个人脸与dst
   3. faceRegister(Person person,String srcImg)；

      1. person 待注册的人物（name，id） srcImg 人脸照片
      2. 作用：将人脸注册
   4. isRegistered(Person person)

      1. 判断person是否已注册

   **注意:** 以下方法每张图片只能包含一个人脸信息
   5. faceRecognizerByPerson(String srcImage, Person person)
   1. 与已注册人物比对，返回真假

   6. faceRecognizerSimilarityRatioByPerson(String srcImage, Person person)；
      1. 与已注册人物比对，返回相似度

## Linux 下环境测试：

`.so`文件编译于WSL
WSL 下测试正常：
![WSL测试图片](http://oss.re1ife.top//media/WSL测试图片.png)

**若需要在Linux下运行，我强烈建议您在目标运行环境下重新构建OpenCV4.6的so文件。** 然后将 `resoucecs`文件夹中的so文件替换。
