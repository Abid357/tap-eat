package com.example.tapeat.core;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "vendors")
public class Vendor {
    @PrimaryKey
    private int id;

    private String name;
    private String imagePath;
    private String audioPath;
    private boolean hasAudio;
    private boolean isTextToSpeech;
    private int color;

    @Ignore
    private List<Menu> menuList;

    public Vendor(String name, String imagePath, String audioPath, boolean hasAudio, boolean isTextToSpeech, int color) {
        id = Global.getVendorMaxId();
        this.name = name;
        this.imagePath = imagePath;
        this.audioPath = audioPath;
        this.hasAudio = hasAudio;
        this.isTextToSpeech = isTextToSpeech;
        this.color = color;
        this.menuList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public boolean hasAudio() {
        return hasAudio;
    }

    public void setAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public boolean isTextToSpeech() {
        return isTextToSpeech;
    }

    public void setTextToSpeech(boolean textToSpeech) {
        isTextToSpeech = textToSpeech;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public void addMenu(Menu newMenu) {
        if (!menuList.contains(newMenu))
            menuList.add(newMenu);
    }

    public void deleteMenu(Menu oldMenu) {
        menuList.remove(oldMenu);
    }
}
