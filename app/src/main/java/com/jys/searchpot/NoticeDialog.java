package com.jys.searchpot;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoticeDialog extends Dialog implements View.OnClickListener{

    public Button btn_cancel;
    public Button btn_ok;

    public NoticeDialogListener noticeDialogListener;
    public Context context;

    public NoticeDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    interface NoticeDialogListener{
        void onOkClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(NoticeDialogListener noticeDialogListener){
        this.noticeDialogListener = noticeDialogListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_dialog);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                //각각의 변수에 EidtText에서 가져온 값을 저장
                noticeDialogListener.onOkClicked();
                break;
            case R.id.btn_cancel:
                cancel();
                break;
        }
    }

    //뒤로가기 버튼 클릭시 앱 종료
    @Override
    public void onBackPressed() {
        dismiss();
    }
}
