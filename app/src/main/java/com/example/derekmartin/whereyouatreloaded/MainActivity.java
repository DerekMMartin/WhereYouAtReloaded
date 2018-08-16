package com.example.derekmartin.whereyouatreloaded;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity
        implements
        FirstFragment.OnFragmentInteractionListener,
        SecondFragment.OnFragmentInteractionListener
{

    private DrawerLayout dLayout;
    private android.support.v4.app.FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fManager=getSupportFragmentManager();

        //initializes to the first fragment in menu and selects it
        NavigationView nv= findViewById(R.id.nav_view);
        nv.setCheckedItem(R.id.menufirst);
        changeFragment(new FirstFragment());

        dLayout=findViewById(R.id.drawerLayout);
        NavigationView nView = findViewById(R.id.nav_view);
        nView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        item.setChecked(true);
                        dLayout.closeDrawers();



                        if(item.getItemId()==R.id.menufirst){
                            changeFragment(new FirstFragment());
                        }else{
                            changeFragment(new SecondFragment());
                        }

                        return true;
                    }
                });
        }

        public void changeFragment(Fragment f){
            FragmentTransaction fTrans=fManager.beginTransaction();

            fTrans.replace(R.id.content_frame,f);
            fTrans.addToBackStack(null);
            fTrans.commit();
        }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
