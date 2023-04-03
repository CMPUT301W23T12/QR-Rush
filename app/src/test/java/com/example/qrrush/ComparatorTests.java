package com.example.qrrush;
import com.example.qrrush.controller.DateComparator;
import com.example.qrrush.controller.NameComparator;
import com.example.qrrush.controller.RankComparator;
import com.example.qrrush.controller.ScoreComparator;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.User;

import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.google.firebase.Timestamp;

import static org.junit.Assert.assertEquals;

public class ComparatorTests {

    private QRCode qr1 = new QRCode("0000abcd0000", new Timestamp(new Date()));
    private QRCode qr2 = new QRCode("001111abcd11", new Timestamp(new Date()));
    private QRCode qr3 = new QRCode("2222abcd2222", new Timestamp(new Date()));

    @Test
    public void testDateComparator() {
        List<QRCode> qrCodeList = Arrays.asList(qr1, qr2, qr3);
        Collections.sort(qrCodeList, new DateComparator());

        assertEquals(qr1, qrCodeList.get(0));
        assertEquals(qr2, qrCodeList.get(1));
        assertEquals(qr3, qrCodeList.get(2));
    }

    @Test
    public void testNameComparator() {
        List<QRCode> qrCodeList = Arrays.asList(qr1, qr2, qr3);
        Collections.sort(qrCodeList, new NameComparator());

        assertEquals(qr1, qrCodeList.get(0));
        assertEquals(qr2, qrCodeList.get(1));
        assertEquals(qr3, qrCodeList.get(2));
    }
    @Test
    public void testScoreComparator() {
        List<QRCode> qrCodes = new ArrayList<>();
        qrCodes.add(qr1);
        qrCodes.add(qr2);
        qrCodes.add(qr3);

        // sort the QRCode list using ScoreComparator
        Collections.sort(qrCodes, new ScoreComparator());

        // check the order of the sorted list
        assertEquals(qr3, qrCodes.get(2)); // qr3 should be first since it has the highest score
        assertEquals(qr1, qrCodes.get(0)); // qr1 should be second since it has the second highest score
        assertEquals(qr2, qrCodes.get(1)); // qr2 should be last since it has the lowest score
    }
    @Test
    public void testRankComparator() {
        User user1 = new User("John", 1,new ArrayList<QRCode>(),100);
        User user2 = new User("Bruh", 2,new ArrayList<QRCode>(),100);
        User user3 = new User("LeBron", 3,new ArrayList<QRCode>(),100);

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        // sort the User list using RankComparator
        Collections.sort(users, new RankComparator());

        // check the order of the sorted list
        assertEquals(user3, users.get(2)); // user3 should be first since it has the highest rank
        assertEquals(user2, users.get(1)); // user2 should be second since it has the second highest rank
        assertEquals(user1, users.get(0)); // user1 should be last since it has the lowest rank
    }

}
