//package com.example.qrrush;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//@RunWith(JUnit4.class)
//public class FirebaseWrapperTest {
//    @Test
//    public void testAddData() {
//        String collectionName = "testCollection";
//        String documentID = "testDocument";
//        Map<String, Object> data = new HashMap<>();
//        data.put("testKey", "testValue");
//
//        FirebaseWrapper.addData(collectionName, documentID, data);
//
//        // Assert that the document was added successfully
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection(collectionName).document(documentID).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    assertTrue(documentSnapshot.exists());
//                    assertEquals("testValue", documentSnapshot.get("testKey"));
//                });
//    }
//    @Test
//    public void testUpdateData() {
//        String collectionName = "testCollection";
//        String documentID = "testDocument";
//        Map<String, Object> data = new HashMap<>();
//        data.put("testKey", "testValue");
//        FirebaseWrapper.addData(collectionName, documentID, data);
//
//        Map<String, Object> updatedData = new HashMap<>();
//        updatedData.put("testKey", "updatedValue");
//
//        FirebaseWrapper.updateData(collectionName, documentID, updatedData);
//
//        // Assert that the document was updated successfully
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection(collectionName).document(documentID).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    assertTrue(documentSnapshot.exists());
//                    assertEquals("updatedValue", documentSnapshot.get("testKey"));
//                });
//    }
//    @Test
//    public void testDeleteDocument() {
//        String collectionName = "testCollection";
//        String documentID = "testDocument";
//        Map<String, Object> data = new HashMap<>();
//        data.put("testKey", "testValue");
//        FirebaseWrapper.addData(collectionName, documentID, data);
//
//        FirebaseWrapper.deleteDocument(collectionName, documentID);
//
//        // Assert that the document was deleted successfully
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection(collectionName).document(documentID).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    assertFalse(documentSnapshot.exists());
//                });
//    }
//    @Test
//    public void testDeleteQrcode() {
//        String collectionName = "testCollection";
//        String documentID = "testDocument";
//        List<String> qrCodes = new ArrayList<>();
//        qrCodes.add("hash1");
//        qrCodes.add("hash2");
//        Map<String, Object> data = new HashMap<>();
//        data.put("qrcodes", qrCodes);
//        FirebaseWrapper.addData(collectionName, documentID, data);
//
//        FirebaseWrapper.deleteQrcode(collectionName, documentID, "hash1");
//
//        // Assert that the QR code was deleted successfully
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection(collectionName).document(documentID).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    assertTrue(documentSnapshot.exists());
//                    List<String> updatedQrCodes = (List<String>) documentSnapshot.get("qrcodes");
//                    assertEquals(1, updatedQrCodes.size());
//                    assertFalse(updatedQrCodes.contains("hash1"));
//                    assertTrue(updatedQrCodes.contains("hash2"));
//                });
//    }
//    @Test
//    public void testCheckUsernameAvailability() {
//        String username = "testUser";
//        Map<String, Object> data = new HashMap<>();
//        FirebaseWrapper.addData("profiles", username, data);
//
//        FirebaseWrapper.checkUsernameAvailability(username, task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//            }
//        });
//    }
//}
