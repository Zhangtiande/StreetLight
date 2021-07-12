package com.app.streetlight.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.streetlight.Device.Device;
import com.app.streetlight.Device.GetDevice;
import com.app.streetlight.Device.RequestCommand;
import com.app.streetlight.Device.UpdateProperties;
import com.app.streetlight.R;
import com.app.streetlight.databinding.FragmentDetailBinding;
import com.app.streetlight.databinding.FragmentLineBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final StringBuilder str = new StringBuilder();
    private static Device device;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler handler = new Handler();
    private int index = 1;
    private TextView log;
    private FragmentDetailBinding binding;
    private FragmentLineBinding binding2;
    private Runnable task;

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
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw = root.findViewById(R.id.fog_switch);
            sw.setChecked(device.isFog());
            sw.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                RequestCommand requestCommand = new RequestCommand("fog", String.valueOf(isChecked),
                        device.getDeviceId(), device.getIndex());
                Thread thread = new Thread(requestCommand);
                thread.start();
            }));
            TextView name = root.findViewById(R.id.detail_id);
            name.setText(device.getDeviceName());
            log = root.findViewById(R.id.log);
            log.setMovementMethod(ScrollingMovementMethod.getInstance());
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
                    RequestCommand requestCommand = new RequestCommand("light", String.valueOf(seekBar.getProgress())
                            , device.getDeviceId(), device.getIndex());
                    Thread thread = new Thread(requestCommand);
                    thread.start();
                }
            });
            task = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 15 * 1000);
                    GetDevice getDevice = new GetDevice();
                    Future<List<Device>> future = executorService.submit(getDevice);
                    try {
                        List<Device> deviceList;
                        deviceList = future.get();
                        if (deviceList != null) {
                            deviceList.forEach(device1 -> {
                                if (device1.getDeviceId().equals(device.getDeviceId())) {
                                    device = device1;
                                    setTextEdit(device);
                                }
                            });
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.postDelayed(task, 1000);//延迟调用
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


    public synchronized void setTextEdit(Device device) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String date = format.format(new Date());
        str.append(date).append('\n');
        str.append(device.toString()).append('\n').append('\n');
        log.setText(str);
    }
}