package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SavingEntity;
import com.example.mobile_basedsaccomanagement.data.entity.ShareEntity;
import com.example.mobile_basedsaccomanagement.data.entity.UserEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;
import com.example.mobile_basedsaccomanagement.data.repository.MemberRepository;
import com.example.mobile_basedsaccomanagement.data.repository.UserRepository;
import com.example.mobile_basedsaccomanagement.util.Constants;
import com.example.mobile_basedsaccomanagement.util.DateUtils;
import com.example.mobile_basedsaccomanagement.util.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final AppDatabase appDatabase;
    private final ExecutorService executorService;

    private final MutableLiveData<AuthResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<AuthResult> registerResult = new MutableLiveData<>();

    public static class AuthResult {
        public boolean success;
        public String message;
        public UserEntity user;
        public MemberEntity member;

        public AuthResult(boolean success, String message, UserEntity user, MemberEntity member) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.member = member;
        }
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        memberRepository = new MemberRepository(application);
        appDatabase = AppDatabase.getInstance(application);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<AuthResult> getLoginResult() { return loginResult; }
    public LiveData<AuthResult> getRegisterResult() { return registerResult; }

    public void login(String loginInput, String password) {
        executorService.execute(() -> {
            UserEntity user = null;
            MemberEntity member = null;

            if (loginInput.contains("@")) {
                user = appDatabase.userDao().findByEmail(loginInput);
            } else {
                member = appDatabase.memberDao().findByMemberNumber(loginInput);
                if (member != null) {
                    user = appDatabase.userDao().findById(member.getUserId());
                }
            }

            if (user == null) {
                loginResult.postValue(new AuthResult(false, "User account not found!", null, null));
                return;
            }

            boolean passwordMatch = PasswordUtils.verifyPassword(password, user.getPasswordHash(), user.getSalt());
            if (!passwordMatch) {
                loginResult.postValue(new AuthResult(false, "Invalid password! Please try again.", null, null));
                return;
            }

            if (Constants.ROLE_MEMBER.equalsIgnoreCase(user.getRole())) {
                if (member == null) {
                    member = appDatabase.memberDao().findByUserId(user.getId());
                }

                if (member != null) {
                    if (Constants.STATUS_PENDING.equalsIgnoreCase(member.getStatus())) {
                        loginResult.postValue(new AuthResult(false, "Your registration is PENDING administrator approval. Please check back later.", user, member));
                        return;
                    } else if (Constants.STATUS_REJECTED.equalsIgnoreCase(member.getStatus())) {
                        loginResult.postValue(new AuthResult(false, "Your SACCO account has been REJECTED or disabled. Contact SACCO admin.", user, member));
                        return;
                    }
                }
            }

            loginResult.postValue(new AuthResult(true, "Login Successful!", user, member));
        });
    }

    public void registerMember(String fullName, String nationalId, String phone, String email,
                               String gender, String dob, String address, String password) {
        executorService.execute(() -> {
            UserEntity existingUser = appDatabase.userDao().findByEmail(email);
            if (existingUser != null) {
                registerResult.postValue(new AuthResult(false, "An account with this email address already exists!", null, null));
                return;
            }

            String salt = PasswordUtils.generateSalt();
            String hash = PasswordUtils.hashPassword(password, salt);
            String currentDate = DateUtils.getCurrentDate();

            UserEntity newUser = new UserEntity(email, hash, salt, Constants.ROLE_MEMBER, currentDate);
            long userId = appDatabase.userDao().insertUser(newUser);
            newUser.setId(userId);

            String memberNo = "SACCO-" + (1000 + (int) (Math.random() * 9000));
            MemberEntity newMember = new MemberEntity(
                    userId, memberNo, fullName, nationalId, phone, gender, dob, address, null, Constants.STATUS_PENDING
            );
            long memberId = appDatabase.memberDao().insertMember(newMember);
            newMember.setId(memberId);

            // Initialize Savings Account
            appDatabase.savingDao().insertSaving(new SavingEntity(memberId, 0.0, 0.0, currentDate));

            // Initialize Share Capital Account
            appDatabase.shareDao().insertShare(new ShareEntity(memberId, 0, 100.0, 0.0, 100, "BELOW_MINIMUM"));

            registerResult.postValue(new AuthResult(true, "Registration Successful! Member No: " + memberNo + ". Pending admin approval.", newUser, newMember));
        });
    }
}
