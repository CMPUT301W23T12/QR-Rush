package com.example.qrrush.model;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A wrapper class for accessing the Firebase database for QR Rush.
 * Detailed documentation can be found at
 * <a href="https://github.com/CMPUT301W23T12/6ixStacks/wiki/FirebaseWrapper-API-Documentation"> the wiki.</a>
 */
public class FirebaseWrapper {
    /**
     * This methods creates a new collection (collectionName) and new Document (documentName)
     * you must create a hashmap of type &lt;String, Object&gt; and populate the data before adding
     * it.
     * <p>
     * WARNING: THIS METHOD SHOULD ONLY EVERY BE CALLED IF ITS THE FIRST TIME CREATING THE DOCUMENT
     * OTHERWISE THIS WILL OVERWRITE THE GIVEN DOCUMENT WITH THE NEW DATA YOU GIVE IT.
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
     * This method updates an existing document to add a new field/update an existing field. You
     * can use this method to delete a field but its highly discouraged as you can use one of the
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
     * This method deletes a given document name, data will be lost upon delete so look into
     * different methods if you want to save the document information (getUserData) and then delete.
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
     * This method deletes a specific qr code array from firebase given the hash of the code
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

            ArrayList<String> hashes = (ArrayList<String>) documentSnapshot.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) documentSnapshot.get("qrcodescomments");

            DocumentReference docRef = FirebaseFirestore.getInstance().collection(collectionName).document(documentName);
            Map<String, Object> updates = new HashMap<>();
            updates.put("qrcodes", FieldValue.arrayRemove(hash));
            int i = hashes.indexOf(hash);
            if (comments.size() > 0 && comments.size() >= hashes.size() && i != -1) {
                String comment = comments.get(i);
                updates.put("qrcodescomments", FieldValue.arrayRemove(comment));
            }
            docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("FirebaseWrapper", "Document successfully updated!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("FirebaseWrapper", "Error updating document", e);
                }
            });
        });
    }

    /**
     * This method checks if the username is already inside the firebase collection under
     * "profiles", this is mainly used for edit name on profile page or create name on first time
     * login.
     *
     * @param username           The username to check availability for.
     * @param onCompleteListener The OnCompleteListener which will receive the result.
     */
    public static void checkUsernameAvailability(String username, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // if you want to change the collection name that stores profile information, change it in
        // here too
        CollectionReference usersRef = db.collection("profiles");
        Query query = usersRef.whereEqualTo(FieldPath.documentId(), username);
        query.get().addOnCompleteListener(onCompleteListener);
    }

    public static Task<DocumentSnapshot> getData(String collection, String documentID, Consumer<DocumentSnapshot> callback) {
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
     * This method will retrieve all the data under a given name and you can access optionally
     * store the data check the documentation for example code usage
     *
     * @param username The username to retrieve the data for.
     */
    public static void getUserData(String username, Consumer<User> userConsumer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        db.collection("profiles").document(username)
                .get()
                .addOnCompleteListener((Task<DocumentSnapshot> t) -> {
                    if (!t.isSuccessful()) {
                        Log.e("getUserData", "task failed!");
                        return;
                    }

                    DocumentSnapshot ds = t.getResult();
                    if (!ds.exists()) {
                        Log.e("getUserData", "User " + username + " is not in the database!");
                        return;
                    }

                    User user = new User(
                            username,
                            ds.getString("phone-number"),
                            ds.getLong("rank").intValue(),
                            ds.getLong("score").intValue(),
                            new ArrayList<>()
                    );

                    ArrayList<String> hashes = (ArrayList<String>) ds.get("qrcodes");
                    ArrayList<String> comments = (ArrayList<String>) ds.get("qrcodescomments");
                    for (String hash : hashes) {
                        Task<DocumentSnapshot> task = FirebaseWrapper.getData("qrcodes", hash, qrCodeDoc -> {
                            GeoPoint g = (GeoPoint) ds.get("location");
                            Timestamp timestamp = (Timestamp) qrCodeDoc.get("date");
                            QRCode code = new QRCode(hash, timestamp);
                            if (g != null) {
                                Location l = new Location("");
                                l.setLatitude(g.getLatitude());
                                l.setLongitude(g.getLongitude());
                                code.setLocation(l);
                            }

                            user.addQRCodeWithoutFirebase(code);
                            if (comments.size() > 0 && comments.size() >= hashes.size()) {
                                user.setCommentWithoutUsingFirebase(code, comments.get(hashes.indexOf(hash)));
                            }
                        });

                        while (!task.isComplete()) {
                            // Empty loop is on purpose. We need to wait for these to finish.
                        }
                    }

                    userConsumer.accept(user);
                });
    }

    /**
     * Retrieves the data for a QR code with the given hash from the Firestore database.
     *
     * @param hash The hash string of the QR code for which to retrieve data.
     * @return A Task object representing the asynchronous Firestore database operation.
     * The resulting DocumentSnapshot can be obtained from the task's getResult() method.
     */
    public static Task<DocumentSnapshot> getQRCodeData(String hash, Consumer<QRCode> qrCodeConsumer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        return db.collection("qrcodes").document(hash)
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

                    QRCode code = new QRCode(hash, ds.getTimestamp("date"));
                    qrCodeConsumer.accept(code);
                });
    }

    /**
     * This method will delete a given username under the "profiles" collection, this method should
     * not be used unless you are 100% certain you wish to delete that user, this method was
     * implemented for the case where edit name was used.
     *
     * @param username The username of the profile to remove.
     */
    public static void removeUserProfile(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles").document(username)
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
                        Log.d("FirebaseWrapper", "Error deleting the document.");
                    }
                });
    }
}
