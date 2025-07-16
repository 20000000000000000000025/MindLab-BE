package org.example.mindlab.infrastructure.s3;

import lombok.RequiredArgsConstructor;
import org.example.mindlab.infrastructure.s3.service.S3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class Controller {

    private final S3Service s3FileService;

    @PostMapping("/{postId}")
    public void upload(@PathVariable Long postId,
                @RequestParam("file") MultipartFile file) {
        s3FileService.upload(file, postId);
    }

    @GetMapping("/{postId}")
    public void load(@PathVariable Long postId) {
        s3FileService.load(postId);
    }
}
