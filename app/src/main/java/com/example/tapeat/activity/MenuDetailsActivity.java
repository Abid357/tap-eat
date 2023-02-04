package com.example.tapeat.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tapeat.R;
import com.example.tapeat.core.Global;
import com.example.tapeat.core.Menu;
import com.example.tapeat.core.Vendor;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuDetailsActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;

    @BindView(R.id.menuAddButton)
    Button addButton;

    @BindView(R.id.menuDeleteButton)
    Button deleteButton;

    @BindView(R.id.menuBrowseImageButton)
    Button imageBrowseButton;

    @BindView(R.id.menuNameEditText)
    EditText nameEditText;

    @BindView(R.id.menuImageDirectoryImageView)
    ImageView menuImageView;

    @BindView(R.id.menuColorImageView)
    ImageView colorImageView;

    @BindView(R.id.menuAudioSwitch)
    Switch audioSwitch;

    @BindView(R.id.menuRadioButton1)
    RadioButton radioButton1;

    @BindView(R.id.menuRadioButton2)
    RadioButton radioButton2;

    @BindView(R.id.menuBrowseAudioButton)
    Button audioBrowseButton;

    private String name;
    private String imageDirectory;
    private String audioDirectory;
    private int color;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_details);
        ButterKnife.bind(this);

        color = getColor(R.color.menuList);

        audioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                radioButton1.setEnabled(checked);
                radioButton2.setEnabled(checked);
                audioBrowseButton.setEnabled(checked);
            }
        });

        imageBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        colorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(MenuDetailsActivity.this, R.style.AppTheme)
                        .setTitle("ColorPicker Dialog")
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton("Select",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        colorImageView.setBackgroundColor(envelope.getColor());
                                        color = envelope.getColor();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachAlphaSlideBar(true) // default is true. If false, do not show the AlphaSlideBar.
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        int menuIndex = getIntent().getIntExtra("menuIndex", -1);
        if (menuIndex != -1) {
            int vendorIndex = getIntent().getIntExtra("vendorIndex", -1);
            Vendor vendor = Global.getVendorList().get(vendorIndex);
            Menu menu = vendor.getMenuList().get(menuIndex);

            name = menu.getName();
            nameEditText.setText(name);

            bitmap = Global.loadBitmap(menu.getImagePath());
            imageDirectory = Global.updateImage(menuImageView, menu.getImagePath());

            audioSwitch.setChecked(menu.hasAudio());
            radioButton1.setChecked(menu.isTextToSpeech());
            radioButton2.setChecked(!menu.isTextToSpeech());

            color = menu.getColor();
            if (color != -1)
                colorImageView.setBackgroundColor(color);

            addButton.setText("UPDATE");
            addButton.setBackgroundResource(R.color.updateButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = nameEditText.getText().toString().trim();
                    menu.setName(name);

                    imageDirectory = Global.saveBitmap(getApplicationContext(), bitmap, vendor.getId(), menu.getId());
                    menu.setImagePath(imageDirectory);

                    menu.setColor(color);

                    menu.setAudio(audioSwitch.isChecked());

                    menu.setTextToSpeech(radioButton1.isChecked());

                    Global.updateMenu(menu);

                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });

            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Global.getVendorList().get(vendorIndex).getMenuList().remove(menuIndex);

                    Global.deleteBitmap(menu.getImagePath());

                    Global.deleteMenu(menu);

                    Intent intent = getIntent();
                    intent.putExtra("menuDeleted", true);

                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });
        } else {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int vendorIndex = getIntent().getIntExtra("vendorIndex", -1);
                    Vendor vendor = Global.getVendorList().get(vendorIndex);
                    int vendorId = vendor.getId();
                    List<Menu> menuList = vendor.getMenuList();

                    name = nameEditText.getText().toString().trim();

                    imageDirectory = Global.saveBitmap(getApplicationContext(), bitmap, vendor.getId(), Global.getMenuMaxId());

                    Menu menu = new Menu(vendorId, name, imageDirectory, audioDirectory, audioSwitch.isChecked(), radioButton1.isChecked(), color);
                    menuList.add(menu);

                    Global.insertMenu(menu);

                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) return;

            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                menuImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image cannot be retrieved", Toast.LENGTH_LONG).show();
            }
        }
    }
}
