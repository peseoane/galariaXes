package com.peseoane.galaria.xestion.component;

import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class Ping {

    public static boolean pingIpv4(String ipv4) {
        try {
            InetAddress address = InetAddress.getByName(ipv4);
            boolean reachable = address.isReachable(1024);
            if (!reachable) {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 " + ipv4);
                int returnVal = p1.waitFor();
                return returnVal == 0;
            }
            return reachable;
        } catch (Exception e) {
            try {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 " + ipv4);
                int returnVal = p1.waitFor();
                return returnVal == 0;
            } catch (Exception e1) {
                e1.printStackTrace();
                return false;
            }
        }
    }

}