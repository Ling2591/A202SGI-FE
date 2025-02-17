// ExpenseAdapter.java
package com.example.a202sgi_fe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class ExpenseAdapter extends ArrayAdapter<Expense> {
    private Context context;
    private ArrayList<Expense> expenses;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        super(context, 0, expenses);
        this.context = context;
        this.expenses = expenses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Expense expense = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        }

        TextView amountTextView = convertView.findViewById(R.id.amountTextView);
        TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        Button editButton = convertView.findViewById(R.id.editButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        amountTextView.setText(String.format("RM %.2f", expense.getAmount()));
        categoryTextView.setText(expense.getCategory());
        dateTextView.setText(expense.getDate());
        descriptionTextView.setText(expense.getDescription());

        // Edit Button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).editExpense(expense);
                }
            }
        });

        // Delete Button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).deleteExpense(expense);
                }
            }
        });

        return convertView;
    }
}