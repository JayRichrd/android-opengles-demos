package com.demo.openglesdemos.shape;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.demo.openglesdemos.base.BaseFragmentActivity;
import com.demo.openglesdemos.R;

public class ShapeActivity extends BaseFragmentActivity implements View.OnClickListener {

    Button btnColor, btnTriangle, btnRect, btnCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape);

        btnColor = findViewById(R.id.btnColor);
        btnColor.setOnClickListener(this);

        btnTriangle = findViewById(R.id.btnTriganle);
        btnTriangle.setOnClickListener(this);

        btnRect = findViewById(R.id.btnRect);
        btnRect.setOnClickListener(this);

        btnCircle = findViewById(R.id.btnCircle);
        btnCircle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnColor:
                transformFragment(new SimpleFragment());
                break;
            case R.id.btnTriganle:
                transformFragment(new TriangleFragment());
                break;
            case R.id.btnRect:
                transformFragment(new RectFragment());
                break;
            case R.id.btnCircle:
                transformFragment(new CircleFragment());
                break;
        }
    }
}
