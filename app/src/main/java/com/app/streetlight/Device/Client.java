package com.app.streetlight.Device;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.region.IoTDARegion;

public class Client {
    private static IoTDAClient client;


    public Client() {
        String ak = "69W1S9K4PXTMSXF9TVDM";
        String sk = "BcgYaUtsJijJLkT3n8ZLO7xGDxuWblCi5NTVeCbr";
        // 创建认证
        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);
        // 创建IoTDAClient实例并初始化
        client = IoTDAClient.newBuilder()
                .withCredential(auth)
                .withRegion(IoTDARegion.CN_NORTH_4)
                .build();
    }


    public static synchronized IoTDAClient getClient() {
        if (client != null) {
            return client;
        }else {
            String ak = "69W1S9K4PXTMSXF9TVDM";
            String sk = "BcgYaUtsJijJLkT3n8ZLO7xGDxuWblCi5NTVeCbr";
            // 创建认证
            ICredential auth = new BasicCredentials()
                    .withAk(ak)
                    .withSk(sk);
            // 创建IoTDAClient实例并初始化
            client = IoTDAClient.newBuilder()
                    .withCredential(auth)
                    .withRegion(IoTDARegion.CN_NORTH_4)
                    .build();
        }
        return client;
    }
}
