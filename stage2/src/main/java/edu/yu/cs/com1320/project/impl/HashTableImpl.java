package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;
import java.util.Set;
import java.util.Collection;
import java.util.List;


public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {
    private Node[] table; 
    private int count = 0; 

    private class Node {
        Key key;
        Value value;
        Node next; 

        Node (Key key, Value value) {
            this.key = key;
            this.value = value;
        }
    }


    public HashTableImpl() {
        this.table = (Node[]) new HashTableImpl.Node[5];       
    }

    private int hashFunction(Key key) {
        return (key.hashCode() & 0x7fffffff) % this.table.length; 
    }


    @Override
    public Value get(Key k) {
        if (k == null) return null;
        
        int index = hashFunction(k);
        Node current = table[index];

        while (current != null) {
            if (current.key.equals(k)) {
                return current.value;
            }
            current = current.next;
        }
        return null; 
    }

    @Override
    public Value put(Key k, Value v) {
        if (k == null) return null;     
        int index = hashFunction(k);
        Node current = table[index];
        Node previous = null;       
        while (current != null) {
            if (current.key.equals(k)) {
                Value oldValue = current.value;
                if (v == null) {
                    if (previous == null) {
                        table[index] = current.next;
                        count--; 
                    } else {
                        previous.next = current.next; 
                        count--; 
                    }
                } else {
                    current.value = v;
                }
                return oldValue; 
            }
            previous = current; 
            current = current.next; 
        }
        if (v != null) {
           addNode(k, v, index); 
        }      
        return null; 
    }

    private void addNode(Key k, Value v, int index) {
        if (v != null) {
            Node newNode = new Node(k, v);
            newNode.next = table[index];
            table[index] = newNode;
            count++; 
        }   
    }

    @Override
    public boolean containsKey(Key key) {
        if (key == null) {
            throw new NullPointerException("Key searching for is null");
        }
        int index = hashFunction(key);
        Node current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next; 
        }
        return false;
    }

    @Override
    public Set<Key> keySet() {
        Key[] keysArray = (Key[]) new Object[count];
        int index = 0; 
        for (Node head : table) {
                Node current = head;
                while (current != null) {
                    keysArray[index++] = current.key;
                    current = current.next;
                }     
        }
        return Set.of(keysArray); 
    }

    @Override
    public Collection<Value> values() {
        Value[] valuesArray = (Value[]) new Object[count];
        int index = 0; 
        for (Node head : table) {
                Node current = head;
                while (current != null) {
                    valuesArray[index++] = current.value; 
                    current = current.next;
                }      
        }
        return List.of(valuesArray); 
    }

    @Override
    public int size() {
        return count; 
    }

}