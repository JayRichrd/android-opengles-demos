package com.demo.openglesdemos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.openglesdemos.shape.ShapeActivity;
import com.demo.openglesdemos.utils.CommonUtil;
import com.demo.openglesdemos.utils.EGLUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnShape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EGLUtil.init(this);
        CommonUtil.init(this);

        //检验是否支持 opengles3.0
        if (!CommonUtil.checkGLVersion()){
            Log.e(CommonUtil.TAG, "not supported opengl es 3.0+");
            finish();
        }

        btnShape = findViewById(R.id.btnShape);
        btnShape.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnShape:
                toActivity(ShapeActivity.class);
                break;
        }
    }

    private void toActivity(Class cls){
        startActivity(new Intent(MainActivity.this, cls));
    }
}
