package main.network;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Méthode simplifiée qui fournit uniquement les propriétés nécessaires pour les interfaces réseaux
 */
public class NetworkInterfacePerso {
    private String interfaceName;   // interface name (eth0)
    private String interfaceDisplayname; // interface display name (Intel(R) 82579LM Gigabit Network Connection)
    private String ip; // interface IPv4 IP (172.16.41.134)
    private InetAddress address; // objet inetaddress utilisé pour la création du socket

    public NetworkInterfacePerso(String _interfaceName, String _interfaceDisplayname, String _ip, InetAddress _address) {
        interfaceName = _interfaceName;
        interfaceDisplayname = _interfaceDisplayname;
        ip = _ip;
        address = _address;
    }

    @Override public String toString() {
        return "NetworkInterfacePerso{" + "interfaceName='" + interfaceName + '\'' + ", interfaceDisplayname='"
          + interfaceDisplayname + '\'' + ", ip='" + ip + '\'' + '}';
    }

    /**
     * Retourne le nom système de l'interface réseau
     * @return une chaîne de caractère du nom système de l'interface réseau
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Retourne le nom convivial de l'interface réseau
     * @return une chaîne de caractère du nom convivial de l'interface réseau
     */
    public String getInterfaceDisplayname() {
        return interfaceDisplayname;
    }

    /**
     * Retourne l'adresse IP de l'interface réseau
     * @return une chaîne de caractère de l'adresse IP de l'interface réseau
     */
    public String getIp() {
        return ip;
    }

    /**
     * Retourne l'objet InetAddress de l'interface réseau
     * @return un object InetAddress de l'interface réseau
     */
    public InetAddress getAddress() {
        return address;
    }
}
