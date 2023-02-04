package com.example.tapeat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.tapeat.core.Menu;
import com.example.tapeat.core.Vendor;

@Database(entities = {Vendor.class, Menu.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VendorDao vendorDao();

    public abstract MenuDao menuDao();
}
