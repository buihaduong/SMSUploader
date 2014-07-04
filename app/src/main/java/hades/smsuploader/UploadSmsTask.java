package hades.smsuploader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by hades on 04/07/2014.
 */
public class UploadSmsTask extends AsyncTask<Void, Void, Long> {

    Context context;
    String username, password, url;

    public UploadSmsTask(Context c, String username, String password, String url) {
        this.context = c;
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        postData();
        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Toast.makeText(context.getApplicationContext(), "Upload successfully", Toast.LENGTH_SHORT).show();
    }

    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();

        if (!url.startsWith("http://"))
            url = "http://" + url;
        HttpPost httppost = new HttpPost(url);

        JSONArray jsonArray = GetSMS();

        try {
            // Add your data

            httppost.setEntity(new StringEntity(jsonArray.toString()));

            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray GetSMS() {
        JSONArray jsonArray = new JSONArray();
        ArrayList<String> numbers = readFile();

        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "body", "date"};
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        while (c.moveToNext()) {
            String number = c.getString(1);
            if (numbers.contains(number)) {
                SMSEntity newSMS = new SMSEntity(username, password, c.getString(1),
                        c.getString(2), c.getString(3));
                jsonArray.put(toJSon(newSMS));
            }
        }

        return jsonArray;
    }

    private JSONObject toJSon(SMSEntity smsEntity) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", smsEntity.getUsername());
            jsonObject.put("password", smsEntity.getPassword());
            jsonObject.put("sender", smsEntity.getSender());
            jsonObject.put("content", smsEntity.getContent());
            jsonObject.put("datetime", smsEntity.getDatetime());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return jsonObject;
        }
    }

    private ArrayList<String> readFile() {
        ArrayList<String> returnList = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(SUPPORT_CONSTANTS.FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            returnList = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }
}
