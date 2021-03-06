package test;

import junit.framework.TestCase;
import core.algorithmhandlers.openpgp.*;
import core.algorithmhandlers.openpgp.packets.*;
import core.exceptions.*;
import java.io.*;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * <p>This test will attempt to load and verify the structure of a pgp keyring 
 * generated by a third party app.</p>
 * <p>The following EBNF represents the structure of a Public/Private keyring:</p>
 * <pre>
 * PublicKeyring = { PublicCertificate }.
 * PrivateKeyring = { PrivateCertificate }.
 * PublicCertificate = 
 *     PublicPrimaryKey UserID { UserID } { UserAttribute } { PublicSubkey }.
 * PrivateCertificate = 
 *     PrivatePrimaryKey UserID { UserID } { UserAttribute } { PrivateSubkey }.
 * Signature = SignaturePacket [ TrustPacket ].
 * UserID = UserIDPacket [ TrustPacket ] { Signature }.
 * UserAttribute = UserAttributePacket [TrustPacket] {Signature}.
 * PublicPrimaryKey = PublicKeyPacket [ TrustPacket ].
 * PrivatePrimaryKey = SecretKeyPacket [ TrustPacket ].
 * PublicSubkey = PublicSubkeyPacket [ TrustPacket ] Signature [ Signature ].
 * PrivateSubkey = SecretSubkeyPacket [ TrustPacket ] Signature [ Signature ].
 * </pre>
 */
public class TestKeyringStructure extends TestCase {
	
	private final static int 
		PublicKeyEncryptedSessionKeyPacket = 1,
		SignaturePacket = 2,
		SymmetricKeyEncryptedSessionKeyPacket = 3,
		OnePassSignaturePacket = 4,
		SecretKeyPacket = 5,
		PublicKeyPacket = 6,
		SecretSubkeyPacket = 7,
		CompressedDataPacket = 8,
		SymmetricallyEncryptedDataPacket = 9,
		MarkerPacket = 10,
		LiteralDataPacket = 11,
		TrustPacket = 12,
		UserIDPacket = 13,
		PublicSubkeyPacket = 14,
		UserAttributePacket = 17,
		SymmetricallyEncryptedIntegrityProtectedDataPacket = 18,
		ModificationDetectionCodePacket = 19;

	private final static String[] packetTypes = {
		"Reserved [0] - NOT TO BE USED",
		"PublicKeyEncryptedSessionKeyPacket",
		"SignaturePacket",
		"SymmetricKeyEncryptedSessionKeyPacket",
		"OnePassSignaturePacket",
		"SecretKeyPacket",
		"PublicKeyPacket",
		"SecretSubkeyPacket",
		"CompressedDataPacket",
		"SymmetricallyEncryptedDataPacket",
		"MarkerPacket",
		"LiteralDataPacket",
		"TrustPacket",
		"UserIDPacket",
		"PublicSubkeyPacket",
		"Reserved [15]", 
		"Reserved [16]",
		"UserAttributePacket",
		"SymmetricallyEncryptedIntegrityProtectedDataPacket",
		"ModificationDetectionCodePacket"
	};
	
	/** public keyring */
	public final String publickeyfile = "/testdata/pgp_pubring.pkr";
	/** Private keyring */
    public final String privatekeyfile = "/testdata/pgp_secring.skr";
    
    private OpenPGPPacketInputStream keyfile;	// the keyring file
    
    private Packet p;	// current packet (recently recognized)
	private Packet la;	// lookahead packet
	private int sym;	// should always contain la.getPacketHeader().getType()
	
	private void reset() {
		keyfile = null;
		p = la = null;
		sym = 0;
	}
	
	private void scan() throws IOException, AlgorithmException {
		p = la;
		la = keyfile.readPacket();
		if (la != null) 
			sym = la.getPacketHeader().getType();
		else 
			sym = -1;
	}
	
	private void check(int expected) throws IOException, AlgorithmException {
		if (sym == expected) scan();
		else error(packetTypes[expected] + " expected");
	}
	
	public static void error(String msg) { // print error and notify junit
		System.err.println("Packet Structure Error: " + msg);
		assertTrue( false );
	}
	
	/** <p>Execute the public keyring test.</p>
     * <p>You should implement this method with your test. Return true if the test
     * was successful, otherwise return false.</p>
     */
    public void testPublicKeyringStructure()
    {
        boolean allOK = true;
        reset();
        //debug.Debug.setLevel(1);
        
        try {
			System.out.println("Adding Bouncy Castle JCE provider...");
			Security.addProvider(new BouncyCastleProvider());

			// prepare keyfile stream...
			keyfile = new OpenPGPPacketInputStream(
					getClass().getResourceAsStream(publickeyfile));
			
			// start reading in packets...
			scan();
			publicKeyring();
			
		} catch (Exception e) {
			e.printStackTrace();
			allOK = false;
		}
        
        // tell JUnit the result
        assertTrue( allOK );
    }
    
    /** <p>Execute the private keyring test.</p>
     * <p>You should implement this method with your test. Return true if the test
     * was successful, otherwise return false.</p>
     */
    public void testPrivateKeyringStructure()
    {
        boolean allOK = true;
        reset();
        //debug.Debug.setLevel(1);
        
        try {
			System.out.println("Adding Bouncy Castle JCE provider...");
			Security.addProvider(new BouncyCastleProvider());

			// prepare keyfile stream...
			keyfile = new OpenPGPPacketInputStream(
					getClass().getResourceAsStream(privatekeyfile));
			
			// start reading in packets...
			scan();
			privateKeyring();
			
		} catch (Exception e) {
			e.printStackTrace();
			allOK = false;
		}
        
        // tell JUnit the result
        assertTrue( allOK );
    }
    
    // PublicKeyring = { PublicCertificate }.
    private void publicKeyring() throws AlgorithmException, IOException {
    	for (;;) {
			if (sym != -1) publicCertificate(); 
			else break;
		}
    }
    
    // PrivateKeyring = { PrivateCertificate }.
    private void privateKeyring() throws AlgorithmException, IOException {
    	for (;;) {
			if (sym != -1) privateCertificate(); 
			else break;
		}
    }
    
    // PublicCertificate = 
    //     PublicPrimaryKey UserID { UserID } { PublicSubkey }.
    private void publicCertificate() throws AlgorithmException, IOException {
    	publicPrimaryKey();
    	for (;;) {
    		userID();
			if (sym != UserIDPacket) break;
		}
    	while (sym == UserAttributePacket) userAttribute();
    	while (sym == PublicSubkeyPacket) PublicSubkey();
    }
    
    // PrivateCertificate = 
    //     PrivatePrimaryKey UserID { UserID } { PrivateSubkey }.
    private void privateCertificate() throws AlgorithmException, IOException {
    	privatePrimaryKey();
    	for (;;) {
    		userID();
			if (sym != UserIDPacket) break;
		}
    	while (sym == UserAttributePacket) userAttribute();
    	while (sym == SecretSubkeyPacket) PrivateSubkey();
    }
    
    // PublicPrimaryKey = PublicKeyPacket [ TrustPacket ] {Signature}.
    private void publicPrimaryKey() throws AlgorithmException, IOException {
    	check(PublicKeyPacket);
    	if (sym == TrustPacket) scan();
    	while (sym == SignaturePacket) signature();
    }
    
    // PrivatePrimaryKey = SecretKeyPacket [ TrustPacket ] {Signature}.
    private void privatePrimaryKey() throws AlgorithmException, IOException {
    	check(SecretKeyPacket);
    	if (sym == TrustPacket) scan();
    	while (sym == SignaturePacket) signature();
    }
    
    // UserID = UserIDPacket [ TrustPacket ] { Signature }.
    private void userID() throws AlgorithmException, IOException {
    	check(UserIDPacket);
    	if (sym == TrustPacket) scan();
    	while (sym == SignaturePacket) signature();
    }
    
    // UserAttribute = UserAttributePacket [ TrustPacket ] { Signature }.
    private void userAttribute() throws AlgorithmException, IOException {
    	check(UserAttributePacket);
    	if (sym == TrustPacket) scan();
    	while (sym == SignaturePacket) signature();
    }
    
    // Signature = SignaturePacket [ TrustPacket ].
    private void signature() throws AlgorithmException, IOException {
    	check(SignaturePacket);
    	if (sym == TrustPacket) scan();
    }
    
    // PublicSubkey = PublicSubkeyPacket [ TrustPacket ] Signature [ Signature ].
    private void PublicSubkey() throws AlgorithmException, IOException {
    	check(PublicSubkeyPacket);
    	if (sym == TrustPacket) scan();
    	signature();
    	if (sym == SignaturePacket) signature();
    }
    
    // PrivateSubkey = SecretSubkeyPacket [ TrustPacket ] Signature [ Signature ].
    private void PrivateSubkey() throws AlgorithmException, IOException {
    	check(SecretSubkeyPacket);
    	if (sym == TrustPacket) scan();
    	signature();
    	if (sym == SignaturePacket) signature();
    }
}
