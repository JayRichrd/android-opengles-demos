### 简介

android 中使用 opengles 基本思路：

1. 使用 `GLSurfaceView` 作为显示渲染的视图；
2. 实现 `GLSurfaceView.Renderer` 接口，创建自定义的渲染器，然后设置到 GLSurfaceView。

### GLSurfaceView 配置

首先确定所使用的 opengles 版本，然后设置指定的渲染器，最后显示到 Activity 上。

需要注意的是，在 Activity 的生命周期函数中，控制 GLSurfaceView 渲染的开始和暂停。

```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "opengl-demos";
    private static final int GL_VERSION = 3;

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化 GLSurfaceView
        glSurfaceView = new GLSurfaceView(this);
        //检验是否支持 opengles3.0
        if (!checkGLVersion()){
            Log.e(TAG, "not supported opengl es 3.0+");
            finish();
        }
        //使用 opengles 3.0
        glSurfaceView.setEGLContextClientVersion(GL_VERSION);
        //设置渲染器
        glSurfaceView.setRenderer(new DemoRender());
        //将 GLSurfaceView 显示到 Activity
        setContentView(glSurfaceView);

    }

    private boolean checkGLVersion(){
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo ci = am.getDeviceConfigurationInfo();
        return ci.reqGlEsVersion >= 0x30000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //执行渲染
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //暂停渲染
        glSurfaceView.onPause();
    }
}
```

### 实现渲染器

实现 `GLSurfaceView.Renderer` 接口即可自定义渲染器。

接口中定义了三个方法：

* onSurfaceCreated：Surface 创建的时候，GLSurfaceView 会调用该方法。程序创建的时候会调用，app切换的时候也有可能调用。
* onSurfaceChanged：Surface 尺寸变化的时候调用，比如横竖屏切换的时候。
* onDrawFrame：绘制帧。

```java
public class DemoRender implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //清空屏幕所用的颜色
        GLES30.glClearColor(1.0f, 0f, 0f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置适口尺寸
        GLES30.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //使用 glClearColor 指定的颜色擦除屏幕
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
    }
}
```

### 总结

本文梳理了 android 下使用 opengles 的基本流程及核心类的使用，显示了一个纯色窗口。