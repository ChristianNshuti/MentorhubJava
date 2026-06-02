package com.mentorhub.auth.service;

import com.mentorhub.auth.dto.AuthResponse;
import com.mentorhub.auth.dto.LoginRequest;
import com.mentorhub.auth.dto.RegisterRequest;
import com.mentorhub.auth.security.JwtService;
import com.mentorhub.auth.security.UserPrincipal;
import com.mentorhub.common.exception.BadRequestException;
import com.mentorhub.student.entity.StudentProfile;
import com.mentorhub.student.repository.StudentProfileRepository;
import com.mentorhub.mentor.entity.MentorProfile;
import com.mentorhub.mentor.repository.MentorProfileRepository;
import com.mentorhub.user.entity.Role;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin accounts cannot be self-registered");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        if (request.getRole() == Role.MENTOR) {
            mentorProfileRepository.save(MentorProfile.builder().user(user).verified(false).build());
        } else if (request.getRole() == Role.STUDENT) {
            studentProfileRepository.save(StudentProfile.builder().user(user).build());
        }

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal, user.getId(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
