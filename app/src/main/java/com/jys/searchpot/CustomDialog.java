package com.jys.searchpot;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog extends Dialog implements View.OnClickListener{

    private Button btn_cancel;
    private Button btn_ok;
    private EditText et_name;
    private EditText et_ins;
    private EditText et_store;
    private TextView tv_notice;

    private CustomDialogListener customDialogListener;
    private Context context;

    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    interface CustomDialogListener{
        void onOkClicked(String name, String ins, String store);
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        et_name = (EditText) findViewById(R.id.et_Name);
        et_ins = (EditText) findViewById(R.id.et_Ins);
        et_store = (EditText) findViewById(R.id.et_Store);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
        tv_notice.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                //각각의 변수에 EidtText에서 가져온 값을 저장
                if(et_name.getText().toString().length() < 1){
                    Toast.makeText(context, "브랜드명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    String name = et_name.getText().toString();
                    String ins = et_ins.getText().toString();
                    String stroe = et_store.getText().toString();
                    customDialogListener.onOkClicked(name,ins,stroe);
                }
                break;
            case R.id.btn_cancel:
                cancel();
                break;
        }
    }
}
