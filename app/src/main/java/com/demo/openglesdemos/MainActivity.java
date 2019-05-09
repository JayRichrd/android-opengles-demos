package com.demo.openglesdemos;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.demo.openglesdemos.fragment.DemoFragment;
import com.demo.openglesdemos.fragment.EGLFragment;
import com.demo.openglesdemos.fragment.TriangleFragment;
import com.demo.openglesdemos.render.TriangleRender;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnDemo, btnColorTriangle, btnEGL;

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

        btnEGL = findViewById(R.id.btnEGL);
        btnEGL.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDemo:
                transactFragment(new DemoFragment());
                break;
            case R.id.btnColorTriangle:
                transactFragment(new TriangleFragment());
                break;
            case R.id.btnEGL:
                transactFragment(new EGLFragment());
                break;
        }
    }

    private void transactFragment(Fragment fagment){
        fragmentManager.beginTransaction()
                .replace(R.id.container, fagment)
                .commit();
    }
}