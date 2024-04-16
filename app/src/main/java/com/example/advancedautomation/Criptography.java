package com.example.advancedautomation;
public class Criptography {
    private static final String SECRET_KEY = "10011010110101001";
    public Criptography(){};
    public static String encrypt(String data) {
        String encryptString = "";

        char[] chars = data.toCharArray(); // Converte a string em uma matriz de caracteres
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] ^ SECRET_KEY.charAt(i % SECRET_KEY.length())); // Aplica a operação XOR com a chave secreta
        }
        encryptString = new String(chars);
        return encryptString;  // Retorna a string criptografada
    }

    public static String decrypt(String encryptedData){

        String decryptedString = ""; // Inicializa uma string para armazenar o texto descriptografado
        char[] chars = encryptedData.toCharArray(); // Converte a string criptografada em uma matriz de caracteres
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] ^ SECRET_KEY.charAt(i % SECRET_KEY.length())); // Aplica a operação XOR com a chave secreta
        }
        decryptedString = new String(chars); // Converte a matriz de caracteres de volta para uma string
        return decryptedString;
    }
}


