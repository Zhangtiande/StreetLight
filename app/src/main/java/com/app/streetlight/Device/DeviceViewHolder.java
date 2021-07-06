package com.app.streetlight.Device;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.streetlight.R;

import org.jetbrains.annotations.NotNull;

public class DeviceViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public ImageView statusImg;
    public TextView status;
    public TextView lum;
    public TextView light;
    public TextView rain;

    public DeviceViewHolder(@NonNull @NotNull View itemView, RecyclerView.Adapter adapter) {
        super(itemView);
        this.statusImg = itemView.findViewById(R.id.statusImg);
        this.name = itemView.findViewById(R.id.name);
        this.status = itemView.findViewById(R.id.Status);
        this.lum = itemView.findViewById(R.id.lum);
        this.light = itemView.findViewById(R.id.light);
        this.rain = itemView.findViewById(R.id.rain);
    }


}
