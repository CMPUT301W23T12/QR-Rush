package com.example.qrrush;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.location.Location;

import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Rarity;

import org.junit.Before;
import org.junit.Test;

import com.google.firebase.Timestamp;
import java.util.Date;
import java.util.Optional;

public class QRCodeTest {
    private QRCode qrCode;
    private byte[] data = "exampledata".getBytes();
    private Timestamp timestamp;
    @Before
    public void setUp() {
        timestamp = new Timestamp(new Date());
        qrCode = new QRCode(data, new Location("Provider"));
        qrCode.setTimestamp(timestamp);
    }
    @Test
    public void testGenerateCommon() {
        QRCode code = QRCode.withRarity(Rarity.Common);
        assertEquals(Rarity.Common, code.getRarity());
    }

    @Test
    public void testGenerateRare() {
        QRCode code = QRCode.withRarity(Rarity.Rare);
        assertEquals(Rarity.Rare, code.getRarity());
    }

    @Test
    public void testGenerateLegendary() {
        QRCode code = QRCode.withRarity(Rarity.Legendary);
        assertEquals(Rarity.Legendary, code.getRarity());
    }

    @Test
    public void testImageSize() {
        // All the generated images should be 6x6 pixels large.
        QRCode code = QRCode.withRarity(Rarity.Common);
        assertEquals(6, code.getImage().getWidth());
        assertEquals(6, code.getImage().getHeight());
    }

    @Test
    public void testScorePositive() {
        QRCode code = QRCode.withRarity(Rarity.Legendary);
        assertTrue(code.getScore() >= 0);
    }
    @Test
    public void testSetAndGetLocation() {
        Location location = new Location("Provider");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);

        qrCode.setLocation(location);

        Optional<Location> retrievedLocation = qrCode.getLocation();
        assertEquals(Optional.of(location), retrievedLocation);
    }

    @Test
    public void testRemoveLocation() {
        qrCode.removeLocation();
        assertEquals(Optional.empty(), qrCode.getLocation());
    }

    @Test
    public void testSetAndGetLocationImage() {
        String locationImageUrl = "https://example.com/location_image.png";
        qrCode.setLocationImage(locationImageUrl);
        assertEquals(locationImageUrl, qrCode.getLocationImage());
    }

    @Test
    public void testSetAndGetTimestamp() {
        Timestamp newTimestamp = new Timestamp(new Date());
        qrCode.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, qrCode.getTimestamp());
    }

    @Test
    public void testGetHash() {
        assertEquals(qrCode.getHash(), qrCode.getHash());
    }
}