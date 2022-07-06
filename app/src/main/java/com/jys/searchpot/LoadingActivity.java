package com.jys.searchpot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        NetworkCheck();
    }

    //네트워크 연결 체크
    private void NetworkCheck() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //연결되어있지 않으면
        if(!isConnected){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);

            builder.setTitle("네트워크 오류").setMessage("네크워크가 원활하지 않습니다.\n네트워크 연결을 확인해주세요.");
            builder.setPositiveButton("종료",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    //액티비티 종료 후
                    ActivityCompat.finishAffinity(LoadingActivity.this);
                    //프로세스 종료
                    System.exit(0);
                }
            });
            builder.show();


            //연결되어 있으면 로딩화면에서 메인화면으로 넘어감
        }else{
            Loadingstart();
        }
    }

    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1200);
    }
}