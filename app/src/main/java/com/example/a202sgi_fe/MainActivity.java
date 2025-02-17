// MainActivity.java
package com.example.a202sgi_fe;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText amountEditText, dateEditText, descriptionEditText;
    private Spinner categorySpinner;
    private Button addExpenseButton;
    private ListView expenseListView;
    private Calendar calendar;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ArrayList<Expense> expenseList;
    private ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("expenses");

        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        expenseListView = findViewById(R.id.expenseListView);
        calendar = Calendar.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("expenses");

        // Set up Category Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set up DatePicker
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Load Expenses
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(this, expenseList);
        expenseListView.setAdapter(expenseAdapter);
        loadExpenses();

        // Add Expense Button
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateEditText();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateEditText() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    private void addExpense() {
        Toast.makeText(this, "ADD EXPENSE", Toast.LENGTH_SHORT).show();
        String amount = amountEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String date = dateEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        // Input validation
        if (amount.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amountValue;
        try {
            amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure user is logged in before accessing Firebase
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String expenseId = databaseReference.child(userId).push().getKey(); // Firebase generates unique ID

        Expense expense = new Expense(expenseId, amountValue, category, date, description);

        databaseReference.child(userId).child(expenseId).setValue(expense)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    } else {
                        Toast.makeText(this, "Failed to Add Expense", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadExpenses() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        expenseList.add(expense);
                    }
                }
                expenseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to Load Expenses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        amountEditText.setText("");
        dateEditText.setText("");
        descriptionEditText.setText("");
    }
    // MainActivity.java (continued)
    public void editExpense(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Expense");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_expense, null);
        EditText amountEditText = view.findViewById(R.id.amountEditText);
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        EditText dateEditText = view.findViewById(R.id.dateEditText);
        EditText descriptionEditText = view.findViewById(R.id.descriptionEditText);

        amountEditText.setText(String.valueOf(expense.getAmount()));
        dateEditText.setText(expense.getDate());
        descriptionEditText.setText(expense.getDescription());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(adapter.getPosition(expense.getCategory()));

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedAmount = amountEditText.getText().toString();
            String updatedCategory = categorySpinner.getSelectedItem().toString();
            String updatedDate = dateEditText.getText().toString();
            String updatedDescription = descriptionEditText.getText().toString();

            if (updatedAmount.isEmpty() || updatedDate.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double updatedAmountValue = Double.parseDouble(updatedAmount);
            if (updatedAmountValue <= 0) {
                Toast.makeText(MainActivity.this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = mAuth.getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("amount", updatedAmountValue);
            updates.put("category", updatedCategory);
            updates.put("date", updatedDate);
            updates.put("description", updatedDescription);

            databaseReference.child(userId).child(expense.getId()).updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Expense Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to Update Expense", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void deleteExpense(Expense expense) {
        // Show a confirmation dialog before deleting
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userId = mAuth.getCurrentUser().getUid();
                        databaseReference.child(userId).child(String.valueOf(expense.getId())).removeValue()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Expense Deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to Delete Expense", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}