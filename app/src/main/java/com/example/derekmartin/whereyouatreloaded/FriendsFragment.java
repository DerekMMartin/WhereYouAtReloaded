package com.example.derekmartin.whereyouatreloaded;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String Email = "";
    private FirebaseAuth FireBase;
    private FirebaseUser FireUser;
    private LinearLayout FriendsLayout;
    private FirebaseStorage storage;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }
    public void SetupFragment(){
        FireBase = FirebaseAuth.getInstance();
        FireUser = FireBase.getCurrentUser();
        Email = FireUser.getEmail();
        storage=FirebaseStorage.getInstance();
        SetupSearchClick();
        getFriends();
    }

    private void SetupSearchClick(){
        Button b = FriendsLayout.findViewById(R.id.FriendsSearchButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc : task.getResult())
                        {
                            if(doc.getId().equals(((EditText)getView().findViewById(R.id.SearchTextField)).getText().toString())){
                                AddFriend(doc.getId());
                            }
                        }
                    }
                });
            }
        });
    }
    private void getFriends(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(Email)
                .collection("Friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> Friends = new ArrayList<>();

                        for (DocumentSnapshot doc: task.getResult()) {
                            Friends.add(doc.getId());
                        }
                        SetupFriends(Friends);

                    }
                });
    }
    private void SetupFriends(ArrayList<String> friends){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,20,20,20);

        FrameLayout.LayoutParams ButtonParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButtonParams.setMargins(20,20,20,20);
        ButtonParams.gravity = Gravity.END;

        FrameLayout.LayoutParams TextParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextParams.setMargins(20,20,20,20);
        TextParams.gravity = Gravity.START;

        for (int i = 0; i< friends.size();i++)
        {
            FrameLayout l = new FrameLayout(getView().getContext());
            l.setLayoutParams(params);

            StorageReference ref=storage.getReference();
            StorageReference path =ref.child(Email+"/");

            TextView tv = new TextView(this.getContext());
            tv.setLayoutParams(TextParams);
            tv.setTextSize(14);
            tv.setText(friends.get(i));
            tv.setGravity(Gravity.CENTER);


            Button b = new Button(l.getContext());
            b.setText("Remove");
            b.setContentDescription(friends.get((i)));
            b.setLayoutParams(ButtonParams);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFriend(((Button)v).getContentDescription().toString());
                }
            });
            l.addView(b);
            l.addView(tv);


            FriendsLayout.addView(l);
        }
    }
    private void AddFriend(String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(Email)
                .collection("Friends")
                .document(name)
                .set(new HashMap<>());
        Toast.makeText(getActivity(),"Friend added",Toast.LENGTH_LONG).show();
    }
    private void removeFriend(final String FriendEmail){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(Email)
                .collection("Friends")
                .document(FriendEmail)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),(FriendEmail+" removed."),Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Friend removal error",Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.fragment_friends, container, false);
        FriendsLayout = ll.findViewById(R.id.FriendsLayout);
        SetupFragment();
        return ll;
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
