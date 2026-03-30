package ecourse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import ecourse.model.UserClass;
import ecourse.repository.CategoriesRepository;
import ecourse.repository.CourseRepository;
import ecourse.repository.TeacherRepository;
import ecourse.service.UserInterface;
import ecourse.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class LayoutController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    private UserInterface userService;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired 
    private CategoriesRepository categoryRepository;
    @Autowired
    private UserService userServiceImpl;
    @GetMapping("/home/course")
  
    public String course(Model model) {
        model.addAttribute("list", courseRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "home/course";
    }

    @GetMapping("/home/index")
    public String index() {
        return "home/index";
    }


    @GetMapping("/home/about")
    public String about() {
        return "home/about";
    }

    @GetMapping("/home/blog")
    public String blog() {
        return "home/blog";
    }

    @GetMapping("/home/single")
    public String single() {
        return "home/single";
    }

    @GetMapping("/home/teacher")
    public String teacher(Model model) {
        model.addAttribute("teacher", teacherRepository.findAll());
        return "home/teacher";
    }

    @GetMapping("/home/signin")
    public String login() {
        return "home/login";
    }
    @GetMapping("/home/register")
    public String register() {
        return "home/register";
    }
    @PostMapping("/createUser")
    public String createUser(@ModelAttribute UserClass user, HttpSession session) {

        // ===== PASSWORD POLICY =====
        if (!userServiceImpl.isPasswordStrong(user.getPassword())) {
            session.setAttribute("msg", "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@#$%^&+=!)");
            return "redirect:/home/register?error";
        }

        boolean f = userService.checkEmail(user.getEmail());
        if (f) {
            session.setAttribute("msg", "Email đã tồn tại");
            return "redirect:/home/register?error";
        } else {
            UserClass userClass = userService.createUser(user);
            session.setAttribute("msg", userClass != null ? "Đăng ký thành công" : "Đăng ký thất bại");
        }
        return "redirect:/home/register?success";
    }

    @PostMapping("/clearSessionMsg")
    public void clearSessionMsg(HttpSession session) {
        session.removeAttribute("msg");
    }

    @GetMapping("/home/forgot-password")
    public String forgotPassword() {
        return "home/forgot-password";
    }
    @GetMapping("/home/profile")
    public String profile() {
        return "home/profile";
    }
    
}
