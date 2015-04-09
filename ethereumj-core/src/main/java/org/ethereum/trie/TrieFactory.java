package org.ethereum.trie;

import org.ethereum.datasource.KeyValueDataSource;

/**
 *  null values for KeyValueDataSources are acceptable: an in-memory test KeyValueDataSource should be supplied.
 *  null values for rootHash are acceptable: EMPTY_TRIE_HASH wil be supplied.
 */  
public interface TrieFactory {
  public Trie createTrie( KeyValueDataSource db, byte[] rootHash );
}
