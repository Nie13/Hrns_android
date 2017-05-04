package com.hrns.l.bluetooth_hrns.BLUE;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by l on 5/3/2017.
 */

public class FullscreenActivity extends AppCompatActivity {
    private actRealTime mLogic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String name = getIntent().getStringExtra(FullscreenExample.ARG_ID);
        FullscreenExample example = FullscreenExample.valueOf(name);

        setContentView(example.contentView);
        try {
            mLogic = example.exampleClass.newInstance();
            mLogic.onCreate(this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLogic.onResume();
    }

    @Override
    protected void onPause() {
        mLogic.onPause();
        super.onPause();
    }
}
