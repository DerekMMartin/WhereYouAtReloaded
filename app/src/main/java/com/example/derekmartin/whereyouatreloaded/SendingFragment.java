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
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

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

    private void SetupFragment(){
        listView =  MyView.findViewById(R.id.SendingScrollView);
        adapter = new ArrayAdapter<String>(MyView.getContext(),R.layout.list_item,R.id.SendingTextView,listItems);
        listView.setAdapter(adapter);

        MyView.findViewById(R.id.SendingSendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPictures();
                getSendingToPeople();
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance()
                .getCurrentUser()
                .getEmail())
                .collection("Friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc: task.getResult()) {
                            listItems.add(doc.getId());
                        }
                        adapter.notifyDataSetChanged();

                    }
                });
    }

    private void SendPictures() {
        ArrayList<String> people = getSendingToPeople();
        //TODO put sending in here..
    }

    public ArrayList<String> getSendingToPeople(){
        ArrayList<String> People = new ArrayList<>();
        int Count = listView.getChildCount();

        for (int i = 0; i<Count;i++) {
            View ls = listView.getChildAt(i);
            if (((CheckBox) ls.findViewById(R.id.SendingCheckBox)).isChecked()) {
                People.add(((TextView) ls.findViewById(R.id.SendingTextView)).getText().toString());
            }

        }
        Toast.makeText(MyView.getContext(),("People: "+People.size()),Toast.LENGTH_LONG).show();
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
