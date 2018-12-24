package com.example.kabicin.rhythmkk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayScreenFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.play_screen_fragment, container, false);

        // Initialize recycler view if the activity exists
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        // set fixed size to reduce overhead
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager recyclerLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerLayoutManager);

        String[][] data = new String[][]{
                {"Bingo Remix", "Take a walk down to Bingo Town", "1000", "3.4f"}
        };
        RecyclerView.Adapter recyclerAdapter = new MyAdapter(getContext(), data,
                ((GameActivity) getActivity()).getUsername());
        recyclerView.setAdapter(recyclerAdapter);

        return view;
    }
}
