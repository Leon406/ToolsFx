import java.util.Arrays;

/**
 * Tested against the actual RFC.
 *
 * @author Chase (Robert Maupin)
 * @link http://tools.ietf.org/rfc/rfc4503.txt
 */
public class Rabbit {
    private static final int KEYSTREAM_LENGTH = 16;
    private static final int[] A =
            new int[] {
                0x4D34D34D,
                0xD34D34D3,
                0x34D34D34,
                0x4D34D34D,
                0xD34D34D3,
                0x34D34D34,
                0x4D34D34D,
                0xD34D34D3
            };

    private static final int rotl(final int value, final int shift) {
        return value << shift | value >>> 32 - shift;
    }

    private final int[] X = new int[8];
    private final int[] C = new int[8];
    private byte b;
    private int keyindex = 0;
    private byte[] keystream = null;

    public Rabbit() {
        b = 0;
    }

    /** Should be fed an array with a length that is a multiple of 16 for proper key sequencing. */
    public byte[] crypt(final byte[] message) {
        int index = 0;
        while (index < message.length) {
            if (keystream == null || keyindex == KEYSTREAM_LENGTH) {
                keystream = keyStream();
                keyindex = 0;
            }
            for (; keyindex < KEYSTREAM_LENGTH && index < message.length; ++keyindex)
                message[index++] ^= keystream[keyindex];
        }
        return message;
    }

    /** returns 16 bytes */
    private byte[] keyStream() {
        nextState();
        final byte[] s = new byte[16];
        /* unroll */
        int x = X[6] ^ X[3] >>> 16 ^ X[1] << 16;
        s[0] = (byte) (x >>> 24);
        s[1] = (byte) (x >> 16);
        s[2] = (byte) (x >> 8);
        s[3] = (byte) x;
        x = X[4] ^ X[1] >>> 16 ^ X[7] << 16;
        s[4] = (byte) (x >>> 24);
        s[5] = (byte) (x >> 16);
        s[6] = (byte) (x >> 8);
        s[7] = (byte) x;
        x = X[2] ^ X[7] >>> 16 ^ X[5] << 16;
        s[8] = (byte) (x >>> 24);
        s[9] = (byte) (x >> 16);
        s[10] = (byte) (x >> 8);
        s[11] = (byte) x;
        x = X[0] ^ X[5] >>> 16 ^ X[3] << 16;
        s[12] = (byte) (x >>> 24);
        s[13] = (byte) (x >> 16);
        s[14] = (byte) (x >> 8);
        s[15] = (byte) x;
        return s;
    }

    private void nextState() {
        /* counter update */
        for (int j = 0; j < 8; ++j) {
            final long t = (C[j] & 0xFFFFFFFFL) + (A[j] & 0xFFFFFFFFL) + b;
            b = (byte) (t >>> 32);
            C[j] = (int) (t & 0xFFFFFFFF);
        }
        /* next state function */
        final int G[] = new int[8];
        for (int j = 0; j < 8; ++j) {
            // TODO: reduce this to use 32 bits only
            long t = X[j] + C[j] & 0xFFFFFFFFL;
            G[j] = (int) ((t *= t) ^ t >>> 32);
        }
        /* unroll */
        X[0] = G[0] + rotl(G[7], 16) + rotl(G[6], 16);
        X[1] = G[1] + rotl(G[0], 8) + G[7];
        X[2] = G[2] + rotl(G[1], 16) + rotl(G[0], 16);
        X[3] = G[3] + rotl(G[2], 8) + G[1];
        X[4] = G[4] + rotl(G[3], 16) + rotl(G[2], 16);
        X[5] = G[5] + rotl(G[4], 8) + G[3];
        X[6] = G[6] + rotl(G[5], 16) + rotl(G[4], 16);
        X[7] = G[7] + rotl(G[6], 8) + G[5];
    }

    /** Clears all internal data. You must set the key again to use this cypher. */
    public void reset() {
        b = 0;
        keyindex = 0;
        keystream = null;
        Arrays.fill(X, 0);
        Arrays.fill(C, 0);
    }

    /** @param IV An array of 8 bytes */
    public void setupIV(final byte[] IV) {
        short[] sIV = new short[IV.length >> 1];
        for (int i = 0; i < sIV.length; ++i) {
            sIV[i] = (short) ((IV[i << 1] << 8) | IV[(2 << 1) + 1]);
        }
        setupIV(sIV);
    }

    /** @param iv array of 4 short values */
    public void setupIV(final short[] iv) {
        /* unroll */
        C[0] ^= iv[1] << 16 | iv[0] & 0xFFFF;
        C[1] ^= iv[3] << 16 | iv[1] & 0xFFFF;
        C[2] ^= iv[3] << 16 | iv[2] & 0xFFFF;
        C[3] ^= iv[2] << 16 | iv[0] & 0xFFFF;
        C[4] ^= iv[1] << 16 | iv[0] & 0xFFFF;
        C[5] ^= iv[3] << 16 | iv[1] & 0xFFFF;
        C[6] ^= iv[3] << 16 | iv[2] & 0xFFFF;
        C[7] ^= iv[2] << 16 | iv[0] & 0xFFFF;

        nextState();
        nextState();
        nextState();
        nextState();
    }

    /** @param key An array of 16 bytes */
    public void setupKey(final byte[] key) {
        short[] sKey = new short[key.length >> 1];
        for (int i = 0; i < sKey.length; ++i) {
            sKey[i] = (short) ((key[i << 1] << 8) | key[(2 << 1) + 1]);
        }
        setupKey(sKey);
    }

    /** @param key An array of 8 short values */
    public void setupKey(final short[] key) {
        /* unroll */
        X[0] = key[1] << 16 | key[0] & 0xFFFF;
        X[1] = key[6] << 16 | key[5] & 0xFFFF;
        X[2] = key[3] << 16 | key[2] & 0xFFFF;
        X[3] = key[0] << 16 | key[7] & 0xFFFF;
        X[4] = key[5] << 16 | key[4] & 0xFFFF;
        X[5] = key[2] << 16 | key[1] & 0xFFFF;
        X[6] = key[7] << 16 | key[6] & 0xFFFF;
        X[7] = key[4] << 16 | key[3] & 0xFFFF;
        /* unroll */
        C[0] = key[4] << 16 | key[5] & 0xFFFF;
        C[1] = key[1] << 16 | key[2] & 0xFFFF;
        C[2] = key[6] << 16 | key[7] & 0xFFFF;
        C[3] = key[3] << 16 | key[4] & 0xFFFF;
        C[4] = key[0] << 16 | key[1] & 0xFFFF;
        C[5] = key[5] << 16 | key[6] & 0xFFFF;
        C[6] = key[2] << 16 | key[3] & 0xFFFF;
        C[7] = key[7] << 16 | key[0] & 0xFFFF;
        nextState();
        nextState();
        nextState();
        nextState();
        /* unroll */
        C[0] ^= X[4];
        C[1] ^= X[5];
        C[2] ^= X[6];
        C[3] ^= X[7];
        C[4] ^= X[0];
        C[5] ^= X[1];
        C[6] ^= X[2];
        C[7] ^= X[3];
    }
}
