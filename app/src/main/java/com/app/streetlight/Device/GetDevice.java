package com.app.streetlight.Device;


import android.util.Log;

import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.ListDevicesRequest;
import com.huaweicloud.sdk.iotda.v5.model.ListDevicesResponse;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceShadowRequest;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceShadowResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetDevice implements Callable<List<Device>> {
    private static final String TAG = "GetDevice";

    @Override
    public List<Device> call() {
        ArrayList<Device> devices = new ArrayList<>();
        IoTDAClient client = Client.getClient();
        ListDevicesRequest request = new ListDevicesRequest();
        ShowDeviceShadowRequest req = new ShowDeviceShadowRequest();
        try {
            int count = 0;
            // 调用查询设备列表接口
            ListDevicesResponse response = client.listDevices(request);
            String re = response.toString();
            String idPattern = "deviceId: (.*)\n";
            String namePattern = "deviceName: (.*)\n";
            String desPattern = "description: (.*)\n";
            String statusPattern = "status: (.*)\n";
            Pattern pattern = Pattern.compile(idPattern);
            Matcher matcher = pattern.matcher(re);
            while (matcher.find()) {
                Device temp = new Device();
                temp.setDeviceId(Objects.requireNonNull(matcher.group(1)));
                devices.add(temp);
            }
            pattern = Pattern.compile(namePattern);
            matcher = pattern.matcher(re);
            while (matcher.find()) {
                Device temp = devices.get(count);
                temp.setDeviceName(matcher.group(1));
                devices.set(count++, temp);
            }
            count = 0;
            pattern = Pattern.compile(desPattern);
            matcher = pattern.matcher(re);
            while (matcher.find()) {
                Device temp = devices.get(count);
                temp.setDescription(matcher.group(1));
                devices.set(count++, temp);
            }
            count = 0;
            pattern = Pattern.compile(statusPattern);
            matcher = pattern.matcher(re);
            while (matcher.find()) {
                Device temp = devices.get(count);
                temp.setStatus(matcher.group(1));
                devices.set(count++, temp);
            }
        } catch (ConnectionException | RequestTimeoutException | ServiceResponseException e) {
            e.printStackTrace();
            Log.e(TAG,"获取设备列表失败!");
            return null;
        }

        devices.forEach(device -> {
            String t = String.valueOf(device.getIndex());
            req.withDeviceId(device.getDeviceId());
            ShowDeviceShadowResponse response = client.showDeviceShadow(req);
//            String str = "luminance_light" + t + "=(.*), intensity_light" + t +
//                    "=(.*), auto_light" + t + "=(.*)";
            String s1 = "luminance_light" + t + "=([0-9]{1,6})";
            String s2 = "intensity_light" + t + "=([0-9]{1,2}.[0-9]{1,2})";
            String s3 = "auto_light" + t + "=([a-z]{4,5})";
            String s4 = "rain_light" + t + "=([0-9]{1,4})";
            String s5 = "fog_light" + t + "=([a-z]{4,5})";
            Pattern pattern = Pattern.compile(s1);
            Matcher matcher = pattern.matcher(response.toString());
            while (matcher.find()) {
                device.setLum(matcher.group(1));
            }
            pattern = Pattern.compile(s2);
            matcher = pattern.matcher(response.toString());
            while (matcher.find()) {
                device.setLight(matcher.group(1));
            }
            pattern = Pattern.compile(s3);
            matcher = pattern.matcher(response.toString());
            while (matcher.find()) {
                String s = matcher.group(1);
                assert s != null;
                device.setAuto(Boolean.parseBoolean(s));
            }
            pattern = Pattern.compile(s4);
            matcher = pattern.matcher(response.toString());
            while (matcher.find()) {
                String s = matcher.group(1);
                device.setRain(s);
            }
            pattern = Pattern.compile(s5);
            matcher = pattern.matcher(response.toString());
            while (matcher.find()) {
                String s = matcher.group(1);
                assert s != null;
                device.setFog(Boolean.parseBoolean(s));
            }
        });
        return devices;
    }
}
