package com.example.qrrush;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirebaseWrapper {

    /**
     * This method allows you to create a collection without haivng to write redudant code.
     * Firebase unfortuantely only allows non-empty collection, so this collection is created with an inital empty document (which can be deleted afterwards)
     * ACTUALLY NO NEED FOR THIS(?) ADD DATA METHOD CREATES THE COLLECTION FOR YOU SO POSSIBLE DELETE?
     * TODO: Error handling for reused collection names (rerunning a program shouldn't recreate an empty document)
     * @param collectionName
     */
    public static void createCollection(String collectionName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(collectionName).add(new HashMap<String, Object>());
    }

    /**
     * This method adds Data to a collection, if the collection doesn't exist, it creates one, so might not need createCollection
     * Few problems with this
     * 1. Duplicates entries, gotta add error handling to check if the collection already has the item trying to be added (if so dont duplicate)
     * 2. ..
     *
     * @param collectionName
     * @param key
     * @param pair
     */
    public static void addData(String collectionName, String key, String pair){

        Map<String, Object> data = new HashMap<>();
        data.put(key, pair);
        FirebaseFirestore.getInstance().collection(collectionName).add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("FirebaseWrapper", "Document added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FirebaseWrapper", "Error adding document", e);
                    }
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





