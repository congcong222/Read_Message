package com.example.bt5_thltdtdd;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.widget.TextView;

import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 1;
    private static final int CALL_LOG_PERMISSION_CODE = 2;
    private static final int CONTACTS_PERMISSION_CODE = 3;
    private TextView tvMessages, tvCallLog, tvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvMessages = findViewById(R.id.tvMessages);
        tvCallLog = findViewById(R.id.tvCallLog);
        tvContacts = findViewById(R.id.tvContacts);

        // Request permissions
        requestPermissions();
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        } else {
            readSms();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, CALL_LOG_PERMISSION_CODE);
        } else {
            readCallLog();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_CODE);
        } else {
            readContacts();
        }
    }

    private void readSms() {
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder smsBuilder = new StringBuilder();
            do {
                int addressIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
                int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);
                int dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE);

                if (addressIndex != -1 && bodyIndex != -1 && dateIndex != -1) {
                    String address = cursor.getString(addressIndex);
                    String smsBody = cursor.getString(bodyIndex);
                    long dateMillis = cursor.getLong(dateIndex);
                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(dateMillis));

                    smsBuilder.append("Gửi đến: ").append(address).append("\n")
                            .append("Nội dung: ").append(smsBody).append("\n")
                            .append("Thời gian: ").append(date).append("\n\n");
                }
            } while (cursor.moveToNext());
            tvMessages.setText(smsBuilder.toString());
            cursor.close();
        } else {
            tvMessages.setText("Không có tin nhắn nào.");
        }
    }

    private void readCallLog() {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder callLogBuilder = new StringBuilder();
            do {
                int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);

                if (numberIndex != -1 && typeIndex != -1 && dateIndex != -1) {
                    String number = cursor.getString(numberIndex);
                    String type = cursor.getString(typeIndex);
                    long dateMillis = cursor.getLong(dateIndex);
                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(dateMillis));

                    String callType = (type.equals(String.valueOf(CallLog.Calls.INCOMING_TYPE)) ? "Đến" : "Đi");
                    callLogBuilder.append("Số: ").append(number).append("\n")
                            .append("Loại: ").append(callType).append("\n")
                            .append("Thời gian: ").append(date).append("\n\n");
                }
            } while (cursor.moveToNext());
            tvCallLog.setText(callLogBuilder.toString());
            cursor.close();
        } else {
            tvCallLog.setText("Không có nhật ký cuộc gọi nào.");
        }
    }

    private void readContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder contactsBuilder = new StringBuilder();
            do {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                if (nameIndex != -1 && numberIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);

                    contactsBuilder.append("Tên: ").append(name).append("\n")
                            .append("Số: ").append(number).append("\n\n");
                }
            } while (cursor.moveToNext());
            tvContacts.setText(contactsBuilder.toString());
            cursor.close();
        } else {
            tvContacts.setText("Không có danh bạ nào.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readSms();
        } else if (requestCode == CALL_LOG_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readCallLog();
        } else if (requestCode == CONTACTS_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readContacts();
        }
    }
}