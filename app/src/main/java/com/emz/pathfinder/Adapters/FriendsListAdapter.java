package com.emz.pathfinder.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emz.pathfinder.Models.Users;
import com.emz.pathfinder.R;
import com.emz.pathfinder.Utils.UserHelper;
import com.emz.pathfinder.Utils.Utils;

import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {
    private Context context;
    private List<Users> friendsList;

    private Utils utils;
    private UserHelper usrHelper;

    public FriendsListAdapter(Context context, List<Users> friendsList) {
        utils = new Utils(context);
        usrHelper = new UserHelper(context);

        this.context = context;
        this.friendsList = friendsList;
    }


    @Override
    public FriendsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsListAdapter.MyViewHolder holder, int position) {
        Users user = friendsList.get(position);
        holder.nameTv.setText(user.getFullName());
        holder.emailTv.setText(user.getEmail());
        Glide.with(context).load(utils.PROFILEPIC_URL + user.getProPic()).apply(RequestOptions.centerInsideTransform().error(R.drawable.defaultprofilepicture)).into(holder.profilePic);
        holder.actionBtn.setText(holder.setActionButton(user.getFriendStatus()));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv, emailTv;
        Button actionBtn;
        ImageView profilePic;

        MyViewHolder(View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            actionBtn = itemView.findViewById(R.id.action_button);
            profilePic = itemView.findViewById(R.id.profilePic);
        }

        String setActionButton(int status) {
            switch (status){
                case 0:
                    return "Add Friend";
                case 1:
                    return "Request Sent";
                case 2:
                    return "Accept Request";
                case 3:
                    return "Friend";
                case 4:
                    return "Edit Profile";
            }
            return null;
        }
    }
}
