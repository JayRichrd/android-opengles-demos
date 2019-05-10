### 简介

本文在上一篇文章的基础上完成纹理的绘制。

绘制纹理流程也不复杂：

* 一张作为纹理的图片
* 编写相应的着色器
* 准备图形和纹理的坐标
* 将图片转换成纹理
* 将纹理绑定到着色器指定属性的位置
* 绘制图形和纹理

### 编写着色器

**顶点着色器**

* a_Position：图形定点坐标
* a_texCoord：对应的纹理坐标，其他位置的坐标 opengles 通过插值进行计算
* v_texCoord：输出到到片段着色器的纹理坐标

在 main() 方法中进行赋值。

```
#version 300 es

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec2 a_texCoord;

out vec2 v_texCoord;

void main()
{
    gl_Position = a_Position;
    v_texCoord = a_texCoord;
}
```

**片段着色器**

s_texture：纹理的采样

通过 texture() 方法将传进来的纹理和坐标进行差值采样，输出到颜色缓冲区。

```
#version 300 es
precision mediump float;

in vec2 v_texCoord;
layout (location = 0) out vec4 outColor;
uniform sampler2D s_texture;

void main(){
    outColor = texture(s_texture, v_texCoord);
}
```

### 准备顶点、纹理坐标

opengl 的坐标系是归一化坐标系，原点在屏幕中心，横向是横坐标，纵向是纵坐标，范围都是[-1,1]

一般设备的屏幕坐标系远点在屏幕左上角，横向是横坐标，纵向是纵坐标，但是纵坐标的正方向向下

纹理的坐标系是左下角是原点，右上方是正方向，也是归一化坐标系，范围是[0,1]

所以纹理显示到屏幕上是，纹理坐标与定点坐标的 y 坐标方向是反着的。

```java
    //顶点，按逆时针顺序排列
    public static final float[] VERTEX = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f};

    //纹理坐标，（s,t），t坐标方向和顶点y坐标反着
    public static final float[] TEXTURE_COORD = {
            0.5f,0.0f,
            0.0f,1.0f,
            1.0f,1.0f
    };
```

### 图片转换成纹理

主要就是通过 `glGenTextures` 方法创建纹理对象

然后将图片加载进来生成位图，绑定到纹理对象上，别忘了设置放大和缩小的过滤模式

再使用 `glBindTexture` 方法绑定纹理到 opengl

通过 texImage2D 将位图绑定到纹理上，`glGenerateMipmap` 创建 mip 贴图

最后位图的资源已经绑定并复制到纹理对象上了，所以 bitmap 对象就可以释放了，并且纹理也可以从 opengl 解绑。

```java
    public static int loadTexture(int resId){
        //创建纹理对象
        int[] textureObjIds = new int[1];
        //生成纹理：纹理数量、保存纹理的数组，数组偏移量
        glGenTextures(1, textureObjIds,0);
        if(textureObjIds[0] == 0){
            throw new RuntimeException("创建纹理对象失败");
        }
        //原尺寸加载位图资源（禁止缩放）
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (bitmap == null){
            //删除纹理对象
            glDeleteTextures(1, textureObjIds, 0);
            throw new RuntimeException("加载位图失败");
        }
        //绑定纹理到opengl
        glBindTexture(GL_TEXTURE_2D, textureObjIds[0]);
        //设置放大、缩小时的纹理过滤方式，必须设定，否则纹理全黑
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        //将位图加载到opengl中，并复制到当前绑定的纹理对象上
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //创建 mip 贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        //释放bitmap资源（上面已经把bitmap的数据复制到纹理上了）
        bitmap.recycle();
        //解绑当前纹理，防止其他地方以外改变该纹理
        glBindTexture(GL_TEXTURE_2D, 0);
        //返回纹理对象
        return textureObjIds[0];
    }
```

### 渲染器绘制

流程基本和之前文章的绘制流程相同，具体的细节下面代码中都有很详细的注释。

```java
    public void render(Surface surface, int width, int height){
        //创建屏幕上渲染区域：EGL窗口
        int[] surfaceAttribList = {EGL_NONE};
        EGLSurface eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribList, 0);
        //指定当前上下文
        eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
        //获取着色器
        int texVertexShader = loadShader(GL_VERTEX_SHADER, loadShaderSource(R.raw.texture_vertex_shader));
        int texFragmentShader = loadShader(GL_FRAGMENT_SHADER, loadShaderSource(R.raw.texture_fragtment_shader));
        //创建并连接程序
        int program = createAndLinkProgram(texVertexShader, texFragmentShader);
        //设置清除渲染时的颜色
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        //设置视口
        glViewport(0, 0, width, height);
        //获取顶点、纹理坐标数据
        FloatBuffer vertexBuffer = getVertextBuffer();
        FloatBuffer texCoordBuffer = getTextureCoordBuffer();
        //擦除屏幕
        glClear(GL_COLOR_BUFFER_BIT);
        //使用程序
        glUseProgram(program);

        //绑定顶点、纹理坐标到指定属性位置
        int aPosition = glGetAttribLocation(program, "a_Position");
        int aTexCoord = glGetAttribLocation(program, "a_texCoord");
        glVertexAttribPointer(aPosition,3,GL_FLOAT,false,0,vertexBuffer);
        glVertexAttribPointer(aTexCoord, 2, GL_FLOAT, false, 0, texCoordBuffer);
        glEnableVertexAttribArray(aPosition);
        glEnableVertexAttribArray(aTexCoord);
        //绑定纹理
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, loadTexture(R.drawable.texture));
        //Set the sampler texture unit to 0
        glUniform1i(glGetUniformLocation(program, "s_texture"),0);
        //绘制
        glDrawArrays(GL_TRIANGLES,0,3);
        //交换 surface 和显示器缓存
        eglSwapBuffers(eglDisplay, eglSurface);
        //释放
        eglDestroySurface(eglDisplay, eglSurface);
    }
```

### 总结

本文在前文的基础上，梳理了具体的纹理操作方法，给绘制的图形贴上一张图片纹理，整个的流程和前文差不多。

需要注意就是纹理到顶点的坐标匹配时的 y 方向，以正常显示纹理。