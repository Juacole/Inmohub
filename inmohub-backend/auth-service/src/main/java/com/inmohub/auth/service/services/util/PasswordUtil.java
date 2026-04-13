package com.inmohub.auth.service.services.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para el manejo de contraseñas.
 */
// TODO: Eliminar esta clase para implementar BCryptPasswordEncoder. Esta implementación de SHA-256 es provisional
public class PasswordUtil {

    /**
     * Genera un hash SHA-256 de la contraseña proporcionada.
     * @param password Contraseña en texto plano.
     * @return Cadena hexadecimal del hash.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            // SHA-256 simple
            MessageDigest aux = MessageDigest.getInstance("SHA-256");
            byte[] hash = aux.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convertimos el byte array a representación Hexadecimal
            return toHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }

    /**
     * Realiza una comprobación entre dos contraseñas hasheadas.
     *
     * @param pp Contraseña en texto plano.
     * @param hp Hash de la contraseña del usuario.
     * @return Valor booleano de la comparación.
     */
    public static boolean checkPassword(String pp, String hp) {
        String newHash = hashPassword(pp);
        return newHash.equals(hp);
    }

    /**
     * Convierte un array de bytes (hash) es una cadena de texto
     * hexadecimal, para un facil almacenamiento en base de datos.
     *
     * @param hash Array de bytes del hash de la contraseña.
     * @return Contraseña en formato texto hexadecimal.
     */
    private static String toHexString(byte[] hash) {
        BigInteger n = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(n.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
