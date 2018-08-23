package com.example.derekmartin.whereyouatreloaded;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;


public class SendingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private View MyView;
    private ListView listView;
    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    public SendingFragment() {
        // Required empty public constructor
    }

    public void SetupFragment(){
        listView =  MyView.findViewById(R.id.SendingScrollView);

        adapter = new ArrayAdapter<String>(MyView.getContext(),R.layout.list_item,R.id.SendingTextView,listItems);
        listView.setAdapter(adapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Toast.makeText(MyView.getContext(),"Adding now", Toast.LENGTH_LONG);
                        for (DocumentSnapshot doc: task.getResult()) {
                            listItems.add(doc.getId());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }



    public ArrayList<String> getSendingToPeople()
    {
        ArrayList<String> People = new ArrayList<>();
        People.addAll(listItems);
        return People;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyView =  inflater.inflate(R.layout.fragment_sending, container, false);
        SetupFragment();
        return MyView;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
