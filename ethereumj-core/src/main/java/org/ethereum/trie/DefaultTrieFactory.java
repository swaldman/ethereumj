package org.ethereum.trie;

import org.ethereum.datasource.KeyValueDataSource;

public class DefaultTrieFactory implements TrieFactory {
    public Trie createTrie( KeyValueDataSource db, byte[] rootHash ) {
	return new TrieImpl( db, rootHash );
    }
}
