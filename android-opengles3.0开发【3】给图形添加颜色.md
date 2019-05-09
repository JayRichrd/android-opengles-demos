opengles 的给图形添加颜色很简单，在上篇文章的基础上改动几处即可。

### 1. 修改着色器

顶点着色器需要声明一个接收颜色数据的输入变量 aColor ，以及一个输出变量 vColor ，并在 main() 方法中，将 aColor 赋值给 vColor，用以后续将颜色输出到片段着色器。

```java
    private static final String vertextShaderSource =
            "#version 300 es\n"
                    + "layout (location = 0) in vec4 vPosition;\n"
                    //接收颜色数据的输入变量
                    + "layout (location = 1) in vec4 aColor;\n"
                    //输出变量
                    + "out vec4 vColor;\n"
                    + "void main()\n"
                    + "{\n"
                    + "    gl_Position = vPosition;\n"
                    //给输出变量赋值
                    + "    vColor = aColor;\n"
                    + "}\n";
```

片段着色器需要声明一个接收颜色数据的输入变量 vColor，并赋值给 fragColor。

```java
    private static final String fragmentShaderSource =
            "#version 300 es		 			          	\n"
                    + "precision mediump float;					  	\n"
                    //接收颜色数据的输入变量
                    + "in vec4 vColor;					  	\n"
                    + "out vec4 fragColor;	 			 		  	\n"
                    + "void main()                                  \n"
                    + "{                                            \n"
                    //将颜色数据输出
                    + "  fragColor = vColor;	\n"
                    + "}                                            \n";
```

### 2. 准备颜色数据

颜色数据的准备操作和顶点数据的操作一样。

```java
    //顶点颜色
    private static final float[] verticeColors = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };

        //将顶点颜色数据拷贝映射到 native 内存中，以便opengl能够访问
        verticeColorsBuffer = ByteBuffer
                .allocateDirect(verticeColors.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        verticeColorsBuffer
                .put(verticeColors)
                .position(0);
```

### 3. 绘制

绘制的时候将颜色数据绑定到相应的属性位置，具体操作和顶点数据一样。

```java
        //获取 vColor 属性位置
        int aColor = GLES30.glGetAttribLocation(program, "aColor");
        //加载顶点颜色数据到 vColor 属性位置
        GLES30.glEnableVertexAttribArray(aColor);
        GLES30.glVertexAttribPointer(aColor, 4, GLES30.GL_FLOAT, false, 0, verticeColorsBuffer);
```

### 总结

本文梳理了颜色添加的基本流程，给绘制的三角形添加了简单的颜色。