package com.matbia.service;

import com.matbia.enums.PostFileExtension;
import com.matbia.misc.Utils;
import com.matbia.model.PostFile;
import com.matbia.repository.PostFileRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
class PostFileService {
    private final Logger LOGGER = Logger.getLogger(PostFileService.class.getName());

    @Autowired
    private PostFileRepository postFileRepository;

    private void store(byte[] file, String filename, String relativePath) {
        try {
            final Path path = Paths.get(new File(".").getCanonicalPath() + relativePath);

            try (InputStream inputStream = new ByteArrayInputStream(file)) {
                Files.copy(inputStream, path.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to store file: " + e.toString());
        }
    }

    PostFile registerPostFile(MultipartFile multipartFile) throws IOException {
        PostFile postFile = new PostFile();

        if(multipartFile != null && !multipartFile.isEmpty()) {
            try {
                postFile.setFileExtension(PostFileExtension.valueOf(FilenameUtils.getExtension(multipartFile.getOriginalFilename().toUpperCase())));
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Illegal file extension");
                return null;
            }
            String filename = UUID.randomUUID().toString() + "." + postFile.getFileExtension().toString().toLowerCase();
            postFile.setFilename(filename);

            //Store original file
            store(multipartFile.getBytes(), filename, "/files");

            //If file is an image
            if(!postFile.getFileExtension().equals(PostFileExtension.MP4))
                //Generate and store a thumbnail
                store(Utils.scaleImage(multipartFile.getBytes(), postFile.getFileExtension().toString(), 1024), "thumb-" + filename, "/files/thumbnails");
        }
        save(postFile);
        return postFile;
    }

    private void save(PostFile postFile) {
        try {
            postFileRepository.save(postFile);
            postFileRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(PostFile postFile) {
        postFileRepository.delete(postFile);
        if(postFile.getFileExtension() == null) return;
        try {
            if(new File("files/" + postFile.getFilename()).delete())
                LOGGER.log(Level.INFO, "Deleted file: " + postFile.getFilename());
            else
                LOGGER.log(Level.SEVERE, "Failed to delete file: " + postFile.getFilename());

            //Delete thumbnail
            if(!postFile.getFileExtension().equals(PostFileExtension.MP4)) {
                if(new File("files/thumbnails/thumb-" + postFile.getFilename()).delete())
                    LOGGER.log(Level.INFO, "Deleted file: thumb-" + postFile.getFilename());
                else
                    LOGGER.log(Level.SEVERE, "Failed to delete file: thumb-" + postFile.getFilename());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
