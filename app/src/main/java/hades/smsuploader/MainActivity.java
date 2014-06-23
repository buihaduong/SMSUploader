package hades.smsuploader;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ShowDialogAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Onclick button "Add Numbers"
     * @param view
     */
    public void addNumber(View view)
    {
        Intent intent = new Intent(this, NumberManagerActivity.class);
        startActivity(intent);
    }

    /**
     * Onclick button "Upload SMS"
     * @param view
     */
    public void uploadSMS(View view)
    {
        GetAccountData();
        if (username == null || password == null) {
            ShowDialogAccount();
            GetAccountData();
            if (username == null || password == null)
                return;
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                postData();
            }
        };

        thread.start();
    }

    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(SUPPORT_CONSTANTS.HOST_NAME);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            ArrayList<String> listMsg = GetSMS();
            for (String s : listMsg) {
                nameValuePairs.add(new BasicNameValuePair("messages[]", s));
            }

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> GetSMS() {
        ArrayList<String> returnMsg = new ArrayList<String>();
        ArrayList<String> numbers = readFile();

        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "body"};
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        while (c.moveToNext()) {
            String number = c.getString(1);
            if (numbers.contains(number))
                returnMsg.add(c.getString(2));
        }

        return returnMsg;
    }

    private ArrayList<String> readFile() {
        ArrayList<String> returnList = null;
        FileInputStream fis;
        try {
            fis = openFileInput(SUPPORT_CONSTANTS.FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            returnList = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    private void GetAccountData() {
        SharedPreferences settings = getSharedPreferences(SUPPORT_CONSTANTS.PREFS_NAME, MODE_PRIVATE);
        username = settings.getString("username", null);
        password = settings.getString("password", null);
    }

    private void ShowDialogAccount() {
        FragmentManager manager = getFragmentManager();
        AccountDialog dialog = new AccountDialog();
        dialog.show(manager, "AccManager");
    }
}
