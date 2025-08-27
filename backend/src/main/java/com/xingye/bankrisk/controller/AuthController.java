package com.xingye.bankrisk.controller;

import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、注册、认证相关请求
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        log.info("[INFO]AuthController::register: 用户注册请求 - Account: {}", request.getAccount());

        try {
            User user = authService.register(
                    request.getAccount(),
                    request.getPassword(),
                    request.getName(),
                    request.getTelephone(),
                    request.getNuid(),
                    request.getEmail()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("data", user);

            log.info("[INFO]AuthController::register: 用户注册成功 - ID: {}", user.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuthController::register: 用户注册失败", e);
            return createErrorResponse("注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户账号登录")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("[INFO]AuthController::login: 用户登录请求 - Account: {}", request.getAccount());

        try {
            Map<String, Object> loginResult = authService.login(request.getAccount(), request.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", loginResult);

            log.info("[INFO]AuthController::login: 用户登录成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuthController::login: 用户登录失败", e);
            return createErrorResponse("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        log.info("[INFO]AuthController::getCurrentUser: 获取当前用户信息");

        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            User user = authService.getCurrentUser(actualToken)
                    .orElseThrow(() -> new RuntimeException("用户未找到"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuthController::getCurrentUser: 获取用户信息失败", e);
            return createErrorResponse("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "刷新JWT访问令牌")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String token) {
        log.info("[INFO]AuthController::refreshToken: 刷新Token");

        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            String newToken = authService.refreshToken(actualToken);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token刷新成功");
            response.put("data", Map.of("token", newToken, "expiresIn", 86400000));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuthController::refreshToken: Token刷新失败", e);
            return createErrorResponse("Token刷新失败: " + e.getMessage());
        }
    }

    /**
     * 更新密码
     */
    @PutMapping("/password")
    @Operation(summary = "更新密码", description = "更新用户密码")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdatePasswordRequest request) {

        log.info("[INFO]AuthController::updatePassword: 更新密码");

        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = authService.getUserIdFromToken(actualToken);

            authService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密码更新成功");

            log.info("[INFO]AuthController::updatePassword: 密码更新成功 - UserID: {}", userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuthController::updatePassword: 密码更新失败", e);
            return createErrorResponse("密码更新失败: " + e.getMessage());
        }
    }

    /**
     * 验证Token有效性
     */
    @PostMapping("/validate")
    @Operation(summary = "验证Token", description = "验证JWT令牌有效性")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            boolean isValid = authService.validateToken(actualToken);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of("valid", isValid));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Token验证失败: " + e.getMessage());
        }
    }

    /**
     * 创建错误响应
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("code", "AUTH_ERROR");

        return ResponseEntity.badRequest().body(response);
    }

    // 请求/响应 DTO 类

    /**
     * 注册请求
     */
    public static class RegisterRequest {
        private String account;
        private String password;
        private String name;
        private String telephone;
        private String nuid;
        private String email;

        // Getters and Setters
        public String getAccount() { return account; }
        public void setAccount(String account) { this.account = account; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }

        public String getNuid() { return nuid; }
        public void setNuid(String nuid) { this.nuid = nuid; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /**
     * 登录请求
     */
    public static class LoginRequest {
        private String account;
        private String password;

        // Getters and Setters
        public String getAccount() { return account; }
        public void setAccount(String account) { this.account = account; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * 更新密码请求
     */
    public static class UpdatePasswordRequest {
        private String oldPassword;
        private String newPassword;

        // Getters and Setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
