package org.ethereum.trie;

public interface FatTrie extends Trie {
    public Trie getOrigTrie();
    public Trie getSecureTrie();
}
