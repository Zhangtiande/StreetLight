package com.app.streetlight.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.streetlight.DetailActivity;
import com.app.streetlight.Device.Device;
import com.app.streetlight.Device.RequestCommand;
import com.app.streetlight.Device.UpdateProperties;
import com.app.streetlight.MainActivity;
import com.app.streetlight.R;
import com.app.streetlight.databinding.FragmentDetailBinding;
import com.app.streetlight.databinding.FragmentLineBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static Device device;
    private static final StringBuilder str = new StringBuilder();

    private int index = 1;

    private FragmentDetailBinding binding;
    private FragmentLineBinding binding2;

    public static PlaceholderFragment newInstance(int index, Device d) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        device = d;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root;

        if (index == 1) {
            binding = FragmentDetailBinding.inflate(inflater, container, false);
            root = binding.getRoot();
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = root.findViewById(R.id.switch1);
            aSwitch.setChecked(device.isAuto());
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                UpdateProperties updateProperties = new UpdateProperties(device.getDeviceId(), isChecked, device.getIndex());
                Thread thread = new Thread(updateProperties);
                thread.start();
            });
            TextView name = root.findViewById(R.id.detail_id);
            name.setText(device.getDeviceName());
            TextView log = root.findViewById(R.id.log);
            SeekBar seekBar = root.findViewById(R.id.seekBar2);
            seekBar.setMax(1024);
            seekBar.setMin(0);
            str.append("当前亮度：").append(seekBar.getProgress()).append('\n');
            log.setText(str);
            if (device.getLight() == null) {
                seekBar.setProgress(0);
            } else {
                seekBar.setProgress(Math.round(Float.parseFloat(device.getLight()) / 100 * 1024));
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    str.append("当前亮度：").append(seekBar.getProgress()).append('\n');
                    log.setText(str);
                    RequestCommand requestCommand = new RequestCommand(String.valueOf(seekBar.getProgress())
                            , device.getDeviceId(), device.getIndex());
                    Thread thread = new Thread(requestCommand);
                    thread.start();
                }
            });
        } else {
            binding2 = FragmentLineBinding.inflate(inflater, container, false);
            root = binding2.getRoot();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}