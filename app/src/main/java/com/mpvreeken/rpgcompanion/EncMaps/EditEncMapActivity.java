package com.mpvreeken.rpgcompanion.EncMaps;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCAuthActivity;
import com.mpvreeken.rpgcompanion.Widgets.MultiselectSpinner;

public class EditEncMapActivity extends RPGCAuthActivity {

    public static EncMap map=null;

    private EditText title_et, desc_et, external_et, img_et;
    private MultiselectSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_enc_map);

        setupLoadingAnimTransparent();

        if (map==null) {
            Toast.makeText(application, "An unknown error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        map.setEditEventListener(new EncMap.EditEventListener() {
            @Override
            public void onUpdatePostFail() {
                EditEncMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
            }

            @Override
            public void onUpdatePostSuccess() {
                Log.d("onUpdatePostSuccess", "EditEncMapActivity");
                EditEncMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
                Intent resultIntent = new Intent();
                resultIntent.putExtra("SERIALIZED_OBJ", map.getSerialized());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onDeletePostSuccess() {
                EditEncMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
                Intent resultIntent = new Intent();
                resultIntent.putExtra("DELETED", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
            @Override
            public void onDeletePostFail() {
                EditEncMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
            }
        });

        title_et = findViewById(R.id.edit_map_title_et);
        title_et.setText(map.getTitle());
        desc_et = findViewById(R.id.edit_map_desc_et);
        desc_et.setText(map.getDescription());

        external_et = findViewById(R.id.edit_map_link_et);
        external_et.setText(map.getExternalLink());

        spinner = findViewById(R.id.edit_map_envs_spinner);
        spinner.setItems(EncMap.ENVS);
        int[] selection = new int[map.getEnvironments().size()];
        for (int i=0; i<map.getEnvironments().size(); i++) {
            selection[i] = map.getEnvironments().get(i);
        }
        spinner.setSelection(selection);


        Button save_btn = findViewById(R.id.edit_map_submit_btn);
        Button cancel_btn = findViewById(R.id.edit_map_cancel_btn);
        Button delete_btn = findViewById(R.id.edit_map_delete_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingAnim();
                submitChanges();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditEncMapActivity.this);
                builder.setMessage("Are you sure you want to delete this Map?")
                        .setTitle("Delete Map?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showLoadingAnim();
                        map.deletePostOnServer();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void submitChanges() {
        map.setTitle(title_et.getText().toString());
        map.setDescription(desc_et.getText().toString());
        map.setExternalLink(external_et.getText().toString());
        map.setEnvironments(spinner.getSelectedIndicies());
        map.updatePostOnServer();
    }

}
