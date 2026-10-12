# Document Store & Search Engine

A Java search engine and document database, built entirely from scratch — no built-in maps, trees, or heaps.

## What it does

- Stores text and binary documents, each identified by a URI
- Searches documents by keyword or prefix, ranked by relevance
- Filters search results by custom metadata (tags)
- Supports multi-step undo for every change
- Automatically moves old, unused documents from memory to disk when limits are hit

## How it works

- **Trie** — indexes every word for fast keyword and prefix search
- **B-Tree** — stores documents, with built-in support for paging data to disk
- **Min-Heap** — tracks least-recently-used documents for automatic memory cleanup
- **Stack** — powers the undo history
- **JSON (via Gson)** — saves documents to disk when they're evicted from memory

Every one of these data structures was implemented from the ground up, not pulled from Java's standard library.

## Built with

Java 17 · Maven · JUnit 5 · Gson

## Project layout

This repo is split into 6 stages, each building on the last. **Stage 6** (`stage6`) is the final, complete version of the project.

See [PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md) for a deeper technical breakdown.
