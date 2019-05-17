### 简介

在之前的文章中，绘制了一些图形，但是有个问题， 以三角形为例，根据设置的坐标，应该显示正三角形，而手机上运行确被拉长为等边三角形（底边比左右两边短）。

如果手机横向防止，三角形就被压扁了。

了解两个坐标系之后，这个问题的原因就清楚了。

### 归一化设备坐标系、屏幕坐标系、虚拟坐标系

opengl 的坐标系是归一化设备坐标系，原点在屏幕中心，横向是横坐标，纵向是纵坐标，范围都是[-1,1] 。

android设备的屏幕坐标系远点在屏幕左上角，横向是横坐标，纵向是纵坐标，但是纵坐标的正方向向下，范围由手机屏幕像素大小决定。

咱们先不管坐标系的方向，因为方向和拉伸/压扁的问题没有关系。

从两坐标系定义的范围来看，归一化设备坐标系定义了一个正方形区域，而屏幕坐标系定义的区域由具体像素决定，一般手机都是矩形。

假如，屏幕尺寸为 1920 * 1080，opengl中的一个顶点的坐标是（0.5，0.5），则换算成屏幕坐标就是 （960，540）。也就是说，这个点在归一化设备坐标中，距离x、y轴的距离是一样的，而在屏幕坐标系中，到x、y轴的距离就不一样了，也就是上文问题中拉伸、压扁的意思。

修复这个问题很简单，加上一个虚拟坐标系。

这个虚拟坐标系的范围由屏幕尺寸计算出来，将屏幕宽高中较小的一方映射到范围 [-1,1] 上，较大的一方映射到范围 [-(big/small),big/small] 。

使用时，顶点的坐标在虚拟坐标系下定义，在着色器中使用正交投影矩阵换算为归一化设备坐标，然后opengl渲染到屏幕坐标系上，绘制出来的图形就不会变形了。

### 使用示例

例如，屏幕尺寸为 1920 * 1080，则虚拟坐标系的范围是 x：[-1920/1080，1920/1080]，y：[-1，1]。

然后我们在虚拟坐标系下定义顶点坐标 （0.5，0.5）。

接着着色其中通过正交投影矩阵，将顶点坐标换算为 （0.28……，0.5），这里的0.28…… 是个近似小数。

opengl 渲染到屏幕坐标系上的坐标是（~540，540），x坐标在精度范围内无限接近y坐标，也就是说顶点在屏幕坐标系下，到x、y轴的距离也是一样了，也就不会再变形了。

下面看看正交投影是怎么进行换算的。

### 正交投影

android 中 Matrix 类中 orthoM() 方法可以用来生成一个正交投影矩阵，具体参数的含义看下面的代码注释。

```java
    /**
     * Computes an orthographic projection matrix.
     *
     * @param m 存放结果矩阵的数组
     * @param mOffset 结果矩阵的起始偏移值
     * @param left x轴最小范围
     * @param right x轴最大范围
     * @param bottom y轴最小范围
     * @param top y轴最大范围
     * @param near z轴最小范围
     * @param far z轴最大范围
     */
    public static void orthoM(float[] m, int mOffset,float left, float right, float bottom, float top,float near, float far) {}
```

生成的结果矩阵的元素值如下

![](https://wangyt-imgs.oss-cn-beijing.aliyuncs.com/blog/android-opengles-%E6%AD%A3%E4%BA%A4%E6%8A%95%E5%BD%B1/001.png)

### 代码实现

计算正交投影矩阵

```java
    public final float[] projectionMatrix = new float[16];


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        glViewport(0, 0, width, height);
        //计算正交投影矩阵，修正变形
        float aspectRatio = width > height ?
                (float)width / (float)height : (float)height / (float)width;
        if (width > height){
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        }else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }
```

顶点着色器中使用正交投影矩阵进行换算

```
#version 300 es

in vec4 vPosition;
uniform mat4 matrix;

void main()
{
    gl_Position = matrix * vPosition;
}
```

绘制时将计算出的矩阵传入着色器中

```java
        //传入正交矩阵修复变形
        int matrixLoc = glGetUniformLocation(program, VERTEX_ATTRIB_PROJECTION_MATRIX);
        glUniformMatrix4fv(matrixLoc, 1, false, projectionMatrix, 0);
```

其他就和以前一样了，可以参考之前的文章。

### 总结

本文梳理了归一化设备坐标系、屏幕坐标系、虚拟坐标系三个坐标系之间的关系，通过正交投影，完成虚拟坐标到归一化设备坐标的换算，进而修复图形变形的问题。