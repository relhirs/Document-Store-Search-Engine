package edu.yu.cs.com1320.project.impl;


import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class MinHeapImplTest {
    private MinHeapImpl<Integer> minHeap;

    @BeforeEach
    public void setUp() {
        this.minHeap = new MinHeapImpl<Integer>();
        this.minHeap.insert(10);
        this.minHeap.insert(20);
        this.minHeap.insert(5);
    }
    // Heap state: 5, 20, 10 

    @Test
    void test_Reheapify_throws_noSuchElementException() {
        int index = this.minHeap.getArrayIndex(99);
        assertEquals(-1, index, "index that doesn't exist returns -1");

        assertThrows(NoSuchElementException.class, () -> {
            this.minHeap.reHeapify(99);
        });
    }
    
    @Test
    void test_remove() {
        int minElement = this.minHeap.remove();
        assertEquals(5, minElement);
    }

    @Test
    void test_swap() {
        this.minHeap.swap(1, 3);
        int topElement = this.minHeap.peek();
        assertEquals(10, topElement);
    }
    
    @Test
    void test_get_index() {
        int topIndex = this.minHeap.getArrayIndex(5);
        int middleIndex = this.minHeap.getArrayIndex(10);
        int lastIndex = this.minHeap.getArrayIndex(20);
        assertEquals(1, topIndex);
        assertEquals(3, middleIndex);
        assertEquals(2, lastIndex);
    }

    @Test
    @DisplayName("Insert enough elements to trigger doubleArraySize()")
    void testArrayResize() {
        MinHeapImpl<Integer> largeHeap = new MinHeapImpl<>();
        
        // Starts at capacity 5. Inserting 10 will force doubleArraySize() to trigger
        for (int i = 10; i > 0; i--) {
            largeHeap.insert(i);
        }
        
        assertEquals(1, largeHeap.remove(), "Smallest element should be 1");
        assertEquals(2, largeHeap.remove(), "Next element should be 2");
    }

    @Test
    @DisplayName("Test reHeapify moving an element UP and DOWN")
    void testReheapifyMovements() {
        // Setup a custom node so we can mutate its value while it sits inside the heap
        class MutableNode implements Comparable<MutableNode> {
            int value;
            MutableNode(int v) { this.value = v; }
            @Override 
            public int compareTo(MutableNode o) { 
                return Integer.compare(this.value, o.value); 
            }
        }
        
        MinHeapImpl<MutableNode> heap = new MinHeapImpl<>();
        MutableNode node1 = new MutableNode(10);
        MutableNode node2 = new MutableNode(20);
        MutableNode node3 = new MutableNode(30);
        
        heap.insert(node1);
        heap.insert(node2);
        heap.insert(node3);
        
        // Mutate node1 to a larger value to force it to move DOWN the heap
        node1.value = 50; 
        heap.reHeapify(node1);
        assertEquals(20, heap.peek().value, "Root should now be 20 since 50 moved down");
        
        // Mutate node3 to a smaller value to force it to move UP the heap
        node3.value = 5;
        heap.reHeapify(node3);
        assertEquals(5, heap.peek().value, "Root should now be 5 since it moved up");
    }
}