package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {

    private class Node {
        T data;
        Node next;

        Node(T data, Node next) {
            this.data = data;
            this.next = next; 
        }
    }

    private Node top;
    private int size; 
    
    public StackImpl() {
        this.top = null;
        this.size = 0; 
    }

    public void push(T element) {
        Node newNode = new Node(element, this.top);
        this.top = newNode;
        size++; 
    }

    public T pop() {
        if (top == null) return null;       
        
        T oldValue = this.top.data;
        this.top = this.top.next;
        size--; 
        return oldValue;
    }

    public T peek() {
        if (top == null) return null; 
        return top.data; 
    }

    public int size() {
        return size; 
    }   
}
