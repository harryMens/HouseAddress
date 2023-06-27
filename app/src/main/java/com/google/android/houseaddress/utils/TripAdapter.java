package com.google.android.houseaddress.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.houseaddress.R;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    List<Trip> tripList;
    Context context;
    TripViewHolder.TripListener tripListener;

    public TripAdapter(Context context, TripViewHolder.TripListener tripListener) {
        this.context = context;
        this.tripListener = tripListener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.trip_layout, parent, false);
        return new TripViewHolder(view, tripListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.title.setText(tripList.get(position).title);
        holder.description.setText(tripList.get(position).description);
        holder.date.setText(tripList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        if (tripList != null){
            return tripList.size();
        }
        return 0;
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, description, date;
        TripListener listener;

        public TripViewHolder(@NonNull View itemView, TripListener listener) {
            super(itemView);
            this.listener = listener;
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.getPosition(getAdapterPosition());
        }
        public interface TripListener{
            void getPosition(int position);
        }
    }
    public void setTripList(List<Trip> tripList){
        this.tripList = tripList;
        notifyDataSetChanged();

    }
}
