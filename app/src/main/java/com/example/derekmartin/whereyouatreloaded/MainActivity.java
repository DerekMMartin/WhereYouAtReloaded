package com.example.derekmartin.whereyouatreloaded;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends FragmentActivity
        implements
        FirstFragment.OnFragmentInteractionListener,
        SecondFragment.OnFragmentInteractionListener,
        FriendsFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener,
        CameraEmbeddedFragment.OnFragmentInteractionListener,
        SendingFragment.OnFragmentInteractionListener
{

    private DrawerLayout dLayout;
    private android.support.v4.app.FragmentManager fManager;

    private void LogOutAndSwitch() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fManager=getSupportFragmentManager();

        dLayout=findViewById(R.id.drawerLayout);
        NavigationView nView = findViewById(R.id.nav_view);

        ((TextView)nView.findViewById(R.id.EmailDisplay)).setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        ((Button)nView.findViewById(R.id.LogOutButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOutAndSwitch();
            }
        });
        nView.setCheckedItem(R.id.menufirst);
        changeFragment(new FirstFragment());

        nView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        item.setChecked(true);
                        dLayout.closeDrawers();

                        switch (item.getItemId()){
                            case R.id.menufirst:
                                changeFragment(new FirstFragment());
                                break;
                            case R.id.menusecond:
                                changeFragment(new SecondFragment());
                                break;
                            case R.id.menuFriends:
                                changeFragment(new FriendsFragment());
                                break;
                            case R.id.menuCamera:
                                changeFragment(new CameraFragment());
                                break;
                            case R.id.menuCameraEmbedded:
                                changeFragment(new CameraEmbeddedFragment());
                                break;
                            case R.id.menuSending:
                                changeFragment(new SendingFragment());
                                break;
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
