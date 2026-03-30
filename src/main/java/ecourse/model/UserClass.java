package ecourse.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity(name = "users")
public class UserClass {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private short userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "created_at", nullable = true)
    private Date createdAt;
    @Enumerated(EnumType.STRING) // Ánh xạ enum thành chuỗi (ADMIN, SUPERADMIN)
    @Column(name = "role")
    private Role role;
    @Transient
    private MultipartFile imageFile;
    @Column(name = "user_image_url")
    private String userImageUrl;
    @Column(name = "fullname")
    private String fullname;

    @Column(name = "failed_attempt")
    private int failedAttempt;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Column(name = "lock_time")
    private java.util.Date lockTime;
    // nối phiếu học
    @OneToMany(mappedBy = "clazz")
    private List<Enrollments> enroll;

    public List<Enrollments> getEnoll() {
        return enroll;
    }

    public void setEnroll(List<Enrollments> enroll) {
        this.enroll = enroll;
    }
    //nối xong
    //nối với bảng order
    @OneToMany(mappedBy = "user")
    private List<Order> order;
    public List<Order> getOrder(){
        return order;
    }
    public void setOrder(List<Order> order){
        this.order = order;
    }
    //nối xong
    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Enrollments> getEnroll() {
        return enroll;
    }

    @Transient
    private MultipartFile userImage;

    // Getters and Setters
    public short getUserId() {
        return userId;
    }

    public void setUserId(short userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public MultipartFile getUserImage() {
        return userImage;
    }

    public void setUserImage(MultipartFile userImage) {
        this.userImage = userImage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Date.valueOf(LocalDate.now());
        }
        if (this.role == null) {
            this.role = Role.user; // Thiết lập giá trị mặc định cho role là USER
        }
    }

    public void setImageUrl(String imageUrl) {
        throw new UnsupportedOperationException("Unimplemented method 'setImageUrl'");
    }

    public String getImageUrl() {
        throw new UnsupportedOperationException("Unimplemented method 'getImageUrl'");
    }

    public void setProfilePicture(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'setProfilePicture'");
    }

    // @Service
    // public class UserService {

    //     @Autowired
    //     private UserRepository userRepository;

    //     @Transactional
    //     public void updateFullname(String username, String fullname) {
    //         UserClass user = userRepository.findByUsername(username);
    //         if (user != null) {
    //             user.setFullname(fullname);
    //             userRepository.save(user);
    //         }
    //     }
    // }

}