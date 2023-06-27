package com.google.android.houseaddress.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.houseaddress.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder>{

    private static final String TAG = "LocationAdapter";
    Context context;
    LocationViewHolder.TripLocationListener listener;
    List<LocationInfo> tripLocation;

    public LocationAdapter(Context context, LocationViewHolder.TripLocationListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.location_layout,parent,false);
        return new LocationViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        int list = position+1;
        holder.title.setText("House "+list);
        holder.date.setText(tripLocation.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        if (tripLocation != null){
            Log.d(TAG, "getItemCount: list size "+tripLocation.size());
            return tripLocation.size();

        }
        else {
            Log.d(TAG, "getItemCount: list was null");
        }
        return 0;
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder implements
    View.OnClickListener{
        TripLocationListener listener;
        TextView title, date;

        public LocationViewHolder(@NonNull View itemView, TripLocationListener listener) {
            super(itemView);
            this.listener = listener;
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            listener.getPosition(getAdapterPosition());
        }

        public interface TripLocationListener{
            void getPosition(int position);
        }
    }
    public void setTripLocation(List<LocationInfo> tripLocation){
        this.tripLocation = tripLocation;
        notifyDataSetChanged();
    }
}
