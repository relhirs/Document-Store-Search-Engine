package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage4.Document;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;





public class DocumentImpl implements Document {
    private URI uri;
    private String txt;
    private byte[] binaryData;
    private HashTable<String, String> metadata; 
    private Map<String, Integer> wordMap; 
   
    

    
    public DocumentImpl(URI uri, String txt) {
        if (uri == null || uri.toString().isBlank() || txt == null || txt.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.txt = txt;
        this.metadata = new HashTableImpl<>(); 
        this.wordMap = new HashMap<>(); 
        fillWordMap(txt); 
    }

    
    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || uri.toString().isBlank() || binaryData == null || binaryData.length == 0) {
            throw new IllegalArgumentException();
        }

        this.uri = uri;
        this.binaryData = binaryData;
        this.metadata = new HashTableImpl<>();
        this.wordMap = new HashMap<>();
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
    public HashTable<String, String> getMetadata() {
        HashTable<String, String> metadataCopy = new HashTableImpl<>();

        for (String key : this.metadata.keySet()) {
            metadataCopy.put(key, this.metadata.get(key));
        }
        return metadataCopy; 
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

    private void fillWordMap(String text) {
        String[] words = text.split("[^a-zA-Z0-9]+");

        for (String word : words) {
            if (word.isEmpty()) continue; 

            this.wordMap.put(word, this.wordMap.getOrDefault(word, 0) + 1); 
        }
    }

    @Override
    public int wordCount(String word) {
        return this.wordMap.getOrDefault(word, 0); 
    }

    @Override
    public Set<String> getWords() {
        return this.wordMap.keySet(); 
    }

}

