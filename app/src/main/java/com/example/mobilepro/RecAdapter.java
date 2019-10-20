package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView recitemName;
        TextView recshopName;
        TextView recdistance;
        TextView recprice;
        ImageView recimageView;
        OnRecmendationListener onRecmendationListener;

        public ViewHolder(View view, OnRecmendationListener onRecmendationListener){
            super(view);
            recitemName = view.findViewById(R.id.recitemName);
            recshopName = view.findViewById(R.id.recshopName);
            recdistance = view.findViewById(R.id.recdistance);
            recprice = view.findViewById(R.id.recprice);
            recimageView = view.findViewById(R.id.recImageView);
            this.onRecmendationListener = onRecmendationListener;
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onRecmendationListener.OnRecmendationClick(getAdapterPosition());
        }

    }
    private List<item> items;
    public List<item> getItems() {return items;}
    private OnRecmendationListener mOnRecmendationListener;

    private Context context;
    public RecAdapter(List<item> items, Context context, OnRecmendationListener onRecmendationListener){
        this.items = items;
        this.context = context;
        this.mOnRecmendationListener = onRecmendationListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recommendation, parent, false);
        ViewHolder vh = new ViewHolder(view, mOnRecmendationListener);
        return vh;
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.recitemName.setText(items.get(position).getName());
        holder.recshopName.setText(items.get(position).getShopName());
        holder.recdistance.setText(items.get(position).getAddress());
        holder.recprice.setText(""+items.get(position).getPrice());
        Glide.with(context)
                .load(items.get(position).getImage())
                .into(holder.recimageView);
    }

    public interface OnRecmendationListener{
        void OnRecmendationClick(int position);

    }

}