package com.mycryptobinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mycryptobinder.R;

/**
 * Fragment responsible for displaying ICOs
 * <p>
 * Created by Yann on 07/10/2017
 */

public class IcoFragment extends Fragment {

    public IcoFragment() {
        // required empty public constructor
    }

    public static IcoFragment newInstance() {
        IcoFragment fragment = new IcoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ico, container, false);

        // set click listener for the add ico button
        Button button = view.findViewById(R.id.btn_add_ico);
        button.setOnClickListener(view1 -> {
            Intent add_ico = new Intent(view1.getContext(), AddIcoActivity.class);
            startActivity(add_ico);
        });

        return view;
    }

}