package mmbuw.com.brokenproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

public class AnotherBrokenActivity extends Activity {

    private EditText url;
    private TextView txtView;
    private WebView wbvw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_broken);

        Intent intent = getIntent();
        String message = intent.getStringExtra(BrokenActivity.EXTRA_MESSAGE);
        //What happens here? What is this? It feels like this is wrong.
        //Maybe the weird programmer who wrote this forgot to do something?

        url=(EditText)findViewById(R.id.edtxtUrl);
        txtView=(TextView)findViewById(R.id.txtvwContext);
        wbvw=(WebView)findViewById(R.id.wbvw);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.another_broken, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchHTML(View view) throws IOException {

        //According to the exercise, you will need to add a button and an EditText first.
        //Then, use this function to call your http requests
        //Following hints:
        //Android might not enjoy if you do Networking on the main thread, but who am I to judge?
        //An app might not be allowed to access the internet without the right (*hinthint*) permissions
        //Below, you find a staring point for your HTTP Requests - this code is in the wrong place and lacks the allowance to do what it wants
        //It will crash if you just un-comment it.

        new fetchContent().execute(url.getText().toString());




    }


    private class fetchContent extends AsyncTask<String, Void, String> {
        private String responseAsString;
        @Override

        //We have to do this process in background
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpResponse response ;
            try {
                response = client.execute(new HttpGet(url.getText().toString())); //Set url = user input
                StatusLine status = response.getStatusLine();
                if (status.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    response.getEntity().writeTo(outStream);
                    responseAsString = outStream.toString();
                    System.out.println("Response string: " + responseAsString);
                }
                else{
                    response.getEntity().getContent().close();
                    throw new IOException(status.getReasonPhrase());
                }
            }
            //exception handling
            //gathered different exception titles from various topics of stackoverflow.com
            catch (ConnectTimeoutException e) {
                Toast.makeText(getApplicationContext(), "Error: ConnectTimeoutException", Toast.LENGTH_LONG).show();
            }catch (MalformedURLException e){
                Toast.makeText(getApplicationContext(), "URL: Malformed URL", Toast.LENGTH_LONG).show();
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(getApplicationContext(), "Error: UnsupportedEncodingException", Toast.LENGTH_LONG).show();
            } catch (ClientProtocolException e) {
                Toast.makeText(getApplicationContext(), "Error: ClientProtocolException", Toast.LENGTH_LONG).show();
            } catch (SocketTimeoutException e) {
                Toast.makeText(getApplicationContext(), "Error: SocketTimeoutException", Toast.LENGTH_LONG).show();
            }catch (ConnectException e) {
                Toast.makeText(getApplicationContext(), "Error: ConnectException", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error: IOException", Toast.LENGTH_LONG).show();
            }

            return null;
        }
        //This method will be executed after doInBackground
        @Override
        protected void onPostExecute(String result) {
            RadioButton radioText=(RadioButton)findViewById(R.id.radioText);
            RadioButton radioWeb=(RadioButton)findViewById(R.id.radioWeb);
            try{
                //check if the user want to show context in a textView or webView
                if (radioText.isChecked()){
                    wbvw.setVisibility(View.INVISIBLE);
                    txtView.setVisibility(View.VISIBLE);
                    txtView.setMovementMethod(new ScrollingMovementMethod());
                    txtView.setText(Html.fromHtml(responseAsString));
                }
                else if(radioWeb.isChecked()){
                    txtView.setVisibility(View.INVISIBLE);
                    wbvw.setVisibility(View.VISIBLE);
                    wbvw.setWebViewClient(new WebViewClient());
                    wbvw.loadUrl(url.getText().toString());
                }

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "No content", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
