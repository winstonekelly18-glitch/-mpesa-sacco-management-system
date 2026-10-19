package com.example.mobile_basedsaccomanagement.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mobile_basedsaccomanagement.models.Announcement;
import com.example.mobile_basedsaccomanagement.models.Loan;
import com.example.mobile_basedsaccomanagement.models.LoanRepayment;
import com.example.mobile_basedsaccomanagement.models.NotificationModel;
import com.example.mobile_basedsaccomanagement.models.Savings;
import com.example.mobile_basedsaccomanagement.models.ShareAccount;
import com.example.mobile_basedsaccomanagement.models.Transaction;
import com.example.mobile_basedsaccomanagement.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SACCOManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_SAVINGS = "savings";
    public static final String TABLE_LOANS = "loans";
    public static final String TABLE_LOAN_REPAYMENTS = "loan_repayments";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_SHARES = "shares";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_ANNOUNCEMENTS = "announcements";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "member_number TEXT UNIQUE, " +
                "full_name TEXT, " +
                "national_id TEXT UNIQUE, " +
                "phone_number TEXT, " +
                "email TEXT UNIQUE, " +
                "gender TEXT, " +
                "date_of_birth TEXT, " +
                "address TEXT, " +
                "password TEXT, " +
                "role TEXT, " +
                "status TEXT, " +
                "created_at TEXT);";

        // Savings table
        String createSavingsTable = "CREATE TABLE " + TABLE_SAVINGS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER UNIQUE, " +
                "balance REAL, " +
                "total_contributions REAL, " +
                "monthly_contribution REAL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id));";

        // Loans table
        String createLoansTable = "CREATE TABLE " + TABLE_LOANS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "loan_number TEXT UNIQUE, " +
                "user_id INTEGER, " +
                "loan_type TEXT, " +
                "requested_amount REAL, " +
                "interest_rate REAL, " +
                "total_repayment REAL, " +
                "monthly_installment REAL, " +
                "duration_months INTEGER, " +
                "purpose TEXT, " +
                "repayment_frequency TEXT, " +
                "amount_repaid REAL, " +
                "outstanding_balance REAL, " +
                "status TEXT, " +
                "issued_date TEXT, " +
                "next_repayment_date TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id));";

        // Loan Repayments table
        String createRepaymentsTable = "CREATE TABLE " + TABLE_LOAN_REPAYMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "loan_id INTEGER, " +
                "user_id INTEGER, " +
                "amount_paid REAL, " +
                "payment_date TEXT, " +
                "payment_method TEXT, " +
                "reference_code TEXT, " +
                "FOREIGN KEY(loan_id) REFERENCES " + TABLE_LOANS + "(id), " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id));";

        // Transactions table
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "transaction_id TEXT UNIQUE, " +
                "user_id INTEGER, " +
                "type TEXT, " +
                "amount REAL, " +
                "date TEXT, " +
                "time TEXT, " +
                "description TEXT, " +
                "status TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id));";

        // Shares table
        String createSharesTable = "CREATE TABLE " + TABLE_SHARES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER UNIQUE, " +
                "total_shares INTEGER, " +
                "share_value REAL, " +
                "total_contribution REAL, " +
                "min_required_shares INTEGER, " +
                "status TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id));";

        // Notifications table
        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "title TEXT, " +
                "message TEXT, " +
                "timestamp TEXT, " +
                "is_read INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id));";

        // Announcements table
        String createAnnouncementsTable = "CREATE TABLE " + TABLE_ANNOUNCEMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "date_posted TEXT, " +
                "posted_by TEXT);";

        db.beginTransaction();
        try {
            db.execSQL(createUsersTable);
            db.execSQL(createSavingsTable);
            db.execSQL(createLoansTable);
            db.execSQL(createRepaymentsTable);
            db.execSQL(createTransactionsTable);
            db.execSQL(createSharesTable);
            db.execSQL(createNotificationsTable);
            db.execSQL(createAnnouncementsTable);

            // Auto-seed initial demo accounts and SACCO data
            seedInitialData(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHARES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOAN_REPAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        String currentDate = getCurrentDate();
        String currentTime = getCurrentTime();

        // 1. Seed Admin
        ContentValues adminValues = new ContentValues();
        adminValues.put("member_number", "ADM-001");
        adminValues.put("full_name", "SACCO Super Administrator");
        adminValues.put("national_id", "10000000");
        adminValues.put("phone_number", "+254700000000");
        adminValues.put("email", "admin@sacco.com");
        adminValues.put("gender", "Other");
        adminValues.put("date_of_birth", "1985-01-01");
        adminValues.put("address", "SACCO HQ, Nairobi");
        adminValues.put("password", hashPassword("Admin123!"));
        adminValues.put("role", "ADMIN");
        adminValues.put("status", "APPROVED");
        adminValues.put("created_at", currentDate);
        db.insert(TABLE_USERS, null, adminValues);

        // 2. Seed Member 1 (John Doe)
        ContentValues user1 = new ContentValues();
        user1.put("member_number", "SACCO-1001");
        user1.put("full_name", "John Doe");
        user1.put("national_id", "34567890");
        user1.put("phone_number", "+254712345678");
        user1.put("email", "john@example.com");
        user1.put("gender", "Male");
        user1.put("date_of_birth", "1992-05-15");
        user1.put("address", "Upperhill, Nairobi");
        user1.put("password", hashPassword("User123!"));
        user1.put("role", "MEMBER");
        user1.put("status", "APPROVED");
        user1.put("created_at", currentDate);
        long userId1 = db.insert(TABLE_USERS, null, user1);

        // Seed Savings for John
        ContentValues sav1 = new ContentValues();
        sav1.put("user_id", userId1);
        sav1.put("balance", 125000.0);
        sav1.put("total_contributions", 150000.0);
        sav1.put("monthly_contribution", 10000.0);
        db.insert(TABLE_SAVINGS, null, sav1);

        // Seed Shares for John
        ContentValues share1 = new ContentValues();
        share1.put("user_id", userId1);
        share1.put("total_shares", 150);
        share1.put("share_value", 100.0);
        share1.put("total_contribution", 15000.0);
        share1.put("min_required_shares", 100);
        share1.put("status", "ACTIVE");
        db.insert(TABLE_SHARES, null, share1);

        // Seed Active Loan for John
        ContentValues loan1 = new ContentValues();
        loan1.put("loan_number", "LN-2025-001");
        loan1.put("user_id", userId1);
        loan1.put("loan_type", "Development Loan");
        loan1.put("requested_amount", 200000.0);
        loan1.put("interest_rate", 10.0);
        loan1.put("total_repayment", 220000.0);
        loan1.put("monthly_installment", 18333.33);
        loan1.put("duration_months", 12);
        loan1.put("purpose", "Business Expansion");
        loan1.put("repayment_frequency", "Monthly");
        loan1.put("amount_repaid", 55000.0);
        loan1.put("outstanding_balance", 165000.0);
        loan1.put("status", "ACTIVE");
        loan1.put("issued_date", currentDate);
        loan1.put("next_repayment_date", "2025-04-15");
        db.insert(TABLE_LOANS, null, loan1);

        // Seed Transactions for John
        ContentValues tx1 = new ContentValues();
        tx1.put("transaction_id", "TXN-" + System.currentTimeMillis() + "-1");
        tx1.put("user_id", userId1);
        tx1.put("type", "DEPOSIT");
        tx1.put("amount", 25000.0);
        tx1.put("date", currentDate);
        tx1.put("time", currentTime);
        tx1.put("description", "Monthly Share and Savings Deposit");
        tx1.put("status", "COMPLETED");
        db.insert(TABLE_TRANSACTIONS, null, tx1);

        // Seed Notification for John
        ContentValues notif1 = new ContentValues();
        notif1.put("user_id", userId1);
        notif1.put("title", "Welcome to SACCO Mobile");
        notif1.put("message", "Your account SACCO-1001 is active. Explore savings and loan services.");
        notif1.put("timestamp", currentDate + " " + currentTime);
        notif1.put("is_read", 0);
        db.insert(TABLE_NOTIFICATIONS, null, notif1);

        // Seed Announcement
        ContentValues ann = new ContentValues();
        ann.put("title", "Annual General Meeting (AGM) Notice");
        ann.put("content", "Members are invited for the upcoming AGM scheduled for end of month at SACCO HQ.");
        ann.put("date_posted", currentDate);
        ann.put("posted_by", "SACCO Board");
        db.insert(TABLE_ANNOUNCEMENTS, null, ann);
    }

    // --- SECURITY & PASSWORD UTILS ---
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // --- USER AUTHENTICATION & MANAGEMENT ---
    public User registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String memberNo = "SACCO-" + (1000 + (int)(Math.random() * 9000));
        values.put("member_number", memberNo);
        values.put("full_name", user.getFullName());
        values.put("national_id", user.getNationalId());
        values.put("phone_number", user.getPhoneNumber());
        values.put("email", user.getEmail());
        values.put("gender", user.getGender());
        values.put("date_of_birth", user.getDateOfBirth());
        values.put("address", user.getAddress());
        values.put("password", hashPassword(user.getPassword()));
        values.put("role", "MEMBER");
        values.put("status", "APPROVED"); // Default approved for smooth demo
        values.put("created_at", getCurrentDate());

        long id = db.insert(TABLE_USERS, null, values);
        if (id != -1) {
            user.setId(id);
            user.setMemberNumber(memberNo);
            user.setRole("MEMBER");
            user.setStatus("APPROVED");

            // Initialize Savings Account
            ContentValues sav = new ContentValues();
            sav.put("user_id", id);
            sav.put("balance", 0.0);
            sav.put("total_contributions", 0.0);
            sav.put("monthly_contribution", 0.0);
            db.insert(TABLE_SAVINGS, null, sav);

            // Initialize Share Account
            ContentValues share = new ContentValues();
            share.put("user_id", id);
            share.put("total_shares", 0);
            share.put("share_value", 100.0);
            share.put("total_contribution", 0.0);
            share.put("min_required_shares", 100);
            share.put("status", "INCOMPLETE");
            db.insert(TABLE_SHARES, null, share);

            // Generate Welcome Notification
            addNotification(id, "Registration Successful", "Welcome to SACCO Mobile! Your Member No is " + memberNo);
            return user;
        }
        return null;
    }

    public User authenticateUser(String loginInput, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE (email=? OR member_number=?) AND password=?";
        Cursor cursor = db.rawQuery(query, new String[]{loginInput, loginInput, hashedPassword});

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public boolean updateUserProfile(long userId, String phone, String email, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_number", phone);
        values.put("email", email);
        values.put("address", address);
        return db.update(TABLE_USERS, values, "id=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean updatePassword(long userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        String hashedOld = hashPassword(oldPassword);
        Cursor cursor = db.query(TABLE_USERS, null, "id=? AND password=?", new String[]{String.valueOf(userId), hashedOld}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("password", hashPassword(newPassword));
            db.update(TABLE_USERS, values, "id=?", new String[]{String.valueOf(userId)});
            return true;
        }
        cursor.close();
        return false;
    }

    public List<User> getAllMembers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "role='MEMBER'", null, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToUser(cursor));
        }
        cursor.close();
        return list;
    }

    public boolean updateUserStatus(long userId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int rows = db.update(TABLE_USERS, values, "id=?", new String[]{String.valueOf(userId)});
        if (rows > 0) {
            addNotification(userId, "Account Status Updated", "Your SACCO account status is now: " + status);
            return true;
        }
        return false;
    }

    private User cursorToUser(Cursor cursor) {
        User u = new User();
        u.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        u.setMemberNumber(cursor.getString(cursor.getColumnIndexOrThrow("member_number")));
        u.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
        u.setNationalId(cursor.getString(cursor.getColumnIndexOrThrow("national_id")));
        u.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
        u.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        u.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
        u.setDateOfBirth(cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")));
        u.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        u.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        u.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
        u.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        u.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        return u;
    }

    // --- SAVINGS MODULE ---
    public Savings getSavingsByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SAVINGS, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        Savings s = null;
        if (cursor.moveToFirst()) {
            s = new Savings(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("balance")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("total_contributions")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("monthly_contribution"))
            );
        } else {
            // Default empty
            s = new Savings(0, userId, 0.0, 0.0, 0.0);
        }
        cursor.close();
        return s;
    }

    public boolean depositSavings(long userId, double amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        Savings current = getSavingsByUserId(userId);
        double newBalance = current.getBalance() + amount;
        double newTotal = current.getTotalContributions() + amount;

        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        values.put("total_contributions", newTotal);
        values.put("monthly_contribution", amount);

        int updated = db.update(TABLE_SAVINGS, values, "user_id=?", new String[]{String.valueOf(userId)});
        if (updated > 0) {
            addTransaction(userId, "DEPOSIT", amount, description, "COMPLETED");
            addNotification(userId, "Deposit Received", "Successfully deposited KES " + String.format(Locale.getDefault(), "%.2f", amount) + " into savings.");
            return true;
        }
        return false;
    }

    public boolean withdrawSavings(long userId, double amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        Savings current = getSavingsByUserId(userId);
        if (current.getBalance() < amount) return false;

        double newBalance = current.getBalance() - amount;
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);

        int updated = db.update(TABLE_SAVINGS, values, "user_id=?", new String[]{String.valueOf(userId)});
        if (updated > 0) {
            addTransaction(userId, "WITHDRAWAL", amount, description, "COMPLETED");
            addNotification(userId, "Withdrawal Processed", "Successfully withdrew KES " + String.format(Locale.getDefault(), "%.2f", amount) + " from savings.");
            return true;
        }
        return false;
    }

    // --- LOAN MANAGEMENT MODULE ---
    public Loan applyForLoan(long userId, String loanType, double amount, int durationMonths, String purpose, String repaymentFrequency) {
        SQLiteDatabase db = this.getWritableDatabase();

        double interestRate = 10.0; // 10% standard rate
        if ("Emergency Loan".equalsIgnoreCase(loanType)) interestRate = 5.0;
        if ("School Fees Loan".equalsIgnoreCase(loanType)) interestRate = 7.0;

        double totalInterest = amount * (interestRate / 100.0);
        double totalRepayment = amount + totalInterest;
        double monthlyInstallment = totalRepayment / durationMonths;

        String loanNo = "LN-" + System.currentTimeMillis() % 100000;

        ContentValues values = new ContentValues();
        values.put("loan_number", loanNo);
        values.put("user_id", userId);
        values.put("loan_type", loanType);
        values.put("requested_amount", amount);
        values.put("interest_rate", interestRate);
        values.put("total_repayment", totalRepayment);
        values.put("monthly_installment", monthlyInstallment);
        values.put("duration_months", durationMonths);
        values.put("purpose", purpose);
        values.put("repayment_frequency", repaymentFrequency);
        values.put("amount_repaid", 0.0);
        values.put("outstanding_balance", totalRepayment);
        values.put("status", "PENDING");
        values.put("issued_date", getCurrentDate());
        values.put("next_repayment_date", "Pending Approval");

        long id = db.insert(TABLE_LOANS, null, values);
        if (id != -1) {
            addNotification(userId, "Loan Application Submitted", "Your loan application " + loanNo + " of KES " + amount + " is under review.");
            return getLoanById(id);
        }
        return null;
    }

    public Loan getLoanById(long loanId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOANS, null, "id=?", new String[]{String.valueOf(loanId)}, null, null, null);
        Loan l = null;
        if (cursor.moveToFirst()) {
            l = cursorToLoan(cursor);
        }
        cursor.close();
        return l;
    }

    public List<Loan> getLoansByUserId(long userId) {
        List<Loan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOANS, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToLoan(cursor));
        }
        cursor.close();
        return list;
    }

    public List<Loan> getAllLoans() {
        List<Loan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOANS, null, null, null, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToLoan(cursor));
        }
        cursor.close();
        return list;
    }

    public boolean updateLoanStatus(long loanId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        Loan loan = getLoanById(loanId);
        if (loan == null) return false;

        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        if ("APPROVED".equalsIgnoreCase(newStatus) || "ACTIVE".equalsIgnoreCase(newStatus)) {
            values.put("status", "ACTIVE");
            values.put("issued_date", getCurrentDate());
            values.put("next_repayment_date", getCurrentDate());
            // Disburse loan to user savings balance
            depositSavings(loan.getUserId(), loan.getRequestedAmount(), "Loan Disbursement (" + loan.getLoanNumber() + ")");
            addTransaction(loan.getUserId(), "LOAN_DISBURSEMENT", loan.getRequestedAmount(), "Disbursement for Loan " + loan.getLoanNumber(), "COMPLETED");
            addNotification(loan.getUserId(), "Loan Approved!", "Your loan " + loan.getLoanNumber() + " of KES " + loan.getRequestedAmount() + " has been approved & disbursed.");
        } else if ("REJECTED".equalsIgnoreCase(newStatus)) {
            addNotification(loan.getUserId(), "Loan Application Status", "Your loan application " + loan.getLoanNumber() + " was rejected.");
        }

        return db.update(TABLE_LOANS, values, "id=?", new String[]{String.valueOf(loanId)}) > 0;
    }

    // --- LOAN REPAYMENT ---
    public boolean makeLoanRepayment(long loanId, long userId, double amountPaid, String paymentMethod, String refCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Loan loan = getLoanById(loanId);
        if (loan == null || !"ACTIVE".equalsIgnoreCase(loan.getStatus())) return false;

        double newAmountRepaid = loan.getAmountRepaid() + amountPaid;
        double newOutstanding = loan.getOutstandingBalance() - amountPaid;
        if (newOutstanding < 0) newOutstanding = 0;

        String newStatus = newOutstanding == 0 ? "PAID" : "ACTIVE";

        ContentValues loanValues = new ContentValues();
        loanValues.put("amount_repaid", newAmountRepaid);
        loanValues.put("outstanding_balance", newOutstanding);
        loanValues.put("status", newStatus);

        db.update(TABLE_LOANS, loanValues, "id=?", new String[]{String.valueOf(loanId)});

        // Record repayment record
        ContentValues repValues = new ContentValues();
        repValues.put("loan_id", loanId);
        repValues.put("user_id", userId);
        repValues.put("amount_paid", amountPaid);
        repValues.put("payment_date", getCurrentDate());
        repValues.put("payment_method", paymentMethod);
        repValues.put("reference_code", refCode);
        db.insert(TABLE_LOAN_REPAYMENTS, null, repValues);

        // Record Transaction & Notification
        addTransaction(userId, "LOAN_REPAYMENT", amountPaid, "Repayment for Loan " + loan.getLoanNumber() + " [Ref: " + refCode + "]", "COMPLETED");
        addNotification(userId, "Loan Repayment Received", "Repayment of KES " + amountPaid + " received for " + loan.getLoanNumber() + ". Outstanding balance: KES " + newOutstanding);

        return true;
    }

    public List<LoanRepayment> getRepaymentsForLoan(long loanId) {
        List<LoanRepayment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOAN_REPAYMENTS, null, "loan_id=?", new String[]{String.valueOf(loanId)}, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(new LoanRepayment(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("loan_id")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("amount_paid")),
                    cursor.getString(cursor.getColumnIndexOrThrow("payment_date")),
                    cursor.getString(cursor.getColumnIndexOrThrow("payment_method")),
                    cursor.getString(cursor.getColumnIndexOrThrow("reference_code"))
            ));
        }
        cursor.close();
        return list;
    }

    private Loan cursorToLoan(Cursor cursor) {
        return new Loan(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("loan_number")),
                cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("loan_type")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("requested_amount")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("interest_rate")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("total_repayment")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("monthly_installment")),
                cursor.getInt(cursor.getColumnIndexOrThrow("duration_months")),
                cursor.getString(cursor.getColumnIndexOrThrow("purpose")),
                cursor.getString(cursor.getColumnIndexOrThrow("repayment_frequency")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("amount_repaid")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("outstanding_balance")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getString(cursor.getColumnIndexOrThrow("issued_date")),
                cursor.getString(cursor.getColumnIndexOrThrow("next_repayment_date"))
        );
    }

    // --- TRANSACTIONS MODULE ---
    public void addTransaction(long userId, String type, double amount, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("transaction_id", "TXN-" + System.currentTimeMillis());
        values.put("user_id", userId);
        values.put("type", type);
        values.put("amount", amount);
        values.put("date", getCurrentDate());
        values.put("time", getCurrentTime());
        values.put("description", description);
        values.put("status", status);
        db.insert(TABLE_TRANSACTIONS, null, values);
    }

    public List<Transaction> getTransactionsByUserId(long userId) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToTransaction(cursor));
        }
        cursor.close();
        return list;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToTransaction(cursor));
        }
        cursor.close();
        return list;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        return new Transaction(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("transaction_id")),
                cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("type")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("time")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("status"))
        );
    }

    // --- SHARES MODULE ---
    public ShareAccount getSharesByUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SHARES, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        ShareAccount s = null;
        if (cursor.moveToFirst()) {
            s = new ShareAccount(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("total_shares")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("share_value")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("total_contribution")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("min_required_shares")),
                    cursor.getString(cursor.getColumnIndexOrThrow("status"))
            );
        } else {
            s = new ShareAccount(0, userId, 0, 100.0, 0.0, 100, "INCOMPLETE");
        }
        cursor.close();
        return s;
    }

    public boolean buyShares(long userId, int numberOfShares) {
        SQLiteDatabase db = this.getWritableDatabase();
        ShareAccount current = getSharesByUserId(userId);
        double shareValue = 100.0;
        double cost = numberOfShares * shareValue;

        int newTotalShares = current.getTotalShares() + numberOfShares;
        double newTotalContrib = current.getTotalContribution() + cost;
        String newStatus = newTotalShares >= current.getMinRequiredShares() ? "ACTIVE" : "INCOMPLETE";

        ContentValues values = new ContentValues();
        values.put("total_shares", newTotalShares);
        values.put("total_contribution", newTotalContrib);
        values.put("status", newStatus);

        int updated = db.update(TABLE_SHARES, values, "user_id=?", new String[]{String.valueOf(userId)});
        if (updated > 0) {
            addTransaction(userId, "SHARE_CONTRIBUTION", cost, "Purchased " + numberOfShares + " SACCO Shares", "COMPLETED");
            addNotification(userId, "Share Contribution", "Purchased " + numberOfShares + " shares worth KES " + cost + ". Total shares: " + newTotalShares);
            return true;
        }
        return false;
    }

    // --- NOTIFICATIONS MODULE ---
    public void addNotification(long userId, String title, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("message", message);
        values.put("timestamp", getCurrentDate() + " " + getCurrentTime());
        values.put("is_read", 0);
        db.insert(TABLE_NOTIFICATIONS, null, values);
    }

    public List<NotificationModel> getNotificationsForUser(long userId) {
        List<NotificationModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(new NotificationModel(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("message")),
                    cursor.getString(cursor.getColumnIndexOrThrow("timestamp")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_read")) == 1
            ));
        }
        cursor.close();
        return list;
    }

    public int getUnreadNotificationCount(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS + " WHERE user_id=? AND is_read=0", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public void markNotificationsAsRead(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        db.update(TABLE_NOTIFICATIONS, values, "user_id=?", new String[]{String.valueOf(userId)});
    }

    // --- ANNOUNCEMENTS ---
    public boolean addAnnouncement(String title, String content, String postedBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("date_posted", getCurrentDate());
        values.put("posted_by", postedBy);
        long id = db.insert(TABLE_ANNOUNCEMENTS, null, values);
        if (id != -1) {
            // Notify all members
            List<User> members = getAllMembers();
            for (User u : members) {
                addNotification(u.getId(), "New Announcement: " + title, content);
            }
            return true;
        }
        return false;
    }

    public List<Announcement> getAllAnnouncements() {
        List<Announcement> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANNOUNCEMENTS, null, null, null, null, null, "id DESC");
        while (cursor.moveToNext()) {
            list.add(new Announcement(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date_posted")),
                    cursor.getString(cursor.getColumnIndexOrThrow("posted_by"))
            ));
        }
        cursor.close();
        return list;
    }

    // --- ADMIN STATS & METRICS ---
    public double getTotalSACCOSavings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(balance) FROM " + TABLE_SAVINGS, null);
        double total = 0.0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public double getTotalActiveLoansAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(outstanding_balance) FROM " + TABLE_LOANS + " WHERE status='ACTIVE'", null);
        double total = 0.0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public int getPendingLoansCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LOANS + " WHERE status='PENDING'", null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}
