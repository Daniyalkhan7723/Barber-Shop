package com.example.barbershopadmin;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.barbershopadmin.Common.Common;
import com.example.barbershopadmin.Interface.ItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;


class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{
    TextView name,percent;
    CircleImageView imageView;
 //   ImageView imageView;
    private ItemClickListener itemClickListener;
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        name =(TextView)itemView.findViewById(R.id.textview);
        percent =(TextView)itemView.findViewById(R.id.per);
        imageView=(CircleImageView) itemView.findViewById(R.id.imageview1);
       // imageView=(ImageView)itemView.findViewById(R.id.profile_image);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}



