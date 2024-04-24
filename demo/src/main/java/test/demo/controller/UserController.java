package test.demo.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.demo.dto.DateDto;
import test.demo.dto.UserDto;
import test.demo.entity.User;
import test.demo.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${user.age.limit}")
    private int userAgeLimit;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {

        LocalDateTime birthDate = user.getBirthDay();
        LocalDateTime birthDateTime = birthDate.withHour(0).withMinute(0).withSecond(0).withNano(0);

        LocalDateTime currentDate = LocalDate.now().atStartOfDay();

        Period age = Period.between(birthDateTime.toLocalDate(), currentDate.toLocalDate());
        if (age.getYears() < userAgeLimit) {
            throw new IllegalArgumentException("User must be older " + userAgeLimit + " age.");
        }

        if (userService.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        return userService.createUser(user);
    }

    @DeleteMapping("delete-user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        if (!userService.existsById(id)) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }
        userService.deleteById(id);
        return ResponseEntity.ok("User with ID " + id + " deleted");
    }

    @PutMapping("/update/{id}")
    public User updateUserFields(@PathVariable Long id, @RequestBody UserDto updateRequest) {
        return userService.updateUserFields(id, updateRequest);
    }

    @PutMapping("/update-all/{id}")
    public User updateAllUserFields(@PathVariable Long id, @RequestBody UserDto updateRequest) {
        return userService.updateAllUserFields(id, updateRequest);
    }

    @PostMapping("/search")
    public List<User> searchUsersByBirthDateRange(@RequestBody DateDto request) {
        LocalDateTime from = request.getFrom();
        LocalDateTime to = request.getTo();

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("The 'From' date must come before the 'To' date");
        }

        return userService.findUsersByBirthDateRange(from, to);
    }
}