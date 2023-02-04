package com.example.tapeat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tapeat.R;
import com.example.tapeat.activity.MenuActivity;
import com.example.tapeat.activity.MenuDetailsActivity;
import com.example.tapeat.core.Global;
import com.example.tapeat.core.Menu;
import com.example.tapeat.core.Vendor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private List<Menu> menus;
    private Context context;
    private Activity activity;
    private int vendorIndex;

    public MenuAdapter(Activity activity, int vendorIndex, List<Menu> menus) {
        this.vendorIndex = vendorIndex;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.menus = menus;
    }

    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
        MenuViewHolder viewHolder = new MenuViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MenuAdapter.MenuViewHolder holder, int position) {
        holder.bindMenu(menus.get(position));
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menuImageView)
        ImageView menuImageView;
        @BindView(R.id.menuTextView)
        TextView menuTextView;
        private Context context;

        public MenuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vendor vendor = Global.getVendorList().get(vendorIndex);
                    Menu menu = vendor.getMenuList().get(getAdapterPosition());
                    if (menu.hasAudio() && menu.isTextToSpeech())
                        Global.speak(menu.getName());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(context, MenuDetailsActivity.class);
                    intent.putExtra("menuIndex", getAdapterPosition());
                    intent.putExtra("vendorIndex", vendorIndex);
                    activity.startActivityForResult(intent, MenuActivity.EDIT_MENU);
                    return true;
                }
            });
        }

        public void bindMenu(Menu menu) {
            menuTextView.setText(menu.getName());

            Global.updateImage(menuImageView, menu.getImagePath());

            int color = menu.getColor();
            if (color != -1)
                menuTextView.setTextColor(color);
        }
    }
}