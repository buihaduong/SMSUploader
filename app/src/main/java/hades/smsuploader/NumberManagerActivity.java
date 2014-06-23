package hades.smsuploader;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class NumberManagerActivity extends Activity implements AddNumberDialog.Communicator, AdapterView.OnItemClickListener {

    ListView listview;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_manager);

        list = readFile();

        if (list == null)
            list = new ArrayList<String>();

        listview = (ListView) findViewById(R.id.listNumbers);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        listview.setOnItemClickListener(this);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.number_manager, menu);
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
        if (id == R.id.action_add_new_number) {
            FragmentManager manager = getFragmentManager();
            AddNumberDialog dialog = new AddNumberDialog();
            dialog.show(manager, "AddNumberDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogMessage(String message) {
        String toastMessage = "Added " + message;
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        message.replace(" ", "");
        if (!list.contains(message)) {
            list.add(message);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
        String item = (String) adapterView.getItemAtPosition(i);
        String toastMessage = "Removed " + item;
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        list.remove(item);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            FileOutputStream fos = openFileOutput(SUPPORT_CONSTANTS.FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
