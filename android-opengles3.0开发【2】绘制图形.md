### 简介

android 下 opengles 的绘制图形简单来说步骤如下：

1. 定义图形顶点数据
2. 编写/编译 顶点着色器 和 片段着色器。
3. 创建程序，将着色器绑定到程序上，然后连接程序。如果着色器中没有定义属性的位置，则在绑定着色器之后、连接程序之前，将属性名称和位置进行绑定。
4. 使用程序，将图形顶点数据放到相应的属性位置上，然后进行绘制。

### 定义图形顶点数据

浮点型数组，顶点的顺序按逆时针排列。

android 平台上，app 运行在 jvm 中，内存由 jvm 管理，而 opengles 运行在 native 环境，所以为了式 opengles 能够访问图形顶点数据，需要把顶点拷贝到 native 内存中。

另外，为了方便操作 native 中的顶点字节，将其映射到 FloatBuffer 中，然后就可以像使用数组一样使用了。

```java
    //顶点，按逆时针顺序排列
    private static final float[] vertices = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f};

    public TriangleRender() {
        //将顶点数据拷贝映射到 native 内存中，以便opengl能够访问
        verticesBuffer = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT)//直接分配 native 内存，不会被gc
                .order(ByteOrder.nativeOrder())//和本地平台保持一致的字节序（大/小头）
                .asFloatBuffer();//将底层字节映射到FloatBuffer实例，方便使用
        verticesBuffer
                .put(vertices)//将顶点拷贝到 native 内存中
                .position(0);//每次 put position 都会 + 1，需要在绘制前重置为0
    }
```

### 编写/编译着色器

着色器的源代码可以写到单独的文件中，也可以用字符串拼接（本文使用的方式，为了方便）

**顶点着色器**

* 第一行指定opengles版本，不指定默认为 opengles2.0
* 第二行声明一个输入变量 vPosition，表示顶点的坐标，4分量向量。`layout (location = 0)` 用来指定该 vPosition 的属性位置，后面需要使用该位置将顶点数据匹配到 vPosition 上。
* 后面几行定义了一个 main()  方法，是着色器的入口，方法中将 vPosition 值赋值给 gl_position 内建变量中，表示的是顶点的最终位置。

简单来说，该着色器的内容就是：使用 opengles3.0 版本，将图形顶点数据采用4分量向量的数据结构绑定到着色器的第 0 个属性上，属性的名字叫 vPosition，着色器执行时，将 vPosition 的值传给用来表示顶点最终位置的内建变量 gl_Position。

需要注意的是 `layout (location = 0)`  不是必须要写，如果不写的话，需要在后面着色器绑定到程序之后，程序连接之前使用 `glBindAttribLocation` 方法绑定。 

```java
    //顶点着色器
    private static final String vertextShaderSource =
            "#version 300 es\n"
                    + "layout (location = 0) in vec4 vPosition;\n"
                    + "void main()\n"
                    + "{\n"
                    + "    gl_Position = vPosition;\n"
                    + "}\n";
```

**片段着色器**

* 第一行指定opengles版本，不指定默认为 opengles2.0
* 第二行指明浮点变量的精度
* 第三行声明一个输出变量 fragColor ，4分量向量
* 给输出变量赋值

```java
    //片段着色器
    private static final String fragmentShaderSource =
            "#version 300 es		 			          	\n"
                    + "precision mediump float;					  	\n"
                    + "out vec4 fragColor;	 			 		  	\n"
                    + "void main()                                  \n"
                    + "{                                            \n"
                    + "  fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );	\n"
                    + "}                                            \n";
```

着色器内容编辑完之后，想要使用还要经过编译。

```java
    /**
     * 加载着色器源，并编译
     *
     * @param type         顶点着色器（GL_VERTEX_SHADER）/片段着色器（GL_FRAGMENT_SHADER）
     * @param shaderSource 着色器源(上面编辑的内容)
     * @return 着色器
     */
    private int loadShader(int type, String shaderSource) {
        //创建着色器对象
        int shader = GLES30.glCreateShader(type);
        if (shader == 0) return 0;//创建失败
        //加载着色器源
        GLES30.glShaderSource(shader, shaderSource);
        //编译着色器
        GLES30.glCompileShader(shader);
        //检查编译状态
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;//编译失败
        }

        return shader;
    }
```

### 初始化程序

具体的流程下面代码很清楚，需要注意的就是如果着色器中没有指定属性的位置，则需要调用 `glBindAttribLocation` 进行指定。 

```java
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //获取顶点着色器
        int vertextShader = loadShader(GLES30.GL_VERTEX_SHADER, vertextShaderSource);
        //获取片段着色器
        int fragmentShader =loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderSource);
        //创建程序
        int tmpProgram = GLES30.glCreateProgram();
        if (tmpProgram == 0) return;//创建失败
        //绑定着色器到程序
        GLES30.glAttachShader(tmpProgram, vertextShader);
        GLES30.glAttachShader(tmpProgram, fragmentShader);
        
        //绑定属性位置 vPosition ：0 着色器中没有设定属性位置时使用
//        GLES30.glBindAttribLocation(tmpProgram, 0, "vPosition");

        //连接程序
        GLES30.glLinkProgram(tmpProgram);
        //检查连接状态
        int[] linked = new int[1];
        GLES30.glGetProgramiv(tmpProgram,GLES30.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0){
            Log.e(TAG, "tmpProgram linked error");
            Log.e(TAG, GLES30.glGetProgramInfoLog(tmpProgram));
            GLES30.glDeleteProgram(tmpProgram);
            return;//连接失败
        }
        //保存程序，后面使用
        program = tmpProgram;

        //设置清除渲染时的颜色
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }
```

### 使用程序绘制

```java
    @Override
    public void onDrawFrame(GL10 gl) {
        //擦除屏幕
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //使用程序
        GLES30.glUseProgram(program);
        //获取 vPosition 属性的位置
        int vposition = GLES30.glGetAttribLocation(program, "vPosition");
        //加载顶点数据到 vPosition 属性位置
        GLES30.glVertexAttribPointer(vposition,3,GLES30.GL_FLOAT,false,0,verticesBuffer);
        GLES30.glEnableVertexAttribArray(vposition);
        //绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,3);
    }
```

### 总结

本文通过绘制一个三角形，梳理了 opengles 绘制图形的流程，着色器、程序的使用方法。

