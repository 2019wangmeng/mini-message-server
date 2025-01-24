package com.minimessage.test.algorithm.linklist;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 带哨兵的单向链表
 */
public class SinglyLinkedListSentinel implements Iterable<Integer> {
    private Node head = new Node(Integer.MAX_VALUE, null); //头指针

    /**
     * 节点类
     */
    private static class Node {
        int value;
        Node next; //下一个节点

        public Node(int value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    /**
     * 向链表头部添加
     *
     * @param value
     */
    public void addFirst(int value) {
        //链表为空
        //链表非空
        head.next = new Node(value, head);
    }

    public void loop1(Consumer<Integer> consumer) {
        Node p = head.next;
        while (p != null) {
            consumer.accept(p.value);
            p = p.next;
        }
    }

    public void loop2(Consumer<Integer> consumer) {
        for (Node p = head.next; p != null; p = p.next) {
            consumer.accept(p.value);
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return new NodeIterator();
    }

    private class NodeIterator implements Iterator<Integer> {
        Node p = head.next;

        @Override
        public boolean hasNext() {
            return p != null;
        }

        @Override
        public Integer next() {
            int v = p.value;
            p = p.next;
            return v;
        }
    }

    private Node findLast() {
        Node p;
        for (p = head; p != null; p = p.next) {

        }
        return p;
    }

    public void addLast(int value) {
        Node last = findLast();
        last.next = new Node(value, null);
    }

    private Node findNode(int index) {
        int i = -1;
        for (Node p = head; p != null; p = p.next, i++) {
            if (i == index) {
                return p;
            }
        }
        return null;


    }

    public int get(int index) {
        Node node = findNode(index);
        if (node == null) {
            throw new IllegalArgumentException();
        }
        return node.value;
    }

    public void insert(int index, int value) {
        if (index == 0) {
            addFirst(value);
            return;
        }
        Node prev = findNode(index - 1);//找到索引位置的上一个节点
        if (prev == null) {
            throw new IllegalArgumentException();
        }
        prev.next = new Node(value, prev.next);
    }

    public void removeFirst() {
        if (head == null) {
            throw new IllegalArgumentException();
        }
        head = head.next;
    }

    public void remove(int index) {
        if (index == 0) {
            removeFirst();
        }
        Node prev = findNode(index - 1);
        if (prev == null) {
            throw new IllegalArgumentException();
        }
        Node removed = prev.next;
        if (removed == null) {
            throw new IllegalArgumentException();
        }
        prev.next = removed.next;
    }
}
