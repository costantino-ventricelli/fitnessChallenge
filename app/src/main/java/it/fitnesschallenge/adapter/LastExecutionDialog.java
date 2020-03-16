package it.fitnesschallenge.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.fitnesschallenge.R;

public class LastExecutionDialog extends AlertDialog {

    protected LastExecutionDialog(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.last_execution_dialog, null);
        setView(view);
        TextView lastExecutionLabel = view.findViewById(R.id.last_execution_date);
        RecyclerView recyclerView = view.findViewById(R.id.last_execution_dialog_recyclerview);


    }
}
