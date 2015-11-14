package com.prepo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.parse.ui.ParseLoginDispatchActivity;

public class ParseLoginActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        return HomeActivity.class;
    }
}
