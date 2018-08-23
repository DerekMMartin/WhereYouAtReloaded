package com.example.derekmartin.whereyouatreloaded;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View MyView;
    private FirebaseAuth auth;
    private  LinearLayout ll;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private Context c;


    public HomeFragment() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }
    private void SetupFragment(){

       ll = MyView.findViewById(R.id.HomeLayout);
       c = getContext();

        firestore.collection("Users").document(auth.getCurrentUser().getEmail()).collection("Friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult())
                {
                    ll.addView(lineFactory(doc.getId()));
                }
            }
        });




    }

    private FrameLayout lineFactory(final String FriendEmail){
        FrameLayout layout = new FrameLayout(c);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,20,20,20);

        FrameLayout.LayoutParams ButtonParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButtonParams.setMargins(20,20,20,20);
        ButtonParams.gravity = Gravity.END;

        FrameLayout.LayoutParams TextParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextParams.setMargins(20,20,20,20);
        TextParams.gravity = Gravity.CENTER|Gravity.START;


        TextView tv = new TextView(c);
        tv.setText(FriendEmail);
        tv.setLayoutParams(TextParams);
        tv.setTextSize(20);
        layout.addView(tv);

        final Button count = new Button(c,null,R.style.Widget_AppCompat_Button_Colored);
        count.setTextColor(getResources().getColor(R.color.colorAccent));
        count.setTextSize(20);
        count.setLayoutParams(ButtonParams);
        layout.addView(count);

        firestore.collection("Users").document(auth.getCurrentUser().getEmail()).collection(FriendEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 0;
                for(DocumentSnapshot doc : task.getResult()){
                    i++;
                }
                count.setText((i+""));
            }
        });

        return layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyView =  inflater.inflate(R.layout.fragment_home, container, false);
        SetupFragment();
        return MyView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
