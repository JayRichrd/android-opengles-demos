# 1.简介

前面几篇文章通过 GLSurfaceView 进行 opengles 的渲染，使用简单。但是不够灵活，==一个opengl只能渲染一个GLSurfaceView，一旦GLSurfaceView销毁，对应的opengl也会销毁==。

> 说明：Khronos是OpenGL, OpenGL ES, OpenVG和EGL等规范的定义者。

==EGL是Khronos组织定义的用于**管理绘图表面** (窗口只是绘图表面的一种类型，还有其他的类型) 的API，EGL提供了OpenGL ES(以及其他Khronos图形API(如 OpenVG))和不同操作系统 (Android、Windows 等) 之间的一个**结合层次**。即EGL定义了 Khronos API如何与底层窗口系统交流，是Khronos定义的规范，相当于一个框架，具体的实现由各个操作系统确定。==它是在OpenGL ES等规范中讨论的概念，故应和这些规范的文档结合起来阅读，且其API的说明文档也应在 Khronos 网站上寻找。

> 注意：IOS提供了自己的EGL API 实现，称 EAGL。

通常，在Android中，EGL14实现的是EGL 1.4 规范。其相当于Google官方对JAVA的EGL10(EGL 1.0 规范) 的一次重新设计。通常，我们使用EGL14中的函数。而EGL15是 EGL 1.5 规范，其在设计时，仅仅是做了规范的补充，并未重新设计。通常EGL14、EGL15与GLES20等一起使用。GLES20是OpenGL ES 2.0 规范的实现。OpenGL ES 1.x 规范因为是早期版本，受限于机器性能和架构设计等，基本可以不再使用。而OpenGL ES 2.x 规范从Android 2.3 版本后开始支持，目前市面上的所有手机都支持。==相比于1.0，OpenGL ES 2.0引入了可编程图形管线，具体的渲染相关使用专门的着色语言来表达。==

使用EGL可以避免上述缺点。

==EGL是渲染API 和平台原生窗口系统之间的接口==，主要任务是：

* ==查询并初始化设备的可用显示器==。
* 创建==渲染表面==。
* 创建==渲染上下文==。

# 2.EGL使用流程

EGL使用主要步骤很清晰，每个步骤都有相应的方法进行操作。

* 与窗口系统通信，==获取显示器==：**eglGetDisplay**
* ==初始化EGL==：**eglInitialize**
* 根据需要，==让EGL选择合适的配置==：**eglChooseConfig**
* ==创建上下文==：**eglCreateContext**
* ==创建渲染区域==：EGL窗口：**eglCreateWindowSurface**
* ==指定当前上下文==：**eglMakeCurrent**
* ==加载着色器、连接程序、绑定数据到属性进行渲染==（使用的数据、着色器之类的和前几篇文章一样）

```java
    private void createEGL(){
        //获取显示设备
        eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL_NO_DISPLAY){
            throw new RuntimeException("egl error:" + eglGetError());
        }
        //初始化EGL
        int[] version = new int[2];
        if (!eglInitialize(eglDisplay, version,0,version,1)){
            throw new RuntimeException("egl error:" + eglGetError());
        }
        //EGL选择配置
        int[] configAttribList = {
                EGL_BUFFER_SIZE, 32,
                EGL_ALPHA_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_RED_SIZE, 8,
                EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
                EGL_NONE
        };
        int[] numConfig = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if(!eglChooseConfig(eglDisplay,
                configAttribList, 0,
                configs,0, configs.length,
                numConfig,0)){
            throw new RuntimeException("egl error:" + eglGetError());
        }
        eglConfig = configs[0];
        //创建ELG上下文
        int[] contextAttribList = {
                EGL_CONTEXT_CLIENT_VERSION,2,
                EGL_NONE
        };
        eglContext = eglCreateContext(eglDisplay, eglConfig,EGL_NO_CONTEXT,contextAttribList,0);
        if (eglContext == EGL_NO_CONTEXT){
            throw new RuntimeException("egl error:" + eglGetError());
        }
    }
    
    public void render(Surface surface, int width, int height){
        //创建屏幕上渲染区域：EGL窗口
        int[] surfaceAttribList = {EGL_NONE};
        EGLSurface eglSurface = eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribList, 0);
        //指定当前上下文
        eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
        //获取着色器
        int vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER_SOURCE);
        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_SOURCE);
        //创建并连接程序
        int program = createAndLinkProgram(vertexShader, fragmentShader);
        //设置清除渲染时的颜色
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        //设置视口
        glViewport(0, 0, width, height);
        //获取顶点、颜色数据
        FloatBuffer vertexBuffer = getVertextBuffer();
        FloatBuffer vertexColorBuffer = getVertexColorBuffer();
        //擦除屏幕
        glClear(GL_COLOR_BUFFER_BIT);
        //使用程序
        glUseProgram(program);
        //绑定顶点、颜色数据到指定属性位置
        int vposition = glGetAttribLocation(program, "vPosition");
        glVertexAttribPointer(vposition,3,GL_FLOAT,false,0,vertexBuffer);
        glEnableVertexAttribArray(vposition);
        int aColor = glGetAttribLocation(program, "aColor");
        glEnableVertexAttribArray(aColor);
        glVertexAttribPointer(aColor, 4, GL_FLOAT, false, 0, vertexColorBuffer);
        //绘制
        glDrawArrays(GL_TRIANGLES,0,3);
        //交换 surface 和显示器缓存
        eglSwapBuffers(eglDisplay, eglSurface);
        //释放
        eglDestroySurface(eglDisplay, eglSurface);
    }
```

# 3.基于线程实现渲染器

==opengles渲染是基于线程的==，需要自己实现一个==管理opengles环境和渲染的线程==的渲染器。

```java
public class EGLRender extends HandlerThread {

    private EGLConfig eglConfig;
    private EGLDisplay eglDisplay;
    private EGLContext eglContext;

    public EGLRender() {
        super("ELGRender");
    }

    private void createEGL(){
     //代码在上面
    }

    private void destroyEGL(){
        eglDestroyContext(eglDisplay, eglContext);
        eglContext = EGL_NO_CONTEXT;
        eglDisplay = EGL_NO_DISPLAY;
    }

    @Override
    public synchronized void start() {
        super.start();

        new Handler(getLooper()).post(new Runnable() {
            @Override
            public void run() {
                createEGL();
            }
        });
    }

    public void release(){
        new Handler(getLooper()).post(new Runnable() {
            @Override
            public void run() {
                destroyEGL();
                quit();
            }
        });
    }

    public void render(Surface surface, int width, int height){
        //代码在上面
    }
}
```

# 4.使用SurfaceView进行显示

布局文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".fragment.EGLFragment">

    <SurfaceView
        android:id="@+id/surfaceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</RelativeLayout>
```

==将**渲染器**与布局中的**SurfaceView**进行关联==。

```java
public class EGLFragment extends Fragment {

    private SurfaceView surfaceView;
    private EGLRender eglRender;

    public EGLFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_egl, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eglRender = new EGLRender();
        eglRender.start();

        surfaceView = view.findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                eglRender.render(holder.getSurface(), width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    public void onDestroy() {
        eglRender.release();
        eglRender = null;
        super.onDestroy();
    }
}
```

# 5.总结

本文梳理了EGL的使用流程，基于线程自定义了EGL渲染器，将内容显示到SurfaceView。 

 <center>
    <img src="https://picgo-1256537295.cos.ap-guangzhou.myqcloud.com/pictures/20210708201645.png">
    <br>
    <div>图1 EGL使用基本流程</div>
</center>