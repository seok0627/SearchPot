package com.jys.searchpot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{

    private ArrayList<Store> arrayList;
    private Context context;
    private String storeName = "";

    public CustomAdapter(ArrayList<Store> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    //firebase로부터 받아오는 이미지 셋팅
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .error(R.drawable.ic_list_0)
                .circleCrop()
                .into(holder.iv_profrile);

        holder.tv_storeName.setText(arrayList.get(position).getStoreName());
        holder.txt_ins = arrayList.get(position).getInsUrl();
        holder.txt_store = arrayList.get(position).getSellUrl();

        if(holder.txt_ins.length() < 1){
            holder.btn_ins.setBackgroundResource(R.drawable.ic_ins_nourl_1);
        }else{
            holder.btn_ins.setBackgroundResource(R.drawable.ic_ins_yesurl_2);
        }
        if(holder.txt_store.length() < 1){
            holder.btn_store.setBackgroundResource(R.drawable.ic_store_nourl_1);
        }else{
            holder.btn_store.setBackgroundResource(R.drawable.ic_store_yesurl_2);
        }
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profrile;
        TextView tv_storeName;
        Button btn_ins;
        Button btn_store;
        String txt_ins = "";
        String txt_store = "";

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profrile = itemView.findViewById(R.id.iv_profile);
            this.tv_storeName = itemView.findViewById(R.id.tv_storeName);
            this.btn_ins = itemView.findViewById(R.id.btn_ins);
            this.btn_store = itemView.findViewById(R.id.btn_store);

            this.btn_ins.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txt_ins.length() > 0){
                        Context context = v.getContext();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(txt_ins));
                        try{
                            context.startActivity(intent);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        Snackbar.make(itemView, "등록된 인스타 URL이 없습니다.", 800).show();
//                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            this.btn_store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txt_store.length() > 0){
                        Context context = v.getContext();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(txt_store));
                        try{
                            context.startActivity(intent);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        Snackbar.make(itemView, "등록된 스토어 URL이 없습니다.", 800).show();
                    }
                }
            });
        }
    }
}