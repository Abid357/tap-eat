package com.example.tapeat.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.room.Room;

import com.example.tapeat.R;
import com.example.tapeat.database.AppDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Global {

    private static List<Vendor> vendorList;

    private static Context context;

    private static TextToSpeech speaker;

    private static AppDatabase database;

    public Global(Context context) {
        Global.context = context;

        database = Room.databaseBuilder(context,
                AppDatabase.class, "vendors")
                .fallbackToDestructiveMigration()
                .build();

        speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = speaker.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "English voice is not supported on this device.", Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(context, "Text-to-speech is not supported in this device.", Toast.LENGTH_LONG).show();
            }
        });

        vendorList = new ArrayList<>();
    }

    public static List<Vendor> getVendorList() {
        return vendorList;
    }

    public static String updateImage(ImageView view, String directory) {
        if (directory == null)
            view.setImageResource(R.drawable.ic_error_image);
        else {
            view.setImageBitmap(null);
            Bitmap bitmap = loadBitmap(directory);
            if (bitmap == null) {
                view.setImageResource(R.drawable.ic_error_image);
                directory = null;
            } else
                view.setImageBitmap(bitmap);
        }
        return directory;
    }

    public static String saveBitmap(Bitmap bitmap, int vendorIndex) {
        return saveBitmap(context, bitmap, vendorIndex, -1);
    }

    public static String saveBitmap(Context context, Bitmap bitmap, int vendorIndex, int menuIndex) {
        if (bitmap == null)
            return null;

        String root = context.getFilesDir().getAbsolutePath() + "/images";
        File rootFile = new File(root);
        rootFile.mkdirs();

        String name = "vendor" + vendorIndex;
        if (menuIndex != -1)
            name += "_menu" + menuIndex;
        name += ".jpg";

        File file = new File(rootFile, name);
        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static Bitmap loadBitmap(String filePath) {
        if (filePath == null)
            return null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Toast.makeText(context, "Image cannot be found", Toast.LENGTH_SHORT).show();
                return null;
            }
            return BitmapFactory.decodeFile(filePath);
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteBitmap(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void speak(String thing) {
        if (thing != null && !thing.isEmpty())
            speaker.speak(thing, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public static int getVendorMaxId() {
        int maxId = 0;
        if (vendorList.isEmpty())
            return maxId;
        for (Vendor vendor : vendorList)
            if (vendor.getId() > maxId)
                maxId = vendor.getId();
        return ++maxId;
    }

    public static int getMenuMaxId() {
        int maxId = 0;
        if (vendorList.size() == 1 && vendorList.get(0).getMenuList().isEmpty())
            return maxId;
        for (Vendor vendor : vendorList)
            for (Menu menu : vendor.getMenuList())
                if (menu.getId() > maxId)
                    maxId = menu.getId();
        return ++maxId;
    }

    public static void loadDatabase() {
        vendorList = database.vendorDao().getAll();

        List<Menu> allMenuList = database.menuDao().getAll();
        for (Menu menu : allMenuList)
            for (Vendor vendor : vendorList)
                if (menu.getVendorId() == vendor.getId()) {
                    vendor.getMenuList().add(menu);
                    break;
                }
    }

    public static void insertVendor(Vendor vendor) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.vendorDao().insert(vendor);
                return null;
            }
        }.execute();
    }

    public static void updateVendor(Vendor vendor) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.vendorDao().update(vendor);
                return null;
            }
        }.execute();
    }

    public static void deleteVendor(Vendor vendor) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (Menu menu : vendor.getMenuList())
                    database.menuDao().delete(menu);
                database.vendorDao().delete(vendor);
                return null;
            }
        }.execute();
    }

    public static void insertMenu(Menu menu) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.menuDao().insert(menu);
                return null;
            }
        }.execute();
    }

    public static void updateMenu(Menu menu) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.menuDao().update(menu);
                return null;
            }
        }.execute();
    }

    public static void deleteMenu(Menu menu) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.menuDao().delete(menu);
                return null;
            }
        }.execute();
    }
}
