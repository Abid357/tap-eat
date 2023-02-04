package com.example.tapeat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tapeat.core.Menu;

import java.util.List;

@Dao
public interface MenuDao {
    @Query("SELECT * FROM menus")
    List<Menu> getAll();

    @Insert
    void insert(Menu menu);

    @Update
    void update(Menu menu);

    @Delete
    void delete(Menu menu);
}
