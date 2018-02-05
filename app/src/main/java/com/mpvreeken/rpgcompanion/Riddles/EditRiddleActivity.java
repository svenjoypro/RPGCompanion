package com.mpvreeken.rpgcompanion.Riddles;

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

public class EditRiddleActivity extends RPGCAuthActivity {

    public static Riddle riddle=null;

    private EditText answer_et, riddle_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_riddle);

        setupLoadingAnimTransparent();

        if (riddle==null) {
            Toast.makeText(application, "An unknown error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        riddle.setEditEventListener(new Riddle.EditEventListener() {
            @Override
            public void onUpdatePostFail() {
                EditRiddleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
            }

            @Override
            public void onUpdatePostSuccess() {
                Log.d("onUpdatePostSuccess", "EditRiddleActivity");
                EditRiddleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SERIALIZED_OBJ", riddle.getSerialized());
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

        answer_et = findViewById(R.id.edit_riddle_answer_et);
        answer_et.setText(riddle.getAnswer());
        riddle_et = findViewById(R.id.edit_riddle_body_et);
        riddle_et.setText(riddle.getRiddle());

        Button save_btn = findViewById(R.id.edit_riddle_submit_btn);
        Button cancel_btn = findViewById(R.id.edit_riddle_cancel_btn);
        Button delete_btn = findViewById(R.id.edit_riddle_delete_btn);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditRiddleActivity.this);
                builder.setMessage("Are you sure you want to delete this Riddle?")
                        .setTitle("Delete Riddle?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        riddle.deletePostOnServer();
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
        riddle.setAnswer(answer_et.getText().toString());
        riddle.setRiddle(riddle_et.getText().toString());
        riddle.updatePostOnServer();
    }

}
