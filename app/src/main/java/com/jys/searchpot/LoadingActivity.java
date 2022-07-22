package com.jys.searchpot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;


public class LoadingActivity extends AppCompatActivity {

    public Animation anim_FadeIn;
    public LinearLayout lay_loading;
    public SharedPreferences prefs;
    public static boolean flag_theme = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        onInit();
        onAnimation();

    }

    public void onInit() {
        lay_loading = (LinearLayout) findViewById(R.id.lay_loading);
        anim_FadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_splash_fadein);

        prefs = this.getSharedPreferences("prefs", MODE_PRIVATE) ;
        flag_theme = prefs.getBoolean(String.valueOf(R.string.key_setting_theme), false);
        if(flag_theme){
            lay_loading.setBackgroundResource(R.color.darktheme_white);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            lay_loading.setBackgroundResource(R.color.white);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void onAnimation() {
        anim_FadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                NetworkCheck();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        lay_loading.startAnimation(anim_FadeIn);
    }

    //네트워크 연결 체크
    public void NetworkCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //연결되어있지 않으면
        if (!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setCancelable(false);

            builder.setTitle("네트워크 오류").setMessage("네크워크가 원활하지 않습니다.\n네트워크 연결을 확인해주세요.");
            builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //액티비티 종료 후
                    ActivityCompat.finishAffinity(LoadingActivity.this);
                    //프로세스 종료
                    System.exit(0);
                }
            });
            builder.show();

        } else {
            Loadingstart();
        }
    }

    public void Loadingstart() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 0);
    }
}