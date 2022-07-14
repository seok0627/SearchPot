package com.jys.searchpot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

public class LoadingActivity extends AppCompatActivity {

    Animation anim_FadeIn;
    LinearLayout lay_loading;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

            //연결되어 있으면 로딩화면에서 메인화면으로 넘어감
        } else {
            onVersionCheck();
            Loadingstart();
        }
    }

    public void onVersionCheck() {
        

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