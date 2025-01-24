package com.minimessage.test.algorithm.linklist;

import java.util.Iterator;

/**
 * 带哨兵的双向链表
 */
public class DoublyLinkedListSentinel implements Iterable<Integer> {
    static class Node {
        Node prev;
        Node next;
        int value;

        public Node(Node prev, Node next, int value) {
            this.prev = prev;
            this.next = next;
            this.value = value;
        }
    }

    private Node head; //头部哨兵
    private Node tail; //尾部哨兵

    public DoublyLinkedListSentinel() {
        head = new Node(null, null, Integer.MAX_VALUE);
        tail = new Node(null, null, Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    private Node findNode(int index) {
        int i = -1;
        for (Node p = head; p != tail; p = p.next, i++) {
            if (i == index) {
                return p;
            }
        }
        return null;
    }

    public void insert(int index, int value) {
        Node prev = findNode(index - 1);
        if (prev == null) {
            throw new IllegalArgumentException();
        }
        Node next = prev.next;
        Node node = new Node(prev, next, value);
        prev.next = node;
        next.prev = node;
    }

    public void addFirst(int value) {
        insert(0, value);
    }
    
    public void addLast(int value){
        Node last = tail.prev;
        Node newNode = new Node(last, tail, value);
        last.next = newNode;
        tail.prev = newNode;
    }

    public void removeLast(){
        Node removed = tail.prev;
        if (removed == tail){
            throw new IllegalArgumentException();
        }
        Node prev = removed.prev;
        prev.next = tail;
        tail.prev = prev;
    }

    public void remove(int index) {
        Node prev = findNode(index - 1);
        if (prev == null){
            throw new IllegalArgumentException();
        }
        Node removed = prev.next;
        if (removed == tail){
            throw new IllegalArgumentException();
        }
        Node next = removed.next;
        prev.next = next;
        next.prev = prev;
    }

    @Override
    public Iterator<Integer> iterator() {
        return null;
    }

    public static void main(String[] args) {

    }
}
