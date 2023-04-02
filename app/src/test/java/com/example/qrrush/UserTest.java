package com.example.qrrush;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Rarity;
import com.example.qrrush.model.User;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UserTest {
    public User user1;
    public User user2;
    private ArrayList<QRCode> qrCodes1;
    private ArrayList<QRCode> qrCodes2;
    @Test
    public void testUserName(){
        user1 = new User("user1",5,qrCodes1,100);
        assertEquals("user1",user1.getUserName());
        user1.setUserName("LeBron James");
        assertEquals("LeBron James",user1.getUserName());
    }

    @Test
    public void testRanks(){
        user1 = new User("user1",5,qrCodes1,100);
        user2 = new User("user2",4,qrCodes2,100);
        assertTrue(user1.getRank()> user2.getRank());
        user2.setRank(6);
        assertFalse(user1.getRank()>user2.getRank());
    }

    @Test
    public void testScores(){
        ArrayList<QRCode> qrCodes1 = new ArrayList<>();
        user1 = new User("user1",5,qrCodes1,100);
        String dataString = "LeBron James";
        byte[] dataBytes = dataString.getBytes(StandardCharsets.UTF_8);
        assertEquals(user1.getTotalScore(),0);
        qrCodes1.add(new QRCode(dataBytes));
        assertTrue(user1.getTotalScore()> 0);
    }

}
