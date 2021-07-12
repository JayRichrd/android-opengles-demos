package com.demo.openglesdemos.texture;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.demo.openglesdemos.R;
import com.demo.openglesdemos.base.BaseFragmentActivity;
import com.demo.openglesdemos.egl.EGLFragment;
import com.demo.openglesdemos.egl.EGLTextureFragment;

public class TextureActivity extends BaseFragmentActivity implements View.OnClickListener {

    Button btnTexture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture);

        btnTexture = findViewById(R.id.btnTexture);
        btnTexture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTexture:
                transformFragment(new EGLTextureFragment());
//                transformFragment(new EGLFragment());
//                transformFragment(new TextureFragment());
                break;
        }
    }
}
