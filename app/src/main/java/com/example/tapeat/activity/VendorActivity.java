package com.example.tapeat.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapeat.R;
import com.example.tapeat.adapter.VendorAdapter;
import com.example.tapeat.core.Global;
import com.example.tapeat.core.Vendor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendorActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int ADD_VENDOR = 2;
    public static final int EDIT_VENDOR = 3;
    public List<Vendor> vendorList;
    @BindView(R.id.vendorRecyclerView)
    RecyclerView recyclerView;
    private VendorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);
        ButterKnife.bind(this);

        vendorList = Global.getVendorList();
        init();

        Toolbar toolbar = findViewById(R.id.vendorToolbar);
        setSupportActionBar(toolbar);

        checkPermissions();
    }

    private void init() {
        adapter = new VendorAdapter(this, vendorList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VendorActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vendor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_vendor:
                Intent intent = new Intent(this, VendorDetailsActivity.class);
                startActivityForResult(intent, ADD_VENDOR);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_VENDOR && resultCode == Activity.RESULT_OK) {
            adapter.notifyItemInserted(vendorList.size() - 1);
            Toast.makeText(this, "Vendor added", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_VENDOR && resultCode == Activity.RESULT_OK) {
            int vendorIndex = data.getIntExtra("vendorIndex", -1);
            boolean vendorDeleted = data.getBooleanExtra("vendorDeleted", false);
            if (vendorDeleted) {
                adapter.notifyItemRemoved(vendorIndex);
                Toast.makeText(this, "Vendor deleted", Toast.LENGTH_SHORT).show();
            } else {
                adapter.notifyItemChanged(vendorIndex);
                Toast.makeText(this, "Vendor updated", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                    new AlertDialog.Builder(this)
                            .setMessage("This app requires to access your external storage to load image files. Without permission, the app will shut down.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                }
                            })
                            .create()
                            .show();
                else {
                    new AlertDialog.Builder(this)
                            .setMessage("This app requires to access your external storage to load image files. Please consider granting permission from settings.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.parse("package:" + getPackageName()));
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .create()
                            .show();
                }
        }
    }
}
