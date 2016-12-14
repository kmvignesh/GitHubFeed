package in.blogspot.kmvignesh.githubfeed;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    CardView cardView;
    Button fetch, get;
    EditText username;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardView = (CardView) findViewById(R.id.cardView);
        logo = (ImageView) findViewById(R.id.IV_Logo);
        fetch = (Button) findViewById(R.id.BT_fetch);
        get = (Button) findViewById(R.id.BT_get);
        username = (EditText) findViewById(R.id.ET_Username);
        username.setSingleLine(true);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("FetchClick", "button State " + fetch.getVisibility());
                Log.d("FetchClick", "cardview State " + cardView.getVisibility());
                cardView.setVisibility(View.VISIBLE);
                get.setVisibility(View.VISIBLE);
                fetch.setVisibility(View.GONE);
                Animation flipAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flip);
                get.startAnimation(flipAnimation);
                Animation slideAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide);
                username.startAnimation(slideAnimation);
                Animation shiftAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shift);
                logo.startAnimation(shiftAnimation);
                Animation shapeAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shape);
                cardView.startAnimation(shapeAnimation);
                Log.d("FetchClick", "button State " + fetch.getVisibility());
                Log.d("FetchClick", "cardview State " + cardView.getVisibility());

            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().length() > 0)
                    new getRequest(username.getText().toString().trim()).execute();
                else
                    Toast.makeText(MainActivity.this, "Please enter Username", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private class getRequest extends AsyncTask<Void, Void, Void> {

        String username, response;
        ProgressDialog dialog;

        getRequest(String username) {
            this.username = username;
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(MainActivity.this); // this = YourActivity
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuffer stringBuffer = new StringBuffer("");
            BufferedReader bufferedReader = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();

                String url = "https://api.github.com/users/" + username + "/events";

                Log.d("FetchClick", "url " + url);

                URI uri = new URI(url);
                httpGet.setURI(uri);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                InputStream inputStream = httpResponse.getEntity().getContent();
                bufferedReader = new BufferedReader(new InputStreamReader(
                        inputStream));

                String readLine = bufferedReader.readLine();
                while (readLine != null) {
                    stringBuffer.append(readLine);
                    stringBuffer.append("\n");
                    readLine = bufferedReader.readLine();
                }
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        // TODO: handle exception
                    }
                }
            }
            response = stringBuffer.toString();
            Log.d("Resp", " data " + stringBuffer.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            if (response != null && response.length() > 0) {

                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<List<FeedData>>() {
                    }.getType();
                    List<FeedData> posts = (List<FeedData>) gson.fromJson(response, listType);
                    Intent i = new Intent(MainActivity.this, FeedActivity.class);
                    i.putExtra("data", response);
                    startActivity(i);
                    finish();
                    Log.d("Resp", " data " + posts.size());
                } catch (Exception e) {
                    Log.d("Resp", " data " + e.getMessage());
                    FeedError error = gson.fromJson(response, FeedError.class);
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(error.getMessage());
                    alertDialog.show();
                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Please check your internet connection");
                alertDialog.show();
            }
        }
    }

}
