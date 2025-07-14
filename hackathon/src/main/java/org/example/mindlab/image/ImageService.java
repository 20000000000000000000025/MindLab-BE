package org.example.mindlab.image;

import org.springframework.stereotype.Service;
import static org.example.mindlab.global.exception.error.ErrorCodes.INVALID_IMAGE_EXTENSION;

@Service
public class ImageService {

    public String getImageUrl(String fileName) {
        if (fileName == null || !(fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png"))) {
            throw INVALID_IMAGE_EXTENSION.throwException();
        }
        return fileName;
    }
}
