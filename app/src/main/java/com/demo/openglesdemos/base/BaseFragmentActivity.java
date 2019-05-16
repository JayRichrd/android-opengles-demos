package com.demo.openglesdemos.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.demo.openglesdemos.R;

public class BaseFragmentActivity extends AppCompatActivity {

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getSupportFragmentManager();

    }

    public void transformFragment(int containerId, Fragment fragment){
        fm.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }

    public void transformFragment(Fragment fragment){
        transformFragment(R.id.container, fragment);
    }
}
