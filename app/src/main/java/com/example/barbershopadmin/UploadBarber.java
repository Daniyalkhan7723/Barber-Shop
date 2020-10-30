package com.example.barbershopadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershopadmin.ModelClasses.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadBarber extends AppCompatActivity {
    ImageView imageView;
    EditText name,mpercentage;
    Button selectbtn,upload;
    Uri filePath;
    String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_barber);
        imageView=(ImageView)findViewById(R.id.imageview);
        name=(EditText)findViewById(R.id.name);
        mpercentage=(EditText)findViewById(R.id.mpercentage);
        selectbtn=(Button)findViewById(R.id.select);
        upload=(Button)findViewById(R.id.upload);


    }
    //Here we get the path of our image and  set image on image view and choose the image
    public void SelectButton(View view) {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            filePath=data.getData();
            imageView.setImageURI(filePath);
        }
        else{
            Toast.makeText(this, "You haven't pick any image", Toast.LENGTH_SHORT).show();
        }
    }
//Here we get url of image that download and save it into imageUrl.
    public void UploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Data Uploading....");
        progressDialog.show();
        //Storage reference is use to upload and download the file.
        StorageReference reference= FirebaseStorage.getInstance().getReference().child("Pictures").child(filePath.getLastPathSegment());
        reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                    Uri urlimage=uriTask.getResult();
                    imageUrl=urlimage.toString();
                    uploadInformation();
                    progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }
    public void UploadBtn(View view){
        UploadImage();

    }
    public void uploadInformation(){
       Category itemsClass=new Category(imageUrl,name.getText().toString(),mpercentage.getText().toString());
        FirebaseDatabase.getInstance().getReference("categories").push().setValue(itemsClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UploadBarber.this, "Data is uploaded", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadBarber.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
