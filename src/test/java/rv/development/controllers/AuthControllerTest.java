package rv.development.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import rv.development.dtos.requests.LoginRequest;
import rv.development.dtos.requests.SignupRequest;
import rv.development.entities.RoleEntity;
import rv.development.entities.UserEntity;
import rv.development.enums.ERole;
import rv.development.repositories.RoleRepository;
import rv.development.repositories.UserRepository;
import rv.development.securities.JwtUtils;
import rv.development.securities.UserDetailsImpl;
import rv.development.services.impls.UserDetailsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@WithMockUser(username = "user")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RoleRepository roleRepository;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtUtils jwtUtils;

    @Autowired
    ObjectMapper mapper;

    SignupRequest request;
    UserEntity user;

    @BeforeEach
    public void setup(){
        request = new SignupRequest();
        request.setUsername("user.test");
        request.setPassword("123456789");
        request.setCreatedBy("bot.test");
        request.setUpdatedBy("bot.test");
        request.setEmail("user.test@gmail.com");
        request.setRoles(null);

        user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .activated(true)
                .build();
    }

    /****************************SIGNUP TESTS****************************/
    @Test
    void registerUser_withoutRole_success() throws Exception{
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(1L);
        roleEntity.setRoleName(ERole.ROLE_USER);

        UserEntity newUser = UserEntity.builder()
                .userId(1L)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .activated(true)
                .roles(Set.of(roleEntity))
                .build();

        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        Mockito.when(roleRepository.findByRoleName(ERole.ROLE_USER)).thenReturn(Optional.of(roleEntity));
        Mockito.when(userRepository.save(user)).thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void registerUser_withRoleAdmin_success() throws Exception{
        registerUserWithRole("admin", ERole.ROLE_ADMIN);
    }

    @Test
    void registerUser_withRoleMod_success() throws Exception{
        registerUserWithRole("mod", ERole.ROLE_MODERATOR);
    }

    @Test
    void registerUser_withRoleUser_success() throws Exception{
        registerUserWithRole("user", ERole.ROLE_USER);
    }

    void registerUserWithRole(String roleName, ERole roleValue)  throws Exception{
        request.setRoles(Set.of(roleName));

        RoleEntity newRole = new RoleEntity();
        newRole.setRoleId(1L);
        newRole.setRoleName(roleValue);

        user.setRoles(Set.of(newRole));

        UserEntity newUser = UserEntity.builder()
                .userId(1L)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .updatedBy(request.getUpdatedBy())
                .updatedAt(LocalDateTime.now())
                .roles(Set.of(newRole))
                .activated(true)
                .build();

        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        Mockito.when(roleRepository.findByRoleName(roleValue)).thenReturn(Optional.of(newRole));
        Mockito.when(userRepository.save(user)).thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void registerExistingUsername_error() throws Exception{
        registerExistingUser(true, false);
    }

    @Test
    void registerExistingEmail_error() throws Exception{
        registerExistingUser(false, true);
    }

    void registerExistingUser(boolean existsUsername, boolean existsEmail) throws Exception {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(1L);
        roleEntity.setRoleName(ERole.ROLE_USER);

        Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(existsUsername);
        if(!existsUsername){
            Mockito.when(userRepository.existsByEmail(request.getEmail())).thenReturn(existsEmail);
        }

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    /****************************SIGNIN TESTS****************************/
    @Test
    void loginUser_success() throws Exception {
        String username = "test.user";

        List<RoleEntity> roles = List.of(new RoleEntity(1L, ERole.ROLE_USER));

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username(username)
                .authorities(authorities)
                .userId(1L)
                .email("test.user@hmail.com")
                .build();

        LoginRequest requestLogin = new LoginRequest();
        requestLogin.setUsername(username);
        requestLogin.setPassword("123456789");

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String jwt = "123fsdf34tgaerg5yqa3dg4wjhszdfgtywrtytsy465jw4jq4taersgdsheq3423";
        ResponseCookie responseCookie = ResponseCookie.from("testCookie", jwt).path("/api").maxAge(1800L).httpOnly(true).build();

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(jwtUtils.generateJwtCookie(userDetails)).thenReturn(responseCookie);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signin").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestLogin))
                )
                .andExpect(status().isOk());
    }

    /****************************SIGNOUT TESTS****************************/
    @Test
    void logoutUser_success() throws Exception {

        String jwt = "123fsdf34tgaerg5yqa3dg4wjhszdfgtywrtytsy465jw4jq4taersgdsheq3423";
        ResponseCookie responseCookie = ResponseCookie.from("testCookie", jwt).path("/api").maxAge(1800L).httpOnly(true).build();

        Mockito.when(jwtUtils.getCleanJwtCookie()).thenReturn(responseCookie);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/signout").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

}
