package hades.smsuploader;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hades on 04/07/2014.
 */
public class UploadSmsTask extends AsyncTask<Void, Void, Integer> {

    Context context;
    String username, password, url;
    Long max_last_accessed = -1L;
    Long last_accessed = -1L;

    public UploadSmsTask(Context c, String username, String password, String url, String last_accessed) {
        this.context = c;
        this.username = username;
        this.password = password;
        this.url = url;
        this.last_accessed = Long.parseLong(last_accessed);
        max_last_accessed = 0L;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int num_post = 0;
        while (SUPPORT_CONSTANTS.isRunning) {
            Integer num_send = postData();
            num_post++;
//            if (num_send <= 0)
//                Toast.makeText(context.getApplicationContext(), "No message to upload", Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(context.getApplicationContext(), "Upload " + num_send.toString() +
//                " message(s) successfully", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return num_post;
    }

    @Override
    protected void onPostExecute(Integer aInteger) {
//        if (aInteger <= 0)
//            Toast.makeText(context.getApplicationContext(), "No message to upload", Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(context.getApplicationContext(), "Upload " + aInteger.toString() +
//                " message(s) successfully", Toast.LENGTH_SHORT).show();
    }

    public int postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();

        if (!url.startsWith("http://"))
            url = "http://" + url;
        HttpPost httppost = new HttpPost(url);

        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String strIMEI = mngr.getDeviceId();

        JSONArray jsonArray = GetSMS();

        if (jsonArray.length() <= 0)
            return 0;

        try {
            // Add your data
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("IMEI", strIMEI));
            params.add(new BasicNameValuePair("last_accessed", last_accessed.toString()));
            params.add(new BasicNameValuePair("json", jsonArray.toString()));

            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);

            String resData = EntityUtils.toString(response.getEntity());

            if (resData.equals("1")) {
                if (max_last_accessed > last_accessed) {
                    SharedPreferences settings = context.getSharedPreferences(
                            SUPPORT_CONSTANTS.PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString("last_time", max_last_accessed.toString());
                    editor.commit();
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray.length();
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
                String datetime = c.getString(3);
                SMSEntity newSMS = new SMSEntity(c.getString(1),
                        c.getString(2), datetime);
                Long temp_last_accessed = Long.parseLong(datetime);
                if (temp_last_accessed > last_accessed) {
                    if (temp_last_accessed > max_last_accessed)
                        max_last_accessed = temp_last_accessed;
                    jsonArray.put(toJSon(newSMS));
                }
            }
        }

        return jsonArray;
    }

    private JSONObject toJSon(SMSEntity smsEntity) {
        JSONObject jsonObject = new JSONObject();
        try {
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
