package com.example.qrrush;

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
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for accessing the Firebase database for QR Rush.
 * Detailed documentation can be found at
 * <a href="https://github.com/CMPUT301W23T12/6ixStacks/wiki/FirebaseWrapper-API-Documentation">the wiki.</a>
 */
public class FirebaseWrapper {
    /**
     * This methods creates a new collection with name collectionName
     * and creates a new document with name documentID
     * and takes in a hashmap of data, HASHMAP must be populated with data before adding
     *
     * @param collectionName
     * @param documentID
     * @param data
     */
    public static void addData(String collectionName, String documentID, Map<String, Object> data) {
        FirebaseFirestore.getInstance().collection(collectionName)
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
     * This method deletes a field in a array.
     *
     * @param collectionName
     * @param documentID
     * @param data
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
    public static void deleteQrcode(String collectionName, String documentName, String hash) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(collectionName).document(documentName);

        Map<String, Object> updates = new HashMap<>();
        updates.put("qrcodes", FieldValue.arrayRemove(hash));

        docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("QR Rush", "Document successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("QR Rush", "Error updating document", e);
            }
        });
    }

    public static void checkUsernameAvailability(String username, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // if you want to change the collection name that stores profile information, change it in here too
        CollectionReference usersRef = db.collection("profiles");
        Query query = usersRef.whereEqualTo(FieldPath.documentId(), username);
        query.get().addOnCompleteListener(onCompleteListener);
    }

    public static void getUserData(String username, OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the user document for the given username
        db.collection("profiles").document(username)
                .get()
                .addOnCompleteListener(listener);
    }

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
