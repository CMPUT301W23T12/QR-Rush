package com.example.qrrush.model;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qrrush.controller.ScoreComparator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A wrapper class for accessing the Firebase database for QR Rush.
 * Detailed documentation can be found at
 * <a href=
 * "https://github.com/CMPUT301W23T12/6ixStacks/wiki/FirebaseWrapper-API-Documentation">
 * the wiki.</a>
 */
public class FirebaseWrapper {

    /**
     * This methods creates a new collection (collectionName) and new Document
     * (documentName)
     * you must create a hashmap of type &lt;String, Object&gt; and populate the
     * data before adding
     * it.
     * <p>
     * WARNING: THIS METHOD SHOULD ONLY EVERY BE CALLED IF ITS THE FIRST TIME
     * CREATING THE DOCUMENT
     * OTHERWISE THIS WILL OVERWRITE THE GIVEN DOCUMENT WITH THE NEW DATA YOU GIVE
     * IT.
     * LOOK AT updateData IF YOU WANT TO UPDATE AN EXISTING DOCUMENT
     *
     * @param collectionName The collection to add to.
     * @param documentID     The document name to add.
     * @param data           The data to add to firebase.
     */
    public static Task<Void> addData(String collectionName, String documentID, Map<String, Object> data) {
        return FirebaseFirestore.getInstance().collection(collectionName)
                .document(documentID)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseWrapper", "Document added with ID: " + documentID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseWrapper", "Error adding document", e);
                });
    }

    /**
     * This method updates an existing document to add a new field/update an
     * existing field. You
     * can use this method to delete a field but its highly discouraged as you can
     * use one of the
     * delete methods below.
     *
     * @param collectionName The name of the collection to update.
     * @param documentID     The name of the document to update.
     * @param data           The data to update the document with.
     */
    public static void updateData(String collectionName, String documentID, Map<String, Object> data) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .document(documentID)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseWrapper", "Document updated with ID: " + documentID);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseWrapper", "Error updating document", e);
                });
    }

    /**
     * This method deletes a given document name, data will be lost upon delete so
     * look into
     * different methods if you want to save the document information (getUserData)
     * and then delete.
     *
     * @param collectionName The name of the document to delete.
     * @param documentName   The name of the document to delete.
     */
    public static void deleteDocument(String collectionName, String documentName) {
        FirebaseFirestore.getInstance().collection(collectionName).document(documentName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirebaseWrapper", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FirebaseWrapper", "Error deleting document", e);
                    }
                });
    }

    /**
     * This method deletes a specific qr code array from firebase given the hash of
     * the code
     *
     * @param collectionName
     * @param documentName
     * @param hash
     */
    public static void deleteQrcode(String collectionName, String documentName, String hash) {
        FirebaseWrapper.getData("profiles", documentName, documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.e("deleteQRCode", "Document " + documentName +
                        " does not exist in the collection " + collectionName + "!");
                return;
            }

            Map<String, Object> data = documentSnapshot.getData();
            ArrayList<String> hashes = (ArrayList<String>) documentSnapshot.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) documentSnapshot.get("qrcodescomments");
            ArrayList<String> pictures = (ArrayList<String>) documentSnapshot.get("qrcodespictures");

            int i = hashes.indexOf(hash);
            if (hashes.size() > 0 && i != -1) {
                hashes.remove(i);
            }

            if (comments.size() > 0 && comments.size() >= hashes.size() && i != -1) {
                comments.remove(i);
            }

            if (pictures.size() > 0 && pictures.size() >= hashes.size() && i != -1) {
                pictures.remove(i);
            }

            data.put("qrcodes", hashes);
            data.put("qrcodescomments", comments);
            data.put("qrcodespictures", pictures);
            FirebaseFirestore.getInstance().collection("profiles")
                    .document(documentName)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FirebaseWrapper", "Document updated with ID: " + documentName);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseWrapper", "Error updating document", e);
                    });
        });
    }

    public static Task<DocumentSnapshot> getData(String collection, String documentID,
                                                 Consumer<DocumentSnapshot> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        return db.collection(collection).document(documentID)
                .get()
                .addOnCompleteListener((Task<DocumentSnapshot> t) -> {
                    if (!t.isSuccessful()) {
                        Log.e("getData", "task failed!");
                        return;
                    }

                    DocumentSnapshot ds = t.getResult();
                    callback.accept(ds);
                });
    }

    /**
     * This method will retrieve all the data under a given name and you can access
     * optionally
     * store the data check the documentation for example code usage
     *
     * @param username The username to retrieve the data for.
     */
    public static Task<DocumentSnapshot> getUserData(String username, Consumer<Optional<User>> userConsumer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        return db.collection("profiles").document(username)
                .get()
                .addOnCompleteListener((Task<DocumentSnapshot> t) -> {
                    if (!t.isSuccessful()) {
                        Log.e("getUserData", "task failed!");
                        return;
                    }

                    DocumentSnapshot ds = t.getResult();
                    if (!ds.exists()) {
                        userConsumer.accept(Optional.empty());
                        return;
                    }
                    User user = new User(
                            username,
                            ds.getString("phone-number"),
                            ds.getLong("rank").intValue(),
                            new ArrayList<>(),
                            ds.getLong("money").intValue(),
                            ds.getString("profile_picture")
                    );

                    Map<String, Object> data = ds.getData();
                    for (int i = 0; i < 3; i += 1) {
                        long progress = ((ArrayList<Long>) data.get("quests-progress")).get(i);
                        user.setQuestProgressWithoutFirebase(i, (int) progress);
                    }

                    Calendar dateRefreshed = Calendar.getInstance();
                    dateRefreshed.setTime(((Timestamp) data.get("quests-date-refreshed")).toDate());
                    Calendar today = Calendar.getInstance();

                    // Do we need to reset the quests progress?
                    if (dateRefreshed.get(Calendar.YEAR) != today.get(Calendar.YEAR) ||
                            dateRefreshed.get(Calendar.MONTH) != today.get(Calendar.MONTH) ||
                            dateRefreshed.get(Calendar.DAY_OF_MONTH) != today.get(Calendar.DAY_OF_MONTH)) {
                        user.setQuestProgressWithoutFirebase(0, 0);
                        user.setQuestProgressWithoutFirebase(1, 0);
                        user.setQuestProgressWithoutFirebase(2, 0);

                        HashMap<String, Object> newData = new HashMap<>();
                        ArrayList<Integer> progress = new ArrayList<>();
                        progress.add(0);
                        progress.add(0);
                        progress.add(0);
                        newData.put("quests-progress", progress);
                        newData.put("quests-date-refreshed", new Timestamp(new Date()));
                        FirebaseWrapper.updateData("profiles", user.getUserName(), newData);
                    }

                    ArrayList<String> hashes = (ArrayList<String>) ds.get("qrcodes");
                    ArrayList<String> comments = (ArrayList<String>) ds.get("qrcodescomments");
                    ArrayList<String> pictures = (ArrayList<String>) ds.get("qrcodespictures");
                    for (String hash : hashes) {
                        Task<DocumentSnapshot> task = FirebaseWrapper.getData("qrcodes", hash, qrCodeDoc -> {
                            GeoPoint g = qrCodeDoc.getGeoPoint("location");
                            Timestamp timestamp = (Timestamp) qrCodeDoc.get("date");
                            QRCode code = new QRCode(hash, timestamp);
                            if (g != null) {
                                Location l = new Location("");
                                l.setLatitude(g.getLatitude());
                                l.setLongitude(g.getLongitude());
                                code.setLocation(l);
                                Log.e("FirebaseWrapper", "QR Code has location");
                            }

                            user.addQRCodeWithoutFirebase(code);
                            if (comments.size() > 0 && comments.size() >= hashes.size()) {
                                user.setCommentWithoutUsingFirebase(code, comments.get(hashes.indexOf(hash)));
                            }
                            if (pictures.size() > 0 && pictures.size() >= hashes.size()) {
                                code.setLocationImage(pictures.get(hashes.indexOf(hash)));
                            }
                        });

                        while (!task.isComplete()) {
                            // Empty loop is on purpose. We need to wait for these to finish.
                        }
                    }

                    userConsumer.accept(Optional.of(user));
                });
    }

    public static void getAllScannedQRCodeData(String hash,
                                               Consumer<List<String>> scannedByListConsumer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        db.collection("qrcodes").document(hash)
                .get()
                .addOnCompleteListener((Task<DocumentSnapshot> t) -> {
                    if (!t.isSuccessful()) {
                        Log.e("getQRCodeData", "task failed!");
                        return;
                    }

                    DocumentSnapshot ds = t.getResult();
                    if (!ds.exists()) {
                        Log.e("getQRCodeData", "QR code with hash " + hash + " is not in the database!");
                        return;
                    }

                    // Retrieve the array of users who have scanned the QR code
                    ArrayList<String> scannedByList = (ArrayList<String>) ds.get("scannedby");

                    scannedByListConsumer.accept(scannedByList);
                });
    }

    public static void getScannedQRCodeData(String hash, String username,
                                            Consumer<List<String>> scannedByListConsumer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        db.collection("qrcodes").document(hash)
                .get()
                .addOnCompleteListener((Task<DocumentSnapshot> t) -> {
                    if (!t.isSuccessful()) {
                        Log.e("getQRCodeData", "task failed!");
                        return;
                    }

                    DocumentSnapshot ds = t.getResult();
                    if (!ds.exists()) {
                        Log.e("getQRCodeData", "QR code with hash " + hash + " is not in the database!");
                        return;
                    }

                    // Retrieve the array of users who have scanned the QR code
                    ArrayList<String> scannedByList = (ArrayList<String>) ds.get("scannedby");

                    // Filter out the given username
                    scannedByList.remove(username);

                    scannedByListConsumer.accept(scannedByList);
                });
    }

    public static void getScannedQRCodeDataLeader(String hash, Consumer<List<String>> scannedByListConsumer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        db.collection("qrcodes").document(hash)
                .get()
                .addOnCompleteListener((Task<DocumentSnapshot> t) -> {
                    if (!t.isSuccessful()) {
                        Log.e("getQRCodeData", "task failed!");
                        return;
                    }

                    DocumentSnapshot ds = t.getResult();
                    if (!ds.exists()) {
                        Log.e("getQRCodeData", "QR code with hash " + hash + " is not in the database!");
                        return;
                    }

                    // Retrieve the array of users who have scanned the QR code
                    ArrayList<String> scannedByList = (ArrayList<String>) ds.get("scannedby");

                    // Filter out the given username

                    scannedByListConsumer.accept(scannedByList);
                });
    }

    private static void getUsers(QuerySnapshot profileSnapshot, QuerySnapshot qrCodeSnapshot,
                                 Consumer<ArrayList<User>> callback) {
        // Get all the QR codes into a list
        HashMap<String, QRCode> qrCodes = new HashMap<>(qrCodeSnapshot.size());
        Iterator<QueryDocumentSnapshot> it = qrCodeSnapshot.iterator();
        while (it.hasNext()) {
            DocumentSnapshot d = it.next();

            QRCode code = new QRCode(d.getId(), d.getTimestamp("date"));
            Object maybeLocation = d.get("location");
            if (maybeLocation != null) {
                GeoPoint g = (GeoPoint) maybeLocation;
                Location l = new Location("");
                l.setLongitude(g.getLongitude());
                l.setLatitude(g.getLatitude());
                code.setLocation(l);
            }

            qrCodes.put(d.getId(), code);
        }

        // Add all the users to a list
        ArrayList<User> users = new ArrayList<>(profileSnapshot.size());
        it = profileSnapshot.iterator();
        while (it.hasNext()) {
            DocumentSnapshot d = it.next();

            ArrayList<QRCode> codes = new ArrayList<>();
            ArrayList<String> hashes = (ArrayList<String>) d.get("qrcodes");
            for (String hash : hashes) {
                codes.add(qrCodes.get(hash));
            }

            users.add(new User(
                    d.getId(),
                    d.getString("phone-number"),
                    d.getLong("rank").intValue(),

                    codes,
                    d.getLong("money").intValue(),
                    d.getString("profile_picture")

            ));
        }

        callback.accept(users);
    }

    /**
     * This method will get all QR codes from firebase, and provide an array of
     * QRCode for the
     * person who calls this.
     *
     * @param callback The callback which receives the list of users.
     */
    public static void getAllQRCodes(Consumer<ArrayList<QRCode>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("qrcodes").get().addOnSuccessListener(querySnapshot -> {
                    // Get all the QR codes into a list
                    ArrayList<QRCode> qrCodes = new ArrayList<>(querySnapshot.size());
                    Iterator<QueryDocumentSnapshot> it = querySnapshot.iterator();
                    while (it.hasNext()) {
                        DocumentSnapshot d = it.next();

                        QRCode code = new QRCode(d.getId(), d.getTimestamp("date"));
                        Object maybeLocation = d.get("location");
                        if (maybeLocation != null) {
                            GeoPoint g = (GeoPoint) maybeLocation;
                            Location l = new Location("");
                            l.setLongitude(g.getLongitude());
                            l.setLatitude(g.getLatitude());
                            code.setLocation(l);
                            qrCodes.add(code);
                        }

                        // qrCodes.add(code);
                    }

                    ScoreComparator sc = new ScoreComparator();
                    qrCodes.sort(sc);
                    callback.accept(qrCodes);
                })
                .addOnFailureListener(exception -> {
                    Log.d("getAllQRCodes", "task failed!");
                });
    }

    /**
     * This method will get all users from firebase, and provide an array of User
     * for the person
     * who calls this.
     *
     * @param callback The callback which receives the list of users.
     */
    public static void getAllUsers(Consumer<ArrayList<User>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles").orderBy("score", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(querySnapshot -> {
                    db.collection("qrcodes").get()
                            .addOnSuccessListener(qrCodeQuerySnapshot -> {
                                getUsers(querySnapshot, qrCodeQuerySnapshot, callback);
                            })
                            .addOnFailureListener(exception -> {
                                Log.d("FirebaseWrapper", "Error deleting the document.");
                            });
                })
                .addOnFailureListener(exception -> {
                    Log.d("FirebaseWrapper", "Error deleting the document.");
                });
    }
}
