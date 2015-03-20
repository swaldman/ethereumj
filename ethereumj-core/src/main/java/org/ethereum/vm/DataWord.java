package org.ethereum.vm;

import org.ethereum.util.ByteUtil;
import org.ethereum.util.FastByteComparisons;

import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;
import org.spongycastle.math.raw.Nat256;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.nio.ByteBuffer;

/**
 * DataWord is the 32-byte array representation of a 256-bit number
 * Calculations can be done on this word with other DataWords
 *
 * @author Roman Mandeleil
 * @since 01.06.2014
 */
public class DataWord implements Comparable<DataWord> {

    private static final byte[] EMPTY = new byte[0];

    /* Maximum value of the DataWord */
    public static final BigInteger _2_256 = BigInteger.valueOf(2).pow(256);
    public static final BigInteger MAX_VALUE = _2_256.subtract(BigInteger.ONE);
    public static final DataWord ZERO = new DataWord(BigInteger.ZERO);      // don't push it in to the stack
    public static final DataWord ZERO_EMPTY_ARRAY = new DataWord(new byte[0]) {      // don't push it in to the stack
       void computeDataBytes() {
	   this.dataBytes = EMPTY;
       }
    };

    private static final int INT_LEN  = 8;
    private static final int BYTE_LEN = 32;

    private static final int[] ONE      = Nat256.fromBigInteger( BigInteger.ONE );
    private static final int[] MAXV     = Nat256.fromBigInteger( MAX_VALUE );

    int[]      dataInts; // note that these get encoded as *little endian* ints, rather than Java std big-endian!
    byte[]     dataBytes = null;
    BigInteger biVal     = null;

    int[] swappee = Nat256.create();

    private void markMutated() {
	this.dataBytes = null;
	this.biVal = null;
    }
    private void swap() {
	int[] tmp = this.dataInts;
	this.dataInts = swappee;
	this.swappee = tmp;
    }

    private DataWord(int[] ints) {
	this.dataInts = ints;
    }

    public DataWord() {
	this.dataInts = Nat256.create();
    }

    public DataWord(BigInteger bi) {
	this.dataInts = Nat256.fromBigInteger(bi);
    }

    public DataWord(int num) {
        this(BigInteger.valueOf(num));
    }

    public DataWord(long num) {
        this(BigInteger.valueOf(num));
    }

    public DataWord(String data) {
        this(Hex.decode(data));
    }

    public DataWord(byte[] data) {
        if (data == null)
            this.dataInts = Nat256.create();
        else if (data.length <= BYTE_LEN)
	    this.dataInts = Nat256.fromBigInteger(new BigInteger(1, data)); //treat as unsigned value
        else
            throw new RuntimeException("Data word can't exceed 32 bytes: " + data);
    }

    void computeDataBytes() {
	BigInteger bi = Nat256.toBigInteger( dataInts );
	if ( bi.compareTo( MAX_VALUE ) > 0 ) { // overflow
	    bi = bi.mod( _2_256 );
	}
	byte[] raw = bi.toByteArray();
	byte[] padded = new byte[32];
	if ( raw.length == 33 && raw[0] == 0) {
	    System.arraycopy( raw, 1, padded, 0, 32 );
	} else {
	    System.arraycopy( raw, 0, padded, padded.length - raw.length, raw.length );
	}
	this.dataBytes = padded;
    }

    public byte[] getData() {
	if (dataBytes == null) computeDataBytes();
	return dataBytes;
    }

    public byte[] getNoLeadZeroesData() {
        return ByteUtil.stripLeadingZeroes(this.getData());
    }

    public byte[] getLast20Bytes() {
        return Arrays.copyOfRange(this.getData(), 12, BYTE_LEN);
    }

    public BigInteger value() {
	if ( biVal == null ) biVal = Nat256.toBigInteger( dataInts );
	return biVal;
    }

    public int intValue() {
        if (this.bytesOccupied() > 4)
            return Integer.MAX_VALUE;
        return value().intValueExact();
    }

    /**
     * Converts this DataWord to a long, checking for lost information.
     * If this DataWord is out of the possible range for a long result
     * then an ArithmeticException is thrown.
     *
     * @return this DataWord converted to a long.
     * @throws ArithmeticException - if this will not fit in a long.
     */
    public long longValue() {
        return value().longValueExact();
    }

    /**
     * Interpret dataBytes as a signed value.
     */
    public BigInteger sValue() {
        return new BigInteger(getData());
    }

    public boolean isZero() {
        for (int tmp : dataInts) {
            if (tmp != 0) return false;
        }
        return true;
    }

    // only in case of signed operation
    // when the number is explicit defined
    // as negative
    public boolean isNegative() {
        int result = getData()[0] & 0x80;
        return result == 0x80;
    }

    public DataWord and(DataWord w2) {
        for (int i = 0; i < INT_LEN; ++i) {
            this.dataInts[i] &= w2.dataInts[i];
        }
	markMutated();
        return this;
    }

    public DataWord or(DataWord w2) {
        for (int i = 0; i < INT_LEN; ++i) {
            this.dataInts[i] |= w2.dataInts[i];
        }
	markMutated();
        return this;
    }

    public DataWord xor(DataWord w2) {
        for (int i = 0; i < INT_LEN; ++i) {
            this.dataInts[i] ^= w2.dataInts[i];
        }
	markMutated();
        return this;
    }

    public void negate() {
        if (!this.isZero()) {
	    for (int i = 0; i < INT_LEN; ++i) {
		this.dataInts[i] ^= -1;
	    }
	    Nat256.add( this.dataInts, ONE, swappee );
	    swap();
	    markMutated();
	}
    }

    public void bnot() {
        if (!this.isZero()) {
	    Nat256.sub( MAXV, this.dataInts, swappee );
	    swap();
	    markMutated();
	}
    }

    public void add(DataWord word) {
	Nat256.add( this.dataInts, word.dataInts, swappee );
	swap();
	markMutated();
    }

    // old add-method with BigInteger quick hack
    public void add2(DataWord word) {
        BigInteger result = value().add(word.value());
        this.dataInts = Nat256.fromBigInteger( result );
	markMutated();
    }

    public void mul(DataWord word) {
	try {
	    Nat256.mul( this.dataInts, word.dataInts, swappee );
	    swap();
	} catch ( IndexOutOfBoundsException e ) { // overflow
	    BigInteger result = value().multiply(word.value());
	    this.dataInts = Nat256.fromBigInteger(result.and(MAX_VALUE));
	}
	markMutated();
    }

    // TODO: improve with no BigInteger
    public void div(DataWord word) {

        if (word.isZero()) {
            this.and(ZERO);
            return;
        }

        BigInteger result = value().divide(word.value());
        this.dataInts = Nat256.fromBigInteger( result );
	markMutated();
    }

    // TODO: improve with no BigInteger
    public void sDiv(DataWord word) {

        if (word.isZero()) {
            this.and(ZERO);
            return;
        }

        BigInteger result = sValue().divide(word.sValue());
        this.dataInts = Nat256.fromBigInteger( result.and(MAX_VALUE) ); 
	markMutated();
    }


    // TODO: improve with no BigInteger
    public void sub(DataWord word) {
	Nat256.sub( this.dataInts, word.dataInts, swappee );
	swap();
	markMutated();
    }

    // TODO: improve with no BigInteger
    public void exp(DataWord word) {
        BigInteger result = value().modPow(word.value(), _2_256);
        this.dataInts = Nat256.fromBigInteger( result );
	markMutated();
    }

    // TODO: improve with no BigInteger
    public void mod(DataWord word) {

        if (word.isZero()) {
            this.and(ZERO);
            return;
        }

        BigInteger result = value().mod(word.value());
        this.dataInts = Nat256.fromBigInteger( result );
	markMutated();
    }

    // TODO: improve with no BigInteger
    public void sMod(DataWord word) {

        if (word.isZero()) {
            this.and(ZERO);
            return;
        }

        BigInteger result = sValue().abs().mod(word.sValue().abs());
        result = (sValue().signum() == -1) ? result.negate() : result;
        this.dataInts = Nat256.fromBigInteger( result );
	markMutated();
    }

    public void addmod(DataWord word1, DataWord word2) {
        this.add(word1);
        this.mod(word2);
    }

    // TODO: improve with no BigInteger
    public void mulmod(DataWord word1, DataWord word2) {

        if (word2.isZero()) {
            this.and(ZERO);
            return;
        }

        BigInteger result = value().multiply(word1.value()).mod(word2.value());
        this.dataInts = Nat256.fromBigInteger( result );
	markMutated();
    }

    public String toString() {
        return Hex.toHexString(getData());
    }

    public String shortHex() {
        String hexValue = Hex.toHexString(getNoLeadZeroesData()).toUpperCase();
        return "0x" + hexValue.replaceFirst("^0+(?!$)", "");
    }

    public DataWord clone() {
        return new DataWord(Arrays.clone(dataInts));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataWord dataWord = (DataWord) o;

        return java.util.Arrays.equals(dataInts, dataWord.dataInts);

    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(dataInts);
    }

    @Override
    public int compareTo(DataWord o) {
        if (o == null || o.getData() == null) return -1;
        int result = FastByteComparisons.compareTo(
		this.getData(), 0, BYTE_LEN,
                o.getData(), 0, BYTE_LEN);
        // Convert result into -1, 0 or 1 as is the convention
        return (int) Math.signum(result);
    }

    // TODO: improve with no BigInteger
    public void signExtend(byte k) {
        if (0 > k || k > 31)
            throw new IndexOutOfBoundsException();

	byte[] d = this.getData();
        byte mask = this.sValue().testBit((k * 8) + 7) ? (byte) 0xff : 0;
        for (int i = 31; i > k; i--) {
            d[31 - i] = mask;
        }
	this.dataInts = Nat256.fromBigInteger( new BigInteger(1, d) );
	markMutated();
    }

    public int bytesOccupied() {
        int firstNonZero = ByteUtil.firstNonZeroByte(getData());
        if (firstNonZero == -1) return 0;
        return 31 - firstNonZero + 1;
    }

    public boolean isHex(String hex) {
        return Hex.toHexString(getData()).equals(hex);
    }
}
