package entidades;

public class NivelAcesso {
    public static final int ADMIN = 1;
    public static final int SECRETARIO = 2;
    public static final int FINANCEIRO = 3;

    public static int fromCodigo(int codigo) {
        if (codigo == ADMIN || codigo == SECRETARIO || codigo == FINANCEIRO) return codigo;
        return 0;
    }

    public static int fromTexto(String texto) {
        if (texto == null) return 0;
        try {
            return fromCodigo(Integer.parseInt(texto.trim()));
        } catch (Exception e) {
            String t = texto.trim();
            if (t.equalsIgnoreCase("ADMIN")) return ADMIN;
            if (t.equalsIgnoreCase("SECRETARIO")) return SECRETARIO;
            if (t.equalsIgnoreCase("FINANCEIRO")) return FINANCEIRO;
            return 0;
        }
    }

    public static String nome(int nivel) {
        if (nivel == ADMIN) return "ADMIN";
        if (nivel == SECRETARIO) return "SECRETARIO";
        if (nivel == FINANCEIRO) return "FINANCEIRO";
        return "DESCONHECIDO";
    }
}
