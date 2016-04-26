package com.flybits.samples.context.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flybits.samples.context.R;

public class ContextFragment  extends Fragment {

    public static Fragment newInstance() {

        ContextFragment newFragment = new ContextFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_context, container, false);
        return view;
    }
}