package com.app.streetlight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.streetlight.Device.Device;
import com.app.streetlight.Device.DeviceViewHolder;
import com.app.streetlight.Device.GetDevice;
import com.app.streetlight.data.Database;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private RecyclerView recyclerView;
    private List<Device> devices = new ArrayList<>();
    private RecyclerView.Adapter<DeviceViewHolder> adapter = null;
    private  Database data;
    private  SQLiteDatabase db;
    private final Handler handler = new Handler();
    private final Runnable task =new Runnable() {
        public void run() {
            handler.postDelayed(this,5*1000);//设置延迟时间，此处是5秒
            GetDevice getDevice = new GetDevice();
            Future<List<Device>> future = executorService.submit(getDevice);
            try {
                List<Device> deviceList;
                deviceList = future.get();
                if (deviceList != null) {
                    devices = deviceList;
                    getDevice();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new Database(this);
        db = data.getReadableDatabase();
        setContentView(R.layout.activity_main);
        loadDB();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerView.Adapter<DeviceViewHolder>() {
            @NonNull
            @NotNull
            @Override
            public DeviceViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.item, null);
                return new DeviceViewHolder(view, this);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull @NotNull DeviceViewHolder holder, int position) {
                Device device = devices.get(position);
                String status = device.getStatus();
                holder.name.setText("设备名称：" + device.getDeviceName());
                holder.status.setText("设备状态：" + status);
                holder.light.setText("设备光照强度：" + device.getLight());
                holder.lum.setText("环境光照强度：" + device.getLum());
                if (status.equals("OFFLINE")) {
                    holder.statusImg.setImageResource(R.drawable.off);
                } else {
                    holder.statusImg.setImageResource(R.drawable.on);
                }
                holder.statusImg.setOnClickListener(v -> {
                    if (device.getStatus().equals("OFFLINE")) {
                        Toast.makeText(MainActivity.this, "该设备离线，请激活设备后再尝试。",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("Device", device);
                    startActivity(intent);
                });
            }

            @Override
            public int getItemCount() {
                return devices.size();
            }
        };
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            deviceTask deviceTask = new deviceTask();
            deviceTask.execute();
            swipeRefreshLayout.setRefreshing(false);
        });
        deviceTask deviceTask = new deviceTask();
        deviceTask.execute();
        handler.postDelayed(task,5000);//延迟调用
    }


    public void getDevice() {
        recyclerView.setAdapter(adapter);
        Cursor c = db.rawQuery("select * from device", null);
        if (c.getCount() != 0) {
            devices.forEach(device -> {
                String sql = String.format("update device set name='%s',des='%s',status='%s'," +
                                "lum='%s',zone='%s',light='%s',auto='%s' where Id='%s';",
                        device.getDeviceName(), device.getDescription(), device.getStatus(), device.getLum(),
                        device.getZone(), device.getLight(), device.isAuto(), device.getDeviceId());
                db.execSQL(sql);
            });
        } else {
            devices.forEach(device -> {
                String sql = String.format("insert into device values('%s','%s','%s','%s','%s','%s','%s','%s');",
                        device.getDeviceId(), device.getDeviceName(), device.getDescription(),
                        device.getStatus(), device.getLum(), device.getZone(), device.getLight(), device.isAuto());
                db.execSQL(sql);
            });
        }
        c.close();
    }


    public void loadDB() {
        Cursor c = db.rawQuery("Select * from device;", null);
        while (c.moveToNext()) {
            Device device = new Device();
            device.setAuto(Boolean.parseBoolean(c.getString(7)));
            device.setDeviceId(c.getString(0));
            device.setDeviceName(c.getString(1));
            device.setDescription(c.getString(2));
            device.setStatus(c.getString(3));
            device.setLum(c.getString(4));
            device.setZone(c.getString(5));
            device.setLight(c.getString(6));
            devices.add(device);
        }
        c.close();
    }

    @SuppressLint("StaticFieldLeak")
    class deviceTask extends AsyncTask<Void, Void, Void> {


        private Void Void;


        @Override
        protected Void doInBackground(Void... voids) {
            GetDevice getDevice = new GetDevice();
            Future<List<Device>> future = executorService.submit(getDevice);
            try {
                List<Device> deviceList;
                deviceList = future.get();
                if (deviceList != null) {
                    devices = deviceList;
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Void;

        }

        @Override
        protected void onPostExecute(java.lang.Void unused) {
            getDevice();
        }
    }
}