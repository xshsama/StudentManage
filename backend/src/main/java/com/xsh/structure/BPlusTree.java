package com.xsh.structure;

import java.io.Serializable;

import com.xsh.entity.Student;

public class BPlusTree<K extends Comparable<K>, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_T = 4;

    private Node root;
    private final int t; // minimum degree

    private abstract class Node implements Serializable {
        protected int n; // number of keys
        protected Object[] keys; // 使用 Object 数组
        protected boolean isLeaf;

        @SuppressWarnings("unchecked")
        public Node(int t) {
            this.n = 0;
            this.keys = new Object[2 * t - 1]; // 使用 Object 数组
            this.isLeaf = true;
        }

        @SuppressWarnings("unchecked")
        protected K getKey(int index) {
            return (K) keys[index];
        }

        protected void setKey(int index, K key) {
            keys[index] = key;
        }
    }

    private class LeafNode extends Node implements Serializable {
        protected Object[] values; // 使用 Object 数组
        protected LeafNode next;

        @SuppressWarnings("unchecked")
        public LeafNode(int t) {
            super(t);
            this.values = new Object[2 * t - 1]; // 使用 Object 数组
            this.next = null;
        }

        @SuppressWarnings("unchecked")
        protected V getValue(int index) {
            return (V) values[index];
        }

        protected void setValue(int index, V value) {
            values[index] = value;
        }
    }

    private class InternalNode extends Node implements Serializable {
        protected Object[] children; // 使用 Object 数组

        @SuppressWarnings("unchecked")
        public InternalNode(int t) {
            super(t);
            this.children = new Object[2 * t]; // 使用 Object 数组
            this.isLeaf = false;
        }

        protected Node getChild(int index) {
            return (Node) children[index];
        }

        protected void setChild(int index, Node child) {
            children[index] = child;
        }
    }

    public BPlusTree(int t) {
        this.t = t;
        root = new LeafNode(t);
    }

    public void insert(K key, V value) {
        Node r = root;

        if (r.n == 2 * t - 1) {
            InternalNode s = new InternalNode(t);
            root = s;
            s.setChild(0, r);
            splitChild(s, 0, r);
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(Node x, K key, V value) {
        int i = x.n - 1;

        if (x.isLeaf) {
            LeafNode leaf = (LeafNode) x;
            // 先检查是否已存在相同的键
            for (int j = 0; j < leaf.n; j++) {
                if (key.compareTo(leaf.getKey(j)) == 0) {
                    // 如果找到相同的键，直接更新值
                    leaf.setValue(j, value);
                    return;
                }
            }
            // 如果不存在相同的键，执行正常的插入
            while (i >= 0 && key.compareTo(leaf.getKey(i)) < 0) {
                leaf.setKey(i + 1, leaf.getKey(i));
                leaf.setValue(i + 1, leaf.getValue(i));
                i--;
            }
            i++;
            leaf.setKey(i, key);
            leaf.setValue(i, value);
            leaf.n++;
        } else {
            InternalNode internal = (InternalNode) x;
            while (i >= 0 && key.compareTo(internal.getKey(i)) < 0)
                i--;
            i++;
            Node child = internal.getChild(i);
            if (child.n == 2 * t - 1) {
                splitChild(internal, i, child);
                if (key.compareTo(internal.getKey(i)) > 0)
                    i++;
            }
            insertNonFull(internal.getChild(i), key, value);
        }
    }

    private void splitChild(InternalNode x, int i, Node y) {
        if (y.isLeaf) {
            LeafNode z = new LeafNode(t);
            LeafNode y2 = (LeafNode) y;

            z.n = t - 1;
            for (int j = 0; j < t - 1; j++) {
                z.setKey(j, y2.getKey(j + t));
                z.setValue(j, y2.getValue(j + t));
            }

            y2.n = t;

            for (int j = x.n; j >= i + 1; j--)
                x.setChild(j + 1, x.getChild(j));

            x.setChild(i + 1, z);

            for (int j = x.n - 1; j >= i; j--)
                x.setKey(j + 1, x.getKey(j));

            x.setKey(i, z.getKey(0));
            x.n++;

            z.next = y2.next;
            y2.next = z;
        } else {
            InternalNode z = new InternalNode(t);
            InternalNode y2 = (InternalNode) y;

            z.n = t - 1;
            for (int j = 0; j < t - 1; j++)
                z.setKey(j, y2.getKey(j + t));

            if (!y2.isLeaf)
                for (int j = 0; j < t; j++)
                    z.setChild(j, y2.getChild(j + t));

            y2.n = t - 1;

            for (int j = x.n; j >= i + 1; j--)
                x.setChild(j + 1, x.getChild(j));

            x.setChild(i + 1, z);

            for (int j = x.n - 1; j >= i; j--)
                x.setKey(j + 1, x.getKey(j));

            x.setKey(i, y2.getKey(t - 1));
            x.n++;
        }
    }

    public V search(K key) {
        Node x = root;
        while (!x.isLeaf) {
            int i = 0;
            while (i < x.n && key.compareTo(((InternalNode) x).getKey(i)) >= 0)
                i++;
            x = ((InternalNode) x).getChild(i);
        }

        LeafNode leaf = (LeafNode) x;
        int i = 0;
        while (i < leaf.n && key.compareTo(leaf.getKey(i)) > 0)
            i++;

        if (i < leaf.n && key.compareTo(leaf.getKey(i)) == 0) {
            V value = leaf.getValue(i);
            // 如果值是Student类型的，返回其克隆
            if (value instanceof Student) {
                return (V) ((Student) value).clone();
            }
            return value;
        }
        return null;
    }

    public java.util.List<V> getAllValues() {
        java.util.List<V> result = new java.util.ArrayList<>();

        // 找到最左边的叶子节点
        Node current = root;
        while (!current.isLeaf) {
            current = ((InternalNode) current).getChild(0);
        }

        // 遍历所有叶子节点
        LeafNode leaf = (LeafNode) current;
        while (leaf != null) {
            for (int i = 0; i < leaf.n; i++) {
                V value = leaf.getValue(i);
                // 如果值是Student类型的，添加其克隆
                if (value instanceof Student) {
                    result.add((V) ((Student) value).clone());
                } else {
                    result.add(value);
                }
            }
            leaf = leaf.next;
        }

        return result;
    }
}
