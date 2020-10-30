package com.example.barbershopadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershopadmin.Common.Common;
import com.example.barbershopadmin.ModelClasses.Category;
import com.example.barbershopadmin.ModelClasses.Table;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Map;

public class TableActivity extends AppCompatActivity {
    //Shimmer Effect
    private ShimmerFrameLayout mShimmerFrameLayout;
    Toolbar toolbar;
    RecyclerView mrecyclerView;
    TextView totalamount;
           //Database
    FirebaseRecyclerOptions<Table> options1;
    FirebaseRecyclerAdapter<Table, TableViewHolder> adapter1;
    DatabaseReference reference;
    String getdashId = "";
    //Add new Table
    EditText edtdate, edtperc, edttotal,payable;
    Button btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        /* Shimmer Effect */
        mShimmerFrameLayout =(ShimmerFrameLayout)findViewById(R.id.shimmerFrameLayout2);
        mShimmerFrameLayout.startShimmerAnimation();
        mShimmerFrameLayout.setVisibility(View.VISIBLE);
        //Hooks
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mrecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mrecyclerView.setLayoutManager(mLayoutManager);

       // mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        totalamount =  findViewById(R.id.totalamount);
        reference = FirebaseDatabase.getInstance().getReference().child("table");
        if (getIntent() != null)
            getdashId = getIntent().getExtras().get("tableId").toString();
        if (!getdashId.isEmpty() && getdashId != null) {
            Loadlist(getdashId);
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sum=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Map<String,Object>map=(Map<String, Object>)ds.getValue();
                    Object total=map.get("payable");
                    float pValue=Float.parseFloat(String.valueOf(total));
                   sum+=pValue;
                    totalamount.setText(String.valueOf(sum));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void Loadlist(String getdashtId) {
        Query searchByName = reference.orderByChild("key").equalTo(getdashtId);
        options1 = new FirebaseRecyclerOptions.Builder<Table>().setQuery(searchByName, Table.class).build();
        adapter1 = new FirebaseRecyclerAdapter<Table, TableViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull TableViewHolder holder, final int position, @NonNull Table model) {
                holder.mdate.setText(model.getDate());
                holder.mpercent.setText(model.getPercentage());
                holder.mtotal.setText(model.getTotal());
                holder.mpaid.setText(model.getPayable());
                mShimmerFrameLayout.setVisibility(View.GONE);
                mShimmerFrameLayout.stopShimmerAnimation();
            }
            @NonNull
            @Override
            public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tablecard, parent, false);
                return new TableViewHolder(view);
            }
        };
        mrecyclerView.setVisibility(View.VISIBLE);
        adapter1.startListening();
        mrecyclerView.setAdapter(adapter1);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mShimmerFrameLayout.startShimmerAnimation();
        mShimmerFrameLayout.setVisibility(View.VISIBLE);
        adapter1.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter1.stopListening();
    }

//Floating Button
    public void uploadActivity(View view) {
        showAddFoodDialog();
    }

    private void showAddFoodDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TableActivity.this);
        alertDialog.setTitle("Add New Table");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_tabel_layout, null);

        //Hooks
        edtdate = add_menu_layout.findViewById(R.id.editdate);
        edtperc = add_menu_layout.findViewById(R.id.editpercentage);
        edttotal = add_menu_layout.findViewById(R.id.total);
        payable = add_menu_layout.findViewById(R.id.payablevalue);
        btnUpload = add_menu_layout.findViewById(R.id.upload);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.scissors);
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!edtperc.getText().toString().equals("") && !edttotal.getText().toString().equals("")){
                    float percentage= Float.parseFloat(edtperc.getText().toString());
                    float dec=percentage/100.0f;
                    float total=  dec * Float.parseFloat(edttotal.getText().toString());
                    payable.setText(Float.toString(total));
                   // payable.setText(String.valueOf(percentage + total));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        edtperc.addTextChangedListener(textWatcher);
        edttotal.addTextChangedListener(textWatcher);


        //Events For Button
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final ProgressDialog mDialog = new ProgressDialog(getApplicationContext());
//                mDialog.setMessage("Uploading...");
//                mDialog.show();
                Table itemsClass=new Table(edtdate.getText().toString(),getdashId,edtperc.getText().toString(),edttotal.getText().toString(),payable.getText().toString());
                FirebaseDatabase.getInstance().getReference("table").push().setValue(itemsClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
//                            mDialog.dismiss();
                            Toast.makeText(TableActivity.this, "Data is uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TableActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.show();
    }

    //Update//Delete Data
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter1.getRef(item.getOrder()).getKey(),adapter1.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE)){
            deleteCategory(adapter1.getRef(item.getOrder()).getKey(),adapter1.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }
    private void deleteCategory(String key, Table item) {
        FirebaseDatabase.getInstance().getReference("table").child(key).removeValue();
    }
    private void showUpdateDialog(final String key, final Table item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(TableActivity.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater =this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_tabel_layout,null);
        //hooks
        edtdate = add_menu_layout.findViewById(R.id.editdate);
        edtperc = add_menu_layout.findViewById(R.id.editpercentage);
        edttotal = add_menu_layout.findViewById(R.id.total);
        payable = add_menu_layout.findViewById(R.id.payablevalue);
        btnUpload = add_menu_layout.findViewById(R.id.upload);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.scissors);
        //setDefault name
        edtdate.setText(item.getDate());
        edtperc.setText(item.getPercentage());
        edttotal.setText(item.getTotal());
        payable.setText(item.getPayable());

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final ProgressDialog mDialog = new ProgressDialog(getApplicationContext());
//                mDialog.setMessage("Uploading...");
//                mDialog.show();
                Table itemsClass=new Table(edtdate.getText().toString(),getdashId,edtperc.getText().toString(),edttotal.getText().toString(),payable.getText().toString());
                FirebaseDatabase.getInstance().getReference("table").push().setValue(itemsClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
//                            mDialog.dismiss();
                            Toast.makeText(TableActivity.this, "Data is uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TableActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

    }



//    private void UpdateImage() {
//
//
//
//    }

}
