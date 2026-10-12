package edu.yu.cs.com1320.project.stage2;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.stage2.impl.DocumentImpl;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;


public class DocumentImplTest {
    private URI uri;
    private DocumentImpl doc; 


    @BeforeEach
    void setUp() {
        this.uri = URI.create("http://www.yu.edu/doc");
        this.doc = new DocumentImpl(uri, "Sample Text"); 
    }

    @Test
    @DisplayName("Test for null URI argument for DocumentImpl(URI uri, String txt)")
    void testConstructor_First_URI_Null() {
          assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(null, "Sample Text"); 
        });
    }

    @Test
    @DisplayName("Test for blank URI argument for DocumentImpl(URI uri, String txt)")
    void testConstructor_First_URI_Blank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(URI.create(" "), "Sample Text"); 
        });
    }

    @Test
    @DisplayName("Test for null txt argument for DocumentImpl(URI uri, String txt)")
    void testConstructor_First_Txt_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(uri, (String) null); 
        });
    }

    @Test
    @DisplayName("Test for blank txt argument for DocumentImpl(URI uri, String txt)")
    void testConstructor_First_Txt_Blank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(uri, "   "); 
        });
    }

    @Test
    @DisplayName("Test for null URI argument for DocumentImpl(URI uri, byte[] binaryData)")
    void testConstructor_Second_URI_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(null, new byte[]{1, 2, 3}); 
        });
    }

    @Test
    @DisplayName("Test for blank URI argument for DocumentImpl(URI uri, byte[] binaryData)")
    void testConstructor_Second_URI_Blank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(URI.create(" "), new byte[]{1, 2, 3}); 
        });
    }

    @Test
    @DisplayName("Test for null binaryData argument for DocumentImpl(URI uri, byte[] binaryData)")
    void testConstructor_Second_BinaryData_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(uri, (byte[]) null); 
        });
    }

    @Test
    @DisplayName("Test for empty binaryData argument for DocumentImpl(URI uri, byte[] binaryData)")
    void testConstructor_Second_BinaryData_Empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(uri, new byte[0]); 
        });
    }

      

    @Test
    void testSetMetadataValue_Null_Key() {
        assertThrows(IllegalArgumentException.class, () -> {
            doc.setMetadataValue(null, "value");
        });

    }

    @Test
    void testSetMetadataValue_Blank_Key() {
        assertThrows(IllegalArgumentException.class, () -> {
            doc.setMetadataValue(" ", "value"); 
        });
    }

    @Test
    void testGetMetadataValue_Null_Key() {
        assertThrows(IllegalArgumentException.class, () -> {
            doc.getMetadataValue(null);
        });
    }

    @Test
    void testGetMetadataValue_Blank_Key() {
        assertThrows(IllegalArgumentException.class, () -> {
            doc.getMetadataValue(" ");
        });
    }

    @Test
    @DisplayName("Ensures that the copy returned is seperate from the original")
     void testGetMetadata () {
        doc.setMetadataValue("Name", "Ariel");
        HashTable<String, String> copyOfMetadata = doc.getMetadata(); 
        copyOfMetadata.put("Month", "Feburary");
        assertNull(doc.getMetadataValue("Month"), "The original document metadata should not contain the value 'February' for the 'Month' key because it was only added to the defensive copy.");
    }

    @Test
    void testGetDocumentTxt() {
        assertNull(doc.getDocumentBinaryData(), "A text document should return null for binary data"); 
    }

    @Test
    void testGetDocumentBinaryData() {
        byte[] bytes = {1, 2, 3};
        DocumentImpl binaryDoc = new DocumentImpl(uri, bytes);
        assertNull(binaryDoc.getDocumentTxt(), "A binary document should return null for text data"); 
    }

    @Test
    void testGetKey() {
        assertEquals(this.uri, doc.getKey(), "The URI returned by getKey() must match the one it was created with"); 
    }

    @Test
    @DisplayName("Ensures that two identical docuemnts have the same hashCode")
    void TesthashCode_Equality() {
        DocumentImpl doc1 = new DocumentImpl(uri, "Sample Text");
        DocumentImpl doc2 = new DocumentImpl(uri, "Sample Text");

        assertEquals(doc1.hashCode(), doc2.hashCode(), "Two identical documents must have the same hashCode");
    }

    @Test
    void TesthashCode_Always_Positive() {
        URI negativeUri = URI.create("http://www.yu.edu/negativeDoc");
        DocumentImpl negativeDoc = new DocumentImpl(negativeUri, "Sample Text");
        
        int hCode = negativeDoc.hashCode();
        assertTrue(hCode >= 0, "The hashCode cannot be negative"); 
    }

    @Test
    @DisplayName("Ensures that only objects of same class can be compared to determine equality")
    void Testequals_Class_Type() {
        Object notADocumentImpl = "Just a string, not a DocumentImpl";
        assertFalse(doc.equals(notADocumentImpl), "equals must return false when comparing object thats not a DocumentImpl"); 

    }

    @Test
    @DisplayName("Ensures that the return value for null objects in false isntead of throwing NullPointerException")
    void Testequals_Null() {
        Object nullObj = null;
        assertFalse(doc.equals(nullObj), "equals must return false when comparing a null object");
    }    
}
