package com.matbia.service;

import com.matbia.enums.ImageFileExtension;
import com.matbia.misc.Utils;
import com.matbia.model.ProfilePicture;
import com.matbia.model.User;
import com.matbia.repository.ProfilePictureRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ProfilePictureService {
    @Autowired
    private ProfilePictureRepository profilePictureRepository;

    public Optional<ProfilePicture> getByUser(User user) {
        return profilePictureRepository.findByUser(user);
    }

    public void removeByUser(User user) {
        profilePictureRepository.removeByUser(user);
    }

    public void save(User user, MultipartFile file) {
        ProfilePicture profilePicture = profilePictureRepository.findByUser(user).orElse(new ProfilePicture(user));

        try {
            profilePicture.setExtension(ImageFileExtension.valueOf(FilenameUtils.getExtension(file.getOriginalFilename()).toUpperCase()));
        } catch (IllegalArgumentException e) {
            //Illegal file extension
            return;
        }

        try {
            profilePicture.setOriginal(file.getBytes());
            profilePicture.setThumbnail(Utils.scaleImage(profilePicture.getOriginal(), profilePicture.getExtension().toString(), 150));
            profilePictureRepository.save(profilePicture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
