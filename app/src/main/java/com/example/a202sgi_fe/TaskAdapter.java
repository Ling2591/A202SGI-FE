package com.example.a202sgi_fe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {
    private DatabaseHelper dbHelper;

    public TaskAdapter(Context context, ArrayList<Task> tasks, DatabaseHelper dbHelper) {
        super(context, 0, tasks);
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        TextView taskTitle = convertView.findViewById(R.id.taskTitle);
        TextView taskDescription = convertView.findViewById(R.id.taskDescription);
        CheckBox taskCompleted = convertView.findViewById(R.id.taskCompleted);

        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());
        taskCompleted.setChecked(task.isCompleted());

        // Handle checkbox click to mark task as completed
        taskCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                dbHelper.markTaskCompleted(task.getId(), isChecked);
            }
        });

        return convertView;
    }
}