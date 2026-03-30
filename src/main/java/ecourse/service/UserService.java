package ecourse.service;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ecourse.model.UserClass;
import ecourse.repository.UserRepository;

@Service
public class UserService {

    public static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 60 * 1000; // 1 phut (ms)

    // Regex: >=8 ky tu, >=1 chu hoa, >=1 chu thuong, >=1 so, >=1 ky tu dac biet
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("null")
    public void updateImage(UserClass userClass) {
        if (userClass.getImageFile() != null && !userClass.getImageFile().isEmpty()) {
            String fileName = StringUtils.cleanPath(userClass.getImageFile().getOriginalFilename());
            if (fileName.contains("..")) {
                System.out.println("Loi khong the luu file");
            }
            try {
                userClass.setUserImageUrl(Base64.getEncoder().encodeToString(userClass.getImageFile().getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            UserClass existingCourse = userRepository.findById(userClass.getUserId()).orElse(null);
            if (existingCourse != null) {
                userClass.setUserImageUrl(existingCourse.getUserImageUrl());
            }
        }
        userRepository.save(userClass);
    }

    public UserClass findByUsername(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(UserClass user) {
        userRepository.save(user);
    }

    public void updateFullname(String email, String fullname) {
        Optional<UserClass> userOpt = Optional.ofNullable(userRepository.findByEmail(email));
        if (userOpt.isPresent()) {
            UserClass user = userOpt.get();
            user.setFullname(fullname);
            userRepository.save(user);
        }
    }

    public void updateFullname(String fullname) {
        throw new UnsupportedOperationException("Unimplemented method 'updateFullname'");
    }

    public String getUserImageUrl(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'getUserImageUrl'");
    }

    public String uploadAvatar(String currentUser, MultipartFile avatarFile) {
        throw new UnsupportedOperationException("Unimplemented method 'uploadAvatar'");
    }

    public void updateUserImageUrl(String currentUser, String imageUrl) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUserImageUrl'");
    }

    // ===== PASSWORD POLICY =====
    public boolean isPasswordStrong(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }

    // ===== ACCOUNT LOCKOUT =====
    public void increaseFailedAttempts(UserClass user) {
        int newFail = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFail);
        if (newFail >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setLockTime(new Date());
        }
        userRepository.save(user);
    }

    public void resetFailedAttempts(String email) {
        UserClass user = userRepository.findByEmail(email);
        if (user != null) {
            user.setFailedAttempt(0);
            userRepository.save(user);
        }
    }

    public boolean unlockWhenTimeExpired(UserClass user) {
        if (user.getLockTime() != null) {
            long lockTimeInMillis = user.getLockTime().getTime();
            long currentTimeInMillis = System.currentTimeMillis();
            if (currentTimeInMillis - lockTimeInMillis > LOCK_TIME_DURATION) {
                user.setAccountNonLocked(true);
                user.setLockTime(null);
                user.setFailedAttempt(0);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}
