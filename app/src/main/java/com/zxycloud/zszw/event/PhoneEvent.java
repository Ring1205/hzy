package com.zxycloud.zszw.event;

/**
 * Created by leiming on 2018/1/30.
 */

public class PhoneEvent {
    public static final int callOut = 1055;
    private int phoneState;

    public PhoneEvent(int phoneState) {
        this.phoneState = phoneState;
    }

    public int getPhoneState() {
        return phoneState;
    }
}
