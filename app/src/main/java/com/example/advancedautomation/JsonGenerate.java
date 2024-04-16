package com.example.advancedautomation;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonGenerate {
    private Criptography criptography;
    private Regiao regiao;

    public JsonGenerate(){};

    public JsonGenerate(Regiao regiao){
        this.regiao = regiao;
        criptography = new Criptography();
    }

    public JSONObject generateJson(Regiao regiao){
        JSONObject data = new JSONObject();
        String name = regiao.getName();
        String latitude = Double.toString(regiao.getLatitude());
        String longitude = Double.toString(regiao.getLongitude());
        String user = Integer.toString(regiao.getUser());
        String timestamp = Long.toString(regiao.getTimestamp());
        String dataehora = regiao.getDataehora();

        try{
            data.put("name", name);
            data.put("latitude", latitude);
            data.put("longitude", longitude);
            data.put("user", user);
            data.put("timestamp", timestamp);
            data.put("dataehora", dataehora);
            System.out.println(data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public Regiao readJson(String jsonStr) {
        Regiao regiao = new Regiao(); // Move a declaração e inicialização para fora do bloco try-catch

        try {
            JSONObject strJson = new JSONObject(jsonStr);
            String name = strJson.getString("name");
            String latitude = strJson.getString("latitude");
            String longitude = strJson.getString("longitude");
            String user = strJson.getString("user");
            String timestamp = strJson.getString("timestamp");
            String dataehora = strJson.getString("dataehora");

            double doubleLat = Double.parseDouble(latitude);
            double doubleLong = Double.parseDouble(longitude);
            long timestampLong = Long.parseLong(timestamp);
            int userInt = Integer.parseInt(user);

            // Define os valores para a instância de Regiao
            regiao.setName(name);
            regiao.setLatitude(doubleLat);
            regiao.setLongitude(doubleLong);
            regiao.setUser(userInt);
            regiao.setTimestamp(timestampLong);
            regiao.setDataehora(dataehora);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return regiao; // Retorna a instância de Regiao criada
    }
}
