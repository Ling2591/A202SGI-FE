package com.example.a202sgi_fe;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {
    private EditText taskTitleEditText, taskDescriptionEditText;
    private Button addTaskButton;
    private ListView taskListView;
    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;

    // TaskActivity.java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskTitleEditText = findViewById(R.id.taskTitleEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        addTaskButton = findViewById(R.id.addTaskButton);
        taskListView = findViewById(R.id.taskListView);
        dbHelper = new DatabaseHelper(this);
        taskList = new ArrayList<>();

        loadTasks();

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = taskTitleEditText.getText().toString();
                String description = taskDescriptionEditText.getText().toString();

                if (dbHelper.addTask(title, description, 1)) { // Replace 1 with actual user ID
                    Toast.makeText(TaskActivity.this, "Task Added", Toast.LENGTH_SHORT).show();
                    loadTasks();
                } else {
                    Toast.makeText(TaskActivity.this, "Failed to Add Task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadTasks() {
        taskList.clear();
        Cursor cursor = dbHelper.getTasks(1); // Replace 1 with actual user ID
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3) == 1
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        taskAdapter = new TaskAdapter(this, taskList, dbHelper); // Pass dbHelper to TaskAdapter
        taskListView.setAdapter(taskAdapter);
    }

}