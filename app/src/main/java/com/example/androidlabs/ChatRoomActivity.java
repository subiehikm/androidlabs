package com.example.androidlabs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidlabs.models.Message;
import com.example.androidlabs.models.Message.Type;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private final List<Message> messages = new ArrayList<>();
    MessageListAdapter messageListAdapter = new MessageListAdapter();

    ArrayList<Message> database = new ArrayList<>();

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        setListeners();

        loadDataFromDatabase();

        ContentValues newRowValues = new ContentValues();
        newRowValues.put(MyOpener.COL_RECEIVED, Message.text);
        newRowValues.put(MyOpener.COL_SENT, Message.text);
        long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);
        Message entrySent = new Message(Message.text, Type.SENT);
        Message entryReceived = new Message(Message.text, Type.RECEIVED);
        database.add(entrySent);
        database.add(entryReceived);
        messageListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Inserted item id:"+newId, Toast.LENGTH_LONG).show();

    }

    /**
     * Set event listeners on Views of this Activity
     */
    private void setListeners() {
        // Set the list's adapter: how it will function.
        ListView messageList = findViewById(R.id.list);
        messageList.setAdapter(messageListAdapter);

        // Set actions for send and receive button.
        EditText text = findViewById(R.id.message);
        findViewById(R.id.send).setOnClickListener(v -> {
            addMessage(text.getText().toString(), Type.SENT);
            text.setText(null);
        });
        findViewById(R.id.receive).setOnClickListener(v -> {
            addMessage(text.getText().toString(), Type.RECEIVED);
            text.setText(null);
        });

        // Set long click listener for each item in the row.
        messageList.setOnItemLongClickListener(((parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.
                    setTitle(getString(R.string.delete))
                    .setMessage(
                            getString(R.string.row) + position + "\n" +
                                    getString(R.string.database) + messageListAdapter.getItemId(position)
                    )
                    .setPositiveButton(getString(R.string.yes), (click, arg) -> {
                        messages.remove(position);
                        messageListAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.no, (click, arg) -> {
                    })
                    .show();

            return false;
        }));
    }

    /**
     * Add message to messages list and notifies the adapter.
     *
     * @param text the message to add
     * @param type the type of message
     */
    private void addMessage(String text, Type type) {
        Message message = new Message(text, type);
        messages.add(message);
        messageListAdapter.notifyDataSetChanged();
    }

    private class MessageListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View newView = view;
            LayoutInflater inflater = getLayoutInflater();

            if (messages.get(position).getType() == Type.SENT) {
                newView = inflater.inflate(R.layout.row_send, parent, false);
            } else if (messages.get(position).getType() == Type.RECEIVED) {
                newView = inflater.inflate(R.layout.row_receive, parent, false);
            }

            TextView messageText = newView.findViewById(R.id.message);
            messageText.setText(messages.get(position).getText());

            return newView;
        }
    }

    private void loadDataFromDatabase() {
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {MyOpener.COL_SENT, MyOpener.COL_RECEIVED, MyOpener.COL_ID};
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int SentColumnIndex = results.getColumnIndex(MyOpener.COL_SENT);
        int ReceivedColumnIndex = results.getColumnIndex(MyOpener.COL_RECEIVED);
        int idColumnIndex = results.getColumnIndex(MyOpener.COL_ID);
    }

    public void SimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags){
    }

}


