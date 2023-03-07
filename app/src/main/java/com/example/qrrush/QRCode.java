package com.example.qrrush;

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

    private QRCode(String hash) {
        this.name = "Default Name";
        this.location = Optional.empty();
        this.hash = hash;
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

    public String getImageURL() {
        return imageURL;
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
