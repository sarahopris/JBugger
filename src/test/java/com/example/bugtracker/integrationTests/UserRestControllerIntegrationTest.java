package com.example.bugtracker.integrationTests;


import com.example.bugtracker.BugTrackerApplication;
import com.example.bugtracker.Repository.IUserRepository;
import com.example.bugtracker.controller.UserController;
import com.example.bugtracker.dto.UserDTO;
import com.example.bugtracker.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class UserRestControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IUserRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;


//    @Test
//    public void givenEmployees_whenGetEmployees_thenStatus200() throws Exception {
//
//        UserDTO userDTO = UserDTO.builder()
//                .email("tothnaomi@aol.com")
//                .firstName("naomi")
//                .lastName("toth")
//                .mobileNumber("0786938987")
//                .password("naomi")
//                .build();
//
//        userService.addUser(userDTO);
//
//
//        mvc.perform(get("/users/getAll")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect((ResultMatcher) content()
//                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect((ResultMatcher) jsonPath("$[0].firstName", is("naomi")));
//    }

    @Test
    public void getAllUsersTestForbidden() throws Exception {

        UserDTO userDTO = UserDTO.builder()
                .email("tothnaomi@aol.com")
                .firstName("naomi")
                .lastName("toth")
                .mobileNumber("+40786938987")
                .password("naomi")
                .build();

        userService.addUser(userDTO);

        mvc.perform(get("/users/getAll")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect((ResultMatcher) jsonPath("$[0].firstName", is("naomi")));
    }


//    @Test
//    public void getAllUsersTestPositive() throws Exception {
//        // TODO set the token in the header
//        UserDTO userDTO = UserDTO.builder()
//                .email("tothnaomi@aol.com")
//                .firstName("naomi")
//                .lastName("toth")
//                .mobileNumber("0786938987")
//                .password("naomi")
//                .build();
//
//        userService.addUser(userDTO);
//
//        System.out.println("test");
//
//        userService.findAllUsers().forEach(user -> System.out.println(user.toString()));
//
//        mvc.perform(get("/users/getAll")
//                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJzb2Z0dGVrSldUIiwic3ViIjoiY2luZXZjIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTYyODY4OTc4MCwiZXhwIjoxNjI4NjkwMzgwfQ.FMzZDzYY6DT-e6duc2wB1BQABe2cQYukYk8PoQygOrhdaMtlHQ-mSh427d25RhYVGvTPrj3Pdhw-4vTvawDc3A"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(jsonPath("$[0].firstName", is(userDTO.getFirstName())));
//    }
}
