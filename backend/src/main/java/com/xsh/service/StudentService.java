package com.xsh.service;

import com.xsh.entity.Student;
import java.util.List;

public interface StudentService {

    void insertStudent(Student student);

    void deleteStudent(int id);

    void updateStudent(Student student);

    Student searchStudent(int id);
    
    List<Student> getAllStudents();
    
    /**
     * 搜索学生
     * @param type 搜索类型（序号/学号/专业）
     * @param keyword 搜索关键词
     * @return 匹配的学生列表
     */
    List<Student> searchStudents(String type, String keyword);

}
