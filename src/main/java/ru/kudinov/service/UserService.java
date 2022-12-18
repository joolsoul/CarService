package ru.kudinov.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.Role;
import ru.kudinov.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return true;
    }

    public boolean updateUser(User user) {
        if (!isUserCorrectlyUpdate(user)) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    //TODO Проверка номера телефона по регулярке
    public boolean isUserCorrectlyUpdate(User user) {
        return !user.getName().trim().isEmpty() && !user.getSurname().trim().isEmpty() &&
                !user.getPatronymic().trim().isEmpty() && !user.getPhoneNumber().trim().isEmpty();
    }

    public boolean isUserCorrectly(User user) {
        return !user.getName().equals("") && !user.getSurname().equals("") &&
                !user.getPatronymic().equals("") && !user.getPhoneNumber().equals("") &&
                !user.getUsername().equals("") && !user.getPassword().equals("");
    }

    public boolean checkLoginUniqueness(String username) {
        User user = userRepository.findByUsername(username);

        return user == null;
    }

    public boolean changeLogin(User user, String newLogin) {
        if (!checkLoginUniqueness(newLogin)) {
            return false;
        }
        user.setUsername(newLogin);
        userRepository.save(user);
        return true;
    }

    public boolean changePassword(String oldPassword, String newPassword, User user) {
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(User user) {
        if (userRepository.findById(user.getId()).isPresent()) {
            userRepository.deleteById(user.getId());

            return true;
        }
        return false;
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }


}
