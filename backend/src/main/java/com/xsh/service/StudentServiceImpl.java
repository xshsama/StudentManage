package com.xsh.service;

import com.xsh.entity.Student;
import com.xsh.structure.BPlusTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.InitializingBean;
import javax.annotation.PreDestroy;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

@Service
public class StudentServiceImpl implements StudentService, InitializingBean {
    @Value("${data.storage.path}")
    private String dataStoragePath;
    
    private String getDataFilePath() {
        // 使用 classpath 相对路径
        return "backend/" + dataStoragePath + File.separator + "students.dat";
    }

    private static BPlusTree<Integer, Student> bPlusTree = new BPlusTree<>(1000);

    @Override
    public void afterPropertiesSet() throws Exception {
        File dataDir = new File(dataStoragePath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        bPlusTree = com.xsh.util.Datautil.loadData(getDataFilePath());
        System.out.println("成功加载学生数据");
    }

    private void saveData() {
        try {
            com.xsh.util.Datautil.saveData(bPlusTree, getDataFilePath());
            System.out.println("成功保存学生数据");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("保存数据失败: " + e.getMessage());
        }
    }

    // 对象销毁时保存数据
    @PreDestroy
    public void onDestroy() {
        System.out.println("StudentServiceImpl 正在销毁，保存数据中...");
        saveData();
    }

    @Override
    public void insertStudent(Student student) {
        // 直接使用学生提供的学号作为键
        bPlusTree.insert(student.getNumber(), student);
        saveData(); // 保存更改
        System.out.println("已添加学生，学号: " + student.getNumber());
    }

    @Override
    public void deleteStudent(int studentId) {
        // 检查学生是否存在
        if (bPlusTree.search(studentId) != null) {
            // 创建新的B+树
            BPlusTree<Integer, Student> newTree = new BPlusTree<>(1000);
            
            // 遍历所有学生数据
            List<Student> allStudents = getAllStudents();
            for (Student student : allStudents) {
                if (student.getNumber() != studentId) {
                    newTree.insert(student.getNumber(), student);
                }
            }
            
            // 替换旧树
            bPlusTree = newTree;
            saveData(); // 保存更改
            System.out.println("已删除学号为 " + studentId + " 的学生");
        }
    }

    @Override
    public void updateStudent(Student student) {
        int number = student.getNumber();
        Student existingStudent = bPlusTree.search(number);
        if (existingStudent != null) {
            existingStudent.setName(student.getName());
            existingStudent.setSex(student.getSex());
            existingStudent.setPhoneNumber(student.getPhoneNumber());
            existingStudent.setMail(student.getMail());
            existingStudent.setAge(student.getAge());
            
            // 确保更新专业时处理空值
            String major = student.getMajor();
            if (major == null || major.trim().isEmpty()) {
                major = "未设置";
            }
            existingStudent.setMajor(major);
            // 更新后的学生信息重新插入到B+树中
            bPlusTree.insert(number, existingStudent);
            System.out.println("学生信息已更新");
            saveData(); // 保存更改
        } else {
            System.out.println("未找到该学生信息");
        }
    }

    @Override
    public Student searchStudent(int studentId) {
        return bPlusTree.search(studentId);
    }

    @Override
    public List<Student> getAllStudents() {
        try {
            List<Student> students = bPlusTree.getAllValues();
            // 按学号排序
            students.sort((s1, s2) -> Integer.compare(s1.getNumber(), s2.getNumber()));
            System.out.println("成功获取所有学生数据, 总数: " + students.size() + ", 已按学号排序");
            return students;
        } catch (Exception e) {
            System.err.println("获取学生数据失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Student> searchStudents(String type, String keyword) {
        List<Student> allStudents = getAllStudents();
        List<Student> results = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return allStudents;
        }
        
        keyword = keyword.trim().toLowerCase();
        
        for (Student student : allStudents) {
            boolean matches = false;
            switch (type) {
                case "姓名":
                    matches = student.getName().toLowerCase().contains(keyword);
                    break;
                case "学号":
                    matches = String.valueOf(student.getNumber()).contains(keyword);
                    break;
                case "专业":
                    String major = student.getMajor().toLowerCase();
                    matches = major.contains(keyword);
                    break;
                default:
                    matches = student.getName().toLowerCase().contains(keyword) ||
                             String.valueOf(student.getNumber()).contains(keyword) ||
                             student.getMajor().toLowerCase().contains(keyword);
            }
            if (matches) {
                results.add(student);
            }
        }
        
        return results;
    }
}
