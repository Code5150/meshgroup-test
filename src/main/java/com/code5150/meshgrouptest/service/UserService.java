package com.code5150.meshgrouptest.service;

import com.code5150.meshgrouptest.dto.UpdateUserRequest;
import com.code5150.meshgrouptest.dto.UserResponse;
import com.code5150.meshgrouptest.dto.UserSearchRequest;
import com.code5150.meshgrouptest.entity.*;
import com.code5150.meshgrouptest.exception.ResourceNotFoundException;
import com.code5150.meshgrouptest.repository.*;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final String WILDCARD = "%";

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserResponseById(Long id) {
        return toUserResponse(getUserById(id));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(UserSearchRequest request) {
        Specification<User> spec = buildSearchSpecification(request);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(User.Fields.id));
        Page<User> userPage = userRepository.findAll(spec, pageable);
        return userPage.map(this::toUserResponse);
    }

    @Transactional
    @CachePut(value = "users", key = "#userId")
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = getUserById(userId);
        updateEmails(user, request.getEmails());
        updatePhones(user, request.getPhones());
        return toUserResponse(user);
    }

    private void updateEmails(User user, List<String> newEmails) {
        List<String> current = user.getEmails().stream().map(EmailData::getEmail).toList();
        List<String> toRemove = new ArrayList<>(current);
        toRemove.removeAll(newEmails);
        List<String> toAdd = new ArrayList<>(newEmails);
        toAdd.removeAll(current);

        for (String email : toRemove) {
            if (user.getEmails().size() <= 1) {
                throw new IllegalArgumentException("User must have at least one email");
            }
            emailDataRepository.deleteByUserIdAndEmail(user.getId(), email);
            user.getEmails().removeIf(e -> e.getEmail().equals(email));
        }
        for (String email : toAdd) {
            if (emailDataRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already taken: " + email);
            }
            EmailData emailData = EmailData.builder().user(user).email(email).build();
            user.getEmails().add(emailData);
        }
    }

    private void updatePhones(User user, List<String> newPhones) {
        List<String> current = user.getPhones().stream().map(PhoneData::getPhone).toList();
        List<String> toRemove = new ArrayList<>(current);
        toRemove.removeAll(newPhones);
        List<String> toAdd = new ArrayList<>(newPhones);
        toAdd.removeAll(current);

        for (String phone : toRemove) {
            if (user.getPhones().size() <= 1) {
                throw new IllegalArgumentException("User must have at least one phone");
            }
            phoneDataRepository.deleteByUserIdAndPhone(user.getId(), phone);
            user.getPhones().removeIf(p -> p.getPhone().equals(phone));
        }
        for (String phone : toAdd) {
            if (phoneDataRepository.existsByPhone(phone)) {
                throw new IllegalArgumentException("Phone already taken: " + phone);
            }
            PhoneData phoneData = PhoneData.builder().user(user).phone(phone).build();
            user.getPhones().add(phoneData);
        }
    }

    private Specification<User> buildSearchSpecification(UserSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getDateOfBirth() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(User.Fields.dateOfBirth), request.getDateOfBirth()));
            }
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                Join<User, PhoneData> phoneJoin = root.join(User.Fields.phones, JoinType.INNER);
                predicates.add(cb.equal(phoneJoin.get(PhoneData.Fields.phone), request.getPhone()));
            }
            if (request.getName() != null && !request.getName().isBlank()) {
                predicates.add(cb.like(root.get(User.Fields.name), request.getName() + WILDCARD));
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                Join<User, EmailData> emailJoin = root.join(User.Fields.emails, JoinType.INNER);
                predicates.add(cb.equal(emailJoin.get(EmailData.Fields.email), request.getEmail()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .emails(user.getEmails().stream().map(EmailData::getEmail).toList())
                .phones(user.getPhones().stream().map(PhoneData::getPhone).toList())
                .balance(user.getAccount() != null ? user.getAccount().getBalance() : null)
                .build();
    }
}
