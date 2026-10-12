package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl; 

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    private HashTable<URI, Document> storage;


    public DocumentStoreImpl() {
        this.storage = new HashTableImpl<>(); 

    }

    @Override
    public String setMetadata(URI uri, String key, String value) {
        if (uri == null || uri.toString().isBlank() || key == null || key.isBlank()) {
            throw new IllegalArgumentException("URI/Key cannot be null or blank");
        }
        Document docStore = this.storage.get(uri);
        if (docStore == null) {
            throw new IllegalArgumentException("No document stored at that URI");
        }

        return docStore.setMetadataValue(key, value);  

    }

    @Override
    public String getMetadata(URI uri, String key) {
        if (uri == null || uri.toString().isBlank() || key == null || key.isBlank()) {
            throw new IllegalArgumentException("URI/Key cannot be null or blank");
        }

        Document docStore = this.storage.get(uri);
        if (docStore == null) {
            throw new IllegalArgumentException("No Document stored at that URI");
        }

        return docStore.getMetadataValue(key); 
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        Document deletedDoc = null; 
        if (uri == null || uri.toString().isBlank() || format == null) {
            throw new IllegalArgumentException("URI cannot be null or empty. Format cannot be null");
        }

        if (input == null) {
            deletedDoc = this.storage.put(uri, null);
            return (deletedDoc != null) ? deletedDoc.hashCode() : 0;
        }

        Document newDoc = null; 
        String text = null; 

        byte[] data = input.readAllBytes();
        switch(format) {
            case TXT:
                text = new String(data);
                newDoc = new DocumentImpl(uri, text);
                break;
            case BINARY: 
                newDoc = new DocumentImpl(uri, data); 
                break;
        }

        Document oldDoc = storage.put(uri, newDoc); 
        return (oldDoc != null) ? oldDoc.hashCode() : 0;          
    }

    
    @Override
    public Document get(URI url) {
        
        return this.storage.get(url); 
    }

    @Override
    public boolean delete(URI url) {
        return this.storage.put(url, null) != null; 
    }

}