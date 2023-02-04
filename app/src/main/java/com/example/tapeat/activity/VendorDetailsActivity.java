package com.example.tapeat.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendorDetailsActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;

    @BindView(R.id.vendorAddButton)
    Button addButton;

    @BindView(R.id.vendorDeleteButton)
    Button deleteButton;

    @BindView(R.id.vendorBrowseImageButton)
    Button browseImageButton;

    @BindView(R.id.vendorNameEditText)
    EditText nameEditText;

    @BindView(R.id.vendorImageDirectoryImageView)
    ImageView vendorImageView;

    @BindView(R.id.vendorColorImageView)
    ImageView colorImageView;

    @BindView(R.id.vendorAudioSwitch)
    Switch audioSwitch;

    @BindView(R.id.vendorRadioButton1)
    RadioButton radioButton1;

    @BindView(R.id.vendorRadioButton2)
    RadioButton radioButton2;

    @BindView(R.id.vendorBrowseAudioButton)
    Button audioBrowseButton;

    private String name;
    private String imageDirectory;
    private String audioDirectory;
    private int color;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        ButterKnife.bind(this);

        color = getColor(R.color.vendorList);

        audioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                radioButton1.setEnabled(checked);
                radioButton2.setEnabled(checked);
                audioBrowseButton.setEnabled(checked);
            }
        });

        browseImageButton.setOnClickListener(new View.OnClickListener() {
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
                new ColorPickerDialog.Builder(VendorDetailsActivity.this, R.style.AppTheme)
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

        int vendorIndex = getIntent().getIntExtra("vendorIndex", -1);
        if (vendorIndex != -1) {
            Vendor vendor = Global.getVendorList().get(vendorIndex);

            name = vendor.getName();
            nameEditText.setText(name);

            bitmap = Global.loadBitmap(vendor.getImagePath());
            imageDirectory = Global.updateImage(vendorImageView, vendor.getImagePath());

            audioSwitch.setChecked(vendor.hasAudio());
            radioButton1.setChecked(vendor.isTextToSpeech());
            radioButton2.setChecked(!vendor.isTextToSpeech());

            color = vendor.getColor();
            if (color != -1)
                colorImageView.setBackgroundColor(color);

            addButton.setText("UPDATE");
            addButton.setBackgroundResource(R.color.updateButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name = nameEditText.getText().toString().trim();
                    vendor.setName(name);

                    imageDirectory = Global.saveBitmap(bitmap, vendor.getId());
                    vendor.setImagePath(imageDirectory);

                    vendor.setColor(color);

                    vendor.setAudio(audioSwitch.isChecked());

                    vendor.setTextToSpeech(radioButton1.isChecked());

                    Global.updateVendor(vendor);

                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });

            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Global.getVendorList().remove(vendorIndex);

                    Global.deleteBitmap(vendor.getImagePath());

                    for (Menu menu : vendor.getMenuList())
                        Global.deleteBitmap(menu.getImagePath());

                    Global.deleteVendor(vendor);

                    Intent intent = getIntent();
                    intent.putExtra("vendorDeleted", true);

                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });
        } else {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<Vendor> vendorList = Global.getVendorList();

                    name = nameEditText.getText().toString().trim();

                    imageDirectory = Global.saveBitmap(bitmap, Global.getVendorMaxId());

                    Vendor vendor = new Vendor(name, imageDirectory, audioDirectory, audioSwitch.isChecked(), radioButton1.isChecked(), color);
                    vendorList.add(vendor);

                    Global.insertVendor(vendor);

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
                vendorImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image cannot be retrieved", Toast.LENGTH_LONG).show();
            }
        }
    }
}
