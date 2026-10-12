package edu.yu.cs.com1320.project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName; 
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.impl.StackImpl;

import static org.junit.jupiter.api.Assertions.*;

public class StackImplTest {
    private StackImpl<String> stack;

    @BeforeEach
    void setup() {
        this.stack = new StackImpl<>(); 
    }

    @Test
    @DisplayName("peeking/popping must return top-most element")
    void test_Push() {
        stack.push("first");
        assertEquals("first", stack.peek());
        assertEquals("first", stack.pop());
    }

    @Test
    void test_Push_Multiple_Elements() {
        stack.push("first");
        stack.push("second");
        stack.push("third");
        assertEquals("third", stack.peek());
        stack.pop(); 
        assertEquals("second", stack.pop());
    }

    @Test
    void test_Size() {
        stack.push("first");
        stack.push("second");
        stack.push("third");
        assertEquals(3, stack.size());
        stack.pop(); 
        assertEquals(2, stack.size());
    }

    @Test
    void test_Null_Input() {
        stack.push(null);
        stack.push("second");
        stack.push(null);
        assertNull(stack.pop());
        assertEquals("second", stack.pop());
        assertNull(stack.pop());
    }

    @Test
    void test_Empty_Stack() {
        assertNull(stack.pop());
        stack.pop();
        assertEquals(0, stack.size());
    }

    @Test
    void test_Full_Cycle() {
        stack.push("A");
        stack.pop();
        assertNull(stack.peek());
        assertEquals(0, stack.size());

        stack.push("B");
        stack.pop();
        assertNull(stack.peek());
        assertEquals(0, stack.size());
    }
}
