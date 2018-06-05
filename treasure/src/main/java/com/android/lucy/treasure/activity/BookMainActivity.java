package com.android.lucy.treasure.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.android.lucy.treasure.view.ChangeColorIconWithText;
import com.android.lucy.treasure.R;
import com.android.lucy.treasure.fragment.BookShelfFragment;

import org.litepal.tablemanager.Connector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BookMainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<ChangeColorIconWithText> mTabIndicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOverflowButtonAlways();
        ActionBar actionBar=getActionBar();
        //使自定义的普通View能在菜单栏显示
        actionBar.setDisplayShowCustomEnabled(false);
        //设置在菜单栏里的图标不显示
        actionBar.setDisplayShowHomeEnabled(false);

        initView();
        initDatas();
        mViewPager.setAdapter(mAdapter);
        initEvent();

    }



    /**
     * 初始化所有事件
     */
    private void initEvent() {

        mViewPager.addOnPageChangeListener(this);
    }

    private void initDatas() {
        //创建书架数据库
        Connector.getDatabase();
        String[] mTitles = new String[]{"First Fragment !", "Second Fragment !", "Th",
                "Fourth Fragment !"};
        final List<Fragment> mTabs = new ArrayList<>();
        for (String title : mTitles) {
            BookShelfFragment bookShelfFragment = new BookShelfFragment();
            Bundle bundle = new Bundle();
            bookShelfFragment.setArguments(bundle);
            mTabs.add(bookShelfFragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };

    }

    private void initView() {
        mTabIndicators = new ArrayList<>();
        mViewPager = findViewById(R.id.vp_viewPager);
        ChangeColorIconWithText one = findViewById(R.id.id_indicator_one);
        ChangeColorIconWithText two = findViewById(R.id.id_indicator_two);
        ChangeColorIconWithText three = findViewById(R.id.id_indicator_three);
        ChangeColorIconWithText four = findViewById(R.id.id_indicator_four);
        //进入页面初始化选中
        one.setIconAlpha(1.0f);
        mViewPager.setCurrentItem(0,false);

        mTabIndicators.add(one);
        mTabIndicators.add(two);
        mTabIndicators.add(three);
        mTabIndicators.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        one.setAlpha(1.0f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    /*
    子菜单响应
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_action_search:
                Intent intent=new Intent(BookMainActivity.this,BookSearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
/*
* 反射获取
* */
    private void setOverflowButtonAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKey = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            //使得这个私有属性可以被访问
            menuKey.setAccessible(true);
            //改变configuration中这个field的值
            menuKey.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置menu显示icon
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onClick(View view) {
        clickTab(view);
    }

    /**
     * 点击Tab按钮
     *
     * @param v
     */
    private void clickTab(View v) {
        vresetOtherTabs();

        switch (v.getId()) {
            case R.id.id_indicator_one:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    /**
     * 重置其他的TabIndicator的颜色
     */
    private void vresetOtherTabs() {

        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Log.e("TAG", "position = " + position + " ,positionOffset =  "
        // + positionOffset);
        if (positionOffset > 0) {
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
