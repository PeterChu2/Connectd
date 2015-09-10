package com.example.peter.connectd.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.peter.connectd.R;
import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.ui.activity.UserDetailActivity;

import java.util.List;

/**
 * Created by peter on 05/09/15.
 */
public class ResultsListAdapter extends ArrayAdapter<SearchResult> {
    public ResultsListAdapter(Context context, int resource, List<SearchResult> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.result_list_item, parent, false);
        }
        final SearchResult result = getItem(position);
        if (result != null) {
            TextView tvUsername = (TextView) v.findViewById(R.id.result_display_name);
            tvUsername.setText(result.getDisplayName());
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
                        .getDefaultSharedPreferences(getContext()).edit();
                sharedPreferencesEditor.
                        putString(ConnectdApiClient.SHAREDPREF_LOGIN_KEY, result.getUsername()).commit();
                Intent i = new Intent(getContext(), UserDetailActivity.class);
                getContext().startActivity(i);
            }
        });
        return v;
    }
}
