package com.matbia.model;

import com.matbia.enums.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@MappedSuperclass
public class Person {
    @Column(name = "first_name")
    @Size(min = 2, max = 20, message = "Field has to be be from 2 to 20 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_\\- ]*$", message = "Field contains incorrect characters")
    private String firstName;

    @Column(name = "last_name")
    @Size(min = 2, max = 30, message = "Field has to be be from 2 to 30 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_\\- ]*$", message = "Field contains incorrect characters")
    private String lastName;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birthday")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date birthday;

    @Column(name = "contact_email")
    @Email(message = "Field must contain a valid e-mail address")
    @NotEmpty(message = "Field cannot be empty")
    private String contactEmail;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail.toLowerCase();
    }
}
