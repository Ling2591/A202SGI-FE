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

public class MainActivity extends AppCompatActivity {
    private EditText amountEditText, categoryEditText, dateEditText, descriptionEditText;
    private Button addExpenseButton;
    private ListView expenseListView;
    private DatabaseHelper dbHelper;
    private ArrayList<Expense> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountEditText = findViewById(R.id.amountEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        expenseListView = findViewById(R.id.expenseListView);
        dbHelper = new DatabaseHelper(this);
        expenseList = new ArrayList<>();

        loadExpenses();

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = Double.parseDouble(amountEditText.getText().toString());
                String category = categoryEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                if (dbHelper.addExpense(amount, category, date, description)) {
                    Toast.makeText(MainActivity.this, "Expense Added", Toast.LENGTH_SHORT).show();
                    loadExpenses();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to Add Expense", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadExpenses() {
        expenseList.clear();
        Cursor cursor = dbHelper.getAllExpenses();
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        cursor.getInt(0),
                        cursor.getDouble(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        ExpenseAdapter adapter = new ExpenseAdapter(this, expenseList);
        expenseListView.setAdapter(adapter);
    }
}