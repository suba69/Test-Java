package test.demo.service;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import test.demo.dto.UserDto;
import test.demo.entity.User;
import test.demo.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUserFields(Long id, UserDto updateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));
        if (updateRequest.getEmail() != null) {
            existingUser.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getName() != null) {
            existingUser.setName(updateRequest.getName());
        }
        if (updateRequest.getSurname() != null) {
            existingUser.setSurname(updateRequest.getSurname());
        }
        if (updateRequest.getBirthDay() != null) {
            existingUser.setBirthDay(updateRequest.getBirthDay());
        }
        if (updateRequest.getNumber() != null) {
            existingUser.setNumber(updateRequest.getNumber());
        }
        return userRepository.save(existingUser);
    }

    @Override
    public User updateAllUserFields(Long id, UserDto updateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setEmail(updateRequest.getEmail());
        existingUser.setName(updateRequest.getName());
        existingUser.setSurname(updateRequest.getSurname());
        existingUser.setBirthDay(updateRequest.getBirthDay());
        existingUser.setNumber(updateRequest.getNumber());

        return userRepository.save(existingUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findUsersByBirthDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата 'От' должна быть перед датой 'Кому'");
        }

        List<User> users = userRepository.findUsersByBirthDateRange(from, to);
        return users;
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
