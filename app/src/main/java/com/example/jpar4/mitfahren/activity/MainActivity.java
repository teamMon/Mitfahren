package com.example.jpar4.mitfahren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.fragment.GreenFragment;
import com.example.jpar4.mitfahren.fragment.RedFragment;
import com.example.jpar4.mitfahren.fragment.YellowFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //viewr객체 가져옴
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_table_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        // viewPager.setOffscreenPageLimit(1);
        //프레그먼트 생성
        Fragment[] arrFragments = new Fragment[3];
        arrFragments[0] = new GreenFragment();
        arrFragments[1] = new YellowFragment();
        arrFragments[2] = new RedFragment();
      //  arrFragments[3] = new SearchFragment();


        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), arrFragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_friendgroup_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_talk_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_notifications_black_24dp);
       // tabLayout.getTabAt(3).setIcon(R.drawable.ic_search_black_24dp);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private  Fragment[] arrFragments;

        public MyPagerAdapter(FragmentManager fm, Fragment[] arrFragments) {
            super(fm);
            this.arrFragments=arrFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return arrFragments[position];
        }

        @Override
        public int getCount() {
            return arrFragments.length;
        }

/*        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    //return "HOME";
                case 1:
                    return "AD";
                case 2:
                    return "GAME";
                case 3:
                    return "PROFILE";
                default:
                    return "";
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
/*        if(requestCode==1002 && resultCode == Activity.RESULT_OK){
            //Toast.makeText(this, "222"+data.getStringExtra("content")+requestCode+resultCode, Toast.LENGTH_SHORT).show();
            //this.getFragmentManager().findFragmentById(R.id.kiwis_rv_list).onActivityResult(requestCode,resultCode,data);
            //this.getSupportFragmentManager().findFragmentById(R.id.kiwis_rv_list).onActivityResult(requestCode,resultCode,data);
            //getFragmentManager().findFragmentById(R.id.kiwis_rv_list)
            this.getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.kiwis_vp_pager+":0").onActivityResult(requestCode,resultCode,data);
        }*/
    }

    @Override
    public void onBackPressed() {
         super.onBackPressed();
     //   backPressCloseHandler.onBackPressed();
    }
}

