package com.xsh.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private int number;
    private String name;
    private String sex;
    private int phoneNumber;
    private String mail;
    private int age;
    private String major;

    public Student() {
        this.major = "未设置"; // 在默认构造函数中设置默认值
    }

    public Student(int number, String name, String sex, int phoneNumber, String mail) {
        this.number = number;
        this.name = name;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.major = "未设置";
        this.age = 0;
    }

    @Override
    public Student clone() {
        try {
            return (Student) super.clone();
        } catch (CloneNotSupportedException e) {
            Student copy = new Student();
            copy.number = this.number;
            copy.name = this.name;
            copy.sex = this.sex;
            copy.phoneNumber = this.phoneNumber;
            copy.mail = this.mail;
            copy.age = this.age;
            copy.major = this.major;
            return copy;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    // 为了兼容前端的id字段
    public int getId() {
        return number;
    }

    public void setId(int id) {
        this.number = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
