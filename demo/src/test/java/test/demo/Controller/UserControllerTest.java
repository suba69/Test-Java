package test.demo.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import test.demo.dto.DateDto;
import test.demo.dto.UserDto;
import test.demo.entity.User;
import test.demo.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testCreateUserValid() throws Exception {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setName("Bob");
        user.setSurname("Mike");
        LocalDateTime birthDay = LocalDateTime.of(2003, 1, 1, 0, 0);
        user.setBirthDay(birthDay);
        user.setNumber("+3843849394");

        Mockito.when(userService.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Bob"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Mike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDay").value("2003-01-01T00:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("+3843849394"));

        Mockito.verify(userService).existsByEmail("test@gmail.com");
        Mockito.verify(userService).createUser(user);
    }
    @Test
    public void testCreateUserUnderAge() throws Exception {
        User user = new User();
        user.setEmail("test@gmail.com");
        LocalDateTime birthDay = LocalDateTime.of(2003, 1, 1, 0, 0);
        user.setBirthDay(birthDay);

        Mockito.when(userService.existsByEmail(Mockito.anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@gmail.com\",\"birthDay\":\"" + user.getBirthDay() + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).createUser(Mockito.any(User.class));
    }

    @Test
    public void testCreateUserEmailExists() throws Exception {
        User user = new User();
        user.setEmail("test@email.com");
        LocalDateTime birthDay = LocalDateTime.of(2003, 1, 1, 0, 0);
        user.setBirthDay(birthDay);

        Mockito.when(userService.existsByEmail("test@email.com")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@email.com\",\"birthDay\":\"" + user.getBirthDay() + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).createUser(Mockito.any(User.class));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;

        Mockito.when(userService.existsById(userId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/user/delete-user/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).existsById(userId);
        Mockito.verify(userService).deleteById(userId);
    }

    @Test
    public void testUpdateUserFields() throws Exception {
        Long userId = 1L;
        UserDto updateRequest = new UserDto();
        updateRequest.setEmail("new-email@email.com");

        updateRequest.setName("New Name");
        updateRequest.setSurname("New Surname");

        User user = new User();
        user.setId(userId);
        user.setEmail("old-email@email.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail(updateRequest.getEmail());
        updatedUser.setName(updateRequest.getName());
        updatedUser.setSurname(updateRequest.getSurname());

        Mockito.when(userService.updateUserFields(userId, updateRequest)).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updateRequest.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateRequest.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(updateRequest.getSurname()));

        Mockito.verify(userService).updateUserFields(userId, updateRequest);
    }


    @Test
    public void testUpdateAllUserFields() throws Exception {

        Long userId = 1L;

        UserDto updateRequest = new UserDto();
        updateRequest.setEmail("new-email@email.com");
        updateRequest.setName("New Name");
        updateRequest.setSurname("New Surname");
        LocalDateTime birthDay = LocalDateTime.of(2002, 4, 8, 0, 0);
        updateRequest.setBirthDay(birthDay);
        updateRequest.setNumber("+123456789");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(updateRequest);

        User user = new User();
        user.setEmail("old-email@email.com");
        user.setName("Old Name");
        user.setSurname("Old Surname");
        LocalDateTime birthDayNew = LocalDateTime.of(2001, 3, 7, 0, 0);
        user.setBirthDay(birthDay);
        user.setNumber("+53765423420");


        User updatedUser = new User();
        updatedUser.setEmail("new-email@email.com");
        updatedUser.setName("New Name");
        updatedUser.setSurname("New Surname");
        updatedUser.setBirthDay(LocalDateTime.of(2002, 4, 8, 0, 0));
        updatedUser.setNumber("+123456789");

        Mockito.when(userService.updateAllUserFields(userId, updateRequest)).thenReturn(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user/update-all/"+ userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("new-email@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("New Surname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDay").value("2002-04-08T00:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value("+123456789"));

        Mockito.verify(userService).updateAllUserFields(userId, updateRequest);
    }

    @Test
    public void testSearchUsersByBirthDateRange() throws Exception {
        LocalDateTime from = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2002, 12, 31, 23, 59);
        DateDto dateDto = new DateDto(from, to);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String dateDtoJson = objectMapper.writeValueAsString(dateDto);

        User user1 = new User();
        user1.setId(1L);
        LocalDateTime birthDay1 = LocalDateTime.of(2001, 4, 4, 0, 0);
        user1.setBirthDay(birthDay1);

        User user2 = new User();
        user2.setId(2L);
        LocalDateTime birthDay2 = LocalDateTime.of(2002, 3, 7, 0, 0);
        user2.setBirthDay(birthDay2);

        User user3 = new User();
        user3.setId(3L);
        LocalDateTime birthDay3 = LocalDateTime.of(2003, 6, 5, 0, 0);
        user3.setBirthDay(birthDay3);

        User user4 = new User();
        user4.setId(4L);
        LocalDateTime birthDay4 = LocalDateTime.of(2004, 1, 6, 0, 0);
        user4.setBirthDay(birthDay4);

        List<User> expectedUsers = Arrays.asList(user1, user3, user4);

        Mockito.when(userService.findUsersByBirthDateRange(from, to)).thenReturn(expectedUsers);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dateDtoJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].birthDay").value("2001-04-04T00:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].birthDay").value("2003-06-05T00:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].birthDay").value("2004-01-06T00:00:00Z"));

        Mockito.verify(userService).findUsersByBirthDateRange(from, to);
    }
}
