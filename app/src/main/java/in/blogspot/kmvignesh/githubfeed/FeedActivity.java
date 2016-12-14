package in.blogspot.kmvignesh.githubfeed;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.resource;

/**
 * Created by VickY on 14-12-2016.
 */

public class FeedActivity extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setTitle("Do you wan to exit?").setCancelable(false).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Log.d("FetchClick", "activity_feed");

        ListView list = (ListView) findViewById(R.id.LV_feed);
        if (getIntent().getStringExtra("data") != null && getIntent().getStringExtra("data").length() > 0) {
            String response = getIntent().getStringExtra("data");
            Log.d("FetchClick", "data " + response);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<FeedData>>() {
            }.getType();
            List<FeedData> posts = (List<FeedData>) gson.fromJson(response, listType);
            Log.d("Resp", " data " + posts.size());
            list.setAdapter(new FeedAdapter(this, posts));
        } else {
            Log.d("FetchClick", "data is null");
        }
    }

    public class FeedAdapter extends ArrayAdapter<FeedData> {

        List<FeedData> items;
        Context context;

        public FeedAdapter(Context context, List<FeedData> users) {
            super(context, 0, users);
            items = users;
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false);
            }

            if (items.get(position) != null) {
                TextView tvName = (TextView) convertView.findViewById(R.id.TV_Title);
                TextView tvHome = (TextView) convertView.findViewById(R.id.TV_Content);
                final ImageView IV = (ImageView) convertView.findViewById(R.id.IV_Avatar);
                tvName.setText(items.get(position).getType());
                tvHome.setText(items.get(position).getRepo().getName());
                Glide.with(context).load(items.get(position).getActor().getAvatar_url()).diskCacheStrategy(DiskCacheStrategy.ALL).into(IV);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, FeedInfo.class);
                        intent.putExtra("data", new Gson().toJson(items.get(position)));
                        intent.putExtra("name", items.get(position).getRepo().getName());
                        intent.putExtra("AvatarURL", items.get(position).getActor().getAvatar_url());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation((Activity) context, (View) IV, "profile");
                            context.startActivity(intent, options.toBundle());
                        } else {
                            context.startActivity(intent);
                        }
                    }
                });

            }

            return convertView;

        }

    }
}
