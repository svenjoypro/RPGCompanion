package com.mpvreeken.rpgcompanion.Hooks;

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

public class EditHookActivity extends RPGCAuthActivity {

    public static Hook hook=null;

    private EditText title_et, hook_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hook);

        setupLoadingAnimTransparent();

        if (hook==null) {
            Toast.makeText(application, "An unknown error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        hook.setEditEventListener(new Hook.EditEventListener() {
            @Override
            public void onUpdatePostFail() {
                EditHookActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
            }

            @Override
            public void onUpdatePostSuccess() {
                Log.d("onUpdatePostSuccess", "EditHookActivity");
                EditHookActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SERIALIZED_OBJ", hook.getSerialized());
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

        title_et = findViewById(R.id.edit_hook_title_et);
        title_et.setText(hook.getTitle());
        hook_et = findViewById(R.id.edit_hook_body_et);
        hook_et.setText(hook.getDescription());

        Button save_btn = findViewById(R.id.edit_hook_submit_btn);
        Button cancel_btn = findViewById(R.id.edit_hook_cancel_btn);
        Button delete_btn = findViewById(R.id.edit_hook_delete_btn);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditHookActivity.this);
                builder.setMessage("Are you sure you want to delete this Plot Hook?")
                        .setTitle("Delete Plot Hook?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hook.deletePostOnServer();
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
        hook.setTitle(title_et.getText().toString());
        hook.setDescription(hook_et.getText().toString());
        hook.updatePostOnServer();
    }

}
