package com.xingye.bankrisk.service;

import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 认证服务类
 * 处理用户登录、注册、JWT token管理等认证相关功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret:mySecretKey12345678901234567890123456789012345678901234567890}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    /**
     * 用户注册
     */
    @Transactional
    public User register(String account, String password, String name, String telephone,
                        String nuid, String email) {
        log.info("[INFO]AuthService::register: 用户注册 - Account: {}", account);

        // 检查账号是否已存在
        if (userRepository.existsByAccount(account)) {
            throw new RuntimeException("账号已存在: " + account);
        }

        // 检查手机号是否已存在
        if (telephone != null && userRepository.existsByTelephone(telephone)) {
            throw new RuntimeException("手机号已存在: " + telephone);
        }

        // 检查身份证号是否已存在
        if (nuid != null && userRepository.existsByNuid(nuid)) {
            throw new RuntimeException("身份证号已存在: " + nuid);
        }

        // 创建新用户
        User user = User.builder()
                .account(account)
                .keyHash(passwordEncoder.encode(password))
                .name(name)
                .telephone(telephone)
                .nuid(nuid)
                .email(email)
                .status(User.UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("[INFO]AuthService::register: 用户注册成功 - ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(String account, String password) {
        log.info("[INFO]AuthService::login: 用户登录尝试 - Account: {}", account);

        Optional<User> userOpt = userRepository.findByAccount(account);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("用户账号已被锁定或删除");
        }

        if (!passwordEncoder.matches(password, user.getKeyHash())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT token
        String token = generateToken(user);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        result.put("expiresIn", jwtExpiration);

        log.info("[INFO]AuthService::login: 用户登录成功 - ID: {}", user.getId());
        return result;
    }

    /**
     * 获取当前用户信息
     */
    public Optional<User> getCurrentUser(String token) {
        try {
            Claims claims = parseToken(token);
            Long userId = claims.get("userId", Long.class);
            return userRepository.findById(userId);
        } catch (Exception e) {
            log.error("[ERROR]AuthService::getCurrentUser: Token解析失败", e);
            return Optional.empty();
        }
    }

    /**
     * 根据用户名（用户ID）获取用户详情（用于Spring Security）
     */
    public UserDetails loadUserByUsername(String username) {
        try {
            Long userId = Long.valueOf(username);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            if (user.getStatus() != User.UserStatus.ACTIVE) {
                throw new UsernameNotFoundException("User account is not active: " + username);
            }

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getId().toString())
                    .password(user.getKeyHash())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + determineUserRole(user))))
                    .accountExpired(false)
                    .accountLocked(user.getStatus() == User.UserStatus.LOCKED)
                    .credentialsExpired(false)
                    .disabled(user.getStatus() == User.UserStatus.DELETED)
                    .build();

        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID: " + username);
        }
    }

    /**
     * 刷新token
     */
    public String refreshToken(String oldToken) {
        log.info("[INFO]AuthService::refreshToken: 刷新token");

        try {
            Claims claims = parseToken(oldToken);
            Long userId = claims.get("userId", Long.class);

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("用户不存在");
            }

            return generateToken(userOpt.get());
        } catch (Exception e) {
            log.error("[ERROR]AuthService::refreshToken: Token刷新失败", e);
            throw new RuntimeException("Token刷新失败");
        }
    }

    /**
     * 验证token有效性
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[WARN]AuthService::validateToken: Token已过期");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("[WARN]AuthService::validateToken: Token格式错误");
            return false;
        } catch (Exception e) {
            log.error("[ERROR]AuthService::validateToken: Token验证失败", e);
            return false;
        }
    }

    /**
     * 更新用户密码
     */
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        log.info("[INFO]AuthService::updatePassword: 更新密码 - UserID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(oldPassword, user.getKeyHash())) {
            throw new RuntimeException("原密码错误");
        }

        user.setKeyHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("[INFO]AuthService::updatePassword: 密码更新成功 - UserID: {}", userId);
    }

    /**
     * 生成JWT token
     */
    private String generateToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusSeconds(jwtExpiration / 1000);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(user.getAccount())
                .claim("userId", user.getId())
                .claim("role", determineUserRole(user))
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT token
     */
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 确定用户角色（基于账号前缀或其他规则）
     */
    private String determineUserRole(User user) {
        String account = user.getAccount();

        if (account.contains("AUDITOR_JUNIOR")) {
            return "AUDITOR_JUNIOR";
        } else if (account.contains("AUDITOR_MID")) {
            return "AUDITOR_MID";
        } else if (account.contains("AUDITOR_SENIOR")) {
            return "AUDITOR_SENIOR";
        } else if (account.contains("INVEST_COMMITTEE")) {
            return "INVEST_COMMITTEE";
        } else if (account.contains("ADMIN")) {
            return "ADMIN";
        } else {
            return "CUSTOMER";
        }
    }

    /**
     * 从token中提取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            throw new RuntimeException("无法从token中提取用户ID");
        }
    }

    /**
     * 从token中提取用户角色
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            throw new RuntimeException("无法从token中提取用户角色");
        }
    }

    /**
     * 检查用户是否有指定权限
     */
    public boolean hasPermission(String token, String requiredRole) {
        try {
            String userRole = getRoleFromToken(token);
            return hasRolePermission(userRole, requiredRole);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查角色权限
     */
    private boolean hasRolePermission(String userRole, String requiredRole) {
        // 管理员拥有所有权限
        if ("ADMIN".equals(userRole)) {
            return true;
        }

        // 投资委员会成员可以访问高级审核员权限
        if ("INVEST_COMMITTEE".equals(userRole) &&
            ("AUDITOR_SENIOR".equals(requiredRole) || "CUSTOMER".equals(requiredRole))) {
            return true;
        }

        // 高级审核员可以访问中级审核员权限
        if ("AUDITOR_SENIOR".equals(userRole) &&
            ("AUDITOR_MID".equals(requiredRole) || "CUSTOMER".equals(requiredRole))) {
            return true;
        }

        // 中级审核员可以访问初级审核员权限
        if ("AUDITOR_MID".equals(userRole) &&
            ("AUDITOR_JUNIOR".equals(requiredRole) || "CUSTOMER".equals(requiredRole))) {
            return true;
        }

        // 初级审核员只能访问客户权限
        if ("AUDITOR_JUNIOR".equals(userRole) && "CUSTOMER".equals(requiredRole)) {
            return true;
        }

        // 客户只能访问自己的权限
        if ("CUSTOMER".equals(userRole) && "CUSTOMER".equals(requiredRole)) {
            return true;
        }

        return false;
    }
}
