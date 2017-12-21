package com.emz.pathfinder;

import android.annotation.SuppressLint;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.emz.pathfinder.Fragments.FriendsListFragment;
import com.emz.pathfinder.Fragments.ProfileAboutFragment;
import com.emz.pathfinder.Fragments.SearchFragment;
import com.emz.pathfinder.Fragments.TimelineFragment;
import com.emz.pathfinder.Models.Users;
import com.emz.pathfinder.Utils.UserHelper;
import com.emz.pathfinder.Utils.Utils;
import com.rw.velocity.Velocity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    //TODO: Create New Post

    private Toolbar toolbar;

    private final String TAG = "ProfileActivity";
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private ImageView headerimage;
    private CircleImageView profileimage;
    private Button actionBtn;
    private int uid;

    private TextView nameTV, emailTV, titleNameTv, titleEmailTv;

    private Utils utils;
    private UserHelper usrHelper;
    private Users currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(getIntent().getExtras() != null) {
            uid = getIntent().getExtras().getInt("id");
            utils = new Utils(this);
            usrHelper = new UserHelper(this);
            loadProfile();
        }else{
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadProfile() {
        Velocity.post(utils.UTILITIES_URL+"getProfile/"+uid)
                .withFormData("id", usrHelper.getUserId())
                .connect(new Velocity.ResponseListener() {
                    @Override
                    public void onVelocitySuccess(Velocity.Response response) {
                        currentUser = response.deserialize(Users.class);
                        bindView();
                    }

                    @Override
                    public void onVelocityFailed(Velocity.Response response) {

                    }
                });
    }

    @SuppressLint("ResourceType")
    private void bindView() {
        Bundle timelineBundle = new Bundle();
        timelineBundle.putInt("id", uid);

        final TimelineFragment timelineFragment = new TimelineFragment();
        timelineFragment.setArguments(timelineBundle);

        Bundle aboutBundle = new Bundle();
        aboutBundle.putSerializable("currentUser", currentUser);

        final ProfileAboutFragment profileAboutFragment = new ProfileAboutFragment();
        profileAboutFragment.setArguments(aboutBundle);

        final FriendsListFragment friendsListFragment = new FriendsListFragment();
        friendsListFragment.setArguments(aboutBundle);

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            private final Fragment[] mFragments = new Fragment[]{
                    timelineFragment,
                    profileAboutFragment,
                    friendsListFragment,
            };

            private final String[] mFragmentNames = new String[]{
                    "Timeline",
                    "About",
                    "Friends",
            };

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        nameTV = findViewById(R.id.user_profile_name);
        emailTV = findViewById(R.id.user_email);

        titleNameTv = findViewById(R.id.profile_title_name);
        titleEmailTv = findViewById(R.id.profile_title_email);

        nameTV.setText(currentUser.getFullName());
        emailTV.setText(currentUser.getEmail());

        mViewPager = findViewById(R.id.htab_viewpager);

        headerimage = findViewById(R.id.htab_header);
        profileimage = findViewById(R.id.htab_profileImage);
        toolbar = findViewById(R.id.htab_toolbar);
        actionBtn = findViewById(R.id.add_friend_btn);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleNameTv.setText(currentUser.getFullName());
        titleEmailTv.setText(currentUser.getEmail());

        titleNameTv.setVisibility(View.INVISIBLE);
        titleEmailTv.setVisibility(View.INVISIBLE);

        toolbar.setTitle(" ");

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.htab_collapse_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.htab_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    titleNameTv.setVisibility(View.VISIBLE);
                    titleEmailTv.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if(isShow) {
                    titleNameTv.setVisibility(View.INVISIBLE);
                    titleEmailTv.setVisibility(View.INVISIBLE);
                    isShow = false;
                }
            }
        });

        mViewPager.setAdapter(mPagerAdapter);

        Glide.with(this).load(utils.HEADERPIC_URL+currentUser.getHeaderPic()).into(headerimage);
        Glide.with(this).load(utils.PROFILEPIC_URL+currentUser.getProPic()).into(profileimage);

        setActionButton();

        TabLayout tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setActionButton() {
        int status = currentUser.getFriendStatus();
        switch (status){
            case 0:
                actionBtn.setText("Add Friend");
                break;
            case 1:
                actionBtn.setText("Request Sent");
                break;
            case 2:
                actionBtn.setText("Accept Request");
                break;
            case 3:
                actionBtn.setText("Friend");
                break;
            case 4:
                actionBtn.setText("Edit Profile");
                break;
        }
    }
}
