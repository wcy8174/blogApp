package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.chatapp.Fragment.MyBlog;
import com.example.chatapp.Fragment.chatContacts;
import com.example.chatapp.Fragment.chatMy;
import com.example.chatapp.Fragment.mainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private mainFragment mainFragment;
    private MyBlog MyBlog;
    private com.example.chatapp.Fragment.chatContacts chatContacts;
    private com.example.chatapp.Fragment.chatMy chatMy;
    private  Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initFragment();

        int index = getIntent().getIntExtra("index",-1);
        if(index != -1){
            switchFragment(lastfragment,index);
            lastfragment=index;
        }
    }
    //初始化fragment和fragment数组
    private void initFragment()
    {

        mainFragment = new mainFragment();
        MyBlog = new MyBlog();
        chatContacts = new chatContacts();
        chatMy = new chatMy();
        fragments = new Fragment[]{mainFragment, MyBlog,chatContacts,chatMy};
        lastfragment=0;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview, mainFragment).show(mainFragment).commit();
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bnv);

        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }
    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.id1:
                {
                    if(lastfragment!=0)
                    {
                        switchFragment(lastfragment,0);
                        lastfragment=0;
                    }
                    return true;
                }
                case R.id.id2:
                {
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;
                    }
                    return true;
                }
                case R.id.id3:
                {
                    if(lastfragment!=2)
                    {
                        switchFragment(lastfragment,2);
                        lastfragment=2;
                    }
                    return true;
                }
                case R.id.id4:
                {
                    if(lastfragment!=3)
                    {
                        switchFragment(lastfragment,3);
                        lastfragment=3;
                    }
                    return true;
                }
            }


            return false;
        }
    };
    //切换Fragment
    private void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }
}
