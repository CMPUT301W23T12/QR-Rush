package com.example.qrrush;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseWrapper {


    /**
     * This methods creates a new collection with name collectionName
     * and creates a new document with name documentID
     * and takes in a hashmap of data, HASHMAP must be populated with data before adding
     *
     * FIXES:
     * - This will no longer duplicate entries since we force a document ID
     * @param collectionName
     * @param documentID
     * @param data
     */
    // CUSTOM documentID required but forces no duplicates, and you can set your own ID so its good
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
     *  This method updates an existing collection, if the collection doesn't exist it creates a new one (bruh)
     *  but this way it doesn't overwrite data written in an existing table so you can update to add more information into a document
     * @param collectionName
     * @param documentId
     * @param data
     */
    public static void updateData(String collectionName, String documentId, Map<String, Object> data) {
        FirebaseFirestore.getInstance().collection(collectionName)
                .document(documentId)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseWrapper", "Document updated: " + documentId);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseWrapper", "Error updating document: " + documentId, e);
                });
    }

    /**
     * Reads data from a collection, works fine, just kinda useless since it stores it in the log, maybe figure out a new way to output it? idk might be fine as is.
     * @param collectionName
     */
    public static void readData(String collectionName) {
        FirebaseFirestore.getInstance().collection(collectionName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("FirebaseWrapper", document.getId() + " => " + document.getData());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirebaseWrapper", "Error reading documents", e);
                    }
                });
    }
}





