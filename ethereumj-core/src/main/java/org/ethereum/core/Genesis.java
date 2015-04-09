package org.ethereum.core;

import org.ethereum.trie.Trie;
import org.ethereum.trie.TrieManager;

import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

import static org.ethereum.core.Denomination.FINNEY;
import static org.ethereum.core.Denomination.WEI;
import static org.ethereum.crypto.HashUtil.*;

/**
 * The genesis block is the first block in the chain and has fixed values according to
 * the protocol specification. The genesis block is 13 items, and is specified thus:
 *
 * ( zerohash_256 , SHA3 RLP () , zerohash_160 , stateRoot, 0, 2^22 , 0, 0, 1000000, 0, 0, 0, SHA3 (42) , (), () )
 *
 * - Where zerohash_256 refers to the parent hash, a 256-bit hash which is all zeroes;
 * - zerohash_160 refers to the coinbase address, a 160-bit hash which is all zeroes;
 * - 2^22 refers to the difficulty;
 * - 0 refers to the timestamp (the Unix epoch);
 * - the transaction trie root and extradata are both 0, being equivalent to the empty byte array.
 * - The sequences of both uncles and transactions are empty and represented by ().
 * - SHA3 (42) refers to the SHA3 hash of a byte array of length one whose first and only byte is of value 42.
 * - SHA3 RLP () value refers to the hash of the uncle lists in RLP, both empty lists.
 *
 * See Yellow Paper: http://www.gavwood.com/Paper.pdf (Appendix I. Genesis Block)
 */
public class Genesis extends Block {

    public final static BigInteger PREMINE_AMOUNT = BigInteger.valueOf(2).pow(200);


    private static PremineRaw[] premine = new PremineRaw[]{
        new PremineRaw(Hex.decode("dbdbdb2cbd23b783741e8d7fcf51e459b497e4a6"), PREMINE_AMOUNT, WEI),
        new PremineRaw(Hex.decode("e6716f9544a56c530d868e4bfbacb172315bdead"), PREMINE_AMOUNT, WEI),
        new PremineRaw(Hex.decode("b9c015918bdaba24b4ff057a92a3873d6eb201be"), PREMINE_AMOUNT, WEI),
        new PremineRaw(Hex.decode("1a26338f0d905e295fccb71fa9ea849ffa12aaf4"), PREMINE_AMOUNT, WEI),
        new PremineRaw(Hex.decode("2ef47100e0787b915105fd5e3f4ff6752079d5cb"), PREMINE_AMOUNT, WEI),
        new PremineRaw(Hex.decode("cd2a3d9f938e13cd947ec05abc7fe734df8dd826"), PREMINE_AMOUNT, WEI),
        new PremineRaw(Hex.decode("6c386a4b26f73c802f34673f7248bb118f97424a"), PREMINE_AMOUNT, WEI),
        new PremineRaw(Hex.decode("e4157b34ea9615cfbde6b4fda419828124b70c78"), PREMINE_AMOUNT, WEI),

        new PremineRaw(Hex.decode("b0afc46d9ce366d06ab4952ca27db1d9557ae9fd"), new BigInteger("154162184"), FINNEY),
        new PremineRaw(Hex.decode("f6b1e9dc460d4d62cc22ec5f987d726929c0f9f0"), new BigInteger("102774789"), FINNEY),
        new PremineRaw(Hex.decode("cc45122d8b7fa0b1eaa6b29e0fb561422a9239d0"), new BigInteger("51387394"), FINNEY),
        new PremineRaw(Hex.decode("b7576e9d314df41ec5506494293afb1bd5d3f65d"), new BigInteger("69423399"), FINNEY),
    };



    private static byte[] zeroHash256 = new byte[32];
    private static byte[] zeroHash160 = new byte[20];
    private static byte[] zeroHash512 = new byte[64];

    public static byte[] PARENT_HASH = zeroHash256;
    public static byte[] UNCLES_HASH = EMPTY_LIST_HASH;
    public static byte[] COINBASE = zeroHash160;
    public static byte[] LOG_BLOOM = zeroHash512;
    public static byte[] DIFFICULTY = BigInteger.valueOf(2).pow(17).toByteArray();
    public static long NUMBER = 0;
    public static long GAS_LIMIT = 1000000;
    public static long GAS_USED = 0;
    public static long TIMESTAMP = 0;
    public static byte[] EXTRA_DATA = new byte[0];
    public static byte[] NONCE = sha3(new byte[]{42});

    private static Block instance;

    private Genesis() {
        super(PARENT_HASH, UNCLES_HASH, COINBASE, LOG_BLOOM, DIFFICULTY,
                NUMBER, GAS_LIMIT, GAS_USED, TIMESTAMP,
                EXTRA_DATA, NONCE, null, null);

        Trie state = TrieManager.createTrie();
        // The proof-of-concept series include a development pre-mine, making the state root hash
        // some value stateRoot. The latest documentation should be consulted for the value of the state root.

        for (PremineRaw raw : premine) {
            AccountState acctState = new AccountState(BigInteger.ZERO,
                    raw.getValue().multiply(raw.getDenomination().value()));
            state.update(raw.getAddr(), acctState.getEncoded());
        }

        setStateRoot(state.getRootHash());
    }

    public static Block getInstance() {
        if (instance == null) {
            instance = new Genesis();
        }
        return instance;
    }

    public final static PremineRaw[] getPremine() {
        return premine;
    }


}

