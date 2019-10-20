package com.example.mobilepro;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private double shopLatitude;
    private double shopLongitude;
    private Location shopLocation;
    private double longitude;
    private double latitude;
    private Location loc;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView itemName;
        TextView shopName;
        TextView distance;
        TextView price;
        ImageView imageView;
        OnItemListener onItemListener;


        public ViewHolder(View view, OnItemListener onItemListener){
            super(view);
            itemName = view.findViewById(R.id.itemName);
            shopName = view.findViewById(R.id.shopName);
            distance = view.findViewById(R.id.distance);
            price = view.findViewById(R.id.price);
            imageView = view.findViewById(R.id.listImageView);
            this.onItemListener = onItemListener;
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }

    }
    private List<item> items;
    public List<item> getItems() {return items;}
    private OnItemListener mOnItemListener;

    private Context context;
    public FriendAdapter(List<item> items, double longitude, double latitude, Context context, OnItemListener onItemListener){
        this.items = items;
        this.context = context;
        this.mOnItemListener = onItemListener;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friend, parent, false);
        ViewHolder vh = new ViewHolder(view, mOnItemListener);
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
        shopLongitude = items.get(position).getLongitude();

        shopLatitude = items.get(position).getLatitude();
        shopLocation = new Location("");
        shopLocation.setLatitude(shopLatitude);
        shopLocation.setLongitude(shopLongitude);
        loc = new Location("");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        float distance = loc.distanceTo(shopLocation);
        holder.distance.setText(new Float((float)(Math.round(distance))/1000).toString());
        holder.price.setText(""+items.get(position).getPrice());
        Log.d(""+shopLatitude,new Double(shopLongitude).toString());
        Log.d(new Double(latitude).toString(),new Double(longitude).toString());
        Glide.with(context)
                .load(items.get(position).getImage())
                .into(holder.imageView);
    }

    public interface OnItemListener{
        void onItemClick(int position);

    }

}