package com.xsh.controller;

import com.xsh.entity.Student;
import com.xsh.entity.CommonResponse;
import com.xsh.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public CommonResponse<List<Student>> getAllStudents() {
        return CommonResponse.success(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public CommonResponse<?> getStudentById(@PathVariable int id) {
        Student student = studentService.searchStudent(id);
        if (student != null) {
            return CommonResponse.success("查询成功", student);
        }
        return CommonResponse.error("未找到该学生");
    }

    @PostMapping
    public CommonResponse<?> addStudent(@RequestBody Student student) {
        // 检查学号是否已存在
        if (studentService.searchStudent(student.getNumber()) != null) {
            return CommonResponse.error("该学号已存在");
        }
        
        // 设置默认值
        student.setSex("未知");
        student.setPhoneNumber(10000000); // 设置默认电话号码
        student.setMail("");
        
        // 只有当专业为null或为空时才设置默认值
        if (student.getMajor() == null || student.getMajor().trim().isEmpty()) {
            student.setMajor("未设置");
        }
        
        studentService.insertStudent(student);
        return CommonResponse.success("学生信息已添加成功", null);
    }

    @PutMapping("/{id}")
    public CommonResponse<?> updateStudent(@PathVariable int id, @RequestBody Student student) {
        student.setId(id);
        
        // 获取现有学生信息
        Student existingStudent = studentService.searchStudent(id);
        if (existingStudent != null) {
            // 如果未提供新专业，保留原有专业
            if (student.getMajor() == null || student.getMajor().trim().isEmpty()) {
                student.setMajor(existingStudent.getMajor());
            }
        }
        
        studentService.updateStudent(student);
        return CommonResponse.success("学生信息已修改成功", null);
    }

    @DeleteMapping("/{id}") 
    public CommonResponse<?> deleteStudent(@PathVariable int id) {
        Student student = studentService.searchStudent(id);
        if (student != null) {
            studentService.deleteStudent(id);
            return CommonResponse.success("学生信息已删除成功", null);
        }
        return CommonResponse.error("未找到该学生");
    }
    
    @GetMapping("/search")
    public CommonResponse<List<Student>> searchStudents(@RequestParam String type, @RequestParam String keyword) {
        return CommonResponse.success(studentService.searchStudents(type, keyword));
    }
}
