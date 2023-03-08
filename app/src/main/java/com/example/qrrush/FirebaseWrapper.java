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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

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
     * This method updates a document in a collection.
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

    /**
     * Reads data from a collection, works fine, just kinda useless since it stores it in the log, maybe figure out a new way to output it? idk might be fine as is.
     *
     * ASYNC Function so can't really return an array of contents etc.
     * @param collectionName
     */
    public static void readData(String collectionName, String documentID) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(collectionName).document(documentID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FirebaseWrapper", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("FirebaseWrapper", "No such document");
                    }
                } else {
                    Log.d("FirebaseWrapper", "get failed with ", task.getException());
                }
            }
        });
    }

    public static void deleteDocument(String collectionName, String documentName){
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

    public static void checkUsernameAvailability(String username, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // if you want to change the collection name that stores profile information, change it in here too
        CollectionReference usersRef = db.collection("profiles");
        Query query = usersRef.whereEqualTo(FieldPath.documentId(), username);
        query.get().addOnCompleteListener(onCompleteListener);
    }



}
