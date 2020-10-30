package com.example.barbershopadmin;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershopadmin.Common.Common;

public class TableViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    TextView mdate,mpercent,mtotal,mpaid;
    public TableViewHolder(@NonNull View itemView) {
        super(itemView);
        mdate=(TextView)itemView.findViewById(R.id.date);
        mpercent=(TextView)itemView.findViewById(R.id.percent);
        mtotal=(TextView)itemView.findViewById(R.id.total);
       mpaid=(TextView)itemView.findViewById(R.id.payable);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
