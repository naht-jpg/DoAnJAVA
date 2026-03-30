package ecourse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ecourse.model.UserClass;
import ecourse.repository.UserRepository;
@Service
public class UserServiceImpl implements UserInterface {
    @Autowired
    private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public UserClass createUser(UserClass user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserImageUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        return userRepository.save(user);
    }
    @Override
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
