package org.ethereum.vm;

/**
 * The fundamental network cost unit. Paid for exclusively by Ether, which is converted
 * freely to and from Gas as required. Gas does not exist outside of the internal Ethereum
 * computation engine; its price is set by the Transaction and miners are free to
 * ignore Transactions whose Gas price is too low.
 */
public class GasCost {

    /**
     * Cost 1 gas
     */
    public final static int STEP = 1;
    /**
     * Cost 20 gas
     */
    public final static int BALANCE = 20;
    /**
     * Cost 10 gas
     */
    public final static int SHA3 = 10;
    /**
     * Cost 10 gas
     */
    public final static int SHA3_WORD = 10;
    /**
     * Cost 20 gas
     */
    public final static int SLOAD = 20;
    /**
     * Cost 0 gas
     */
    public final static int STOP = 0;
    /**
     * Cost 0 gas
     */
    public final static int SUICIDE = 0;
    /**
     * Cost 300 gas
     */
    public final static int SSTORE = 300;
    /**
     * Cost 100 gas
     */
    public final static int RESET_SSTORE = 100;
    /**
     * Cost 100 gas
     */
    public final static int REFUND_SSTORE = 100;
    /**
     * Cost 100 gas
     */
    public final static int CREATE = 100;
    /**
     * Cost 1 gas
     */
    public final static int CREATE_DATA_BYTE = 5;
    /**
     * Cost 20 gas
     */
    public final static int CALL = 20;
    /**
     * Cost 1 gas
     */
    public final static int MEMORY = 1;
    /**
     * Cost 5 gas
     */
    public final static int TX_NO_ZERO_DATA = 5;
    /**
     * Cost 1 gas
     */
    public final static int TX_ZERO_DATA = 1;
    /**
     * Cost 500 gas
     */
    public final static int TRANSACTION = 500;
    /**
     * Cost 32 gas
     */
    public static int LOG_GAS = 32;
    /**
     * Cost 1 gas
     */
    public final static int LOG_DATA_GAS = 1;
    /**
     * Cost 32 gas
     */
    public final static int LOG_TOPIC_GAS = 32;
    /**
     * Cost 1 gas
     */
    public final static int COPY_GAS = 1;
    /**
     * Cost 1 gas
     */
    public final static int EXP_GAS = 1;
    /**
     * Cost 1 gas
     */
    public final static int EXP_BYTE_GAS = 1;


}

