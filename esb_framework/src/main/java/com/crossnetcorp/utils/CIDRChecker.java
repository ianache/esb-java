package com.crossnetcorp.utils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class CIDRChecker {

    // Clase interna para representar un CIDR de forma eficiente
    private static class IPAddressRange {
        private final long networkAddress; // IP de inicio del rango
        private final long netmask;        // Máscara de red

        public IPAddressRange(String cidr) throws UnknownHostException {
            // Ejemplo: "192.168.1.0/24"
            int slashIndex = cidr.indexOf('/');
            String ipAddress = cidr.substring(0, slashIndex);
            int maskLength = Integer.parseInt(cidr.substring(slashIndex + 1));

            // Convertir la IP a long (32 bits)
            this.networkAddress = ipToLong(ipAddress);
            
            // Calcular la máscara (ej. /24 -> 0xFFFFFF00)
            this.netmask = ~((1L << (32 - maskLength)) - 1);
        }

        /**
         * Verifica si una IP dada pertenece a este rango CIDR.
         * Se basa en la operación lógica: (IP_A & MASCARA) == (IP_B & MASCARA)
         */
        public boolean contains(long ipAddress) {
            return (ipAddress & netmask) == networkAddress;
        }
    }

    private final List<IPAddressRange> ranges;

    /**
     * Construye el Checker pre-procesando toda la lista de CIDR.
     * @param cidrList Lista de cadenas CIDR (ej. "10.0.0.0/8")
     */
    public CIDRChecker(List<String> cidrList) {
        this.ranges = cidrList.stream()
                .map(cidr -> {
                    try {
                        return new IPAddressRange(cidr);
                    } catch (UnknownHostException e) {
                        // Manejo de errores de IP/CIDR inválido
                        System.err.println("CIDR inválido: " + cidr);
                        return null; 
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // ------------------------------------------------------------------
    // Métodos Principales de Búsqueda
    // ------------------------------------------------------------------

    /**
     * Convierte una dirección IP (string) a su representación numérica (long).
     */
    private static long ipToLong(String ipAddress) throws UnknownHostException {
        // Usa InetAddress para obtener los bytes de la IP de forma segura
        byte[] octets = InetAddress.getByName(ipAddress).getAddress();
        long result = 0;
        
        // Convierte el array de 4 bytes en un único valor long (32 bits)
        for (byte octet : octets) {
            result = (result << 8) | (octet & 0xFF);
        }
        return result;
    }

    /**
     * Búsqueda eficiente: Itera sobre los rangos precalculados.
     * * @param ipString Dirección IP a verificar (ej. "192.168.1.5")
     * @return true si la IP está contenida en algún CIDR.
     */
    public boolean isIPInList(String ipString) throws UnknownHostException {
        long targetIp = ipToLong(ipString);

        // Búsqueda lineal, pero con operaciones lógicas (muy rápido)
        for (IPAddressRange range : ranges) {
            if (range.contains(targetIp)) {
                return true;
            }
        }
        return false;
    }
}