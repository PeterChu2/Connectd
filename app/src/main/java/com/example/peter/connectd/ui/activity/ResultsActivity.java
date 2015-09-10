package com.example.peter.connectd.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.peter.connectd.R;
import com.example.peter.connectd.models.SearchResult;
import com.example.peter.connectd.models.User;
import com.example.peter.connectd.rest.ConnectdApiClient;
import com.example.peter.connectd.rest.ConnectdApiService;
import com.example.peter.connectd.rest.OnAsyncHttpRequestCompleteListener;
import com.example.peter.connectd.ui.adapters.ResultsListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 05/09/15.
 */
public class ResultsActivity extends Activity implements OnAsyncHttpRequestCompleteListener {
    public static final String QUERY_STRING = "com.example.peter.connectd.query";
    private TextView mTvResultsDescription;
    private String mQuery;
    private ResultsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);
        mTvResultsDescription = (TextView) findViewById(R.id.results_description);
        mQuery = getIntent().getExtras().getString(QUERY_STRING);
        ListView resultsList = (ListView) findViewById(R.id.results_list);
        mAdapter = new ResultsListAdapter(this, R.layout.results_activity, new ArrayList<SearchResult>());
        resultsList.setAdapter(mAdapter);
        ConnectdApiService apiService = ConnectdApiClient.getApiService();
        apiService.findUsers(this, mQuery, this);
    }

    @Override
    public void onUserLoaded(User user) {
        // NOP
    }

    @Override
    public void onResultsLoaded(List<SearchResult> results) {
        if(results.size() == 0) {
            mTvResultsDescription.setText(String.format("No results were found for query: %s.", mQuery));
        } else {
            mTvResultsDescription.setText(String.format("There were %d results matching your query: %s.",
                    results.size(), mQuery));
            mAdapter.addAll(results);
        }
    }

    @Override
    public void onUserLoadFailed(String error) {
        // NOP
    }
}
