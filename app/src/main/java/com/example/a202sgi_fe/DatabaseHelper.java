package com.example.a202sgi_fe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExpenseTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_EXPENSES = "expenses";

    // Expense table columns
    private static final String COLUMN_EXPENSE_ID = "expense_id";
    private static final String COLUMN_EXPENSE_AMOUNT = "amount";
    private static final String COLUMN_EXPENSE_CATEGORY = "category";
    private static final String COLUMN_EXPENSE_DATE = "date";
    private static final String COLUMN_EXPENSE_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EXPENSE_AMOUNT + " REAL,"
                + COLUMN_EXPENSE_CATEGORY + " TEXT,"
                + COLUMN_EXPENSE_DATE + " TEXT,"
                + COLUMN_EXPENSE_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // Add expense
    public boolean addExpense(double amount, String category, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPENSE_AMOUNT, amount);
        values.put(COLUMN_EXPENSE_CATEGORY, category);
        values.put(COLUMN_EXPENSE_DATE, date);
        values.put(COLUMN_EXPENSE_DESCRIPTION, description);

        long result = db.insert(TABLE_EXPENSES, null, values);
        return result != -1;
    }

    // Get all expenses
    public Cursor getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EXPENSES, null);
    }

    // Delete expense
    public boolean deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EXPENSES, COLUMN_EXPENSE_ID + "=?", new String[]{String.valueOf(expenseId)});
        return result > 0;
    }
}