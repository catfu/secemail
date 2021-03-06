package test;

import junit.framework.TestCase;
import core.algorithmhandlers.openpgp.*;
import core.algorithmhandlers.openpgp.packets.*;
import org.bouncycastle.jce.provider.*;
import java.security.*;

/**
 * <p>This test will attempt to load and decode a pgp keyring generated by a 
 * third party app.</p>
 */
public class TestKeyringDecode extends TestCase {
	
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
    
    public final String publickeyfile = "/testdata/pgp_pubring.pkr";
    public final String privatekeyfile = "/testdata/pgp_secring.skr";
    
    /** <p>Execute the public keyring test.</p>
     * <p>You should implement this method with your test. Return true if the test
     * was successful, otherwise return false.</p>
     */
    public void testPublicKeyringDecode()
    {
        boolean allOK = true;
        //debug.Debug.setLevel(1);
        
        try {
			System.out.println("Adding Bouncy Castle JCE provider...");
			Security.addProvider(new BouncyCastleProvider());

			// prepare keyfile stream...
			OpenPGPPacketInputStream keyfile = new OpenPGPPacketInputStream(
					getClass().getResourceAsStream(publickeyfile));
			// read in packets...
			boolean done = false;
			while (!done) {
				Packet p = keyfile.readPacket();
				if (p == null)
					done = true;
				else {
					if (p.getPacketHeader().getType() == 6)
						debug.Debug.println(1, "--------------------");
					debug.Debug.println(1, "Packet read, type = "
							+ packetTypes[p.getPacketHeader().getType()]);
				}
			}
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
    public void testPrivateKeyringDecode()
    {
        boolean allOK = true;
        //debug.Debug.setLevel(1);
        
        try {
			System.out.println("Adding Bouncy Castle JCE provider...");
			Security.addProvider(new BouncyCastleProvider());

			// prepare keyfile stream...
			OpenPGPPacketInputStream keyfile = new OpenPGPPacketInputStream(
					getClass().getResourceAsStream(privatekeyfile));
			// read in packets...
			boolean done = false;
			while (!done) {
				Packet p = keyfile.readPacket();
				if (p == null)
					done = true;
				else {
					if (p.getPacketHeader().getType() == 5)
						debug.Debug.println(1, "--------------------");
					debug.Debug.println(1, "Packet read, type = "
							+ packetTypes[p.getPacketHeader().getType()]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			allOK = false;
		}
        
        // tell JUnit the result
        assertTrue( allOK );
    }
    
}