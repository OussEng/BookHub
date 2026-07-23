package fr.eni.bookhub.auth.service;

import fr.eni.bookhub.auth.dto.request.LoginRequest;
import fr.eni.bookhub.auth.dto.response.LoginResponse;
import fr.eni.bookhub.auth.dto.request.RegisterRequest;
import fr.eni.bookhub.auth.dto.response.RegisterResponse;
import fr.eni.bookhub.exception.custom.ConflictException;
import fr.eni.bookhub.jwt.util.JwtUtil;
import fr.eni.bookhub.refreshToken.entity.RefreshToken;
import fr.eni.bookhub.refreshToken.service.RefreshTokenService;
import fr.eni.bookhub.user.dao.IUserDao;
import fr.eni.bookhub.user.entity.Role;
import fr.eni.bookhub.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final IUserDao userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already in use");
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .phone(request.getPhone())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        User saved = userRepository.save(user);
        return RegisterResponse.fromUserEntity(saved);
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.createToken(user);

        return LoginResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getDisplayUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public LoginResponse refresh(String rawRefreshToken) {

        RefreshToken oldToken = refreshTokenService.validateToken(rawRefreshToken);
        User user = oldToken.getUser();

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = refreshTokenService.rotateToken(oldToken);

        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getDisplayUsername())
                .phone(user.getPhone())
                .role(user.getRole())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(String rawRefreshToken) {
        if (rawRefreshToken != null) {
            refreshTokenService.revokeToken(rawRefreshToken);
        }
    }
}


