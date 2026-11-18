package io.github.clfundev.better.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.clfundev.better.server.auth.UserDetailsImpl;
import io.github.clfundev.better.server.common.Result;
import io.github.clfundev.better.server.entity.Backup;
import io.github.clfundev.better.server.mapper.BackupMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/backup")
public class BackupController {

    @Autowired
    private BackupMapper backupMapper;

    @Value("${backup.upload-dir}")
    private String uploadDir;

    @Operation(summary = "下载备份")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Backup backup = backupMapper.selectOne(
                new QueryWrapper<Backup>()
                        .eq("id", id)
                        .eq("user_id", userDetails.getUser().getId())
        );
        if (backup == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(uploadDir, backup.getId());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backup.getId() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    @Operation(summary = "上传备份")
    @PostMapping("/upload")
    public Result<Void> upload(@RequestParam("file") MultipartFile file,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        String id = UUID.randomUUID().toString().replace("-", "");
        File target = new File(dir, id);
        file.transferTo(target);

        Backup backup = new Backup();
        backup.setId(id);
        backup.setUserId(userDetails.getUser().getId());
        backup.setSize(file.getSize());
        backup.setCreatedAt(Instant.now());
        backupMapper.insert(backup);

        return Result.success();
    }

    @Operation(summary = "分页查询备份")
    @GetMapping("/page")
    public Result<Page<Backup>> page(@RequestParam(defaultValue = "1") long current,
                                     @RequestParam(defaultValue = "10") long size,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 查询用户的备份记录，按时间降序
        Page<Backup> page = new Page<>(current, size);
        QueryWrapper<Backup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userDetails.getUser().getId()).orderByDesc("created_at");
        return Result.success(backupMapper.selectPage(page, queryWrapper));
    }
}
