package in.blogspot.kmvignesh.githubfeed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

public class FeedInfo extends AppCompatActivity {


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getStringExtra("AvatarURL") != null && getIntent().getStringExtra("AvatarURL").length() > 0) {
            ImageView iv = (ImageView) findViewById(R.id.IV_Avatar);
            String url = getIntent().getStringExtra("AvatarURL");
            Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);
        }
        if (getIntent().getStringExtra("data") != null && getIntent().getStringExtra("data").length() > 0) {
            String response = getIntent().getStringExtra("data");
            FeedData data = new Gson().fromJson(response, FeedData.class);
            TextView RepoName = (TextView) findViewById(R.id.TV_repo_name);
            RepoName.setText(data.getRepo().getName());
            TextView RepoUrl = (TextView) findViewById(R.id.TV_repo_url);
            RepoUrl.setText(data.getRepo().getUrl());

            if (data.getPayload().getComment() != null) {
                TextView commentBody = (TextView) findViewById(R.id.TV_comment_body);
                commentBody.setText(data.getPayload().getComment().getBody());
            } else {
                LinearLayout commentLayout = (LinearLayout) findViewById(R.id.Layout_comment);
                commentLayout.setVisibility(View.GONE);
            }
        }

    }
}
