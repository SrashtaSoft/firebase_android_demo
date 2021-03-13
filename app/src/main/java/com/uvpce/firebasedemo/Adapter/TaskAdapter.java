package com.uvpce.firebasedemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uvpce.firebasedemo.Activity.TaskDetailActivity;
import com.uvpce.firebasedemo.Model.ModelTask;
import com.uvpce.firebasedemo.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<ModelTask> taskData;
    private Context context;
    DatabaseReference reference;
    String userId;

    public TaskAdapter(Context context, List<ModelTask> taskData, String userId) {
        this.taskData = taskData;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelTask modelTask = taskData.get(position);
        holder.taskName.setText(modelTask.getTask_name());
        holder.date.setText(modelTask.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("taskId", modelTask.getId());
                context.startActivity(intent);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Task").child(userId).child(modelTask.getId());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Delete", "onCancelled", databaseError.toException());
                    }
                });
                taskData.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView taskName, date;
        private CircleImageView image;
        private ImageView edit, delete;

        public ViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.xTvTaskName);
            date = itemView.findViewById(R.id.xTvDate);
            edit = itemView.findViewById(R.id.xBtnEdit);
            delete = itemView.findViewById(R.id.xBtnDelete);
        }
    }
}
