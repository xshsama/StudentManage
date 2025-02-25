package com.xsh.util;

import com.xsh.entity.Student;
import com.xsh.structure.BPlusTree;

import java.io.*;

public class Datautil {

    public static void saveData(BPlusTree<Integer, Student> bPlusTree, String filePath) {
      File file = new File(filePath);
        // 确保父目录存在
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(bPlusTree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BPlusTree<Integer, Student> loadData(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (BPlusTree<Integer, Student>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new BPlusTree<>(1000);
        }
    }
}
