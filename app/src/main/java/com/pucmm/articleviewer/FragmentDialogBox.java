package com.pucmm.articleviewer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FragmentDialogBox extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String [] cameraOptions = getActivity().getResources().getStringArray(R.array.cameraOptions);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your category picture");
        //return super.onCreateDialog(savedInstanceState);
        builder.setItems(cameraOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "you choose : " + cameraOptions[which],Toast.LENGTH_SHORT).show();

            }
        });
        return builder.create();
    }
}
