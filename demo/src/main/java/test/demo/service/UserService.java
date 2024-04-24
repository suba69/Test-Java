package test.demo.service;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import test.demo.dto.UserDto;
import test.demo.entity.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface UserService {

    User createUser(User user);

    User updateUserFields(Long id, UserDto updateRequest);

    User updateAllUserFields(Long id, UserDto updateRequest);

    boolean existsByEmail(String email);

    List<User> findUsersByBirthDateRange(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to);

    void deleteById(Long id);

    boolean existsById(Long id);
}