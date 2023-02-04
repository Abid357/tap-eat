package com.example.tapeat.core;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "menus")
public class Menu {
    @PrimaryKey
    private int id;

    private int vendorId;
    private String name;
    private String imagePath;
    private String audioPath;
    private boolean hasAudio;
    private boolean isTextToSpeech;
    private int color;

    public Menu(int vendorId, String name, String imagePath, String audioPath, boolean hasAudio, boolean isTextToSpeech, int color) {
        this.vendorId = vendorId;
        this.id = Global.getMenuMaxId();
        this.name = name;
        this.imagePath = imagePath;
        this.audioPath = audioPath;
        this.hasAudio = hasAudio;
        this.isTextToSpeech = isTextToSpeech;
        this.color = color;
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

    public int getId() {
        return id;
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

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
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
}
