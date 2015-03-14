package org.ethereum.trie;

import org.ethereum.datasource.KeyValueDataSource;

/**
 *  null values for KeyValueDataSources are acceptable: an in-memory test KeyValueDataSource should be supplied.
 */  
public interface TrieFactory {
  public Trie    createSimpleTrie( KeyValueDataSource db, byte[] rootHash );
  public Trie    createSecureTrie( KeyValueDataSource db, byte[] rootHash );
  public FatTrie createFatTrie( KeyValueDataSource securedb, KeyValueDataSource insecuredb, byte[] rootHash );
}
