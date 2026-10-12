package edu.yu.cs.com1320.project.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {
    private Map<E, Integer> elementToIndex; 
    

    public MinHeapImpl() {
        this.elements = (E[]) new Comparable[5];
        this.elementToIndex = new HashMap<>();
    }


    @Override
    public void reHeapify(E element) {
        int index = getArrayIndex(element); 
        if (index == -1) {
            throw new NoSuchElementException("element at that index doesn't exist"); 
        }
        upHeap(index);
        downHeap(getArrayIndex(element)); 
    }

    @Override
    protected void swap(int i, int j) {
        super.swap(i, j);
        this.elementToIndex.put(elements[i], i);
        this.elementToIndex.put(elements[j], j);
    }

    @Override
    public void insert(E x) {
        this.elementToIndex.put(x, this.count + 1);
        super.insert(x);

    }

    @Override
    public E remove() {
        E minElement = super.remove();
        this.elementToIndex.remove(minElement);

        return minElement; 
    }
    
    @Override
    protected int getArrayIndex(E element) {
        return this.elementToIndex.getOrDefault(element, -1); 
    }

    @Override
    protected void doubleArraySize() {    
        this.elements = Arrays.copyOf(this.elements, this.elements.length * 2); 
    }
}
