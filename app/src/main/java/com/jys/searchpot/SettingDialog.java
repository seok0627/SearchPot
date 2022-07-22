package com.jys.searchpot;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

public class SettingDialog extends Dialog implements View.OnClickListener {

    public MainActivity mainActivity;
    public Button btn_close;
    public Switch switch_theme;
    public TextView tv_version;

    public Context context;

    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    public boolean flag_theme = false;

    public SettingDialog(Context context) {
        super(context);
        this.context = context;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);
        prefs = context.getSharedPreferences("prefs", MODE_PRIVATE) ;
        editor =  prefs.edit();
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        switch_theme = (Switch) findViewById(R.id.switch_theme);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText(mainActivity.appVersion);

        load();
        setting();

        switch_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean(String.valueOf(R.string.key_setting_theme), true);
                    editor.apply();
                    mainActivity.setCustomToast(context, "테마설정은 앱 재시작 후 적용돼요.");
                }else{
                    editor.putBoolean(String.valueOf(R.string.key_setting_theme), false);
                    editor.apply();
                    mainActivity.setCustomToast(context, "테마설정은 앱 재시작 후 적용돼요.");
                }
            }
        });
    }

    public void load() {
        flag_theme = prefs.getBoolean(String.valueOf(R.string.key_setting_theme), false);
    }

    public void setting() {
        if (flag_theme) {
            switch_theme.setChecked(true);
        } else {
            switch_theme.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }
}
