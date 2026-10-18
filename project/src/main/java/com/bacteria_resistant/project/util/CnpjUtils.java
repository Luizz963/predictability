package com.bacteria_resistant.project.util;

public final class CnpjUtils {

    private static final int[] PESOS_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private CnpjUtils() {}


    public static String normalize(String raw) {
        if (raw == null) return "";
        return raw.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }


    public static boolean isValid(String raw) {
        String c = normalize(raw);
        if (c.length() != 14 || !c.matches("[A-Z0-9]{14}")) return false;
        if (c.chars().distinct().count() == 1) return false;          // "11111111111111" etc.
        if (!Character.isDigit(c.charAt(12)) || !Character.isDigit(c.charAt(13))) return false; // DV é numérico

        int dv1 = calcDv(c.substring(0, 12), PESOS_DV1);
        if (dv1 != (c.charAt(12) - '0')) return false;

        int dv2 = calcDv(c.substring(0, 13), PESOS_DV2);
        return dv2 == (c.charAt(13) - '0');
    }

    private static int calcDv(String parte, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < parte.length(); i++) {
            soma += valor(parte.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }


    private static int valor(char ch) {
        return Character.isDigit(ch) ? ch - '0' : ch - 'A' + 10;
    }
}