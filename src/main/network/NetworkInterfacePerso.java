package main.network;

import java.net.NetworkInterface;


public class NetworkInterfacePerso {
    private String interfaceName;   // interface name (eth0)
    private String interfaceDisplayname; // interface display name (Intel(R) 82579LM Gigabit Network Connection)
    private String ip; // interface IPv4 IP (172.16.41.134)

    public NetworkInterfacePerso(String _interfaceName, String _interfaceDisplayname, String _ip) {
        interfaceName = _interfaceName;
        interfaceDisplayname = _interfaceDisplayname;
        ip = _ip;
    }

    @Override public String toString() {
        return "NetworkInterfacePerso{" + "interfaceName='" + interfaceName + '\'' + ", interfaceDisplayname='"
          + interfaceDisplayname + '\'' + ", ip='" + ip + '\'' + '}';
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getInterfaceDisplayname() {
        return interfaceDisplayname;
    }

    public String getIp() {
        return ip;
    }
}
