package org.example.mindlab.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import static org.example.mindlab.global.exception.error.ErrorCodes.INVALID_IMAGE_EXTENSION;

@Service
public class ImageService {

    public String getImageUrl(MultipartFile imageFile) {
        String fileName = imageFile.getOriginalFilename();
        if (fileName == null || !(fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png"))) {
            throw INVALID_IMAGE_EXTENSION.throwException();
        }
        return sendToAIServer(imageFile);
    }

    private String sendToAIServer(MultipartFile imageFile) {
        // ai 서버 호출 로직 작성
        return imageFile.getOriginalFilename();
    }
}