package com.vagad.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.fragments.HeaderNewsFragment;
import com.vagad.dashboard.fragments.NewsDetailFragment;
import com.vagad.model.RSSItem;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.DateUtils;
import com.vagad.utils.customviews.CustomViewPager;
import com.vagad.utils.pageindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class NewsDetailActivity extends BaseActivity {

    private TextView txtTitle, txtTime, txtDesc;
    private ImageView imgCover, imgBack, imgShare, imgFav;;
    private LinearLayout linNewsDetail;
    private RelativeLayout relHeader;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    //private FloatingActionButton mBtnFav;
    public RSSDatabaseHandler rssDatabaseHandler;
    public boolean isFavChange;
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private CustomViewPager customViewPager;
    private int mWhichPage = 0;
    private boolean mIsFromNewsList;
    private static final String TAG = "NewsDetailActivity";
    private RSSItem rssItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_news_detail_pager);
        rssDatabaseHandler = new RSSDatabaseHandler(this);
        initView();
        mIsFromNewsList = getIntent().getBooleanExtra(Constants.Bundle_Is_From_News_List, false);
        if(mIsFromNewsList){
            rssItem = getIntent().getParcelableExtra(Constants.Bundle_Feed_Item);
            setDataFromNewsList();
        }else {
            linNewsDetail.setVisibility(View.GONE);
            customViewPager.setVisibility(View.VISIBLE);
            mNewsList = getIntent().getParcelableArrayListExtra(Constants.Bundle_Feed_Item);
            mWhichPage = getIntent().getIntExtra(Constants.Bundle_Which_Page, 0);
            setData();
        }
    }

    private void setDataFromNewsList() {
        Glide.with(this).load(rssItem.getImage()).placeholder(R.drawable.ic_placeholder).into(imgCover);
        txtTitle.setText(rssItem.getTitle());
        txtTime.setText(DateUtils.convertData(rssItem.getPubdate()));
        txtDesc.setText(rssItem.getDescription());
        if(rssItem.isFav()){
            imgFav.setTag("1");
            imgFav.setImageResource(R.drawable.ic_fav_select);
        }else{
            imgFav.setTag("0");
            imgFav.setImageResource(R.drawable.ic_tab_fav);
        }
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void setViewPagerAdapter(ViewPager viewPager){
        mHeaderPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mNewsList.size();
            }
            @Override
            public Fragment getItem(int position) {
                NewsDetailFragment headerNewsFragment = new NewsDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.Bundle_Feed_Item, mNewsList.get(position));
                headerNewsFragment.setArguments(bundle);
                return headerNewsFragment;
            }
            @Override
            public Parcelable saveState() {return null;}

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }
        };
        viewPager.setAdapter(mHeaderPagerAdapter);
        viewPager.setOffscreenPageLimit(mNewsList.size());
        viewPager.setCurrentItem(mWhichPage);
    }

    private void setData() {
        setViewPagerAdapter(customViewPager);
    }

    private void initView() {
        customViewPager = (CustomViewPager) findViewById(R.id.view_pager);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        txtDesc = (TextView) findViewById(R.id.txtDescription);
        txtTime = (TextView) findViewById(R.id.txtTime);
        relHeader = (RelativeLayout) findViewById(R.id.relHeader);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgFav = (ImageView) findViewById(R.id.imgFav);
        imgShare = (ImageView) findViewById(R.id.imgShare);
        linNewsDetail = (LinearLayout) findViewById(R.id.linNewsDetail);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            relHeader.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShare();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavChange = true;
                if(v.getTag().toString().equals("0")){
                    v.setTag("1");
                    rssItem.setFav(true);
                    imgFav.setImageResource(R.drawable.ic_fav_select);
                    rssDatabaseHandler.setFav(1, ""+rssItem.getId());
                }else{
                    rssItem.setFav(false);
                    v.setTag("0");
                    rssDatabaseHandler.setFav(0, ""+rssItem.getId());
                    imgFav.setImageResource(R.drawable.ic_tab_fav);
                }
            }
        });
    }

    private void openShare() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ""+rssItem.getTitle()+"\n"+rssItem.getDescription()+" Thanks For Using Vagad News. Please download from play store https://play.google.com/store/apps/details?id=com.vagad");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }



    @Override
    public void onBackPressed() {
        if(isFavChange && mIsFromNewsList){
            Intent intent = new Intent();
            intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
            setResult(Activity.RESULT_OK, intent);
            supportFinishAfterTransition();
        }else if(isFavChange){
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constants.Bundle_Feed_List, (ArrayList<? extends Parcelable>) mNewsList);
            setResult(Activity.RESULT_OK, intent);
            supportFinishAfterTransition();
        }else {
            super.onBackPressed();
            supportFinishAfterTransition();
        }
    }

    public void notifyChange() {
        if(mHeaderPagerAdapter != null){
            mHeaderPagerAdapter.notifyDataSetChanged();
        }
    }
}
