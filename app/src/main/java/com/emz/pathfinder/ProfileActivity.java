package com.emz.pathfinder;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.emz.pathfinder.Fragments.ProfileAboutFragment;
import com.emz.pathfinder.Fragments.ProfileTimelineFragment;
import com.emz.pathfinder.Fragments.SearchFragment;
import com.emz.pathfinder.Models.Users;
import com.emz.pathfinder.Utils.UserHelper;
import com.emz.pathfinder.Utils.Utils;
import com.rw.velocity.Velocity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private final String TAG = "ProfileActivity";
    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private ImageView headerimage;
    private CircleImageView profileimage;
    private int uid;

    private TextView nameTV, emailTV;

    private Utils utils;
    private UserHelper usrHelper;
    private Users currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Velocity.initialize(3);

        if(getIntent().getExtras() != null) {
            uid = getIntent().getExtras().getInt("id");
            utils = new Utils(this);
            loadProfile();
        }else{
            finish();
        }
    }

    private void loadProfile() {
        Velocity.get(utils.UTILITIES_URL+"getProfile/"+uid)
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
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            private final Fragment[] mFragments = new Fragment[]{
                    new ProfileTimelineFragment(uid),
                    new ProfileAboutFragment(currentUser),
                    new SearchFragment(),
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

        nameTV.setText(currentUser.getFname()+" "+currentUser.getLname());
        emailTV.setText(currentUser.getEmail());

        mViewPager = findViewById(R.id.htab_viewpager);

        headerimage = findViewById(R.id.htab_header);
        profileimage = findViewById(R.id.htab_profileImage);
        toolbar = findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                    toolbar.setTitle(currentUser.getFname()+" "+currentUser.getLname());
                    isShow = true;
                } else if(isShow) {
                    toolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        mViewPager.setAdapter(mPagerAdapter);

        Glide.with(this).load(utils.HEADERPIC_URL+currentUser.getHeaderPic()).into(headerimage);
        Glide.with(this).load(utils.PROFILEPIC_URL+currentUser.getProPic()).into(profileimage);

        TabLayout tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
}