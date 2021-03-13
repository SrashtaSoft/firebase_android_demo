package com.uvpce.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uvpce.firebasedemo.Activity.AddActivity;
import com.uvpce.firebasedemo.Activity.SignInActivity;
import com.uvpce.firebasedemo.Adapter.TaskAdapter;
import com.uvpce.firebasedemo.Model.ModelTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton xBtnAdd;
    private List<ModelTask> taskData;
    String userId;
    RecyclerView recyclerView;
    DatabaseReference reference;
    TaskAdapter adapter;
    ProgressBar xProgressBar;
    TextView xTvNoTaskFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xBtnAdd = findViewById(R.id.xBtnAdd);
        xProgressBar = findViewById(R.id.xProgressBar);
        xTvNoTaskFound = findViewById(R.id.xTvNoTask);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        recyclerView = findViewById(R.id.xRvMain);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskData = new ArrayList<>();

        xProgressBar.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference("Task").child(userId);

        xBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    private void fetchData() {
        taskData.clear();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    xProgressBar.setVisibility(View.GONE);
                    xTvNoTaskFound.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        ModelTask list = dataSnapshot1.getValue(ModelTask.class);
                        taskData.add(list);
                    }
                    adapter = new TaskAdapter(MainActivity.this, taskData, userId);
                    recyclerView.setAdapter(adapter);
                }
                else {
                    xProgressBar.setVisibility(View.GONE);
                    xTvNoTaskFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}