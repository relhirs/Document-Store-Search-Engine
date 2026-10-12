package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet; 
import java.util.List;
import java.util.ArrayList;


public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {
    private Node[] table; 
    private int count = 0; 
    private final double TABLE_THRESHOLD = 0.75; 

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

    private int hashFunction(Key key, int tableLength) {
        return (key.hashCode() & 0x7fffffff) % tableLength; 
    }


    @Override
    public Value get(Key k) {
        if (k == null) return null;
        
        int index = hashFunction(k, table.length);
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
        int index = hashFunction(k, table.length);
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
            vNotNull(k, v, index); 
        }      
        return null; 
    }

    private void vNotNull(Key k, Value v, int index) {
        Node newNode = new Node(k, v);
            newNode.next = table[index];
            table[index] = newNode;
            count++; 
            
            if (count > table.length * TABLE_THRESHOLD) {
                resize(); 
            }
    }

    private void resize() {
        Node[] newTable = (Node[]) new HashTableImpl.Node[table.length * 2]; 

        for (int i = 0; i < table.length; i++) {
            Node current = table[i];
                while (current != null) {
                    Node nextNode = current.next; 
                    int index = hashFunction(current.key, newTable.length);
                    current.next = newTable[index];
                    newTable[index] = current; 
                    current = nextNode; 
                }
        }
        this.table = newTable;      
    }

    @Override
    public boolean containsKey(Key key) {
        if (key == null) {
            throw new NullPointerException("Key searching for is null");
        }
        int index = hashFunction(key, table.length);
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
        Set<Key> keys = new HashSet<>(); 

        for (Node head : table) {
                Node current = head;
                while (current != null) {
                    keys.add(current.key);
                    current = current.next;
                }     
        }
        return Collections.unmodifiableSet(keys); 
    }

    @Override
    public Collection<Value> values() {
        List<Value> values = new ArrayList<>();

        for (Node head : table) {
                Node current = head;
                while (current != null) {
                    values.add(current.value);
                    current = current.next;
                }      
        }
        return Collections.unmodifiableCollection(values); 
    }

    @Override
    public int size() {
        return count; 
    }

}