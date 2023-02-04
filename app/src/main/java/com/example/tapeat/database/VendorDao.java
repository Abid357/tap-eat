package com.example.tapeat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tapeat.core.Vendor;

import java.util.List;

@Dao
public interface VendorDao {
    @Query("SELECT * FROM vendors")
    List<Vendor> getAll();

    @Insert
    void insert(Vendor vendor);

    @Update
    void update(Vendor vendor);

    @Delete
    void delete(Vendor vendor);
}
