package io.github.clfundev.better.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.clfundev.better.auth.UserDetailsImpl;
import io.github.clfundev.better.common.HashUtil;
import io.github.clfundev.better.common.JwtUtil;
import io.github.clfundev.better.common.Result;
import io.github.clfundev.better.dto.*;
import io.github.clfundev.better.entity.RefreshToken;
import io.github.clfundev.better.entity.User;
import io.github.clfundev.better.mapper.RefreshTokenMapper;
import io.github.clfundev.better.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        // 检查是否有相同用户名的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        if (userMapper.selectOne(queryWrapper) != null) {
            log.info("Username {} already exists", request.getUsername());
            return Result.paramError();
        }

        // 创建新用户
        Instant now = Instant.now();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setProfile(request.getProfile());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);

        return Result.success();
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // 使用AuthenticationManager认证，认证失败会抛异常
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 认证成功，生成token
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // refreshToken持久化
        Claims rtClaims = jwtUtil.parse(refreshToken);
        RefreshToken rtEntity = new RefreshToken();
        rtEntity.setUserId(user.getId());
        rtEntity.setTokenHash(HashUtil.sha256(refreshToken));
        rtEntity.setRevoked(0);
        rtEntity.setExpiresAt(rtClaims.getExpiration().toInstant());
        rtEntity.setCreatedAt(rtClaims.getIssuedAt().toInstant());
        refreshTokenMapper.insert(rtEntity);

        return Result.success(new LoginResponse(accessToken, refreshToken, "Bearer"));
    }

    @Operation(summary = "刷新accessToken")
    @PostMapping("/refresh")
    public Result<RefreshResponse> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!StringUtils.hasText(refreshToken)) {
            return Result.paramError();
        }

        // 校验和解析refreshToken
        Claims claims;
        try {
            claims = jwtUtil.parse(refreshToken);
        } catch (Exception e) {
            return Result.unauthorized();
        }

        // 数据库查询哈希值匹配、未过期、未吊销的refreshToken
        RefreshToken savedRt = refreshTokenMapper.selectOne(
                new LambdaQueryWrapper<RefreshToken>()
                        .eq(RefreshToken::getTokenHash, HashUtil.sha256(refreshToken))
                        .eq(RefreshToken::getRevoked, 0)
                        .gt(RefreshToken::getExpiresAt, Instant.now())
        );
        if (savedRt == null) {
            return Result.unauthorized();
        }

        // 签发新accessToken
        String username = claims.getSubject();
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return Result.success(new RefreshResponse(newAccessToken));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return Result.unauthorized();
        }
        // TODO: 多端登录场景会把所有设备踢下线，需优化
        // 吊销当前登录用户关联的refreshToken
        refreshTokenMapper.update(null,
                new LambdaUpdateWrapper<RefreshToken>()
                        .eq(RefreshToken::getUserId, userDetails.getUser().getId())
                        .eq(RefreshToken::getRevoked, 0)
                        .set(RefreshToken::getRevoked, 1)
        );
        return Result.success();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Result<UserVo> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return Result.unauthorized();
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userDetails.getUser(), userVo);
        return Result.success(userVo);
    }
}
