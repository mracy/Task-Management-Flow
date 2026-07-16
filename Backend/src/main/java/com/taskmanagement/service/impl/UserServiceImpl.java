package com.taskmanagement.service.impl;

import com.taskmanagement.dto.request.UpdateUserRequest;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.dto.response.UserResponse;
import com.taskmanagement.entity.User;
import com.taskmanagement.exception.BadRequestException;
import com.taskmanagement.exception.ConflictException;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.mapper.UserMapper;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.JwtTokenProvider;
import com.taskmanagement.security.SecurityUtil;
import com.taskmanagement.service.AuditService;
import com.taskmanagement.service.UserService;
import com.taskmanagement.enums.AuditAction;
import com.taskmanagement.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditService auditService;
    private final JwtTokenProvider tokenProvider;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           AuditService auditService,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.auditService = auditService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findByDeletedFalse(pageable);
        return PaginationUtil.toPaginatedResponse(users.map(userMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserResponse> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<User> users = userRepository.searchUsers(query, pageable);
        return PaginationUtil.toPaginatedResponse(users.map(userMapper::toResponse));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        String oldValues = "firstName=" + user.getFirstName() + ",lastName=" + user.getLastName();

        userMapper.updateEntityFromRequest(request, user);
        user = userRepository.save(user);

        String newValues = "firstName=" + user.getFirstName() + ",lastName=" + user.getLastName();
        auditService.logAudit(AuditAction.UPDATE, "User", String.valueOf(id),
                user.getId(), oldValues, newValues);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        auditService.logAudit(AuditAction.DELETE, "User", String.valueOf(id),
                user.getId(), "email=" + user.getEmail(), null);

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        if (email == null) {
            throw new BadRequestException("No authenticated user found");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        return getCurrentUser();
    }
}
