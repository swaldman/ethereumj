package org.ethereum.trie;

import org.ethereum.datasource.KeyValueDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ethereum.config.SystemProperties.CONFIG;
import static org.ethereum.crypto.HashUtil.EMPTY_TRIE_HASH;

public final class TrieManager {

    private static final Logger logger = LoggerFactory.getLogger("trie");

    final static TrieFactory FACTORY;

    static {
	String fqcn = CONFIG.trieFactory();
	TrieFactory tmp;
	try { tmp = (TrieFactory) Class.forName( fqcn ).newInstance(); }
	catch ( Exception e ) {
	    if ( logger.isWarnEnabled() )
		logger.warn("Failed to instantiate desired TrieFactory '" + fqcn + "'. Using default.", e ); 
	    tmp = new DefaultTrieFactory();
	}
	FACTORY = tmp;
    }

    public static Trie createSimpleTrie( KeyValueDataSource db, byte[] rootHash ) {
	return FACTORY.createSimpleTrie( db, rootHash );
    }

    public static Trie createSecureTrie( KeyValueDataSource db, byte[] rootHash ) {
	return FACTORY.createSecureTrie( db, rootHash );
    }

    public static FatTrie createFatTrie( KeyValueDataSource insecuredb, KeyValueDataSource securedb, byte[] rootHash ) {
	return FACTORY.createFatTrie( insecuredb, securedb, rootHash );
    }

    public static Trie createSimpleTrie( KeyValueDataSource db ) {
	return FACTORY.createSimpleTrie( db, EMPTY_TRIE_HASH );
    }

    // note that an empty SecureTrie hashes identically to an empty regular Trie
    // there are no keys to be hashed to render anything different.
    public static Trie createSecureTrie( KeyValueDataSource db ) {
	return FACTORY.createSecureTrie( db, EMPTY_TRIE_HASH );
    }

    public static FatTrie createFatTrie( KeyValueDataSource insecuredb, KeyValueDataSource securedb ) {
	return FACTORY.createFatTrie( insecuredb, securedb, EMPTY_TRIE_HASH );
    }
    
    public static Trie createSimpleTrie() {
	return FACTORY.createSimpleTrie( null, EMPTY_TRIE_HASH );
    }
    
    public static Trie createSecureTrie() {
	return FACTORY.createSecureTrie( null, EMPTY_TRIE_HASH );
    }

    public static FatTrie createFatTrie() {
	return FACTORY.createFatTrie( null, null, EMPTY_TRIE_HASH );
    }
    
    private TrieManager() {}
}
