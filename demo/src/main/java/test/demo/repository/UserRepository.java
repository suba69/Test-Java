package test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import test.demo.entity.User;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    boolean existsByEmail(String email);

    void deleteById(Long id);

    @Query("SELECT u FROM User u WHERE u.birthDay BETWEEN :fromDate AND :toDate")
    List<User> findUsersByBirthDateRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
