package com.example.tapeat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapeat.R;
import com.example.tapeat.adapter.MenuAdapter;
import com.example.tapeat.core.Global;
import com.example.tapeat.core.Menu;
import com.example.tapeat.core.Vendor;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {
    public static final int ADD_MENU = 1;
    public static final int EDIT_MENU = 2;

    @BindView(R.id.menuRecyclerView)
    RecyclerView recyclerView;
    private MenuAdapter adapter;

    private List<Menu> menuList;
    private int vendorIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        vendorIndex = getIntent().getIntExtra("vendorIndex", -1);

        Vendor vendor = Global.getVendorList().get(vendorIndex);
        menuList = vendor.getMenuList();

        init();

        Toolbar toolbar = findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);
    }

    private void init() {
        adapter = new MenuAdapter(this, vendorIndex, menuList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MenuActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_menu:
                Intent intent = new Intent(this, MenuDetailsActivity.class);
                intent.putExtra("vendorIndex", vendorIndex);
                startActivityForResult(intent, ADD_MENU);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MENU && resultCode == Activity.RESULT_OK) {
            adapter.notifyItemInserted(menuList.size() - 1);
            Toast.makeText(this, "Menu added", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_MENU && resultCode == Activity.RESULT_OK) {
            int menuIndex = data.getIntExtra("menuIndex", -1);
            boolean menuDeleted = data.getBooleanExtra("menuDeleted", false);
            if (menuDeleted) {
                adapter.notifyItemRemoved(menuIndex);
                Toast.makeText(this, "Menu deleted", Toast.LENGTH_SHORT).show();
            } else {
                adapter.notifyItemChanged(menuIndex);
                Toast.makeText(this, "Menu updated", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
