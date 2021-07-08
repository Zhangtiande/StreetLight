package com.app.streetlight.Device;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Device implements Serializable {
    private String deviceId;
    private String deviceName;
    private String description;
    private String status;
    private String lum;
    private String zone;
    private String light;
    private int index;
    private boolean auto;
    private boolean fog;
    private String rain;

    public Device() {
    }

    public Device(String deviceId, String deviceName, String description, String status,
                  String lum, String zone, String light, int index, boolean auto, boolean fog,
                  String rain) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.description = description;
        this.status = status;
        this.lum = lum;
        this.zone = zone;
        this.light = light;
        this.index = index;
        this.auto = auto;
        this.fog = fog;
        this.rain = rain;
        this.index = Integer.parseInt(String.valueOf(deviceId.charAt(deviceId.length() - 1)));
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        this.index = Integer.parseInt(String.valueOf(deviceId.charAt(deviceId.length() - 1)));
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLum() {
        return lum;
    }

    public void setLum(String lum) {
        this.lum = lum;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean isFog() {
        return fog;
    }

    public void setFog(boolean fog) {
        this.fog = fog;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }


    @NotNull
    @Override
    public String toString() {
        return "环境光照强度：" + lum +
                ",  灯亮度：" + light +
                ",  自动调光：" + auto +
                ",  雾灯：" + fog +
                ",  雨滴测量量：" + rain;
    }

}
