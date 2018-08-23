package com.example.derekmartin.whereyouatreloaded;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String Email = "";
    private FirebaseAuth FireBase;
    private FirebaseUser FireUser;
    private LinearLayout FriendsLayout;
    private FirebaseStorage storage;

    // TODO: Rename and change types of parameters
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);

        Toast.makeText(getActivity(), (Email+" friends : "+friends.size()), Toast.LENGTH_SHORT).show();
        for (int i = 0; i< friends.size();i++)
        {
            LinearLayout l = new LinearLayout(getView().getContext());
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setLayoutParams(params);

            StorageReference ref=storage.getReference();
            StorageReference path =ref.child(Email+"/");
            //TODO get pictures from friends

            TextView tv = new TextView(this.getContext());
            tv.setLayoutParams(params);
            tv.setTextSize(14);
            tv.setText(friends.get(i));
            l.addView(tv);


            Button b = new Button(l.getContext());
            b.setText("Remove");
            b.setContentDescription(friends.get((i)));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFriend(((Button)v).getContentDescription().toString());
                }
            });
            l.addView(b);


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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
