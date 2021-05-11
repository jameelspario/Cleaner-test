package com.spario.misctest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spario.misctest.cleaner.activity.CleanerActivity;
import com.spario.misctest.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.text1.setText("CPU "+readUsage());
        binding.text2.setText("Ram "+get_ram());

        initialCache();
        //catches();
        getCachemethods();
    }

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    double get_ram(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / 0x100000L;

        //Percentage can be calculated for API 16+
        double percentAvail = mi.availMem / (double)mi.totalMem * 100.0;

        return percentAvail;
    }

    void initialCache(){
        File cacheDir = getCacheDir();
        File ex = getExternalCacheDir();

        log("getExternalCacheDir "+ ex.getName() +" "+ex.isDirectory());
        log("cacheDir "+ cacheDir.getName() + " " + cacheDir.isDirectory());
        calcdirSize(cacheDir);
    }

    void catches(){
        File[] ex = getExternalCacheDirs();
        log("catches1 "+ ex.toString());
        for(File file:ex){
            if(file!=null && file.isDirectory()) {
                log("is dir1 "+ file.listFiles());
                browseDir(file);
            }
            if(file!=null && file.isDirectory()){
                log("is file1 " + file.length());
            }
        }
    }

    void browseDir(File dir){
        for(File file:dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                log("is dir2 " );
                browseDir(file);
            }
            if(file!=null && file.isDirectory()){
                log("is file2 " + file.length());
            }
        }
    }

    long size;
    void calcdirSize(File dir){
        long size=0;
        for(File file:dir.listFiles()){
            if(file!=null && file.isDirectory()) {
                log("is dir");
                browseDir(file);
            }
            if(file!=null && file.isDirectory()){
                log("is file " + file.length());
            }
        }
    }


    void getCachemethods(){
        PackageManager pm = getPackageManager();
        // Get all methods on the PackageManager
        Method[] methods = pm.getClass().getDeclaredMethods();
        for (Method m : methods) {
            log(m.getName());
            if (m.getName().equals("freeStorage")) {
                // Found the method I want to use
                try {
                    long desiredFreeStorage = 8 * 1024 * 1024 * 1024; // Request for 8GB of free space
                    m.invoke(pm, desiredFreeStorage , null);
                } catch (Exception e) {
                    // Method invocation failed. Could be a permission problem
                }
                break;
            }
        }
    }


    void log(String s){
        Log.e("TAG", ""+s);
    }

    public void buttonClick1(View view) {
        startActivity(new Intent(this, CleanerActivity.class));
    }
}