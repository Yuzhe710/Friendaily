package com.example.comp90018_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.comp90018_project.R;
import com.example.comp90018_project.adapter.LikeAdapter;
import com.example.comp90018_project.adapter.MomentAdapter;
import com.example.comp90018_project.model.Like;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.comp90018_project.Activity.LoginActivity.USERID;

public class MyLikeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDB;
    private static final String TAG = "My Like";
    private FirebaseUser currentUser;
    private String USERID;
    private ImageView backMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth =  FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_my_like);
        currentUser = mAuth.getCurrentUser();
        backMain = findViewById(R.id.myLikeBackMain);
        if (currentUser == null){
            reload();
        }else {
            // TODO: 2021/10/23 Add construction of layout
            USERID = currentUser.getUid();
            myLikeView();
            backMain.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }
            );
        }
    }

    private void myLikeView() {
        DocumentReference docRef = mDB.collection("likes").document(USERID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d(TAG, "===============================TRY TO GET LIKE MOMENTS LIST ========================================");

                    Log.d(TAG, "===============================" +  task.getResult().getData().size() + "========================================");
                    ArrayList<Map<String, Object>> moments_list = (ArrayList<Map<String, Object>>) task.getResult().getData().get("my_like_moments");
                    if (moments_list != null) {
                        Log.d(TAG, "liked moments list get");
                        ListView MomentListview = (ListView) findViewById(R.id.like_momentsList);
                        List<Map<String, Object>> momentfound_list = new ArrayList<Map<String, Object>>();
                        for (int i=0; i < moments_list.size(); i++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            Map<String, Object> moment_map = moments_list.get(i);
                            String avatar_url = (String) moment_map.get("user_avatar_url");
                            if (avatar_url == null) {
                                map.put("avatar", "");
                            } else {
                                map.put("avatar", moment_map.get("user_avatar_url"));
                            }
                            map.put("uid", moment_map.get("uid"));
                            map.put("name", moment_map.get("username"));
                            map.put("content", moment_map.get("content"));
                            map.put("image", moment_map.get("image_download_url"));
                            map.put("timestamp", moment_map.get("date"));
                            momentfound_list.add(map);
                        }
                        LikeAdapter adapter = new LikeAdapter(MyLikeActivity.this);
                        adapter.setlikedMomentList(momentfound_list);
                        MomentListview.setAdapter(adapter);
                    }
                }
            }
        });
    }

    //if user does not log in, return to the Login
    private void reload(){
        Intent intent = new Intent();
        intent.setClass(MyLikeActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}