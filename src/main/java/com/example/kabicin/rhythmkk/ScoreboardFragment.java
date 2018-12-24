package com.example.kabicin.rhythmkk;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ScoreboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.scoreboard_fragment, container, false);
        ListView scoresListView = view.findViewById(R.id.scoresListView);
        scoresListView.setAdapter(new ScoreDataAdapter(getActivity()));
        return view;
    }

    /**
     * Obtains an ArrayList of User data from the SQLite database
     *
     * @return List of User objects
     */
    private List<User> getData() {
        Cursor data = ((GameActivity) getActivity()).dataHelper.getData();
        List<User> listData = new ArrayList<>();
        if (data != null && data.getCount() > 0) {
            while (data.moveToNext()) {
                listData.add(new User(data.getString(0), data.getString(1)));
            }
        }
        return listData;
    }


    private class ScoreDataAdapter extends ArrayAdapter<User> {
        /**
         * Constructor for ScoreDataAdapter
         *
         * @param context app Context
         */
        public ScoreDataAdapter(Context context) {
            super(context, 0, getData());
        }


        @Override
        @NonNull
        public View getView(int pos, View view, @NonNull ViewGroup parent) {
            User userElem = getItem(pos);
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.scoreboard_list_item, parent, false);
            }
            TextView name = view.findViewById(R.id.textName);
            TextView score = view.findViewById(R.id.textScore);
            name.setText(userElem.getUsername());
            score.setText(userElem.getScore());
            return view;
        }


    }
}
