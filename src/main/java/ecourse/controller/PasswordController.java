package ecourse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.context.SecurityContextHolder;

import ecourse.model.UserClass;
import ecourse.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PasswordController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Để mã hóa mật khẩu

    // GET request để hiển thị trang đổi mật khẩu
    @GetMapping("/home/changePassword")
    public String showChangePasswordPage(Model model) {
        return "home/changePassword"; // Trả về trang changePassword.html
    }

    // POST request để thay đổi mật khẩu
    @PostMapping("/home/changePassword")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword, Model model) {
        try {
            // Lấy người dùng đang đăng nhập
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UserClass user = userService.findByUsername(username);

            // Kiểm tra mật khẩu hiện tại có đúng không
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                logger.debug("Mật khẩu hiện tại đúng.");
                if (newPassword.equals(currentPassword)) {
                    logger.error("Mật khẩu mới giống mật khẩu hiện tại.");
                    model.addAttribute("errorMessage", "Mật khẩu mới không được giống mật khẩu hiện tại.");
                    return "home/changePassword";
                }
                // ===== PASSWORD POLICY =====
                if (!userService.isPasswordStrong(newPassword)) {
                    model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@#$%^&+=!)");
                    return "home/changePassword";
                }
                if (newPassword.equals(confirmPassword)) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userService.save(user);
                    logger.info("User '{}' thay đổi mật khẩu thành công.", username);
                    return "redirect:/home/profile"; // Quay lại trang profile sau khi đổi mật khẩu thành công
                } else {
                    logger.error("Mật khẩu mới và mật khẩu xác nhận không khớp.");
                    model.addAttribute("errorMessage", "Mật khẩu mới và mật khẩu xác nhận không khớp.");
                    return "home/changePassword";
                }
            } else {
                logger.error("Mật khẩu hiện tại không đúng.");
                model.addAttribute("errorMessage", "Mật khẩu hiện tại không đúng.");
                return "home/changePassword";
            }
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi user '{}' thay đổi mật khẩu: {}", SecurityContextHolder.getContext().getAuthentication().getName(), e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi không xác định.");
            return "home/changePassword";
        }
    }
}
