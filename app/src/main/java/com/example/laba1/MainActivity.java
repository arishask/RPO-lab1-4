package com.example.laba1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.laba1.databinding.ActivityMainBinding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;

public class MainActivity extends AppCompatActivity implements TransactionEvents {

    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] array);
    public static native byte[] decrypt(byte[] key, byte[] array);
    public native boolean transaction(byte[] trd);
    private ActivityMainBinding binding;

    static {
        System.loadLibrary("laba1");
        System.loadLibrary("mbedcrypto");
    }
    ActivityResultLauncher activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
//                            String pin = data.getStringExtra("pin");
//                            Toast.makeText(MainActivity.this, pin,
//                                    Toast.LENGTH_SHORT).show();
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }

                });

        int res = initRng();
        byte[] v = randomBytes(10);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
////        TextView tv = binding.sampleText;
////        tv.setText(stringFromJNI());
    }

    public void onButtonClick(View v)
    {

        testHttpClient();
//        byte[] trd = stringToHex("9F0206000000000100");
//        boolean ok = transaction(trd);
//        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
//        byte[] key = stringToHex("0123456789ABCDEF0123456789ABCDE0");
//        byte[] enc = encrypt(key, stringToHex("000000000000000102"));
//        byte[] dec = decrypt(key, enc);
//        String s = new String(Hex.encodeHex(dec)).toUpperCase();
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
//        Intent it = new Intent(this, PinpadActivity.class);
//        startActivity(it);
//        activityResultLauncher.launch(it);
    }

    private String pin;

    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }
    public static byte[] stringToHex(String s) {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (DecoderException ex) {
            hex = null;
        }
        return hex;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }

    protected void testHttpClient()
    {
        new Thread(() -> {
            try {
                HttpURLConnection uc = (HttpURLConnection)
                        (HttpURLConnection) (new URL("http://10.0.2.2:8081/api/v1/title").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream);
                String title = getPageTitle(html);
                runOnUiThread(() ->
                {
                    Toast.makeText(this, title, Toast.LENGTH_LONG).show();
                });

            } catch (Exception ex) {
                Log.e("fapptag", "Http client fails", ex);
            }
        }).start();
    }

    protected String getPageTitle(String html)
    {
        int pos = html.indexOf("<title");
        String p="not found";
        if (pos >= 0)
        {
            int pos2 = html.indexOf("<", pos + 1);
            if (pos >= 0)
                p = html.substring(pos + 7, pos2);
        }
        return p;
    }


}