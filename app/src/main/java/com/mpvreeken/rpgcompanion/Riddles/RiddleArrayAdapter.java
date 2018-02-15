package com.mpvreeken.rpgcompanion.Riddles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.Classes.PostObjectBase;
import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sven on 7/4/2017.
 */

public class RiddleArrayAdapter extends ArrayAdapter<Riddle> {

    private final Context context;
    private final List<Riddle> riddles;
    private final RPGCActivity activity;

    //TODO
    //Consider using a RecyclerView instead of ListView

    public RiddleArrayAdapter(Context context, ArrayList<Riddle> values) {
        super(context, R.layout.riddles_list_item_layout, values);
        this.context = context;
        this.riddles = values;
        this.activity=(RPGCActivity) context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final Riddle riddle = riddles.get(position);
        //The references to the variables needs to be final, but the values need to be editable:
        final int[] voted = new int[1];
        final int[] tempVoted = new int[1];
        View.OnClickListener onClickMe = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (riddle==null) {
                    Toast.makeText(context, "An error occurred trying to retrieve the data", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, DisplayRiddleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SERIALIZED_OBJ", riddle.getSerialized());
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, 1);
            }
        };

        final LayoutInflater[] inflater = {(LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)};

        View rowView = inflater[0].inflate(R.layout.riddles_list_item_layout, parent, false);
        TextView riddle_tv = rowView.findViewById(R.id.riddle_list_item_riddle_tv);
        final TextView votes_tv = rowView.findViewById(R.id.riddle_list_item_votes_tv);
        TextView answer_tv = rowView.findViewById(R.id.riddle_list_item_answer_tv);
        final ImageButton upvote_btn = rowView.findViewById(R.id.riddle_list_item_upvote_btn);
        final ImageButton downvote_btn = rowView.findViewById(R.id.riddle_list_item_downvote_btn);

        voted[0] = tempVoted[0] = riddle.getVoted();
        if (voted[0]==1) {
            upvote_btn.setImageResource(R.mipmap.arrow_upvote);
        }
        else if (voted[0]==0) {
            downvote_btn.setImageResource(R.mipmap.arrow_downvote);
        }

        riddle_tv.setText(riddle.getRiddle());
        votes_tv.setText(riddle.getListItemVotes());

        answer_tv.setText(riddle.getAnswer());

        riddle_tv.setOnClickListener(onClickMe);
        answer_tv.setOnClickListener(onClickMe);

        upvote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voted[0]==1) {
                    return;
                }
                upvote_btn.setImageResource(R.mipmap.arrow_upvote);
                downvote_btn.setImageResource(R.mipmap.arrow_neutral);
                tempVoted[0] = 1;
                riddle.upvote();
            }
        });
        downvote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voted[0]==0) {
                    return;
                }
                downvote_btn.setImageResource(R.mipmap.arrow_downvote);
                upvote_btn.setImageResource(R.mipmap.arrow_neutral);
                tempVoted[0] = 0;
                riddle.downvote();
            }
        });

        riddle.setVoteEventListener(new PostObjectBase.VoteEventListener() {
            @Override
            public void onVoteFail() {
                tempVoted[0] = voted[0];
                updateUI();
            }

            @Override
            public void onVoteSuccess() {
                voted[0] = tempVoted[0];
                updateUI();
            }
        });

        return rowView;
    }

    private void updateUI() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}