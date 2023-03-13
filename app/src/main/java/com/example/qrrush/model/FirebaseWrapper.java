package com.example.qrrush.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        FirebaseWrapper.getUserData(documentName, (Task<DocumentSnapshot> task) -> {
            if (!task.isSuccessful()) {
                Log.e("deleteQrcode", "Failed to read user data!");
                return;
            }

            DocumentSnapshot ds = task.getResult();
            if (!ds.exists()) {
                Log.e("deleteQrcode", "User was deleted from Firebase!");
                return;
            }

            ArrayList<String> hashes = (ArrayList<String>) ds.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) ds.get("qrcodescomments");

            DocumentReference docRef = FirebaseFirestore.getInstance().collection(collectionName).document(documentName);
            Map<String, Object> updates = new HashMap<>();
            updates.put("qrcodes", FieldValue.arrayRemove(hash));

            if (comments.size() > 0) {
                String comment = comments.get(hashes.indexOf(hash));
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

    /**
     * This method will retrieve all the data under a given name and you can access optionally
     * store the data check the documentation for example code usage
     *
     * @param username The username to retrieve the data for.
     * @param listener The OnCompleteListener which will get the data for the user.
     */
    public static void getUserData(String username, OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        db.collection("profiles").document(username)
                .get()
                .addOnCompleteListener(listener);
    }

    public static Task<DocumentSnapshot> getQRCodeData(String hash, OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        return db.collection("qrcodes").document(hash)
                .get()
                .addOnCompleteListener(listener);
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
