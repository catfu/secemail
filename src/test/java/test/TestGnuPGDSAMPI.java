package test;

import java.math.BigInteger;
import openpgp.keystore.util.StringHelper;
import junit.framework.TestCase;

/** 
 * Test case to test a set of DSA signature calculations, using fixed numbers,
 * which give differing results depending on which implementation is used.
 * Proxy, using java implementations, gives one result - GnuPG gives another.
 * Mith much experimentation, finally found that the problems are to do with
 * the sign. This test is now producing the same result as the GnuPG DSA 
 * signature building routine.
 */
public class TestGnuPGDSAMPI extends TestCase {
	private final static byte[] HASH = {
		(byte)0x47, (byte)0xFD, (byte)0xAF, (byte)0x42, (byte)0x19, (byte)0x5B,
		(byte)0xE1, (byte)0x68, (byte)0xBF, (byte)0xD1, (byte)0xE8, (byte)0x21,
		(byte)0x5A, (byte)0x1D, (byte)0x71, (byte)0x0D, (byte)0x1E, (byte)0x1E,
		(byte)0x23, (byte)0x2F
	};
	private final static byte[] P = {
		(byte)0xFD, (byte)0xB8, (byte)0x8D, (byte)0x50, (byte)0xF6, (byte)0x0D,
		(byte)0x4B, (byte)0x2A, (byte)0xED, (byte)0x88, (byte)0xA8, (byte)0x31,
		(byte)0xB4, (byte)0x9B, (byte)0x82, (byte)0x83, (byte)0x76, (byte)0x87,
		(byte)0xCD, (byte)0x27, (byte)0xF6, (byte)0xDB, (byte)0xA1, (byte)0x41,
		(byte)0xD1, (byte)0x11, (byte)0xED, (byte)0x7D, (byte)0xD1, (byte)0x46,
		(byte)0x4C, (byte)0xBF, (byte)0xB9, (byte)0xDB, (byte)0x00, (byte)0xAE,
		(byte)0x3C, (byte)0xDB, (byte)0xA1, (byte)0x3F, (byte)0xC2, (byte)0xF6,
		(byte)0xB2, (byte)0xBF, (byte)0xF7, (byte)0x41, (byte)0x38, (byte)0x6A,
		(byte)0x7F, (byte)0x90, (byte)0x62, (byte)0xF4, (byte)0xBE, (byte)0xAD,
		(byte)0xD3, (byte)0xA8, (byte)0x50, (byte)0xDD, (byte)0x32, (byte)0x6E,
		(byte)0x6A, (byte)0x38, (byte)0x81, (byte)0x2B, (byte)0x18, (byte)0x88,
		(byte)0x43, (byte)0x64, (byte)0x18, (byte)0x8D, (byte)0xF6, (byte)0x34, 
		(byte)0x1E, (byte)0x81, (byte)0x9E, (byte)0x45, (byte)0xAA, (byte)0x71,
		(byte)0x28, (byte)0x69, (byte)0xB8, (byte)0x6B, (byte)0x50, (byte)0xA8,
		(byte)0x82, (byte)0xB6, (byte)0x2D, (byte)0x5E, (byte)0xCF, (byte)0x1C,
		(byte)0x7E, (byte)0xD1, (byte)0x63, (byte)0x62, (byte)0x70, (byte)0xB8,
		(byte)0xC0, (byte)0x58, (byte)0x4E, (byte)0xB8, (byte)0x77, (byte)0x92,
		(byte)0xC7, (byte)0xC8, (byte)0x9B, (byte)0x4B, (byte)0x3C, (byte)0xAE,
		(byte)0x7A, (byte)0xD7, (byte)0x3C, (byte)0x69, (byte)0x4A, (byte)0x93,
		(byte)0x95, (byte)0xB5, (byte)0x2D, (byte)0x32, (byte)0x9C, (byte)0xDD,
		(byte)0x81, (byte)0x38, (byte)0x86, (byte)0x95, (byte)0x8C, (byte)0x43,
		(byte)0x56, (byte)0xC7
	};
	private final static byte[] Q = {
		(byte)0xFF, (byte)0xD3, (byte)0x77, (byte)0x4E, (byte)0xE6, (byte)0x1D,
		(byte)0x7E, (byte)0x24, (byte)0xA0, (byte)0xA9, (byte)0xA4, (byte)0xF5,
		(byte)0xB9, (byte)0xB3, (byte)0x6C, (byte)0x19, (byte)0xB6, (byte)0x22,
		(byte)0x88, (byte)0xC3
	};
	private final static byte[] G = {
		(byte)0x76, (byte)0x36, (byte)0xE6, (byte)0xFB, (byte)0x11, (byte)0xAE,
		(byte)0xEB, (byte)0xC0, (byte)0x20, (byte)0x50, (byte)0x6C, (byte)0x63,
		(byte)0xFE, (byte)0xE3, (byte)0x7B, (byte)0xA7, (byte)0xDC, (byte)0x8D,
		(byte)0x76, (byte)0x90, (byte)0xD9, (byte)0x20, (byte)0xD9, (byte)0xC9,
		(byte)0x01, (byte)0x94, (byte)0xBB, (byte)0xB9, (byte)0xAA, (byte)0xA7,
		(byte)0xFA, (byte)0x4E, (byte)0x16, (byte)0xD1, (byte)0xDA, (byte)0xB3,
		(byte)0xFD, (byte)0xC9, (byte)0xA9, (byte)0x7A, (byte)0x9C, (byte)0xC7,
		(byte)0x26, (byte)0xE6, (byte)0x3A, (byte)0xE0, (byte)0x5E, (byte)0x4B,
		(byte)0xDD, (byte)0xC2, (byte)0x65, (byte)0x0B, (byte)0xBF, (byte)0x0E,
		(byte)0x4B, (byte)0xD4, (byte)0x53, (byte)0x58, (byte)0x26, (byte)0xD2,
		(byte)0xCF, (byte)0xE7, (byte)0xBD, (byte)0x88, (byte)0xDF, (byte)0x53,
		(byte)0x32, (byte)0xF7, (byte)0xD9, (byte)0x06, (byte)0x67, (byte)0x32,
		(byte)0x08, (byte)0x63, (byte)0x51, (byte)0x02, (byte)0xF9, (byte)0xB9,
		(byte)0x14, (byte)0xA8, (byte)0x0D, (byte)0x7E, (byte)0xF0, (byte)0x04,
		(byte)0xE6, (byte)0x6D, (byte)0x4A, (byte)0xBF, (byte)0xF6, (byte)0x9C,
		(byte)0xE1, (byte)0x8E, (byte)0x30, (byte)0x53, (byte)0xCC, (byte)0xC5,
		(byte)0xB4, (byte)0x07, (byte)0x83, (byte)0xC1, (byte)0xAE, (byte)0x78,
		(byte)0xAF, (byte)0x74, (byte)0xFE, (byte)0xFE, (byte)0xB3, (byte)0x54,
		(byte)0x19, (byte)0x45, (byte)0x42, (byte)0x3E, (byte)0x0D, (byte)0x18,
		(byte)0x42, (byte)0xA7, (byte)0xB1, (byte)0x3A, (byte)0x74, (byte)0x2F,
		(byte)0xC6, (byte)0x31, (byte)0x9F, (byte)0xDC, (byte)0x0C, (byte)0x14,
		(byte)0x4F, (byte)0xDA
	};
	private final static byte[] X = {
		(byte)0x9A, (byte)0xF8, (byte)0x94, (byte)0x60, (byte)0xF0, (byte)0xFD,
		(byte)0x70, (byte)0xC2, (byte)0x7F, (byte)0xBD, (byte)0xC7, (byte)0xA7,
		(byte)0x76, (byte)0x34, (byte)0x0E, (byte)0x51, (byte)0xCD, (byte)0x94,
		(byte)0x14, (byte)0x96
	};
	private final static byte[] FIXED_K = {
		(byte)0x1F, (byte)0x1D, (byte)0x0B, (byte)0x5A, (byte)0xC2, (byte)0x48,
		(byte)0x6B, (byte)0x6B, (byte)0x5C, (byte)0xB3, (byte)0xD7, (byte)0x70,
		(byte)0xE3, (byte)0x12, (byte)0x10, (byte)0x82, (byte)0x0A, (byte)0x11,
		(byte)0xAC, (byte)0x0B
	};
	private final static byte[] GNU_EXPECTED_R = {
		(byte)0xED, (byte)0xD3, (byte)0x3A, (byte)0x57, (byte)0xEB, (byte)0x4B,
		(byte)0x62, (byte)0x91, (byte)0x82, (byte)0x76, (byte)0x71, (byte)0x27,
		(byte)0x78, (byte)0x65, (byte)0x42, (byte)0xE6, (byte)0x86, (byte)0x3A,
		(byte)0x0D, (byte)0x1E
	};
	private final static byte[] GNU_EXPECTED_S = {
		(byte)0x27, (byte)0xC5, (byte)0xA0, (byte)0xE3, (byte)0xD8, (byte)0x1F,
		(byte)0xEB, (byte)0x02, (byte)0xA1, (byte)0x73, (byte)0x4C, (byte)0xF8,
		(byte)0x81, (byte)0xA5, (byte)0x22, (byte)0x9F, (byte)0xC6, (byte)0x45,
		(byte)0xF9, (byte)0x0D
	};
	
	/** Use fixed values to test the outputs of various arithmetic functions,
	 * as a part of producing a DSA signature
	 * r = (g^k mod p) mod q
	 * s = (k^-1 (H(m) + xr)) mod q
	 * This produces the same result as GnuPG/PGP as long as the BigIntegers are
	 * always constructed with the signum as 1 (positive).
	 */
	public void testDSASignature() {
		BigInteger mHash = new BigInteger(HASH);
		BigInteger p = new BigInteger(1, P);
		BigInteger q = new BigInteger(1, Q);
		BigInteger g = new BigInteger(1, G);
		BigInteger x = new BigInteger(1, X);
		BigInteger k = new BigInteger(1, FIXED_K);
		BigInteger expectedR = new BigInteger(1, GNU_EXPECTED_R);
		BigInteger expectedS = new BigInteger(1, GNU_EXPECTED_S);
		
		System.out.println("          P: " + 
				StringHelper.toHexString(p.toByteArray()));
		System.out.println("          Q: " + 
				StringHelper.toHexString(q.toByteArray()));
		System.out.println("          G: " + 
				StringHelper.toHexString(g.toByteArray()));
		System.out.println("          X: " + 
				StringHelper.toHexString(x.toByteArray()));
		System.out.println("          K: " + 
				StringHelper.toHexString(k.toByteArray()));
		
		int intlength = g.bitLength() / 32;
		System.out.println("G length in 32-bit words: " + intlength);
		
		BigInteger r = g.modPow(k, p).mod(q);
		System.out.println("result of modPow().mod() operation: " + 
			openpgp.keystore.util.StringHelper.toHexString(r.toByteArray()));
		
		BigInteger kInv = k.modInverse(q);
		BigInteger tmp;
		
		tmp = x.multiply(r);
		tmp = tmp.add(mHash);
		tmp = tmp.multiply(kInv);
		BigInteger s = tmp.mod(q);
		
		System.out.println("          R: " + 
				StringHelper.toHexString(r.toByteArray()));
		if (r.equals(expectedR)) System.out.println("R matches!");
		System.out.println("          S: " + 
				StringHelper.toHexString(s.toByteArray()));
		if (s.equals(expectedS)) System.out.println("S matches!");
		
	}
}
