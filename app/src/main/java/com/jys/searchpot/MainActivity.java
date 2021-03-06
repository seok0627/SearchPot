package com.jys.searchpot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.ThemeUtils;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public LoadingActivity loadingActivity;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    public ArrayList<Store> arrayList;
    public ArrayList<Store> arrayNewList;
    public FirebaseDatabase database;
    public FirebaseFirestore db;
    public DatabaseReference databaseReference;
    public DatabaseReference databaseNewReference;
    public DatabaseReference databaseVersionReference;
    public LinearLayout m_lay_edit;
    public LinearLayout m_lay_main;
    public EditText m_et_search;
    public TextView m_tv_cnt;
    public AdView mAdView;
    public SwipeRefreshLayout layoutSwipeRefresh;
    public String serverVersion = "";
    public static String appVersion = "";
    public boolean flag_theme = false;

    public int cnt = 0;
    public long backKeyPressedTime = 0;
    public boolean overlapFlag, alreadyFlag = false;
    Button button[] = new Button[20];
    Integer[] Rid_button =
            {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,       //??????, ???, ???, ???, ???
                    R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,      //???, ???, ???, ???, ???
                    R.id.btn_10, R.id.btn_11, R.id.btn_12, R.id.btn_13, R.id.btn_14,     //???, ???, ???, ???, ???
                    R.id.btn_15, R.id.btn_16, R.id.btn_17, R.id.btn_18, R.id.btn_19};    //???, ???, ???, ???, ???

    String str_Array[] = new String[]{
            "", "???", "???", "???", "???",
            "???", "???", "???", "???", "???",
            "???", "???", "???", "???", "???",
            "???", "???", "???", "???", "???"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag_theme = loadingActivity.flag_theme;
        setContentView(R.layout.activity_main);

        //????????? ?????? ??????
        getSupportActionBar().setTitle("");

        onInit();
        onSetTheme();
//        onAdLoad();
        onData("");
        onBtnAllSel();
        onServerVersionCheck();

        m_et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //edittext ?????? ?????? ??????????????? ??????(TextWatcher)
            @Override
            public void afterTextChanged(Editable editable) {
                clearText();
                if (m_et_search.getText().toString().length() == 0) {
                    onBtnAllSel();
                }
                String text = m_et_search.getText().toString();
                onData(text);
            }
        });

        layoutSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (m_lay_edit.getVisibility() == View.VISIBLE) {
                    m_lay_edit.setVisibility(View.GONE);
                }
                onBtnAllSel();
                m_et_search.setText("");
                onData("");
                layoutSwipeRefresh.setRefreshing(false);
            }
        });
    }

    public void onInit() {
        //????????????
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        m_et_search = (EditText) findViewById(R.id.et_search);
        m_tv_cnt = (TextView) findViewById(R.id.tv_cnt);
        m_lay_edit = (LinearLayout) findViewById(R.id.lay_edit);
        m_lay_main = (LinearLayout) findViewById(R.id.lay_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // ?????????????????? ???????????? ??????
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User ????????? ?????? ????????? ????????? (??????????????????)
        arrayNewList = new ArrayList<>();
        database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????
        db = FirebaseFirestore.getInstance();
        databaseReference = database.getReference("Store"); // ?????????????????? STORE DB ????????? ??????
        databaseNewReference = database.getReference("NewStore");
        databaseVersionReference = database.getReference("AppVersion");
        layoutSwipeRefresh = findViewById(R.id.swipeRefresh);

        for (int i = 0; i <= 19; i++) {
            button[i] = (Button) findViewById(Rid_button[i]);
            if(flag_theme){
                button[i].setBackgroundResource(R.drawable.bg_round_white_dark);
            }
            else{
                button[i].setBackgroundResource(R.drawable.bg_round_white);
            }
        }
        for (int i = 0; i <= 19; i++) {
            final int INDEX;
            INDEX = i;
            button[INDEX].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    clearText();
                    if(flag_theme){
                        if (m_et_search.getText().toString().length() >= 1) {
                            m_et_search.setText("");
                            button[0].setBackgroundResource(R.drawable.bg_round_white_dark);
                            button[0].setTextColor(Color.parseColor("#000000"));
                        }
                        button[INDEX].setBackgroundResource(R.drawable.bg_round_select_dark);
                        button[INDEX].setTextColor(Color.parseColor("#ffffff"));
                    }else{
                        if (m_et_search.getText().toString().length() >= 1) {
                            m_et_search.setText("");
                            button[0].setBackgroundResource(R.drawable.bg_round_white);
                            button[0].setTextColor(Color.parseColor("#000000"));
                        }
                        button[INDEX].setBackgroundResource(R.drawable.bg_round_select);
                        button[INDEX].setTextColor(Color.parseColor("#ffffff"));
                    }
                    onData(str_Array[INDEX]);
                }
            });
        }
    }

    public void onSetTheme() {
        if(flag_theme){
            m_lay_main.setBackgroundResource(R.color.darktheme_white);
            m_et_search.setBackgroundResource(R.drawable.bg_round_gray_dark);
        }else{
            m_lay_main.setBackgroundResource(R.color.white);
            m_et_search.setBackgroundResource(R.drawable.bg_round_gray);
        }
    }

    //????????? ????????????, ???????????? ??????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.setting:
                SettingDialog settingDialog = new SettingDialog(MainActivity.this);
                settingDialog.setCancelable(false);
                settingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                settingDialog.show();

                break;

            case R.id.review:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
                break;

            case R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plan");
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.jys.searchpot");
                startActivity(Intent.createChooser(share, "????????????"));
                break;

            case R.id.mail:
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.setType("plain/text");
                String[] address = {"dydtjr0627@naver.com"};
                mail.putExtra(Intent.EXTRA_EMAIL, address);
                mail.putExtra(Intent.EXTRA_SUBJECT, "(?????????) ???????????? ?????????!");
                mail.putExtra(Intent.EXTRA_TEXT,
                        "--------------------------------------------\n" +
                                "<<< ??? ??? ??? ??? >>>\n" +
                                "??? ????????? ????????? ????????? ????????? ??????????????? ?????? ????????????(??????)??? ?????? ??????????????????.\n" +
                                "??? ????????? ????????? ????????? ????????? ????????? ????????? ???????????? ??????????????????. \n" +
                                "??? ????????? ????????? ????????? ???????????? ?????? ???????????? ????????? ????????? ????????? ??????????????? ????????????.\n" +
                                "??? ???????????? ???????????? ?????? ????????? ??????????????? ????????? ?????? ???????????????.\n" +
                                "??? ????????? ?????? ??? ???????????? ?????? ???????????? ????????? ??? ????????????.\n" +
                                "--------------------------------------------\n\n\n" +
                                "????????????(??????) : \n\n" +
                                "?????? ???????????? : \n"
                );
                startActivity(mail);
                break;

            case R.id.add:
                onNewData();

                CustomDialog dialog = new CustomDialog(this);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setDialogListener(new CustomDialog.CustomDialogListener() {
                    @Override
                    public void onOkClicked(String name, String ins, String store) {
                        overlapFlag = false; //????????? [??????]??? ?????? ?????? ??????
                        alreadyFlag = false; //????????? [??????]??? ?????? ???????????? ??????

                        for (int i = 0; i < arrayNewList.size(); i++) {
                            if (name.equals(arrayNewList.get(i).storeName)) {
                                overlapFlag = true;
                                break;
                            }
                        }

                        for (int i = 0; i < arrayList.size(); i++) {
                            if (name.equals(arrayList.get(i).storeName)) {
                                alreadyFlag = true;
                                break;
                            }
                        }

                        if (overlapFlag == true) {
                            setCustomToast(MainActivity.this, "?????? ?????? ??????????????? ?????? ?????????????????????.");
                            //Toast.makeText(MainActivity.this, "?????? ?????? ??????????????? ?????? ?????????????????????", Toast.LENGTH_SHORT).show();
                        } else if (alreadyFlag == true) {
                            setCustomToast(MainActivity.this, "?????? ?????? ???????????? ???????????????.");
                            //Toast.makeText(MainActivity.this, "?????? ?????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            NewStore newStore = new NewStore(name, ins, store);
                            databaseNewReference.push().setValue(newStore);
                            setCustomToast(MainActivity.this, "????????? ?????? ????????? ???????????????.");
                            //Toast.makeText(MainActivity.this, "????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;

            case R.id.search:
                if (m_lay_edit.getVisibility() == View.VISIBLE) {
                    m_lay_edit.setVisibility(View.GONE);
                    onBtnAllSel();
                    m_et_search.setText("");
                } else {
                    m_lay_edit.setVisibility(View.VISIBLE);
                    m_et_search.setSelected(true);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //???????????? ??????, ????????? ?????????
    public void clearText() {
        for (int i = 0; i <= 19; i++) {
            if(flag_theme){
                button[i].setBackgroundResource(R.drawable.bg_round_white_dark);
            }else{
                button[i].setBackgroundResource(R.drawable.bg_round_white);
            }
            button[i].setTextColor(Color.parseColor("#000000"));
        }
    }

    //???????????? ???????????? ??????
    public void onBtnAllSel() {
        if(flag_theme){
            button[0].setBackgroundResource(R.drawable.bg_round_select_dark);
        }else{
            button[0].setBackgroundResource(R.drawable.bg_round_select);
        }
        button[0].setTextColor(Color.parseColor("#ffffff"));
    }

    //????????? ????????????, ???????????? ???????????? ?????? ??????
    public void onData(String searchText) {
        RealtimeDatabase(searchText);
        //FirestoreDatabase(searchText);
    }

    private void onNewData() {
        databaseNewReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayNewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //??????????????? ????????? List ??????
                    Store store = snapshot.getValue(Store.class); //??????????????? store ????????? ???????????? ??????
                    arrayNewList.add(store);
                    Collections.sort(arrayNewList, sortStoreName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void RealtimeDatabase(String searchText) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear(); // ?????? ??????????????? ?????????

                if (searchText.length() == 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //??????????????? ????????? List ??????
                        Store store = snapshot.getValue(Store.class); //??????????????? store ????????? ???????????? ??????
                        arrayList.add(store);
                        Collections.sort(arrayList, sortStoreName);
                    }
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Store store = snapshot.getValue(Store.class);
                        String iniName = HangulUtils.getHangulInitialSound(store.getStoreName(), searchText); //?????? ????????? ??????????????? ??????
//                        if (store.getStoreName().toLowerCase().contains(searchText)) {
//                            arrayList.add(store);
//                            Collections.sort(arrayList, sortStoreName);
//                        }
//                        else
                        if (iniName.indexOf(searchText) >= 0) {
                            arrayList.add(store);
                            Collections.sort(arrayList, sortStoreName);
                        }
                    }
                }
                //int numberCount = arrayList.size(); //arrayList ????????? ??? ??????

                adapter.notifyDataSetChanged(); // ????????? ?????? ??? ????????????
                cnt = adapter.getItemCount();
                m_tv_cnt.setText("??? " + cnt + "???");
                getSupportActionBar().setTitle("??? " + cnt + "???");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ????????? ??????????????? ?????? ?????? ??? ??????
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); // ????????????????????? ????????? ??????
    }

    public void FirestoreDatabase(String searchText) {
        db.collection("store")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        arrayList.clear(); // ?????? ??????????????? ?????????
                        if (task.isSuccessful()) {
                            if (searchText.length() == 0) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    Store store = snapshot.toObject(Store.class);
                                    arrayList.add(store);
                                    Collections.sort(arrayList, sortStoreName);
                                }
                            } else {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    Store store = snapshot.toObject(Store.class);
                                    String iniName = HangulUtils.getHangulInitialSound(store.getStoreName(), searchText); //?????? ????????? ??????????????? ??????

                                    if (iniName.indexOf(searchText) >= 0) {
                                        arrayList.add(store);
                                        Collections.sort(arrayList, sortStoreName);
                                    }
                                }
                            }
                        } else {
                            setCustomToast(MainActivity.this, "?????? ??? ?????? ??????????????????.");
                            //Toast.makeText(MainActivity.this, "?????? ??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged(); // ????????? ?????? ??? ????????????
                        cnt = adapter.getItemCount();
                        m_tv_cnt.setText("??? " + cnt + "???");
                        getSupportActionBar().setTitle("??? " + cnt + "???");

                    }
                });
    }

    //?????? ????????? ????????? ?????????
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onAdLoad() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    //???????????? ?????? ????????? ??? ??????
    @Override
    public void onBackPressed() {
        if (m_lay_edit.getVisibility() == View.VISIBLE) {
            m_lay_edit.setVisibility(View.GONE);
            onBtnAllSel();
            m_et_search.setText("");
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                setCustomToast(this, "\'??????\' ????????? ?????? ??? ????????????\n?????? ????????????.");
//                Toast.makeText(this, "\'??????\' ????????? ?????? ??? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                //???????????? ?????? ???
                ActivityCompat.finishAffinity(this);
                //???????????? ??????
                finish();
            }
        }
    }

    // Integer type sorting example
    public final static Comparator<Store> sortStoreName = new Comparator<Store>() {

        @Override
        public int compare(Store o1, Store o2) {
            return Collator.getInstance().compare(o1.storeName, o2.storeName);
        }
    };

    public void onServerVersionCheck() {
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        databaseVersionReference.child("AppVersion").child("versionName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serverVersion = dataSnapshot.getValue(String.class);

                if (!serverVersion.equals(appVersion)) {
                    NoticeDialog dialog = new NoticeDialog(MainActivity.this);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setDialogListener(new NoticeDialog.NoticeDialogListener() {
                        @Override
                        public void onOkClicked() {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                            startActivity(intent);

                            //??????????????? ?????? ???
                            dialog.dismiss();
                            //???????????? ?????? ???
                            ActivityCompat.finishAffinity(MainActivity.this);
                            //???????????? ??????
                            finish();
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void setCustomToast(Context context, String msg) {
        TextView tvToastMsg = new TextView(context);
        tvToastMsg.setText(msg);
        tvToastMsg.setBackgroundResource(R.drawable.bg_round_toast);
        tvToastMsg.setTextColor(Color.WHITE);
        tvToastMsg.setTextSize(17);
        tvToastMsg.setPadding(50, 30, 50, 30);
        tvToastMsg.setFontFeatureSettings(String.valueOf(R.font.font_3));

        final Toast toastMsg = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toastMsg.setView(tvToastMsg);

        toastMsg.show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                toastMsg.cancel();
            }
        }, 1500);
    }
}

/* --------------------------????????? ?????? ??????-------------------------- */
//        String insUrl = store.insUrl;
//        String profile = store.profile;
//        String sellUrl = store.sellUrl;
//        String storeName = store.storeName;
//
//        Map<String, Object> user = new HashMap<>();
//        user.put("insUrl", insUrl);
//        user.put("profile", profile);
//        user.put("sellUrl", sellUrl);
//        user.put("storeName", storeName);
//
//        db.collection("store")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });