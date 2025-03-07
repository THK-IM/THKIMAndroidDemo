package com.thinking.im.demo.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.thinking.im.demo.databinding.ActivityWelcomeBinding;
import com.thinking.im.demo.module.im.IMManger;
import com.thinking.im.demo.ui.base.BaseActivity;
import com.thk.im.android.core.base.BaseSubscriber;
import com.thk.im.android.core.base.RxTransform;

public class JavaWelcomeActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityWelcomeBinding binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());


        BaseSubscriber<Boolean> subscriber = new BaseSubscriber<>() {
            @Override
            public void onNext(Boolean aBoolean) {
                initIMResult();
            }
        };

        IMManger.INSTANCE.initIMUser("3a2e0af2-d9f3-43c6-b8a0-ce5dcb503ad2", 12)
                .compose(RxTransform.INSTANCE.flowableToMain())
                .subscribe(subscriber);
        addDispose(subscriber);
    }

    private void initIMResult() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
