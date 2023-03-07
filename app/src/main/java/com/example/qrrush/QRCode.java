package com.example.qrrush;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

public class QRCode {
    private String name;
    private final String hash;

//    Unsure of data type for now
    private Optional<String> location;
    private String imageURL;

    /**
     * Makes a new QRCode from some data without specifying a Location.
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
     * @param data The data contained inside the QR Code.
     * @param location The location which the QR Code was scanned.
     */
    public QRCode(byte[] data, String location) {
        this(data);
        this.location = Optional.of(location);
    }

    /**
     * Converts some array of bytes to a hex string.
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
     * @param r The rarity of the QR Code to generate.
     */
    public static QRCode withRarity(Rarity r) {
        int numZeroes = 1;
        if (r == Rarity.Rare) {
            numZeroes = 2;
        } else if (r == Rarity.Legendary) {
                numZeroes = 4;
        }

        StringBuilder sb = new StringBuilder(32);
        Random rand = new Random();
        for (int i = 0; i < 32; i++) {
            sb.append(Integer.toHexString(rand.nextInt(15) + 1));
        }
        byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i <= numZeroes; i += 1) {
            data[i] = '0';
        }

        return new QRCode(data);
    }

    public String getName() {
        return name;
    }

    public Optional<String> getLocation() {
        return location;
    }

    public String getImageURL() {
        return imageURL;
    }

    private static int getNumConsecutiveZeroes(String hashValue) {
        int numConsecutiveZeroes = 0;
        int maxConsecutiveZeroes = 0;
        for (int i = 0; i < hashValue.length(); i++) {
            if (hashValue.charAt(i) != '0') {
                numConsecutiveZeroes = 0;
                continue;
            }

            numConsecutiveZeroes++;
            maxConsecutiveZeroes = Math.max(maxConsecutiveZeroes, numConsecutiveZeroes);
        }

        return maxConsecutiveZeroes;
    }

    public Rarity getRarity() {
        return Rarity.fromScore(this.getScore());
    }

    public String getHash() {
        return this.hash;
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

        return result * (1 + getNumConsecutiveZeroes(this.hash));
    }
}
