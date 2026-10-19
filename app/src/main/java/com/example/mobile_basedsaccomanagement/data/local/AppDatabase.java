package com.example.mobile_basedsaccomanagement.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mobile_basedsaccomanagement.data.dao.AdminDao;
import com.example.mobile_basedsaccomanagement.data.dao.AnnouncementDao;
import com.example.mobile_basedsaccomanagement.data.dao.LoanDao;
import com.example.mobile_basedsaccomanagement.data.dao.LoanRepaymentDao;
import com.example.mobile_basedsaccomanagement.data.dao.MemberDao;
import com.example.mobile_basedsaccomanagement.data.dao.NotificationDao;
import com.example.mobile_basedsaccomanagement.data.dao.SaccoProductDao;
import com.example.mobile_basedsaccomanagement.data.dao.SavingDao;
import com.example.mobile_basedsaccomanagement.data.dao.ShareDao;
import com.example.mobile_basedsaccomanagement.data.dao.TransactionDao;
import com.example.mobile_basedsaccomanagement.data.dao.UserDao;
import com.example.mobile_basedsaccomanagement.data.entity.AdminEntity;
import com.example.mobile_basedsaccomanagement.data.entity.AnnouncementEntity;
import com.example.mobile_basedsaccomanagement.data.entity.LoanEntity;
import com.example.mobile_basedsaccomanagement.data.entity.LoanRepaymentEntity;
import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.data.entity.NotificationEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SaccoProductEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;
import com.example.mobile_basedsaccomanagement.data.entity.ShareEntity;
import com.example.mobile_basedsaccomanagement.data.entity.TransactionEntity;
import com.example.mobile_basedsaccomanagement.data.entity.UserEntity;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.util.DateUtils;
import com.example.mobile_basedsaccomanagement.util.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {
        UserEntity.class,
        MemberEntity.class,
        AdminEntity.class,
        SaccoProductEntity.class,
        SavingEntity.class,
        LoanEntity.class,
        LoanRepaymentEntity.class,
        TransactionEntity.class,
        ShareEntity.class,
        NotificationEntity.class,
        AnnouncementEntity.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract UserDao userDao();
    public abstract MemberDao memberDao();
    public abstract AdminDao adminDao();
    public abstract SaccoProductDao saccoProductDao();
    public abstract SavingDao savingDao();
    public abstract LoanDao loanDao();
    public abstract LoanRepaymentDao loanRepaymentDao();
    public abstract TransactionDao transactionDao();
    public abstract ShareDao shareDao();
    public abstract NotificationDao notificationDao();
    public abstract AnnouncementDao announcementDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            Constants.DATABASE_NAME
                    )
                    .addCallback(sRoomDatabaseCallback)
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                if (INSTANCE != null) {
                    INSTANCE.runInTransaction(() -> seedInitialData(INSTANCE));
                }
            });
        }
    };

    private static void seedInitialData(AppDatabase db) {
        String currentDate = DateUtils.getCurrentDate();
        String currentTime = DateUtils.getCurrentTime();

        // 1. Seed SACCO Products
        SaccoProductDao productDao = db.saccoProductDao();
        long prodEmergency = productDao.insertProduct(new SaccoProductEntity("Emergency Loan", 5.0, 1000.0, 50000.0, 6, "FLAT"));
        long prodSchool = productDao.insertProduct(new SaccoProductEntity("School Fees Loan", 7.0, 5000.0, 150000.0, 12, "FLAT"));
        long prodDev = productDao.insertProduct(new SaccoProductEntity("Development Loan", 10.0, 10000.0, 500000.0, 24, "FLAT"));

        // 2. Seed Admin User
        UserDao userDao = db.userDao();
        String adminSalt = PasswordUtils.generateSalt();
        String adminHash = PasswordUtils.hashPassword("Admin123!", adminSalt);
        long adminUserId = userDao.insertUser(new UserEntity("admin@sacco.com", adminHash, adminSalt, Constants.ROLE_ADMIN, currentDate));

        AdminDao adminDao = db.adminDao();
        adminDao.insertAdmin(new AdminEntity(adminUserId, "SACCO Super Administrator", "HQ Management"));

        // 3. Seed Member 1 (John Doe - Approved)
        String salt1 = PasswordUtils.generateSalt();
        String hash1 = PasswordUtils.hashPassword("User123!", salt1);
        long uId1 = userDao.insertUser(new UserEntity("john@example.com", hash1, salt1, Constants.ROLE_MEMBER, currentDate));

        MemberDao memberDao = db.memberDao();
        long mId1 = memberDao.insertMember(new MemberEntity(uId1, "SACCO-1001", "John Doe", "34567890", "+254712345678", "Male", "1990-05-15", "Upperhill, Nairobi", null, Constants.STATUS_APPROVED));

        db.savingDao().insertSaving(new SavingEntity(mId1, 150000.0, 150000.0, currentDate));
        db.shareDao().insertShare(new ShareEntity(mId1, 150, 100.0, 15000.0, 100, "COMPLIANT"));

        LoanDao loanDao = db.loanDao();
        loanDao.insertLoan(new LoanEntity(mId1, prodDev, "LN-2025-001", 200000.0, 10.0, 220000.0, 18333.33, 12, "Business Expansion", "Monthly", 55000.0, 165000.0, Constants.STATUS_ACTIVE, currentDate, currentDate));

        db.transactionDao().insertTransaction(new TransactionEntity("TXN-" + System.currentTimeMillis() + "-1", mId1, "DEPOSIT", 25000.0, currentDate, currentTime, "Monthly Contribution Deposit", "COMPLETED"));
        db.notificationDao().insertNotification(new NotificationEntity(mId1, "Welcome to SACCO Mobile", "Your account SACCO-1001 is active. Explore financial services.", currentDate + " " + currentTime, false));

        // 4. Seed Member 2 (Jane Smith - Approved)
        String salt2 = PasswordUtils.generateSalt();
        String hash2 = PasswordUtils.hashPassword("User123!", salt2);
        long uId2 = userDao.insertUser(new UserEntity("jane@example.com", hash2, salt2, Constants.ROLE_MEMBER, currentDate));
        long mId2 = memberDao.insertMember(new MemberEntity(uId2, "SACCO-1002", "Jane Smith", "28901234", "+254722987654", "Female", "1994-08-20", "Westlands, Nairobi", null, Constants.STATUS_APPROVED));
        db.savingDao().insertSaving(new SavingEntity(mId2, 80000.0, 80000.0, currentDate));
        db.shareDao().insertShare(new ShareEntity(mId2, 100, 100.0, 10000.0, 100, "COMPLIANT"));

        // 5. Seed Member 3 (Peter Omondi - Pending)
        String salt3 = PasswordUtils.generateSalt();
        String hash3 = PasswordUtils.hashPassword("User123!", salt3);
        long uId3 = userDao.insertUser(new UserEntity("peter@example.com", hash3, salt3, Constants.ROLE_MEMBER, currentDate));
        long mId3 = memberDao.insertMember(new MemberEntity(uId3, "SACCO-1003", "Peter Omondi", "12345678", "+254733112233", "Male", "1995-11-10", "Kilimani, Nairobi", null, Constants.STATUS_PENDING));
        db.savingDao().insertSaving(new SavingEntity(mId3, 0.0, 0.0, currentDate));
        db.shareDao().insertShare(new ShareEntity(mId3, 0, 100.0, 0.0, 100, "BELOW_MINIMUM"));

        // 6. Seed Member 4 (Mary Wanjiku - Pending)
        String salt4 = PasswordUtils.generateSalt();
        String hash4 = PasswordUtils.hashPassword("User123!", salt4);
        long uId4 = userDao.insertUser(new UserEntity("mary@example.com", hash4, salt4, Constants.ROLE_MEMBER, currentDate));
        memberDao.insertMember(new MemberEntity(uId4, "SACCO-1004", "Mary Wanjiku", "45678901", "+254744556677", "Female", "1998-03-25", "Nyeri", null, Constants.STATUS_PENDING));

        // 7. Seed Member 5 (David Hassan - Rejected)
        String salt5 = PasswordUtils.generateSalt();
        String hash5 = PasswordUtils.hashPassword("User123!", salt5);
        long uId5 = userDao.insertUser(new UserEntity("david@example.com", hash5, salt5, Constants.ROLE_MEMBER, currentDate));
        memberDao.insertMember(new MemberEntity(uId5, "SACCO-1005", "David Hassan", "56789012", "+254755889900", "Male", "1988-12-05", "Mombasa", null, Constants.STATUS_REJECTED));

        // 8. Seed Announcement
        AnnouncementDao annDao = db.announcementDao();
        annDao.insertAnnouncement(new AnnouncementEntity("Annual General Meeting (AGM)", "All SACCO members are invited for the upcoming AGM at SACCO HQ.", currentDate, "SACCO Board"));
    }
}
