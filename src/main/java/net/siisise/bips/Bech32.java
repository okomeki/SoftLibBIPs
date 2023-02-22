package net.siisise.bips;

import java.util.HashMap;
import java.util.Map;
import net.siisise.io.BASE32;
import net.siisise.io.BigBitPacket;
import net.siisise.io.BitPacket;
import net.siisise.lang.Bin;

/**
 * BIP-173 Bech32
 * BIP-350 Bech32m
 * SLIP-0173 登録HRPっぽいの一覧
 * 符号化はBASE32内で適度に実装したのでここではヘッダとチェックサムを付加する.
 * 基本小文字。大文字小文字混在不可。
 * オリジナルは5ビットをintに拡張しているが、bitのままBitPacketで扱うのでいろいろ違う.
 */
public class Bech32 {

    static final int Bech32 = 1;
    static final int Bech32m = 0x2bc830a3;
    
    final BASE32 b32;
    final String hrp;
    private final int sum;
    
    public Bech32(String hrp, boolean m) {
        b32 = new BASE32(BASE32.Bech32);
        sum = m ? Bech32m : Bech32;
        if (hrp != null && hrp.indexOf('1') >= 0) {
            throw new java.lang.IllegalStateException();
        }
        this.hrp = hrp;
    }
    
    public Bech32(boolean m) {
        this(null,m);
    }
    
    public Bech32(String hrp) {
        this(hrp,false);
    }

    public Bech32() {
        this(null,false);
    }

    /**
     * Bash32 デコード hrp がある場合は一致していること
     *
     * @param code
     * @return
     */
    public byte[] decode(String code) {
        Map<String,?> m = decodeMap(code);
        return (byte[])m.get("val");
    }
    
    /**
     * 
     * @param code
     * @return 
     * @throws NullPointerException,IllegalStateException
     */
    public Map<String,?> decodeMap(String code) {
        
        int sp = code.lastIndexOf('1');
        if ((hrp != null && (!code.startsWith(hrp + '1') || hrp.length() != sp)) || sp < 0 || code.length() < sp + 7) {
            throw new java.lang.IllegalStateException();
        }
        String lc = code.toLowerCase();
        if (!lc.equals(code) && !code.toUpperCase().equals(code)) {
            throw new java.lang.IllegalStateException(); // てきとー
        }

        String h = lc.substring(0, sp);
        String vs = lc.substring(sp + 1);
        if (!verifyChecksum(h, vs)) {
            throw new java.lang.IllegalStateException(); // てきとー
        }
        Map<String,Object> m = new HashMap<>();
        m.put("hrp", h);
        m.put("val", b32.decode(vs.substring(0,vs.length() - 6)));
        return m;
    }

    /**
     *
     * @param hrp ヘッダ文字
     * @return 5ビット列
     */
    static BitPacket expandHrp(String hrp) {
        BitPacket bp = new BigBitPacket();
        char[] chs = hrp.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            bp.writeBit(chs[i] >>> 5, 5);
        }
        bp.writeBit(0, 5);
        for (int i = 0; i < chs.length; i++) {
            bp.writeBit(chs[i], 5);
        }
        return bp;
    }

    static final int[] GEN = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

    private static int polymod(BitPacket bp) {
        int chk = 1;
        while (bp.bitLength() > 0) {
            int v = bp.readInt(5);
            int b = chk >> 25;
            chk = (chk & 0x1ffffff) << 5 ^ v;
            for (int i = 0; i < 5; i++) {
                chk ^= (((b >> i) & 1) == 0) ? 0 : GEN[i];
            }
        }
        return chk;
    }

    public String encode(byte[] src) {
        StringBuilder sb = new StringBuilder();
        sb.append(hrp);
        sb.append('1');
        sb.append(b32.encode(src));
        sb.append(encodeChecksum(createChecksum(hrp, src)));
        return sb.toString();
    }

    private String encodeChecksum(int sum) {
        return b32.encode(Bin.toByte(sum << 2)).substring(0, 6);
    }

    /**
     *
     * @param hrp 頭文字 10bit文字まで
     * @param data バイト列データ
     * @return チェックサム 30bit
     */
    private int createChecksum(String hrp, byte[] data) {
        BitPacket bp = expandHrp(hrp);
        bp.write(data);
        if (data.length * 8 % 5 > 0) {
            bp.writeBit(0, 5 - (data.length * 8 % 5));
        }

        bp.writeBit(0, 30);
        return polymod(bp) ^ sum;
    }

    /**
     *
     * @param hrp
     * @param data data+sum
     * @return
     */
    private boolean verifyChecksum(String hrp, String data) {
        BitPacket bp = expandHrp(hrp);
        bp.writeBit(b32.decodePacket(data));
        return polymod(bp) == sum;
    }
}
