package com.mqfcu7.jiangmeilan.avatar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mqfcu7.jiangmeilan.avatar.databinding.ActivityBuildAvatarBinding;

public class BuildAvatarActivity extends AppCompatActivity {

    private ActivityBuildAvatarBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_build_avatar);
    }
}
