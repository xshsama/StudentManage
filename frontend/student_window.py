from PyQt6.QtWidgets import (QMainWindow, QWidget, QVBoxLayout, QHBoxLayout, 
                          QTableWidget, QTableWidgetItem, QPushButton, 
                          QDialog, QLineEdit, QLabel, QMessageBox, QHeaderView,
                          QComboBox)  # 添加 QComboBox
from PyQt6.QtCore import Qt
import requests

class StudentWindow(QMainWindow):
    def __init__(self, token):
        super().__init__()
        self.token = token
        self.setWindowTitle("学生管理系统")
        self.setMinimumSize(800, 600)
        
        # 创建主窗口部件
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        layout = QVBoxLayout(central_widget)
        
        # 顶部布局
        top_layout = QHBoxLayout()
        
        # 添加按钮
        add_button = QPushButton("添加学生")
        add_button.clicked.connect(self.show_add_dialog)
        top_layout.addWidget(add_button)
        
        # 搜索部分
        self.search_type = QComboBox()
        self.search_type.addItems(["姓名", "学号", "专业"])
        top_layout.addWidget(self.search_type)
        
        self.search_input = QLineEdit()
        self.search_input.setPlaceholderText("请输入搜索内容")
        top_layout.addWidget(self.search_input)
        
        search_button = QPushButton("搜索")
        search_button.clicked.connect(self.search_students)
        top_layout.addWidget(search_button)
        
        reset_button = QPushButton("重置")
        reset_button.clicked.connect(self.load_students)
        top_layout.addWidget(reset_button)
        
        layout.addLayout(top_layout)
        
        # 创建表格
        self.table = QTableWidget()
        self.table.setColumnCount(6)
        self.table.setHorizontalHeaderLabels(["学号", "姓名", "年龄", "专业", "操作", ""])
        self.table.horizontalHeader().setSectionResizeMode(QHeaderView.ResizeMode.Stretch)
        layout.addWidget(self.table)
        
        # 完成UI初始化后加载数据
        self.loadAllData()

    def search_students(self):
        search_text = self.search_input.text().strip()
        search_type = self.search_type.currentText()
        
        try:
            headers = {'Authorization': f'Bearer {self.token}'}
            params = {
                'type': search_type,
                'keyword': search_text
            }
            response = requests.get('http://localhost:8080/api/students/search', 
                                 headers=headers,
                                 params=params)
            
            if response.status_code == 200:
                result = response.json()
                students = result.get('data', [])
                self.update_table(students)
            else:
                QMessageBox.warning(self, "错误", "搜索失败")
                
        except requests.exceptions.RequestException:
            QMessageBox.warning(self, "错误", "连接服务器失败")
            
    def update_table(self, students):
        self.table.setRowCount(len(students))
        for row, student in enumerate(students):
            self.table.setItem(row, 0, QTableWidgetItem(str(student['number'])))
            self.table.setItem(row, 1, QTableWidgetItem(student['name']))
            self.table.setItem(row, 2, QTableWidgetItem(str(student['age'])))
            self.table.setItem(row, 3, QTableWidgetItem(student.get('major', '未设置')))
            
            # 添加编辑按钮
            edit_btn = QPushButton("编辑")
            edit_btn.clicked.connect(lambda checked, s=student: self.show_edit_dialog(s))
            self.table.setCellWidget(row, 4, edit_btn)
            
            # 添加删除按钮
            delete_btn = QPushButton("删除")
            delete_btn.clicked.connect(lambda checked, number=student['number']: self.delete_student(number))
            self.table.setCellWidget(row, 5, delete_btn)
        
    def loadAllData(self):
        """在窗口初始化时立即加载所有学生数据"""
        try:
            headers = {'Authorization': f'Bearer {self.token}'}
            response = requests.get('http://localhost:8080/api/students', headers=headers)
            
            if response.status_code == 200:
                result = response.json()
                students = result.get('data', [])
                self.update_table(students)
                print(f"成功加载 {len(students)} 个学生数据")  # 添加调试日志
            else:
                QMessageBox.warning(self, "错误", "获取学生列表失败")
                print("加载学生数据失败：", response.text)  # 添加调试日志
                
        except requests.exceptions.RequestException as e:
            QMessageBox.warning(self, "错误", "连接服务器失败")
            print("连接服务器失败：", str(e))  # 添加调试日志

    def load_students(self):
        """用于刷新按钮点击时重新加载数据"""
        self.loadAllData()
            
    def show_add_dialog(self):
        dialog = StudentDialog(self)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            self.add_student(dialog.get_student_data())
            
    def show_edit_dialog(self, student):
        dialog = StudentDialog(self, student)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            self.update_student(student['number'], dialog.get_student_data())
            
    def add_student(self, student_data):
        try:
            headers = {'Authorization': f'Bearer {self.token}'}
            print("Adding student with data:", student_data)  # 添加调试日志
            response = requests.post(
                'http://localhost:8080/api/students',
                json=student_data,
                headers=headers
            )
            print("Server response:", response.text)  # 添加调试日志
            
            if response.status_code == 200:
                QMessageBox.information(self, "成功", "添加学生成功")
                self.load_students()
            else:
                result = response.json()
                error_msg = result.get('msg', '添加学生失败')
                QMessageBox.warning(self, "错误", error_msg)
                
        except requests.exceptions.RequestException:
            QMessageBox.warning(self, "错误", "连接服务器失败")
            
    def update_student(self, student_number, student_data):
        try:
            headers = {'Authorization': f'Bearer {self.token}'}
            print(f"Updating student {student_number} with data:", student_data)  # 添加调试日志
            response = requests.put(
                f'http://localhost:8080/api/students/{student_number}',
                json=student_data,
                headers=headers
            )
            print("Server response:", response.text)  # 添加调试日志
            
            if response.status_code == 200:
                QMessageBox.information(self, "成功", "更新学生信息成功")
                self.load_students()
            else:
                QMessageBox.warning(self, "错误", "更新学生信息失败")
                
        except requests.exceptions.RequestException:
            QMessageBox.warning(self, "错误", "连接服务器失败")
            
    def delete_student(self, student_number):
        reply = QMessageBox.question(
            self,
            "确认删除",
            "确定要删除这名学生吗？",
            QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No
        )
        
        if reply == QMessageBox.StandardButton.Yes:
            try:
                headers = {'Authorization': f'Bearer {self.token}'}
                response = requests.delete(
                    f'http://localhost:8080/api/students/{student_number}',
                    headers=headers
                )
                
                if response.status_code == 200:
                    QMessageBox.information(self, "成功", "删除学生成功")
                    self.load_students()
                else:
                    QMessageBox.warning(self, "错误", "删除学生失败")
                    
            except requests.exceptions.RequestException:
                QMessageBox.warning(self, "错误", "连接服务器失败")

class StudentDialog(QDialog):
    def __init__(self, parent=None, student=None):
        super().__init__(parent)
        self.student = student
        self.setWindowTitle("添加学生" if student is None else "编辑学生信息")
        self.setup_ui()
        
    def setup_ui(self):
        layout = QVBoxLayout(self)
        
        # 学号输入
        number_layout = QHBoxLayout()
        number_label = QLabel("学号:")
        self.number_input = QLineEdit()
        if self.student:
            self.number_input.setText(str(self.student.get('number', '')))
            self.number_input.setReadOnly(True)  # 编辑模式下学号不可修改
        number_layout.addWidget(number_label)
        number_layout.addWidget(self.number_input)
        layout.addLayout(number_layout)
        
        # 姓名输入
        name_layout = QHBoxLayout()
        name_label = QLabel("姓名:")
        self.name_input = QLineEdit()
        if self.student:
            self.name_input.setText(self.student.get('name', ''))
        name_layout.addWidget(name_label)
        name_layout.addWidget(self.name_input)
        layout.addLayout(name_layout)
        
        # 年龄输入
        age_layout = QHBoxLayout()
        age_label = QLabel("年龄:")
        self.age_input = QLineEdit()
        if self.student:
            self.age_input.setText(str(self.student.get('age', 0)))
        age_layout.addWidget(age_label)
        age_layout.addWidget(self.age_input)
        layout.addLayout(age_layout)

        # 专业输入
        major_layout = QHBoxLayout()
        major_label = QLabel("专业:")
        self.major_input = QLineEdit()
        self.major_input.setPlaceholderText("请输入专业")
        if self.student:
            major = self.student.get('major', '未设置')
            self.major_input.setText(major)
        major_layout.addWidget(major_label)
        major_layout.addWidget(self.major_input)
        layout.addLayout(major_layout)
        
        # 按钮
        button_layout = QHBoxLayout()
        save_button = QPushButton("保存")
        save_button.clicked.connect(self.validate_and_accept)
        cancel_button = QPushButton("取消")
        cancel_button.clicked.connect(self.reject)
        button_layout.addWidget(save_button)
        button_layout.addWidget(cancel_button)
        layout.addLayout(button_layout)

    def validate_and_accept(self):
        try:
            if not self.student:  # 添加新学生时验证学号
                student_number = self.number_input.text().strip()
                if not student_number:
                    QMessageBox.warning(self, "错误", "请输入学号")
                    return
                try:
                    int(student_number)
                except ValueError:
                    QMessageBox.warning(self, "错误", "学号必须是数字")
                    return
            
            # 验证必填字段
            if not self.name_input.text().strip():
                QMessageBox.warning(self, "错误", "请输入姓名")
                return
                
            self.accept()
        except Exception as e:
            QMessageBox.warning(self, "错误", f"输入验证失败: {str(e)}")

    def get_student_data(self):
        major = self.major_input.text().strip()
        
        # 如果未输入新专业且是编辑模式，使用原有专业
        if not major and self.student:
            major = self.student.get('major', '')
        
        # 构建数据
        data = {
            'number': int(self.number_input.text().strip()),
            'name': self.name_input.text().strip(),
            'age': int(self.age_input.text().strip() or '0'),
            'major': major if major else '未设置'
        }
        print("Generated student data:", data)
        return data
