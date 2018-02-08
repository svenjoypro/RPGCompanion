package com.mpvreeken.rpgcompanion.Items;

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

public class EditItemActivity extends RPGCAuthActivity {

    public static Item item=null;

    private EditText title_et, item_et, external_et, image_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        setupLoadingAnimTransparent();

        if (item==null) {
            Toast.makeText(application, "An unknown error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        item.setEditEventListener(new Item.EditEventListener() {
            @Override
            public void onUpdatePostFail() {
                EditItemActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
            }

            @Override
            public void onUpdatePostSuccess() {
                Log.d("onUpdatePostSuccess", "EditItemActivity");
                EditItemActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SERIALIZED_OBJ", item.getSerialized());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }

            @Override
            public void onDeletePostSuccess() {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("DELETED", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
            @Override
            public void onDeletePostFail() {

            }
        });

        title_et = findViewById(R.id.edit_item_title_et);
        title_et.setText(item.getTitle());
        item_et = findViewById(R.id.edit_item_body_et);
        item_et.setText(item.getDescription());
        external_et = findViewById(R.id.edit_item_external_et);
        external_et.setText(item.getExternalLink());
        image_et = findViewById(R.id.edit_item_image_et);
        image_et.setText(item.getImageLink());

        Button save_btn = findViewById(R.id.edit_item_submit_btn);
        Button cancel_btn = findViewById(R.id.edit_item_cancel_btn);
        Button delete_btn = findViewById(R.id.edit_item_delete_btn);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
                builder.setMessage("Are you sure you want to delete this Unique Item?")
                        .setTitle("Delete Unique Item?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        item.deletePostOnServer();
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
        item.setTitle(title_et.getText().toString());
        item.setDescription(item_et.getText().toString());
        item.setExternalLink(external_et.getText().toString());
        item.setImageLink(image_et.getText().toString());
        item.updatePostOnServer();
    }

}
