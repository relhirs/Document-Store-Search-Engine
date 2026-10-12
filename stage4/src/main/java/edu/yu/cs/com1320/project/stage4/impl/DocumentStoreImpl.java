package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    private HashTable<URI, Document> storage;
    private Stack<Undoable> commandStack;
    private Trie<Document> trie; 


    public DocumentStoreImpl() {
        this.storage = new HashTableImpl<>(); 
        this.commandStack = new StackImpl<Undoable>(); 
        this.trie = new TrieImpl<>(); 
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
        
        this.commandStack.push(new GenericCommand<URI>(uri, undoLogic)); 
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
        Document existingDoc = this.storage.get(uri);

        if (input == null) {
            if (existingDoc != null) {
                this.storage.put(uri, null); 
                removeDocFromTrie(existingDoc); 
            
            Consumer<URI> undoLogic = (URI targetUri) -> {
                this.storage.put(targetUri, existingDoc);
                addDocToTrie(existingDoc); 
            };
            this.commandStack.push(new GenericCommand<URI>(uri, undoLogic));
            return existingDoc.hashCode();
        }
            return 0; 
        }
        Document newDoc = makeDoc(input, uri, format); 
        Document oldDoc = this.storage.put(uri, newDoc);

        updateIndexAndCommandStack(newDoc, oldDoc, uri); 
        return (oldDoc != null) ? oldDoc.hashCode() : 0;          
    }

    private void updateIndexAndCommandStack(Document newDoc, Document oldDoc, URI uri) {
        if (oldDoc != null) {
            removeDocFromTrie(oldDoc);
        }  
        addDocToTrie(newDoc); 

        if (oldDoc == null) {
            //Undo an add
            Consumer<URI> undoLogic = (URI targetUri) -> {
                this.storage.put(targetUri, null);
                removeDocFromTrie(newDoc); 

            };
            this.commandStack.push(new GenericCommand<URI>(uri, undoLogic)); 
        } else {
            //Undo a replace
            Consumer<URI> undoLogic = (URI targetUri) -> {
                this.storage.put(targetUri, oldDoc);
                removeDocFromTrie(newDoc);
                addDocToTrie(oldDoc);
            };
            this.commandStack.push(new GenericCommand<URI>(uri, undoLogic)); 
        }
    }

    private void addDocToTrie(Document doc) {
        for (String word : doc.getWords()) {
            this.trie.put(word, doc); 
        }
    }

    private void removeDocFromTrie(Document doc) {
        for (String word : doc.getWords()) {
            this.trie.delete(word, doc); 
        }
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
        if (deletedDoc != null) {
            removeDocFromTrie(deletedDoc); 
        }
        Consumer<URI> undoLogic = (URI targetUri) -> {
            this.storage.put(targetUri, deletedDoc);
            if (deletedDoc != null) {
                addDocToTrie(deletedDoc); 
            }
        };
        this.commandStack.push(new GenericCommand<URI>(url, undoLogic));

        return deletedDoc != null; 
    }

    @Override
    public void undo() throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Cannot undo action on an empty stack");
        }
        Undoable poppedCommand = commandStack.pop();
        poppedCommand.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Cannot undo action on an empty stack");
        }

        Stack<Undoable> otherStack = new StackImpl<>();

        boolean found = undoCommandStack(this.commandStack, otherStack, url);        
        while (otherStack.size() > 0) {
            commandStack.push(otherStack.pop()); 
        }
    
        if (!found) {
            throw new IllegalStateException("Cannot undo action for a URL that doesn't exist on the stack");
        }
    }

    private boolean undoCommandStack (Stack<Undoable> stack, Stack<Undoable> otherStack, URI url) {
        while (stack.size() > 0) {
            Undoable current = stack.pop();
            if (current instanceof GenericCommand) {
                GenericCommand<URI> generic = (GenericCommand<URI>) current; 
                if (generic.getTarget().equals(url)) {
                    generic.undo();
                    return true; 
                } else {
                    otherStack.push(current); 
                }
            } else if (current instanceof CommandSet) {
                CommandSet<URI> set = (CommandSet<URI>) current; 
                if (set.containsTarget(url)) {
                    set.undo(url);
                    if (set.size() > 0) {
                        otherStack.push(set);
                    }
                    return true; 
                } else {
                    otherStack.push(current); 
                }
            }
        }
            return false; 
    }

    @Override
    public List<Document> search(String keyword) {
        if (keyword == null) return new ArrayList<>();

        Comparator<Document> relevantDocs = (doc1, doc2) -> {
            int count1 = doc1.wordCount(keyword);
            int count2 = doc2.wordCount(keyword);

            return Integer.compare(count2, count1);
        };
        return this.trie.getSorted(keyword, relevantDocs); 
    }

   @Override
   public List<Document> searchByPrefix(String keywordPrefix) {
        if (keywordPrefix == null) {
            return new ArrayList<>();
        }
        Comparator<Document> relevantDocs = (doc1, doc2) -> {
            int count1 = calculateDocPrecedence(doc1, keywordPrefix);
            int count2 = calculateDocPrecedence(doc2, keywordPrefix);

            return Integer.compare(count2, count1);
        };
        return this.trie.getAllWithPrefixSorted(keywordPrefix, relevantDocs);


   }
   private int calculateDocPrecedence(Document doc, String keywordPrefix) {
        int totalCount = 0; 
        for (String word : doc.getWords()) {
            if (word.startsWith(keywordPrefix)) {
                totalCount += doc.wordCount(word); 
            }
        }
        return totalCount; 
   }

   @Override
   public Set<URI> deleteAll(String keyword) {
        Set<URI> deletedUris = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>(); 
        
        Set<Document> docsToDelete = this.trie.get(keyword);
        if (docsToDelete == null || docsToDelete.isEmpty()) {
            return new HashSet<>();
        }
        List<Document> copyOfDocsToDelete = new ArrayList<>(docsToDelete);
        deleteDocFromSystem(copyOfDocsToDelete, deletedUris, commandSet); 
        return deletedUris;  
   }

   @Override
   public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<URI> deletedUris = new HashSet<>(); 
        CommandSet<URI> commandSet = new CommandSet<>();

        List<Document> prefixDocs = this.trie.getAllWithPrefixSorted(keywordPrefix, (doc1, doc2) -> 0); 
        if (prefixDocs == null || prefixDocs.isEmpty()) {
            return new HashSet<>();
        }
        List<Document> copyOfDocsToDelete = new ArrayList<>(prefixDocs); 
        deleteDocFromSystem(copyOfDocsToDelete, deletedUris, commandSet); 
        return deletedUris;  
   }

   @Override
   public List<Document> searchByMetadata(Map<String, String> keysValues) {
        if (keysValues == null || keysValues.isEmpty()) {
            return new ArrayList<>();
        }
        List<Document> matches = new ArrayList<>();
        
        for (Document doc : this.storage.values()) {
            if (doc == null) {
                continue; 
            }
            boolean matchesAll = true; 
            for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                String expectedKey = entry.getKey();
                String expectedValue = entry.getValue();

                String actualValue = doc.getMetadataValue(expectedKey); 

                if (!Objects.equals(actualValue, expectedValue)) {
                    matchesAll = false; 
                    break;
                }
            }
            if (matchesAll) {
                matches.add(doc);
            }
        }
        return matches;      
   }

   @Override
   public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        if (keyword == null || keyword.isBlank() || keysValues == null || keysValues.isEmpty()) {
            return new ArrayList<>();
        }
        Comparator<Document> relevantDoc = (doc1, doc2) -> {
            int count1 = doc1.wordCount(keyword);
            int count2 = doc2.wordCount(keyword);
            return Integer.compare(count2, count1);
        }; 
        List<Document> keywordDocs = this.trie.getSorted(keyword, relevantDoc); 
        List<Document> matches = new ArrayList<>();

        if (keywordDocs.isEmpty()) return matches; 

        searchSortedDoc(keywordDocs, matches, keysValues); 
        return matches; 
   }

   private void searchSortedDoc(List<Document> document, List<Document> matches, Map<String, String> keysValues) {   
        for (Document doc : document) {
            if (doc == null) {
                continue;
            }
            boolean matchesAll = true;
            
            for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                String expectedKey = entry.getKey();
                String expectedValue = entry.getValue();
                String actualValue = doc.getMetadataValue(expectedKey);
                if (!Objects.equals(actualValue, expectedValue)) {
                    matchesAll = false; 
                    break; 
                }
            }
            if (matchesAll) {
                matches.add(doc); 
            }
        }
   }

   @Override
   public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        if (keywordPrefix == null || keywordPrefix.isBlank() || keysValues == null || keysValues.isEmpty()) {
            return new ArrayList<>();
        }
        Comparator<Document> relevantDoc = (doc1, doc2) -> {
            int count1 = calculateDocPrecedence(doc1, keywordPrefix); 
            int count2 = calculateDocPrecedence(doc2, keywordPrefix); 
            return Integer.compare(count2, count1); 
        };
        List<Document> prefixDocs = this.trie.getAllWithPrefixSorted(keywordPrefix, relevantDoc);
        List<Document> matches = new ArrayList<>();
        
        if (prefixDocs.isEmpty()) return matches;

        searchSortedDoc(prefixDocs, matches, keysValues);
        return matches;     
   }

   @Override
   public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) {
        if (keysValues == null || keysValues.isEmpty()) {
            return new HashSet<>();
        }
        Set<Document> docsToDelete = new HashSet<>(this.storage.values());
        docsToDelete.remove(null);
        filterDocsByMetadata(docsToDelete, keysValues);

        CommandSet<URI> commandSet = new CommandSet<>();
        Set<URI> deletedUris = new HashSet<>();

        deleteDocFromSystem(docsToDelete, deletedUris, commandSet);
        return deletedUris;
    }

    private void deleteDocFromSystem(Collection<Document> docsToDelete, Set<URI> deletedUris, CommandSet<URI> commandSet) {
        for (Document doc : docsToDelete) {
                URI uri = doc.getKey(); 
                this.storage.put(uri, null);
                removeDocFromTrie(doc);
                deletedUris.add(uri);
            
            Consumer<URI> undoLogic = (targetURI) -> {
                this.storage.put(targetURI, doc);
                addDocToTrie(doc); 
            }; 
            GenericCommand<URI> command = new GenericCommand<>(uri, undoLogic); 
            commandSet.addCommand(command);       
            }
            if (commandSet.size() > 0) {
                this.commandStack.push(commandSet);
            }
    }

    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        if (keyword == null || keyword.isBlank() || keysValues == null || keysValues.isEmpty()) {
            return new HashSet<>();
        }
        Set<Document> docsToDelete = this.trie.get(keyword);
        if (docsToDelete == null || docsToDelete.isEmpty()) {
            return new HashSet<>();
        }
        filterDocsByMetadata(docsToDelete, keysValues); 
       
        CommandSet<URI> commandSet = new CommandSet<>();
        Set<URI> deletedUris = new HashSet<>();
        deleteDocFromSystem(docsToDelete, deletedUris, commandSet); 
        return deletedUris; 
    }


    private void filterDocsByMetadata(Collection<Document> docsToDelete, Map<String, String> keysValues) {
        List<Document> toRemove = new ArrayList<>();
        for (Document doc : docsToDelete) {
            if (doc == null) {
                toRemove.add(doc);
                continue;
            }
            for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                if (!Objects.equals(doc.getMetadataValue(entry.getKey()), entry.getValue())) {
                    toRemove.add(doc);
                    break;
                }
            }
        }
        docsToDelete.removeAll(toRemove);
    }

    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        if (keywordPrefix == null || keywordPrefix.isBlank() || keysValues == null || keysValues.isEmpty()) {
            return new HashSet<>();
        }
        Set<URI> deletedUris = new HashSet<>(); 
        List<Document> prefixDocsToDelete = this.trie.getAllWithPrefixSorted(keywordPrefix, (doc1, doc2) -> 0);
        if (prefixDocsToDelete.isEmpty()) {
            return new HashSet<>();
        }
        filterDocsByMetadata(prefixDocsToDelete, keysValues); 
        CommandSet<URI> commandSet = new CommandSet<>();
        deleteDocFromSystem(prefixDocsToDelete, deletedUris, commandSet); 
        return deletedUris; 
    }


}
