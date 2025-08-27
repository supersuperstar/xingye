package com.xingye.bankrisk.service;

import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建新用户
     */
    @Transactional
    public User createUser(User user) {
        log.info("[INFO]UserService::createUser: 创建新用户 - {}", user.getAccount());

        // 检查账号是否已存在
        if (userRepository.existsByAccount(user.getAccount())) {
            throw new RuntimeException("账号已存在: " + user.getAccount());
        }

        // 检查手机号是否已存在
        if (user.getTelephone() != null && userRepository.existsByTelephone(user.getTelephone())) {
            throw new RuntimeException("手机号已存在: " + user.getTelephone());
        }

        // 检查身份证号是否已存在
        if (user.getNuid() != null && userRepository.existsByNuid(user.getNuid())) {
            throw new RuntimeException("身份证号已存在: " + user.getNuid());
        }

        // 加密密码
        if (user.getKeyHash() != null) {
            user.setKeyHash(passwordEncoder.encode(user.getKeyHash()));
        }

        User savedUser = userRepository.save(user);
        log.info("[INFO]UserService::createUser: 用户创建成功 - ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * 根据账号查找用户
     */
    public Optional<User> findByAccount(String account) {
        return userRepository.findByAccount(account);
    }

    /**
     * 根据ID查找用户
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据手机号查找用户
     */
    public Optional<User> findByTelephone(String telephone) {
        return userRepository.findByTelephone(telephone);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(User user) {
        log.info("[INFO]UserService::updateUser: 更新用户信息 - ID: {}", user.getId());

        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在: " + user.getId()));

        // 更新字段
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getTelephone() != null) {
            existingUser.setTelephone(user.getTelephone());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getOccupation() != null) {
            existingUser.setOccupation(user.getOccupation());
        }
        if (user.getInvestAmount() != null) {
            existingUser.setInvestAmount(user.getInvestAmount());
        }
        if (user.getRiskLevel() != null) {
            existingUser.setRiskLevel(user.getRiskLevel());
        }

        User savedUser = userRepository.save(existingUser);
        log.info("[INFO]UserService::updateUser: 用户信息更新成功 - ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public User updateUserStatus(Long userId, User.UserStatus status) {
        log.info("[INFO]UserService::updateUserStatus: 更新用户状态 - ID: {}, Status: {}", userId, status);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setStatus(status);
        User savedUser = userRepository.save(user);

        log.info("[INFO]UserService::updateUserStatus: 用户状态更新成功 - ID: {}, Status: {}", savedUser.getId(), savedUser.getStatus());
        return savedUser;
    }

    /**
     * 更新用户风险等级
     */
    @Transactional
    public User updateUserRiskLevel(Long userId, User.RiskLevel riskLevel) {
        log.info("[INFO]UserService::updateUserRiskLevel: 更新用户风险等级 - ID: {}, RiskLevel: {}", userId, riskLevel);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setRiskLevel(riskLevel);
        user.setEvaluationTime(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("[INFO]UserService::updateUserRiskLevel: 用户风险等级更新成功 - ID: {}, RiskLevel: {}", savedUser.getId(), savedUser.getRiskLevel());
        return savedUser;
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("[INFO]UserService::deleteUser: 删除用户 - ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);

        log.info("[INFO]UserService::deleteUser: 用户删除成功 - ID: {}", userId);
    }

    /**
     * 查找活跃用户
     */
    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }

    /**
     * 根据状态查找用户
     */
    public List<User> findUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatus(status);
    }

    /**
     * 根据风险等级查找用户
     */
    public List<User> findUsersByRiskLevel(User.RiskLevel riskLevel) {
        return userRepository.findByRiskLevel(riskLevel);
    }

    /**
     * 验证用户密码
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 更新用户密码
     */
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        log.info("[INFO]UserService::updatePassword: 更新用户密码 - ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setKeyHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("[INFO]UserService::updatePassword: 用户密码更新成功 - ID: {}", userId);
    }

    /**
     * 统计各风险等级用户数量
     */
    public List<Object[]> countUsersByRiskLevel() {
        return userRepository.countUsersByRiskLevel();
    }

    /**
     * 获取用户总数
     */
    public Long getTotalUserCount() {
        return userRepository.count();
    }

    /**
     * 获取活跃用户总数
     */
    public Long getActiveUserCount() {
        return userRepository.findActiveUsers().stream().count();
    }
}
