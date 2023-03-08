package com.example.qrrush;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

public class QRCode {
    private String name;
    private final String hash;
    private Optional<String> location;
    private String imageURL;

    /**
     * Makes a new QRCode from some data without specifying a Location.
     *
     * @param data The data contained inside the QR Code.
     */
    public QRCode(byte[] data) {
        // TODO: When you generate a QR code, the name and image should be automatically generated
        //       from the hash of data.
        this.name = "Default Name";
        this.location = Optional.empty();
        this.imageURL = imageURL;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            this.hash = bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Makes a new QRCode from some data, specifying the Location which the QR Code was scanned.
     *
     * @param data     The data contained inside the QR Code.
     * @param location The location which the QR Code was scanned.
     */
    public QRCode(byte[] data, String location) {
        this(data);
        this.location = Optional.of(location);
    }

    private QRCode(String hash) {
        this.name = "Default Name";
        this.location = Optional.empty();
        this.hash = hash;
    }

    /**
     * Converts some array of bytes to a hex string.
     *
     * @param bytes The array of bytes to convert to a hex string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Returns a new QR Code of a specified rarity.
     *
     * @param r The rarity of the QR Code to generate.
     */
    public static QRCode withRarity(Rarity r) {
        Random rand = new Random();
        int numZeroes = Rarity.minConsecutiveZeroesFor(r) + rand.nextInt(2);
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++) {
            sb.append(Integer.toHexString(rand.nextInt(15) + 1));
        }
        byte[] data = sb.toString().getBytes(StandardCharsets.US_ASCII);

        for (int i = 0; i < numZeroes; i += 1) {
            data[i] = '0';
        }

        QRCode result = null;

        // If the score isn't high enough, keep adding more fs to the hash, since that's the
        // highest digit.
        for (int i = numZeroes; i < data.length; i += 1) {
            result = new QRCode(new String(data, StandardCharsets.US_ASCII));

            if (result.getRarity() == r) {
                break;
            }

            data[i] = 'f';
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getLocation() {
        return location;
    }

    public Bitmap getImage() {
        // 1. For each character of the hash, if it's even make it a 0, and if its odd make it a 1.
        StringBuilder binaryHash = new StringBuilder(this.hash);
        for (int i = 0; i < this.hash.length(); i += 1) {
            byte[] val = {(byte) (binaryHash.charAt(i) % 2)};
            binaryHash.setCharAt(i, String.valueOf(binaryHash.charAt(i) % 2).charAt(0));
        }

        // 2. Add 4 extra padding characters to make it a square by dividing up the hash into 4
        //    sections and performing the same thing as 1 on the sum of each of the sections.
        int[][] pairs = {
                {0, 7},
                {8, 16},
                {17, 25},
                {26, 32},
        };
        for (int[] pair : pairs) {
            int sum = 0;
            for (int i = pair[0]; i < pair[1]; i += 1) {
                sum += Integer.parseInt(Character.toString(binaryHash.charAt(i)), 16);
            }

            binaryHash.append(String.valueOf(sum).charAt(0));
        }

        String str = binaryHash.toString();

        // 3. Use the first 6 bytes of the hash as a hex color.
        int c = Color.parseColor("#" + this.hash.substring(0, 6));

        // 4. Paint each pixel into a 6x6 square.
        Bitmap result = Bitmap.createBitmap(6, 6, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < 6; y += 1) {
            for (int x = 0; x < 6; x += 1) {
                if (str.charAt(6 * y + x) == '1') {
                    result.setPixel(x, y, c);
                    continue;
                }

                result.setPixel(x, y, Color.WHITE);
            }
        }

        return result;
    }

    public static int getMaxConsecutiveZeroes(String hashValue) {
        int max = 0;
        int current = 0;
        byte[] data = hashValue.getBytes(StandardCharsets.US_ASCII);
        for (byte b : data) {
            if (b != '0') {
                current = 0;
                continue;
            }

            current += 1;
            max = Integer.max(max, current);
        }

        return max;
    }

    public Rarity getRarity() {
        return Rarity.fromHash(this.getHash());
    }

    public String getHash() {
        return this.hash;
    }

    public String getNames() {
        // This Generates a human readable name using fruits

        String pp = getHash();
        StringBuilder poke = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            // Even numbers excepts for 0
            if (pp.charAt(i) == 50 || pp.charAt(i) == 52 || pp.charAt(i) == 54 ||
                    pp.charAt(i) == 56) {
                poke.append("Grape");
            }

            // Odd numbers are Cherry
            else if (pp.charAt(i) == 49 || pp.charAt(i) == 51 || pp.charAt(i) == 53 ||
                    pp.charAt(i) == 55 || pp.charAt(i) == 57) {
                poke.append("Cherry");
            }

            // ABC are plum
            else if (pp.charAt(i) == 65 || pp.charAt(i) == 66 || pp.charAt(i) == 67 ||
                    pp.charAt(i) == 97 || pp.charAt(i) == 98 || pp.charAt(i) == 99) {
                poke.append("Plum");
            }

            // DEF are peach
            else if (pp.charAt(i) == 68 || pp.charAt(i) == 69 || pp.charAt(i) == 70 ||
                    pp.charAt(i) == 100 || pp.charAt(i) == 101 || pp.charAt(i) == 102) {
                poke.append("Peach");
            }

            // GHI are lime
            else if (pp.charAt(i) == 71 || pp.charAt(i) == 72 || pp.charAt(i) == 73 ||
                    pp.charAt(i) == 103 || pp.charAt(i) == 104 || pp.charAt(i) == 105) {
                poke.append("Lime");
            }

            // JKL are lemon
            else if (pp.charAt(i) == 74 || pp.charAt(i) == 75 || pp.charAt(i) == 76 ||
                    pp.charAt(i) == 106 || pp.charAt(i) == 107 || pp.charAt(i) == 108) {
                poke.append("Lemon");
            }

            // MNO are papaya
            else if (pp.charAt(i) == 77 || pp.charAt(i) == 78 || pp.charAt(i) == 79 ||
                    pp.charAt(i) == 109 || pp.charAt(i) == 110 || pp.charAt(i) == 111) {
                poke.append("Papaya");
            }

            // PQR are kiwi
            else if (pp.charAt(i) == 80 || pp.charAt(i) == 81 || pp.charAt(i) == 82 ||
                    pp.charAt(i) == 112 || pp.charAt(i) == 113 || pp.charAt(i) == 114) {
                poke.append("Kiwi");
            }

            // STU are mango
            else if (pp.charAt(i) == 83 || pp.charAt(i) == 84 || pp.charAt(i) == 85 ||
                    pp.charAt(i) == 115 || pp.charAt(i) == 116 || pp.charAt(i) == 117) {
                poke.append("Mango");
            }

            // VWX are orange
            else if (pp.charAt(i) == 86 || pp.charAt(i) == 87 || pp.charAt(i) == 88 ||
                    pp.charAt(i) == 118 || pp.charAt(i) == 119 || pp.charAt(i) == 120) {
                poke.append("Orange");
            }

            // YZ are banana
            else if (pp.charAt(i) == 89 || pp.charAt(i) == 90 || pp.charAt(i) == 121 ||
                    pp.charAt(i) == 122) {
                poke.append("Banana");
            }

            // 0 are apple
            else if (pp.charAt(i) == 48) {
                poke.append("Apple");
            }

        }

        return poke.toString();
    }

    public int getScore() {
        // This is the algorithm for now:
        // 1. Take the hash
        // 2. sum all the characters as hex digits
        // 3. Multiply by (1 + numConsecutiveZeroes())
        int result = 0;
        char[] values = this.hash.toCharArray();
        for (char value : values) {
            result += Character.digit(value, 16);
        }

        result *= (1 + getMaxConsecutiveZeroes(this.hash));
        if (this.getRarity() == Rarity.Legendary) {
            result *= 10;
        }

        return result;
    }
}
