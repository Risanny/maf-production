package com.maf.production.service.impl;

import com.maf.production.dto.UserDTO;
import com.maf.production.dto.SignupRequest;
import com.maf.production.exception.ResourceNotFoundException;
import com.maf.production.model.Role;
import com.maf.production.model.User;
import com.maf.production.repository.RoleRepository;
import com.maf.production.repository.UserRepository;
import com.maf.production.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));
        return convertToDTO(user);
    }

    @Override
    public UserDTO createUser(SignupRequest signupRequest) {
        // Создаем нового пользователя
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());

        // Устанавливаем роли
        Set<Role> roles = new HashSet<>();

        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            // Если роли не указаны, добавляем роль клиента
            Role clientRole = roleRepository.findByName(Role.RoleType.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role CLIENT is not found."));
            roles.add(clientRole);
        } else {
            signupRequest.getRoles().forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
                        roles.add(adminRole);
                        break;
                    case "manager":
                        Role modRole = roleRepository.findByName(Role.RoleType.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role MANAGER is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_CLIENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role CLIENT is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));

        // Обновляем данные пользователя
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());

        // Обновляем роли, если они указаны
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();

            userDTO.getRoles().forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(Role.RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
                        roles.add(adminRole);
                        break;
                    case "ROLE_MANAGER":
                        Role modRole = roleRepository.findByName(Role.RoleType.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role MANAGER is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_CLIENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role CLIENT is not found."));
                        roles.add(userRole);
                }
            });

            existingUser.setRoles(roles);
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Вспомогательные методы для конвертации Entity <-> DTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());

        // Преобразуем роли в строки
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        dto.setRoles(roles);

        return dto;
    }
}
