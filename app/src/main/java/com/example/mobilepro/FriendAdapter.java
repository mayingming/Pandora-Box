package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;
        TextView shopName;
        TextView distance;
        TextView price;
        ImageView imageView;
        public ViewHolder(View view){
            super(view);
            itemName = view.findViewById(R.id.itemName);
            shopName = view.findViewById(R.id.shopName);
            distance = view.findViewById(R.id.distance);
            price = view.findViewById(R.id.price);
            imageView = view.findViewById(R.id.imageView);
        }

    }
    private List<item> items;
    private Context context;
    public FriendAdapter(List<item> items, Context context){
        this.items = items;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
        }

    @Override
    public int getItemCount(){
        return items.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(items.get(position).getName());
        holder.shopName.setText(items.get(position).getShopName());
        holder.distance.setText(items.get(position).getAddress());
        holder.price.setText(""+items.get(position).getPrice());
        Glide.with(context)
                .load(items.get(position).getImage())
                .into(holder.imageView);
    }

}