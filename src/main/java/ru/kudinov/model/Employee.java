package ru.kudinov.model;

import org.springframework.format.annotation.DateTimeFormat;
import ru.kudinov.model.enums.entityEnums.EmployeeWorkSchedule;
import ru.kudinov.model.enums.entityEnums.Post;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String name;

    private String surname;

    private String patronymic;

    private String phoneNumber;

    private String address;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthdate;

    @Enumerated(EnumType.STRING)
    private Post post; //должность

    private Integer workExperience; //in Days

    @Enumerated(EnumType.STRING)
    private EmployeeWorkSchedule employeeWorkSchedule;  //график работы

    private Integer salary; // оклад

    private Integer seniorityAllowance; //надбавка за стаж

    private Integer wages; //зарплата

    private String image;

    private boolean isActive;

    public Employee() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Integer getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(Integer workExperience) {
        this.workExperience = workExperience;
    }

    public EmployeeWorkSchedule getEmployeeWorkSchedule() {
        return employeeWorkSchedule;
    }

    public void setEmployeeWorkSchedule(EmployeeWorkSchedule employeeWorkSchedule) {
        this.employeeWorkSchedule = employeeWorkSchedule;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
        setWages();
    }

    public Integer getSeniorityAllowance() {
        return seniorityAllowance;
    }

    public void setSeniorityAllowance(Integer seniorityAllowance) {
        this.seniorityAllowance = seniorityAllowance;
        setWages();
    }

    public Integer getWages() {
        return this.salary + this.seniorityAllowance;
    }

    public void setWages(Integer wages) {
        this.wages = wages;
    }

    private void setWages() {
        if (this.seniorityAllowance != null && this.salary != null) this.wages = this.salary + this.seniorityAllowance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive() {
        this.isActive = true;
    }

    public void setNonActive() {
        this.isActive = false;
    }
}