package hades.smsuploader;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    String username, password, url;
    String last_accessed;

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
     *
     * @param view
     */
    public void addNumber(View view) {
        Intent intent = new Intent(this, NumberManagerActivity.class);
        startActivity(intent);
    }

    /**
     * Onclick button "Upload SMS"
     *
     * @param view
     */
    public void uploadSMS(View view) {
        Button btnUpload = (Button) findViewById(R.id.btnUpload);
        SUPPORT_CONSTANTS.isRunning = !SUPPORT_CONSTANTS.isRunning;
        if (SUPPORT_CONSTANTS.isRunning)
            btnUpload.setText("Cancel Upload");
        else
            btnUpload.setText("Upload SMS");

        GetAccountData();
        if (username == null || password == null || url == null) {
            ShowDialogAccount();
            GetAccountData();
            if (username == null || password == null || url == null)
                return;
        }

        new UploadSmsTask(this, username, password, url, last_accessed).execute();
        ;
    }

    private void ShowDialogAccount() {
        FragmentManager manager = getFragmentManager();
        AccountDialog dialog = new AccountDialog();
        dialog.show(manager, "AccManager");
    }

    private void GetAccountData() {
        SharedPreferences settings = getSharedPreferences(SUPPORT_CONSTANTS.PREFS_NAME, MODE_PRIVATE);
        last_accessed = settings.getString("last_time", "-1");
        username = settings.getString("username", null);
        password = settings.getString("password", null);
        url = settings.getString("url", null);

    }
}
