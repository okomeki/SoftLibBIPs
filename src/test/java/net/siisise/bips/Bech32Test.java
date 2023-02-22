package net.siisise.bips;

import net.siisise.bips.Bech32;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class Bech32Test {
    
    public Bech32Test() {
    }

    /**
     * Test of decode method, of class Bech32.
     */
    @Test
    public void testDecode() {
        System.out.println("decode");
        String code = "LNURL1DP68GURN8GHJ7AMPD3KX2AR0VEEKZAR0WD5XJTNRDAKJ7TNHV4KXCTTTDEHHWM30D3H82UNVWQHKYUNP0FJKUARJDA6KYMR9XSUQR0HD5H";

//        Bech32 instance = new Bech32("LNURL");
//        String code = "A12UEL5L";
//        Bech32 instance = new Bech32("A");
//        String code = "abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw";
        Bech32 instance = new Bech32();
//        byte[] expResult = new byte[0];
        byte[] result = instance.decode(code);
        System.out.println(new String(result));
//        assertArrayEquals(expResult, result);
    }

    /**
     * Test of encode method, of class Bech32.
     */
    @Test
    public void testEncode() {
        System.out.println("encode");
        byte[] src = new byte[0];
        Bech32 instance = new Bech32("a");
        String expResult = "a12uel5l";
        String result = instance.encode(src);
        System.out.println("Bech32 encode " + result);
        assertEquals(expResult, result);
    }
    
}
