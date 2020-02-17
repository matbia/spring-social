package com.matbia.repository;

import com.matbia.model.ProfilePicture;
import com.matbia.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfilePictureRepository extends CrudRepository<ProfilePicture, Long> {
    Optional<ProfilePicture> findByUser(User user);
    void removeByUser(User user);
}
