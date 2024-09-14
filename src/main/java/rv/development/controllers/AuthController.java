package rv.development.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rv.development.dtos.requests.LoginRequest;
import rv.development.dtos.requests.SignupRequest;
import rv.development.dtos.responses.MessageResponse;
import rv.development.dtos.responses.UserInfoResponse;
import rv.development.entities.RoleEntity;
import rv.development.entities.UserEntity;
import rv.development.enums.ERole;
import rv.development.repositories.RoleRepository;
import rv.development.repositories.UserRepository;
import rv.development.securities.JwtUtils;
import rv.development.securities.UserDetailsImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String ROLE_NOT_FOUND_MESSAGE = "Error: Role not found!";
    
    AuthenticationManager authenticationManager;
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    JwtUtils jwtUtils;
    
    @Autowired
    AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is already taken!"));
        }
        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .activated(true)
                .build();

        Set<String> setRoles = request.getRoles();
        Set<RoleEntity> roles = new HashSet<>();

        if(setRoles == null){
            RoleEntity userRole = roleRepository.findByRoleName(ERole.ROLE_USER)
                    .orElseThrow(()-> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
            roles.add(userRole);
        } else {
            setRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        RoleEntity adminRole = roleRepository.findByRoleName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        RoleEntity modRole = roleRepository.findByRoleName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                        roles.add(modRole);
                        break;
                    default:
                        RoleEntity userRole = roleRepository.findByRoleName(ERole.ROLE_USER)
                                .orElseThrow(()-> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                        roles.add(userRole);
                        break;
                }
                    });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponse> authenticationUser(@Valid @RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(
                        userDetails.getUserId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles
                ));
    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> logoutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }


}
