package edu.yu.cs.com1320.project.stage1;
import edu.yu.cs.com1320.project.stage1.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;



public class DocumentStoreImplTest {
    private URI uri;
    private DocumentStoreImpl docStore;

    @BeforeEach
    void setUp() {
        this.uri = URI.create("http://site.com");
        this.docStore = new DocumentStoreImpl();
    }

private static Stream<Arguments> invalidInputs_Get_Set_Metadata() {
    return Stream.of(
        Arguments.of(null, "validKey"),             
        Arguments.of(URI.create(""), "validKey"),   
        Arguments.of(URI.create("http://site.com"), null), 
        Arguments.of(URI.create("http://site.com"), "")    
    );
}

private static Stream<Arguments> invalidInputs_Put_Metadata() {
    return Stream.of(
        Arguments.of(null, DocumentFormat.TXT),
        Arguments.of(URI.create(""), DocumentFormat.BINARY),
        Arguments.of(URI.create("http://site.com"), null)
    );
}

@ParameterizedTest
@MethodSource("invalidInputs_Get_Set_Metadata")
void testSetMetadata(URI uri, String key) {
    assertThrows(IllegalArgumentException.class, () -> {
        docStore.setMetadata(uri, key, "value"); 
    });
}

@ParameterizedTest
@MethodSource("invalidInputs_Get_Set_Metadata")
void testGetMetadata(URI uri, String key) {
    assertThrows(IllegalArgumentException.class, () -> {
        docStore.getMetadata(uri, key);
    });
}

@ParameterizedTest
@MethodSource("invalidInputs_Put_Metadata")
void testPut_Into_Storage_HashMap(URI uri, DocumentFormat format) {
    InputStream dummyInput = new ByteArrayInputStream("data".getBytes()); 
    assertThrows(IllegalArgumentException.class, () -> {
        docStore.put(dummyInput, uri, format); 
    }); 
}

@Test
void testPut_Delete_Via_Null_Input() throws IOException {
    InputStream dummyInput = new ByteArrayInputStream("data".getBytes()); 
    docStore.put(dummyInput, uri, DocumentFormat.TXT);

    int expectedHash = docStore.get(uri).hashCode(); 
    int returnedHash = docStore.put(null, uri, DocumentFormat.TXT);
    assertEquals(expectedHash, returnedHash, "deleting should returne the hashcode of the document being removed");
    
    assertNull(docStore.get(uri), "Document must be null after deletion");
}

@Test
void testPut_Delete_Returns_Zero() throws IOException {
    int result = docStore.put(null, uri, DocumentFormat.TXT);
    assertEquals(0, result, "should return 0 when deleting a null document"); 

}

@Test
void testPut_Binary_Document() throws IOException {
    byte[] expectedData = "data".getBytes();
     InputStream dummyInput = new ByteArrayInputStream(expectedData); 
     docStore.put(dummyInput, uri, DocumentFormat.BINARY);

     Document currentDoc = docStore.get(uri);
     assertArrayEquals(currentDoc.getDocumentBinaryData(), expectedData);

}

@Test
void testPut_Text_Document() throws IOException {
    InputStream txtStream = new ByteArrayInputStream("Sample Text".getBytes());

    docStore.put(txtStream, uri, DocumentFormat.TXT);

    Document currentDoc = docStore.get(uri);
    assertEquals("Sample Text", currentDoc.getDocumentTxt());
}

@Test
void testPut_Overwrite() throws IOException {
    String txtDoc1 = "Sample Text";
    InputStream textStream = new ByteArrayInputStream(txtDoc1.getBytes());

    String textDoc2 = "Sample Text 2";
    InputStream textStream2 = new ByteArrayInputStream(textDoc2.getBytes());

    int firstDoc = docStore.put(textStream, uri, DocumentFormat.TXT);
    assertEquals(0, firstDoc, "First 'put' at a URI should return 0");
    Document originalDoc = docStore.get(uri);
    int originalHash = originalDoc.hashCode();

    int returnedHash = docStore.put(textStream2, uri, DocumentFormat.TXT); 
    assertEquals(originalHash, returnedHash, "adding another document to HashMap with same URI should return same hashcode"); 
}

@Test
void test_Get_Document_Confirm_Exists() throws IOException {
    InputStream textStream = new ByteArrayInputStream("data".getBytes());
    docStore.put(textStream, uri, DocumentFormat.TXT);

    Document originalDoc = docStore.get(uri);
    assertSame(originalDoc, docStore.get(uri));
}

@Test
void test_Get_Document_When_Null() throws IOException {

    Document doc = docStore.get(uri);
    assertNull(doc, "calling 'get' on null URI must return null");

}

@Test
void test_Get_Document_When_Deleted() throws IOException {
    InputStream textStream = new ByteArrayInputStream("data".getBytes());
    docStore.put(textStream, uri, DocumentFormat.TXT);
    assertNotNull(docStore.get(uri));

    //deleting the doc by calling null Input
    docStore.put(null, uri, DocumentFormat.TXT);
    assertNull(docStore.get(uri), "deleting a document by putting in a null input should return null ");

    InputStream textStream2 = new ByteArrayInputStream("data2".getBytes());
    docStore.put(textStream2, uri, DocumentFormat.TXT);

    boolean deletedDoc = docStore.delete(uri);
    assertTrue(deletedDoc, "deleting a  non-null doc by calling 'delete' must return true");
    assertNull(docStore.get(uri));

}

@Test
void test_Delete_Exisiting_Document() throws IOException {
    InputStream textStream = new ByteArrayInputStream("data".getBytes());
    docStore.put(textStream, uri, DocumentFormat.BINARY);

    boolean deletedDoc = docStore.delete(uri);
    assertTrue(deletedDoc, "Deleting a document that is not null must return true");

}

@Test
void test_Delete_Null_Document() throws IOException {
    docStore.put(null, uri, DocumentFormat.BINARY);
    boolean deletedDoc = docStore.delete(uri);

    assertFalse(deletedDoc, "Deleting a document that is null must return false");
}

}