package com.example.mathlibrary;

public class Utils {
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Converte as coordenadas de graus para radianos
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Aplica a fórmula de Haversine
        double a = Math.pow(Math.sin(latDistance / 2), 2) + Math.pow(Math.sin(lonDistance / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Raio médio da Terra em quilômetros
        double earthRadius = 6371.01;

        // Retorna a distância em metros
        return (earthRadius * c) * 1000;
    }
}