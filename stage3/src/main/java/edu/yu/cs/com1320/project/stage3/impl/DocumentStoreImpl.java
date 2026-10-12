package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.undo.Command;

import java.util.function.Consumer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    private HashTable<URI, Document> storage;
    private Stack<Command> commandStack;


    public DocumentStoreImpl() {
        this.storage = new HashTableImpl<>(); 
        this.commandStack = new StackImpl<Command>(); 
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
        String oldValue = docStore.setMetadataValue(key, value); 

        Consumer<URI> undoLogic = (URI targetUri) -> {
            docStore.setMetadataValue(key, oldValue); 
        };
        
        this.commandStack.push(new Command(uri, undoLogic)); 
        return oldValue;  
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
        if (uri == null || uri.toString().isBlank() || format == null) {
            throw new IllegalArgumentException("URI cannot be null or empty. Format cannot be null");
        }
        if (input == null) {
            Document deletedDoc = this.storage.put(uri, null);
            Consumer<URI> undoLogic = (URI targetUri) -> {
                this.storage.put(targetUri, deletedDoc);
            };
            this.commandStack.push(new Command(uri, undoLogic));
            return (deletedDoc != null) ? deletedDoc.hashCode() : 0;
        }
        Document newDoc = makeDoc(input, uri, format); 
        Document oldDoc = storage.put(uri, newDoc);

        if (oldDoc == null) {
            Consumer<URI> undoLogic = (URI targetUri) -> {
                this.storage.put(targetUri, null);
            };
            this.commandStack.push(new Command(uri, undoLogic)); 
        } else {
            Consumer<URI> undoLogic = (URI targetUri) -> {
                this.storage.put(targetUri, oldDoc);
            };
            this.commandStack.push(new Command(uri, undoLogic)); 
        }
        return (oldDoc != null) ? oldDoc.hashCode() : 0;          
    }

    private Document makeDoc(InputStream input, URI uri, DocumentFormat format) throws IOException {
        Document document = null; 
        String text = null; 

        byte[] data = input.readAllBytes();
        switch(format) {
            case TXT:
                text = new String(data);
                document = new DocumentImpl(uri, text);
                break;
            case BINARY: 
                document = new DocumentImpl(uri, data); 
                break;
        }
        return document; 
    }

    
    @Override
    public Document get(URI url) {   
        return this.storage.get(url); 
    }

    @Override
    public boolean delete(URI url) {
        Document deletedDoc = this.storage.put(url, null);

        Consumer<URI> undoLogic = (URI targetUri) -> {
            this.storage.put(targetUri, deletedDoc);
        };
        this.commandStack.push(new Command(url, undoLogic));

        return deletedDoc != null; 
    }

    @Override
    public void undo() throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Cannot undo action on an empty stack");
        }
        Command poppedCommand = commandStack.pop();
        poppedCommand.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Cannot undo action on an empty stack");
        }
        boolean found = false; 
        Stack<Command> otherStack = new StackImpl<Command>();

        while (commandStack.size() > 0) {
            Command current = commandStack.pop();

            if (current.getUri().equals(url)) {
                current.undo();
                found = true;
                break; 
            } else {
                otherStack.push(current); 
            }    
        }
        while (otherStack.size() > 0) {
            commandStack.push(otherStack.pop()); 
        }
        if (!found) {
            throw new IllegalStateException("Cannot undo action for a URL that doesn't exist");
        }
    }

}