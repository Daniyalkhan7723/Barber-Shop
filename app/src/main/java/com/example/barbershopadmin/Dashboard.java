package com.example.barbershopadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barbershopadmin.Common.Common;
import com.example.barbershopadmin.Interface.ItemClickListener;
import com.example.barbershopadmin.ModelClasses.Category;
import com.example.barbershopadmin.Registration.RegisterActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.UUID;
public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Shimmer Effect
    private ShimmerFrameLayout mShimmerFrameLayout;
                           //DATABASE
    private FirebaseRecyclerOptions<Category> options;
    private FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private EditText mSearchField;
    private MaterialEditText edtName,edtmPer;
    private Button btnSelect,btnUpdate;
    private Uri saveUri;
    private final int PICK_IMAGE_REQUEST=71;
                  //NavigationDrawer Menu
    private DrawerLayout drawerLayout;
    View headerView;
    TextView name,email;
    private NavigationView navigationView;
    private ImageView menuicon;
    private LinearLayout contentView;
    private static final float END_SCALE=0.7f;
//    private SimpleArcDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                           /* Shimmer Effect */
        mShimmerFrameLayout =(ShimmerFrameLayout)findViewById(R.id.shimmerFrameLayout);
        mShimmerFrameLayout.startShimmerAnimation();
        mShimmerFrameLayout.setVisibility(View.VISIBLE);
                            //Hooks&NavigationDrawerHooks
        recyclerView = (RecyclerView) findViewById(R.id.recycleview2);
        mSearchField = (EditText) findViewById(R.id.search_field);

//        headerView=navigationView.getHeaderView(0);
//        email=headerView.findViewById(R.id.demil);
//        name=headerView.findViewById(R.id.username);
//        email.setText(currentUser.getEmail());
        //  email.setText(currentUser.getPhoneNumber());
//        name=headerView.findViewById(R.id.demil);
//        email=headerView.findViewById(R.id.username);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);
        menuicon=findViewById(R.id.menu_icon);
        contentView=findViewById(R.id.content);
        name=navigationView.getHeaderView(0).findViewById(R.id.demil);
                             //DATABASE
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

                             //ArcLoaderProgressBar
//        mDialog = new SimpleArcDialog(this);
//        mDialog.setConfiguration(new ArcConfiguration(this));
//        mDialog.setTitle("Loading...");
                                //setLayout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationDrawer();
//        mDialog.show();
        loadItems("");
                                  //Searching
        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString()!=null)
                {
                    loadItems(s.toString());
                }
                else
                {
                    loadItems("");
                }
            }
        });
   }
                           //Firebase UI RecycleView & Searching Quries and Adaptor
    private void loadItems(String text) {
        Query query=FirebaseDatabase.getInstance().getReference().child("categories").orderByChild("name").startAt(text).endAt(text+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(query, Category.class).setLifecycleOwner(this).build();
        adapter=new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position, @NonNull final Category model) {
                holder.name.setText(model.getName());
                holder.percent.setText(model.getPercentage1());
              Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);
                mShimmerFrameLayout.setVisibility(View.GONE);
                mShimmerFrameLayout.stopShimmerAnimation();

//                mDialog.dismiss();
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        String user_id=getRef(position).getKey();
                        Intent intent = new Intent(Dashboard.this, TableActivity.class);
                        intent.putExtra("tableId",user_id);
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
                return new CategoryViewHolder(v);
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.startListening();


    }

    @Override
    protected void onStart() {
        super.onStart();
//        mDialog.show();
        mShimmerFrameLayout.startShimmerAnimation();
        mShimmerFrameLayout.setVisibility(View.VISIBLE);
        loadItems("");
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    //Navigation Drawer Functions Start
    private void navigationDrawer()  {
        //Navigation Drawer
        navigationView.bringToFront();//because we want to intract ND
        navigationView.setNavigationItemSelectedListener( this);
        navigationView=findViewById(R.id.nav_home);

        menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();

    }
    //NavigationDrawerAnimation
    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
        }
        switch (item.getItemId()) {
            case R.id.new_bar:
                Intent intent1=new Intent(getApplicationContext(), UploadBarber.class);
                startActivity(intent1);
        }

        return true;
    }
                                     //End of Navigation
                                       //FloatingButton
    public void uploadActivity(View view) {
        Intent intent=new Intent(getApplicationContext(), UploadBarber.class);
        startActivity(intent);
    }
                                      //Update//Delete Data
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE)){
            deleteCategory(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key, Category item) {
        FirebaseDatabase.getInstance().getReference("categories").child(key).removeValue();

    }

    private void showUpdateDialog(final String key, final Category item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Dashboard.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_menu_layout,null);
        //hooks
        edtName=add_menu_layout.findViewById(R.id.editname);
        edtmPer=add_menu_layout.findViewById(R.id.editmperc);
        btnSelect=add_menu_layout.findViewById(R.id.select);
        btnUpdate=add_menu_layout.findViewById(R.id.upload);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.scissors);
        //setDefault name
        edtName.setText(item.getName());
        edtmPer.setText(item.getPercentage1());

        //Events For Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateImage(item);
            }
        });

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //updateinfo
                item.setName(edtName.getText().toString());
                item.setPercentage1(edtmPer.getText().toString());
                FirebaseDatabase.getInstance().getReference("categories").child(key).setValue(item);

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();

    }

    private void UpdateImage(final Category item) {
         if(saveUri !=null){
            final ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("Pictures/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Dashboard.this, "Uploading !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(Dashboard.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Upload" +progress+"%");
                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null){
            saveUri=data.getData();
            btnSelect.setText("Image Selected !");
        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("Pictures/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);

    }
}