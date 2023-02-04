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
import com.example.tapeat.activity.VendorActivity;
import com.example.tapeat.activity.VendorDetailsActivity;
import com.example.tapeat.core.Global;
import com.example.tapeat.core.Menu;
import com.example.tapeat.core.Vendor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {
    private List<Vendor> vendors;
    private Context context;
    private Activity activity;

    public VendorAdapter(Activity activity, List<Vendor> vendors) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.vendors = vendors;
    }

    @Override
    public VendorAdapter.VendorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_list_item, parent, false);
        VendorViewHolder viewHolder = new VendorViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VendorAdapter.VendorViewHolder holder, int position) {

        holder.bindVendor(vendors.get(position));
    }

    @Override
    public int getItemCount() {
        return vendors.size();
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.vendorImageView)
        ImageView vendorImageView;
        @BindView(R.id.vendorTextView)
        TextView vendorTextView;
        private Context context;

        public VendorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vendor vendor = Global.getVendorList().get(getAdapterPosition());
                    if (vendor.hasAudio() && vendor.isTextToSpeech())
                        Global.speak(vendor.getName());

                    Intent intent = new Intent(context, MenuActivity.class);
                    intent.putExtra("vendorIndex", getAdapterPosition());
                    context.startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(context, VendorDetailsActivity.class);
                    intent.putExtra("vendorIndex", getAdapterPosition());
                    activity.startActivityForResult(intent, VendorActivity.EDIT_VENDOR);
                    return true;
                }
            });
        }

        public void bindVendor(Vendor vendor) {
            vendorTextView.setText(vendor.getName());

            Global.updateImage(vendorImageView, vendor.getImagePath());

            int color = vendor.getColor();
            if (color != -1)
                vendorTextView.setTextColor(color);
        }
    }
}