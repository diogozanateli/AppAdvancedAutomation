package com.example.advancedautomation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Regiao {
    private String name; // Nome da região
    private double latitude; // Latitude da região
    private double longitude; // Longitude da região
    private int user; // Usuário associado à região
    private long timestamp; // Timestamp da criação da região em milissegundos
    private String dataehora; // Data e hora formatadas da criação da região

    // Obtém a data e hora atuais
    LocalDateTime now = LocalDateTime.now();
    // Formata a data e hora no padrão desejado
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Construtor vazio
    public Regiao() {};

    // Construtor com parâmetros
    public Regiao(String name, double latitude, double longitude, int user) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.timestamp = System.currentTimeMillis(); // Obtém o timestamp atual
        this.dataehora = now.format(formatter); // Formata a data e hora atual
    }

    // Métodos getter para obter os valores das variáveis de uma região
    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDataehora(){
        return dataehora;
    }

    // Métodos setter para definir os valores das variáveis de uma região
    public void setName(String name){
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDataehora(String dataehora){
        this.dataehora = dataehora;
    }

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

    // Método toString para imprimir informações sobre o objeto
    @Override
    public String toString() {
        return "Regiao{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", user=" + user +
                ", timestamp=" + timestamp +
                '}';
    }
}
