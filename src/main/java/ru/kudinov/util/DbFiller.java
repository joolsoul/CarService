package ru.kudinov.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kudinov.model.User;
import ru.kudinov.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class DbFiller {

    @Autowired
    private UserService userService;

    public DbFiller(UserService userService) {
        this.userService = userService;
    }

    public void addUsers() {
        try {
            List<String> passwords = getDetails("src/main/resources/files/password");
            List<String> emails = getDetails("src/main/resources/files/email");
            List<String> usernames = getDetails("src/main/resources/files/username");
            List<String> phones = getDetails("src/main/resources/files/phone");
            List<List<String>> data = getData("src/main/resources/files/data");

            if(passwords.size() != 0 && emails.size() != 0 && usernames.size() != 0 &&
                    phones.size() != 0 && data.size() != 0) {

                for (int i = 0; i < 1000; i++) {
                    String surname = data.get(i).get(0);
                    String name = data.get(i).get(1);
                    String patronymic = data.get(i).get(2);
                    String email = emails.get(i);
                    String password = passwords.get(i);
                    String username = usernames.get(i);
                    String phone = phones.get(i);

                    User user = new User();
                    user.setName(name);
                    user.setSurname(surname);
                    user.setPatronymic(patronymic);
                    user.setPhoneNumber(phone);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(password);

                    userService.saveUser(user);

                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private List<String> getDetails(String filePath) throws IOException {

        return Files.readAllLines(Paths.get(filePath));
    }

    private List<List<String>> getData(String filePath) throws IOException {

        List<String> temp = getDetails(filePath);
        List<List<String>> data = new LinkedList<>();
        for (String s:
             temp) {
            data.add(Arrays.asList(s.split(" ")));
        }

        return data;
    }


}
