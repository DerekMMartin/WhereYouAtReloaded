package com.example.derekmartin.whereyouatreloaded;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.Duration;


public class SendingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private View MyView;
    private ListView listView;
    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    private String Email = "";
    private FirebaseAuth FireBase;
    private FirebaseUser FireUser;
    private LinearLayout FriendsLayout;
    private FirebaseStorage storage;

    public SendingFragment() {
        // Required empty public constructor
    }

    private void SetupFragment() {
        listView = MyView.findViewById(R.id.SendingScrollView);
        adapter = new ArrayAdapter<String>(MyView.getContext(), R.layout.list_item, R.id.SendingTextView, listItems);
        listView.setAdapter(adapter);

        FireBase = FirebaseAuth.getInstance();
        FireUser = FireBase.getCurrentUser();
        Email = FireUser.getEmail();
        storage = FirebaseStorage.getInstance();

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
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            listItems.add(doc.getId());
                        }
                        adapter.notifyDataSetChanged();

                    }
                });
    }

    private void SendPictures() {
        ArrayList<String> people = getSendingToPeople();
        StorageReference ref = storage.getReference();
        //TODO put sending in here..
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Where You At Reloaded").listFiles()[0];
        Uri b = Uri.fromFile(image);

        for (String p : people) {
            //add to users document to tell that they have a picture
            db.collection("Users").document(p).collection(Email).document(b.getLastPathSegment()).set(new HashMap<>());
            //upload picture to storage
            UploadTask task = ref.child(p + "/" + Email + "/" + b.getLastPathSegment()).putFile(b);
            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Sent", Toast.LENGTH_LONG).show();
                }
            });
        }

        //delete the file
        image.delete();

        FragmentManager fManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fTrans = fManager.beginTransaction();
        fTrans.replace(R.id.content_frame, new CameraEmbeddedFragment());
        fTrans.commit();
    }

    public ArrayList<String> getSendingToPeople() {
        ArrayList<String> People = new ArrayList<>();
        int Count = listView.getChildCount();

        for (int i = 0; i < Count; i++) {
            View ls = listView.getChildAt(i);
            if (((CheckBox) ls.findViewById(R.id.SendingCheckBox)).isChecked()) {
                People.add(((TextView) ls.findViewById(R.id.SendingTextView)).getText().toString());
            }

        }
//        Toast.makeText(MyView.getContext(), ("People: " + People.size()), Toast.LENGTH_LONG).show();
        return People;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyView = inflater.inflate(R.layout.fragment_sending, container, false);
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
