package com.zeroetun.oneclickshare.service;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by françois on 25/02/2017.
 */

public class FireBaseService {

    FirebaseStorage storage = FirebaseStorage.getInstance();



    public void uploadFromLocalFile(Uri file,final UploadListener uploadListener) {
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("imagestest/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                uploadListener.onFailure(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                uploadListener.onSuccess(taskSnapshot.getDownloadUrl());
            }
        });
    }
}
