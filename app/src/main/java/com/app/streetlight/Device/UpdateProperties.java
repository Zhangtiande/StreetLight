package com.app.streetlight.Device;

import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.DevicePropertiesRequest;
import com.huaweicloud.sdk.iotda.v5.model.UpdatePropertiesRequest;

import java.util.HashMap;

public class UpdateProperties implements Runnable {
    private final String id;
    private final String auto;
    private final int index;

    public UpdateProperties(String id, boolean auto, int index) {
        this.id = id;
        this.auto = String.valueOf(auto);
        this.index = index;
    }

    @Override
    public void run() {
        IoTDAClient client = Client.getClient();
        // 实例化请求对象
        UpdatePropertiesRequest request = new UpdatePropertiesRequest();
        request.withDeviceId(id);
        HashMap<String, HashMap<String, String>> hashMap = new HashMap<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("auto_light" + index, auto);
        hashMap.put("BasicData", map);
        DevicePropertiesRequest body = new DevicePropertiesRequest();
        body.setServices(hashMap);
        request.withBody(body);
        try {
            client.updateProperties(request);
        } catch (ConnectionException | RequestTimeoutException e) {
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            e.printStackTrace();
            System.out.println(e.getHttpStatusCode());
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMsg());
        }
    }
}
