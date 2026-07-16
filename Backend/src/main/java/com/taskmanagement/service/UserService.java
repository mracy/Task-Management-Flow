package com.taskmanagement.service;

import com.taskmanagement.dto.request.RegisterRequest;
import com.taskmanagement.dto.request.UpdateUserRequest;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.dto.response.UserResponse;
import com.taskmanagement.entity.User;

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    PaginatedResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir);

    PaginatedResponse<UserResponse> searchUsers(String query, int page, int size);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    User getCurrentUser();

    User getCurrentUserEntity();
}
