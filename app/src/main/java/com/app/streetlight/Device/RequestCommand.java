package com.app.streetlight.Device;

import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.CreateCommandRequest;
import com.huaweicloud.sdk.iotda.v5.model.DeviceCommandRequest;

import java.util.HashMap;

public class RequestCommand implements Runnable {
    public String value;
    public String id;
    public int num;
    String[] name = {"intensity", "fog"};
    public String commandName;
    public String command;


    public RequestCommand(String type, String value, String id, int num) {
        if (type.equals("fog")) {
            commandName = name[1];
            command = "fog_status";
        } else {
            commandName = name[0];
            command = "value";
        }
        this.value = value;
        this.id = id;
        this.num = num;
    }

    @Override
    public void run() {
        IoTDAClient client = Client.getClient();
        // 实例化请求对象
        CreateCommandRequest request = new CreateCommandRequest();
        request.withDeviceId(id);
        DeviceCommandRequest body = new DeviceCommandRequest();
        body.withCommandName(commandName);
        body.withServiceId("LightControl");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(command, value);
        hashMap.put("light", String.valueOf(num));
        body.setParas(hashMap);
        request.withBody(body);
        try {
            client.createCommand(request);
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
