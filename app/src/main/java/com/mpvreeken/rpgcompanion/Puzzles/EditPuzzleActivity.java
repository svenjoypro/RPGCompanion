package com.mpvreeken.rpgcompanion.Puzzles;

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

public class EditPuzzleActivity extends RPGCAuthActivity {

    public static Puzzle puzzle=null;

    private EditText title_et, puzzle_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_puzzle);

        setupLoadingAnimTransparent();

        if (puzzle==null) {
            Toast.makeText(application, "An unknown error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        puzzle.setEditEventListener(new Puzzle.EditEventListener() {
            @Override
            public void onUpdatePostFail() {
                EditPuzzleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                    }
                });
            }

            @Override
            public void onUpdatePostSuccess() {
                Log.d("onUpdatePostSuccess", "EditPuzzleActivity");
                EditPuzzleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingAnim();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SERIALIZED_OBJ", puzzle.getSerialized());
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

        title_et = findViewById(R.id.edit_puzzle_title_et);
        title_et.setText(puzzle.getTitle());
        puzzle_et = findViewById(R.id.edit_puzzle_body_et);
        puzzle_et.setText(puzzle.getDescription());

        Button save_btn = findViewById(R.id.edit_puzzle_submit_btn);
        Button cancel_btn = findViewById(R.id.edit_puzzle_cancel_btn);
        Button delete_btn = findViewById(R.id.edit_puzzle_delete_btn);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPuzzleActivity.this);
                builder.setMessage("Are you sure you want to delete this Puzzle?")
                        .setTitle("Delete Puzzle?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        puzzle.deletePostOnServer();
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
        puzzle.setTitle(title_et.getText().toString());
        puzzle.setDescription(puzzle_et.getText().toString());
        puzzle.updatePostOnServer();
    }

}
