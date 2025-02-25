import sys
from PyQt6.QtWidgets import QApplication, QMainWindow, QWidget, QVBoxLayout, QLabel, QLineEdit, QPushButton, QMessageBox
from PyQt6.QtCore import Qt
import requests
import json
from student_window import StudentWindow

class LoginWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("学生管理系统 - 登录")
        self.setFixedSize(400, 300)
        
        # 创建主窗口部件
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        layout = QVBoxLayout(central_widget)
        layout.setAlignment(Qt.AlignmentFlag.AlignCenter)
        
        # 添加标题
        title = QLabel("学生管理系统")
        title.setStyleSheet("font-size: 24px; margin-bottom: 20px;")
        title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title)
        
        # 用户名输入
        self.username_input = QLineEdit()
        self.username_input.setPlaceholderText("用户名")
        self.username_input.setFixedWidth(200)
        layout.addWidget(self.username_input)
        
        # 密码输入
        self.password_input = QLineEdit()
        self.password_input.setPlaceholderText("密码")
        self.password_input.setEchoMode(QLineEdit.EchoMode.Password)
        self.password_input.setFixedWidth(200)
        layout.addWidget(self.password_input)
        
        # 登录按钮
        login_button = QPushButton("登录")
        login_button.setFixedWidth(200)
        login_button.clicked.connect(self.login)
        layout.addWidget(login_button)
        
        # 状态标签
        self.status_label = QLabel("")
        self.status_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(self.status_label)

    def login(self):
        username = self.username_input.text()
        password = self.password_input.text()
        
        if not username or not password:
            self.status_label.setText("请输入用户名和密码")
            self.status_label.setStyleSheet("color: red;")
            return
            
        try:
            response = requests.post(
                'http://localhost:8080/auth/login',
                json={'userName': username, 'password': password}
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get('code') == '200' and data.get('data', {}).get('token'):
                    token = data.get('data', {}).get('token')
                    self.status_label.setText("登录成功")
                    self.status_label.setStyleSheet("color: green;")
                    # 打开学生管理界面
                    self.student_window = StudentWindow(token)
                    self.student_window.show()
                    self.close()
                else:
                    error_msg = data.get('msg', '登录失败')
                    self.status_label.setText(error_msg)
                    self.status_label.setStyleSheet("color: red;")
            else:
                self.status_label.setText("服务器错误")
                self.status_label.setStyleSheet("color: red;")
                
        except requests.exceptions.RequestException:
            self.status_label.setText("连接服务器失败")
            self.status_label.setStyleSheet("color: red;")

def main():
    try:
        app = QApplication(sys.argv)
        window = LoginWindow()
        window.show()
        return app.exec()
    except Exception as e:
        print(f"程序出现错误: {str(e)}")
        return 1

if __name__ == '__main__':
    sys.exit(main())

if __name__ == '__main__':
    main()
