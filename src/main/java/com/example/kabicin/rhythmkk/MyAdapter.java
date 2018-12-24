package com.example.kabicin.rhythmkk;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    /**
     * Song dataset
     */
    private String[][] dataset;

    /**
     * Activity context (PlayScreenFragment)
     */
    private Context context;

    /**
     * Current user
     */
    private String currentUser;

    /**
     * Generates a view holder to display song name, number, description
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView textSongName;
        private TextView textSongNumber;
        private TextView textSongDescription;
        private LinearLayout parent;

        MyViewHolder(View v) {
            super(v);
            textSongName = v.findViewById(R.id.text_song_name);
            textSongNumber = v.findViewById(R.id.text_song_number);
            textSongDescription = v.findViewById(R.id.text_song_desc);
            parent = v.findViewById(R.id.linear_layout);
        }


    }


    /**
     * Initializes an adapter to display song-track list
     *
     * @param context of Activity
     * @param dataset of Song information
     */
    MyAdapter(Context context, String[][] dataset, String username) {
        this.context = context;
        this.dataset = dataset;
        this.currentUser = username;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.play_screen_button, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textSongNumber.setText(String.valueOf(position + 1));
        holder.textSongName.setText(dataset[position][0]);
        holder.textSongDescription.setText(dataset[position][1]);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Now Playing: " + dataset[holder.getAdapterPosition()][0], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SongActivity.class);
                intent.putExtra("name", dataset[holder.getAdapterPosition()][0]);
                intent.putExtra("description", dataset[holder.getAdapterPosition()][1]);
                intent.putExtra("offset", dataset[holder.getAdapterPosition()][2]);
                intent.putExtra("multiplier", dataset[holder.getAdapterPosition()][3]);
                intent.putExtra("username", currentUser);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }
}


