package com.demo.openglesdemos;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.demo.openglesdemos.fragment.DemoFragment;
import com.demo.openglesdemos.fragment.TriangleFragment;
import com.demo.openglesdemos.render.TriangleRender;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "opengl-demos";

    Button btnDemo, btnColorTriangle;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        btnDemo = findViewById(R.id.btnDemo);
        btnDemo.setOnClickListener(this);

        btnColorTriangle = findViewById(R.id.btnColorTriangle);
        btnColorTriangle.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDemo:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new DemoFragment())
                        .commit();
                break;
            case R.id.btnColorTriangle:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TriangleFragment())
                        .commit();
                break;
        }
    }
}
