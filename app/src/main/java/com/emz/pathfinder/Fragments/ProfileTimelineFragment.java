package com.emz.pathfinder.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emz.pathfinder.Adapters.TimelineAdapter;
import com.emz.pathfinder.Models.Posts;
import com.emz.pathfinder.Models.Users;
import com.emz.pathfinder.R;
import com.emz.pathfinder.Utils.UserHelper;
import com.emz.pathfinder.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rw.velocity.Velocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileTimelineFragment extends Fragment {

    private static final String TAG = "ProfileTimelineFragment";

    private List<Posts> postsList;
    private HashMap<Integer, Users> usersList;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TimelineAdapter mAdapter;
    private Utils utils;

    private int userId;

    public ProfileTimelineFragment(){}

    @SuppressLint("ValidFragment")
    public ProfileTimelineFragment(int userId){
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        utils = new Utils(this.getContext());

        usersList = new HashMap<>();
        postsList = new ArrayList<>();

        mSwipeRefreshLayout = rootView.findViewById(R.id.feed_main_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mRecyclerView = rootView.findViewById(R.id.timelineRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadUser();
    }

    private void refreshItems() {
        loadUser();
    }

    private void loadUser() {
        Velocity.post(utils.UTILITIES_URL+"getAllProfiles")
                .connect(new Velocity.ResponseListener() {
                    @Override
                    public void onVelocitySuccess(Velocity.Response response) {
                        Gson gson = new Gson();
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray = parser.parse(response.body).getAsJsonArray();

                        for(int i = 0; i < jsonArray.size(); i++) {
                            JsonElement mJson = jsonArray.get(i);
                            Users user = gson.fromJson(mJson, Users.class);
                            usersList.put(user.getId(), user);
                        }

                        Log.d(TAG, "USERS LOADDED");

                        loadTimeline();
                    }

                    @Override
                    public void onVelocityFailed(Velocity.Response response) {
                        Log.e(TAG, getResources().getString(R.string.no_internet_connection));
                    }
                });
    }

    private void loadTimeline(){
        Velocity.post(utils.UTILITIES_URL+"getProfileTimeline")
                .withFormData("pid", String.valueOf(userId))
                .withFormData("limit", "0")
                .withFormData("perpage", "20")
                .connect(new Velocity.ResponseListener() {
                    @Override
                    public void onVelocitySuccess(Velocity.Response response) {
                        Log.d(TAG, response.body);

                        Gson gson = new Gson();
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray = parser.parse(response.body).getAsJsonArray();

                        for(int i = 0; i < jsonArray.size(); i++) {
                            JsonElement mJson = jsonArray.get(i);
                            Posts posts = gson.fromJson(mJson, Posts.class);
                            postsList.add(posts);
                        }

                        Log.d(TAG, "POST LOADDED");

                        if(mRecyclerView.getAdapter() == null){
                            mAdapter = new TimelineAdapter(getContext(), usersList, postsList, mRecyclerView, ProfileTimelineFragment.this);
                            mRecyclerView.setAdapter(mAdapter);
                        }else{
                            mAdapter.notifyDataSetChanged();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onVelocityFailed(Velocity.Response response) {
                        Log.e(TAG, getResources().getString(R.string.no_internet_connection));
                    }
                });
    }
}
