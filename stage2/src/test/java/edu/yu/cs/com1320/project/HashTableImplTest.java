package edu.yu.cs.com1320.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class HashTableImplTest {
    private HashTable<Object, Object> table;

    @BeforeEach
    public void setup() {
        this.table = new HashTableImpl<>(); 
    }

    @Test
    void test_Negative_HashCode() {
        table.put(-1, "sample text");
        assertEquals("sample text", table.get(-1)); 
    }

    @Test
    void test_collisions() {
        table.put(0, "zero");
        table.put(5, "five");

        assertEquals("zero", table.get(0));
        assertEquals("five", table.get(5),"Prepending a node should return that node");
        assertEquals("zero", table.get(0), "Prepending a node cannot delete the other exisiting nodes");
    }
    
    @Test
    void test_get_Null_Key() {
        assertNull(table.get(null));

        table.put("key1", "Sample text");
        table.put("key2", "Sample text2");
        assertNull(table.get(null), "calling .get() with null parameter must return null even if table populated");
        assertEquals(2, table.size(), "size of table shouldn't be affected when calling .get(null)");
    }

    @Test
    @DisplayName("Ensures that the value returned is the correct pair to the key")
    void test_Get_Key_() {
        table.put("key", "value"); 
        assertEquals("value", table.get("key"), "calling .get() must return the value is associated with the key");
    }

    @Test
    void test_Get_Key_Not_Found_Empty_Table() {
        assertNull(table.get("key"), "calling .get() on a key that was never in the hashtable must return null");
    }

    @Test
    void test_Get_Key_Not_Found_Populated_Table() {
        table.put("key", "value");
        table.put("key2", "value2");
        assertNull(table.get("key3"));
    }

    @Test
    void test_Put_Null_Key() {
        table.put(null, "sample text");
        assertNull(table.get(null));
    }

    @Test
    void test_Put_New_Key_Returns_Null() {
        Object returnedValue = table.put("key", "value");
        assertNull(returnedValue, "put() should return null when a new key-value pair is added"); 
    }

    @Test
    void test_Delete_Via_Null_Value() {
        table.put("key", "value");
        Object deletedValue = table.put("key", null);
        assertEquals("value", deletedValue, "calling .put() with null key should delete it from the hashtable and return the previous value");
        assertEquals(0, table.size(), "deleting a key should remove it from the hashtable");
        assertFalse(table.containsKey("key"));
    }

    @Test
    void test_Deleting_Node_Middle_In_List() {
        table.put(0, "zero");
        table.put(5, "five");
        table.put(10, "10");

        table.put(5, null);
        assertEquals(2, table.size());
        assertEquals("zero", table.get(0));
        assertEquals("10", table.get(10));
    }

    @Test
    void test_Contains_Null_Key() throws NullPointerException {
        assertThrows(NullPointerException.class, () -> {
            table.containsKey(null);
        }); 
    }

    @Test
    void test_Contains_Key_Populated_Table() {
        table.put("key", "value");
        table.put("key2", "value2");
        assertTrue(table.containsKey("key"));
        assertEquals(2, table.size());
    }

    @Test
    void test_Contains_Key_Empty_Table() {
        assertFalse(table.containsKey("key")); 

    }

    @Test
    void test_Contains_Key_In_Collision() {
        table.put(0, "zero");
        table.put(5, "five");
        table.put(10, "10");
        
        assertTrue(table.containsKey(0));
        assertTrue(table.containsKey(5));
        assertTrue(table.containsKey(10));
    }


    @Test
    void test_Content_keySet_Unmodifiable() {
        table.put(0, "zero");
        table.put(5, "five");
        table.put(10, "10");
        Set<Object> keys = table.keySet();
       
        assertTrue(keys.containsAll(List.of(0, 5, 10)));
        assertEquals(3, keys.size());

        assertThrows(UnsupportedOperationException.class, () -> {
            keys.add("key");
        });
    }

    @Test
    void test_Content_Values_Unmodifiable() {
        table.put(0, "zero");
        table.put(5, "five");
        table.put(10, "ten");
        Collection<Object> values = table.values();
        assertEquals(3, values.size());
        assertTrue(values.containsAll(List.of("zero", "five", "ten")));
        assertThrows(UnsupportedOperationException.class, () -> {
            values.add("newKey"); 
        });
    }
       
}