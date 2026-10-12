package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.stage1.Document;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DocumentImpl implements Document {
    private URI uri;
    private String txt;
    private byte[] binaryData;
    private Map<String, String> metadata; 
   
    

    
    public DocumentImpl(URI uri, String txt) {
        if (uri == null || uri.toString().isBlank() || txt == null || txt.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = txt;
        this.metadata = new HashMap<>(); 
    }

    
    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || uri.toString().isBlank() || binaryData == null || binaryData.length == 0) {
            throw new IllegalArgumentException();
        }

        this.uri = uri;
        this.binaryData = binaryData; 
        this.metadata = new HashMap<>(); 
    }


   
    @Override
   public String setMetadataValue(String key, String value) {
         if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(); 
         }

         return metadata.put(key, value); 
    }

    @Override
    public String getMetadataValue(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException();
        }
        return metadata.get(key);   
    }

    @Override
    public HashMap<String, String> getMetadata() {
        return new HashMap<>(this.metadata); 
    }

    @Override
    public String getDocumentTxt() {
        return this.txt; 
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    @Override
    public URI getKey() { 
        return this.uri; 
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (txt != null ? txt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        
        return Math.abs(result);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DocumentImpl)) return false;

        DocumentImpl other = (DocumentImpl) o;
        return this.hashCode() == other.hashCode(); 
    }

}

