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

    public static Trie createTrie( KeyValueDataSource db, byte[] rootHash ) {
	return FACTORY.createTrie( db, rootHash );
    }

    public static Trie createTrie( KeyValueDataSource db ) {
	return FACTORY.createTrie( db, EMPTY_TRIE_HASH );
    }

    public static Trie createTrie() {
	return FACTORY.createTrie( null, EMPTY_TRIE_HASH );
    }
    
    private TrieManager() {}
}
