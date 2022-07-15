package com.jys.searchpot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
    public EditText m_et_search;
    public TextView m_tv_cnt;
    public AdView mAdView;
    public SwipeRefreshLayout layoutSwipeRefresh;
    public String serverVersion = "";
    public String appVersion = "";

    public int cnt = 0;
    public long backKeyPressedTime = 0;
    public boolean overlapFlag, alreadyFlag = false;
    Button button[] = new Button[20];
    Integer[] Rid_button =
            {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,       //전체, ㄱ, ㄲ, ㄴ, ㄷ
                    R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,      //ㄸ, ㄹ, ㅁ, ㅂ, ㅃ
                    R.id.btn_10, R.id.btn_11, R.id.btn_12, R.id.btn_13, R.id.btn_14,     //ㅅ, ㅆ, ㅇ, ㅈ, ㅉ
                    R.id.btn_15, R.id.btn_16, R.id.btn_17, R.id.btn_18, R.id.btn_19};    //ㅊ, ㅋ, ㅌ, ㅍ, ㅎ

    String str_Array[] = new String[]{
            "", "ㄱ", "ㄲ", "ㄴ", "ㄷ",
            "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ",
            "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ",
            "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //상단바 문구 지정
        getSupportActionBar().setTitle("");

        onInit();
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

            //edittext 글자 변화 있을때마다 호출(TextWatcher)
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
        //배너광고
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        m_et_search = (EditText) findViewById(R.id.et_search);
        m_tv_cnt = (TextView) findViewById(R.id.tv_cnt);
        m_lay_edit = (LinearLayout) findViewById(R.id.lay_edit);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)
        arrayNewList = new ArrayList<>();
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        db = FirebaseFirestore.getInstance();
        databaseReference = database.getReference("Store"); // 파이어베이스 STORE DB 테이블 연결
        databaseNewReference = database.getReference("NewStore");
        databaseVersionReference = database.getReference("AppVersion");
        layoutSwipeRefresh = findViewById(R.id.swipeRefresh);

        for (int i = 0; i <= 19; i++) {
            button[i] = (Button) findViewById(Rid_button[i]);
        }
        for (int i = 0; i <= 19; i++) {
            final int INDEX;
            INDEX = i;
            button[INDEX].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    clearText();
                    if (m_et_search.getText().toString().length() >= 1) {
                        m_et_search.setText("");
                        button[0].setBackgroundResource(R.drawable.bg_round_white);
                        button[0].setTextColor(Color.parseColor("#000000"));
                    }
                    button[INDEX].setBackgroundResource(R.drawable.bg_round_select);
                    button[INDEX].setTextColor(Color.parseColor("#ffffff"));
                    onData(str_Array[INDEX]);
                }
            });
        }
    }

    //액션바 공유하기, 검색하기 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.version:
                PackageInfo pi = null;
                try {
                    pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                    Toast.makeText(MainActivity.this, "설치된 버전 : " + pi.versionName, Toast.LENGTH_LONG).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plan");
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.jys.searchpot");
                startActivity(Intent.createChooser(share, "공유하기"));
                break;

            case R.id.mail:
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.setType("plain/text");
                String[] address = {"dydtjr0627@naver.com"};
                mail.putExtra(Intent.EXTRA_EMAIL, address);
                mail.putExtra(Intent.EXTRA_SUBJECT, "(써치팟) 문의사항 입니다!");
                mail.putExtra(Intent.EXTRA_TEXT,
                        "--------------------------------------------\n" +
                                "<<< 주 의 사 항 >>>\n" +
                                "★ 리스트 노출용 프로필 사진을 첨부하시는 경우 브랜드명(한글)과 함께 첨부바랍니다.\n" +
                                "★ 리스트 노출용 프로필 사진은 반드시 브랜드 소유자만 첨부해주세요. \n" +
                                "★ 리스트 노출용 프로필 사진으로 인해 발생되는 저작권 문제는 이메일 발신자에게 있습니다.\n" +
                                "★ 브랜드명 미입력시 사진 첨부를 하시더라도 브랜드 등록 불가합니다.\n" +
                                "★ 브랜드 심사 후 등록까지 최대 일주일이 소요될 수 있습니다.\n" +
                                "--------------------------------------------\n\n\n" +
                                "브랜드명(한글) : \n\n" +
                                "기타 문의사항 : \n"
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
                        overlapFlag = false; //브랜드 [요청]이 이미 있는 경우
                        alreadyFlag = false; //브랜드 [등록]이 이미 되어있는 경우


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
                            Toast.makeText(MainActivity.this, "이미 등록 요청된적이 있는 브랜드명이에요", Toast.LENGTH_SHORT).show();
                        } else if (alreadyFlag == true) {
                            Toast.makeText(MainActivity.this, "이미 등록 되어있는 브랜드예요.", Toast.LENGTH_SHORT).show();
                        } else {
                            NewStore newStore = new NewStore(name, ins, store);
                            databaseNewReference.push().setValue(newStore);
                            Toast.makeText(MainActivity.this, "브랜드 등록 요청이 완료됐어요.", Toast.LENGTH_SHORT).show();
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

    //초성버튼 색상, 글자색 초기화
    public void clearText() {
        for (int i = 0; i <= 19; i++) {
            button[i].setBackgroundResource(R.drawable.bg_round_white);
            button[i].setTextColor(Color.parseColor("#000000"));
        }
    }

    //전체버튼 클릭상태 셋팅
    public void onBtnAllSel() {
        button[0].setBackgroundResource(R.drawable.bg_round_select);
        button[0].setTextColor(Color.parseColor("#ffffff"));
    }

    //리스트 가져오기, 초성으로 검색되는 경우 고려
    public void onData(String searchText) {
        RealtimeDatabase(searchText);
        //FirestoreDatabase(searchText);
    }

    private void onNewData() {
        databaseNewReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayNewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 List 추출
                    Store store = snapshot.getValue(Store.class); //만들어뒀던 store 객체에 데이터를 담음
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
                arrayList.clear(); // 기존 배열리스트 초기화

                if (searchText.length() == 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터 List 추출
                        Store store = snapshot.getValue(Store.class); //만들어뒀던 store 객체에 데이터를 담음
                        arrayList.add(store);
                        Collections.sort(arrayList, sortStoreName);
                    }
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Store store = snapshot.getValue(Store.class);
                        String iniName = HangulUtils.getHangulInitialSound(store.getStoreName(), searchText); //자음 검색을 가게명으로 수행
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
                //int numberCount = arrayList.size(); //arrayList 데이터 수 확인

                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                cnt = adapter.getItemCount();
                m_tv_cnt.setText("총 " + cnt + "개");
                getSupportActionBar().setTitle("총 " + cnt + "개");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시 로그
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
    }

    public void FirestoreDatabase(String searchText) {
        db.collection("store")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        arrayList.clear(); // 기존 배열리스트 초기화
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
                                    String iniName = HangulUtils.getHangulInitialSound(store.getStoreName(), searchText); //자음 검색을 가게명으로 수행

                                    if (iniName.indexOf(searchText) >= 0) {
                                        arrayList.add(store);
                                        Collections.sort(arrayList, sortStoreName);
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                        cnt = adapter.getItemCount();
                        m_tv_cnt.setText("총 " + cnt + "개");
                        getSupportActionBar().setTitle("총 " + cnt + "개");

                    }
                });
    }

    //화면 터치시 키보드 숨기기
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

    //뒤로가기 버튼 클릭시 앱 종료
    @Override
    public void onBackPressed() {
        if (m_lay_edit.getVisibility() == View.VISIBLE) {
            m_lay_edit.setVisibility(View.GONE);
            onBtnAllSel();
            m_et_search.setText("");
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                //액티비티 종료 후
                ActivityCompat.finishAffinity(this);
                //프로세스 종료
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

                            //다이얼로그 종료 후
                            dialog.dismiss();
                            //액티비티 종료 후
                            ActivityCompat.finishAffinity(MainActivity.this);
                            //프로세스 종료
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
}

/* --------------------------데이터 추가 구문-------------------------- */
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