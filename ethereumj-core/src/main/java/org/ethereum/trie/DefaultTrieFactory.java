package org.ethereum.trie;

import org.ethereum.datasource.KeyValueDataSource;

public class DefaultTrieFactory implements TrieFactory {
    public Trie createSimpleTrie( KeyValueDataSource db, byte[] rootHash ) {
	return new TrieImpl( db, rootHash );
    }
    public Trie createSecureTrie( KeyValueDataSource db, byte[] rootHash ) {
	return new SecureTrie( db, rootHash );
    }
    public FatTrie createFatTrie( KeyValueDataSource insecuredb, KeyValueDataSource securedb, byte[] rootHash ) {
	return new FatTrieImpl( insecuredb, securedb, rootHash );
    }
}
