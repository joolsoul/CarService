package ru.kudinov.util;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.kudinov.model.User;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
public class ImageUtil {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${default.user-image.path}")
    private String defaultUserImagePath;


    public void setUserDefaultImage(User user) {
        user.setImage(defaultUserImagePath);
    }

    public String loadImage(MultipartFile uploadImage, String fileDirectory, String fileName) throws IOException {
        if (!Objects.equals(uploadImage.getOriginalFilename(), "")) {

            checkExistDirectory(uploadPath);
            checkExistDirectory(uploadPath + fileDirectory);

            String filePath = fileDirectory + fileName;
            uploadImage.transferTo(new File(uploadPath + filePath));
            return filePath;
        }
        return null;
    }

    public Set<String> loadDetailImages(MultipartFile[] uploadImages, String fileDirectory, String detailId) throws IOException {
        Set<String> filePaths = new HashSet<>();
        if (uploadImages.length != 0) {

            checkExistDirectory(uploadPath);
            checkExistDirectory(uploadPath + fileDirectory);
            checkExistDirectory(uploadPath + fileDirectory + "detail_" + detailId);

            for (MultipartFile uploadImage : uploadImages) {
                String uniqFileName = UUID.randomUUID().toString();
                String filePath = fileDirectory + "detail_" + detailId + "/detailImage_" + uniqFileName + ".png";
                filePaths.add(filePath);
                uploadImage.transferTo(new File(uploadPath + filePath));
            }

        }
        return filePaths;
    }

    private void checkExistDirectory(String path) {
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    public void deleteFile(String path) throws IOException {
        File file = new File(uploadPath + path);
        if (!file.isDirectory()) {
            if (!file.delete()) throw new IOException();
        } else FileUtils.deleteDirectory(file);

    }
}
