package com.code5150.meshgrouptest.service;

import com.code5150.meshgrouptest.dto.AuthRequest;
import com.code5150.meshgrouptest.dto.AuthResponse;
import com.code5150.meshgrouptest.dto.RegisterRequest;
import com.code5150.meshgrouptest.entity.*;
import com.code5150.meshgrouptest.exception.InvalidCredentialsException;
import com.code5150.meshgrouptest.exception.UserAlreadyExistsException;
import com.code5150.meshgrouptest.repository.*;
import com.code5150.meshgrouptest.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.code5150.meshgrouptest.exception.ExceptionMessages.EMAIL_OR_PHONE_ALREADY_TAKEN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void register(RegisterRequest request) {
        List<String> emails = request.getEmails();
        List<String> phones = request.getPhones();

        for (String email : emails) {
            if (emailDataRepository.existsByEmail(email)) {
                throw new UserAlreadyExistsException(EMAIL_OR_PHONE_ALREADY_TAKEN);
            }
        }
        for (String phone : phones) {
            if (phoneDataRepository.existsByPhone(phone)) {
                throw new UserAlreadyExistsException(EMAIL_OR_PHONE_ALREADY_TAKEN);
            }
        }

        User user = User.builder()
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        List<EmailData> emailEntities = emails.stream()
                .map(email -> EmailData.builder().user(user).email(email).build())
                .toList();
        List<PhoneData> phoneEntities = phones.stream()
                .map(phone -> PhoneData.builder().user(user).phone(phone).build())
                .toList();

        user.setEmails(emailEntities);
        user.setPhones(phoneEntities);

        Account account = Account.builder()
                .user(user)
                .balance(request.getInitialBalance())
                .initialBalance(request.getInitialBalance())
                .build();

        user.setAccount(account);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse authenticate(AuthRequest request) {
        User user = findUserByLogin(request);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return new AuthResponse(token);
    }

    private User findUserByLogin(AuthRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return emailDataRepository.findByEmail(request.getEmail())
                    .map(EmailData::getUser)
                    .orElseThrow(() -> new InvalidCredentialsException("User not found by email"));
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            return phoneDataRepository.findByPhone(request.getPhone())
                    .map(PhoneData::getUser)
                    .orElseThrow(() -> new InvalidCredentialsException("User not found by phone"));
        }
        throw new InvalidCredentialsException("Email or phone is required");
    }
}
