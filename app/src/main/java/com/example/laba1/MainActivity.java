package com.example.laba1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.laba1.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] array);
    public static native byte[] decrypt(byte[] key, byte[] array);

    // Used to load the 'laba1' library on application startup.
    static {
        System.loadLibrary("laba1");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int res = initRng();
        byte[] v = randomBytes(10);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }
    /**
     * A native method that is implemented by the 'laba1' native library,
     * which is packaged with this application.
     */


}