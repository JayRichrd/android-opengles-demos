### 简介

前面几篇文章通过 GLSurfaceView 进行 opengles 的渲染，使用简单。但是不够灵活，一个 opengl 只能渲染一个 GLSurfaceView，一旦 GLSurfaceView 销毁，对应的 opengl 也会销毁。

使用 EGL 可以避免上述缺点。

EGL 时渲染 API 和平台原生窗口系统之间的接口，主要任务是：

* 查询并初始化设备的可用显示器。
* 创建渲染表面。
* 创建渲染上下文。

### EGL 使用流程

EGL 使用主要步骤很清晰，每个步骤都有相应的方法进行操作。

* 与窗口系统通信，获取显示器：eglGetDisplay
* 初始化EGL：eglInitialize
* 根据需要，让EGL 选择合适的配置：eglChooseConfig
* 创建上下文：eglCreateContext
* 创建渲染区域：EGL窗口：eglCreateWindowSurface
* 指定当前上下文：eglMakeCurrent
* 加载着色器、连接程序、绑定数据到属性进行渲染（使用的数据、着色器之类的和前几篇文章一样）

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

### 基于线程实现渲染器

opengles 渲染是基于线程的，需要自己实现一个管理 opengles 环境和渲染的线程的渲染器。

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

### 使用 SurfaceView 进行显示

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

将渲染器与布局中的 SurfaceView 进行关联。

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

### 总结

本文梳理了 EGL 的使用流程，基于线程自定义了 EGL 渲染器，将内容显示到 SurfaceView。 