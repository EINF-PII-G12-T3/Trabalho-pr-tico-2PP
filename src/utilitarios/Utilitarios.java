package utilitarios;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import entidades.Aluno;
import entidades.Curso;
import entidades.Emolumento;
import entidades.Turma;
import java.io.File;

public class Utilitarios {

    // REQ 1: BI angolano tem 14 caracteres alfanumericos
    public static boolean validarFormatoBI(String bi) {
        return bi != null && bi.length() == 14 && bi.matches("[a-zA-Z0-9]{14}");
    }

    public static boolean validarEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");
    }

    // ID do curso = 3 primeiros chars do nome (ex: Contabilidade -> CON)
    public static String gerarIdCurso(String nomeCurso) {
        if (nomeCurso == null || nomeCurso.trim().isEmpty()) return "CUR";
        String limpo = nomeCurso.trim().replaceAll("[^A-Za-z]", "").toUpperCase();
        if (limpo.length() >= 3) return limpo.substring(0, 3);
        while (limpo.length() < 3) limpo += "X";
        return limpo;
    }

    public static String gerarIdCurso(ArrayList<Curso> cursos) {
        return "CUR";
    }

    public static String gerarIdEmolumento(ArrayList<Emolumento> emolumentos) {
        int max = 0;
        for (Emolumento e : emolumentos) {
            String id = e.idEmolumento;
            if (id != null && id.toUpperCase().startsWith("EM")) {
                try {
                    int n = Integer.parseInt(id.substring(2));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("EM%04d", max + 1);
    }

    /**
     * REQ 3: ID da Turma = 3 primeiros chars do Curso + Classe + Letra Sequencial
     * Exemplo: Mecanica, 10a Classe, 1a turma -> MEC10A
     *          Mecanica, 11a Classe, 2a turma -> MEC11B
     *
     * A letra e sequencial POR CLASSE dentro do curso.
     */
    public static String gerarIdTurma(String nomeCurso, int classe, ArrayList<Turma> turmas) {
        String sigla = gerarSiglaCurso(nomeCurso);
        String prefixo = sigla + classe; // ex: "MEC10"
        int count = 0;
        for (Turma t : turmas) {
            if (t.idTurma != null
                    && t.idTurma.toUpperCase().startsWith(prefixo.toUpperCase())
                    && t.classe == classe
                    && t.idCurso != null) {
                count++;
            }
        }
        char letra = (char) ('A' + count); // 0->A, 1->B, ...
        return prefixo + letra;
    }

    /**
     * REQ 3: Nome da Turma = "Turma A", "Turma B", ... por classe dentro do curso.
     */
    public static String gerarNomeTurma(String nomeCurso, int classe, ArrayList<Turma> turmas) {
        String sigla = gerarSiglaCurso(nomeCurso);
        String prefixo = sigla + classe;
        int count = 0;
        for (Turma t : turmas) {
            if (t.idTurma != null
                    && t.idTurma.toUpperCase().startsWith(prefixo.toUpperCase())
                    && t.classe == classe) {
                count++;
            }
        }
        char letra = (char) ('A' + count);
        return "Turma " + letra;
    }

    // Overloads de compatibilidade (sem classe) — mantidos para código legado
    public static String gerarIdTurma(String nomeCurso, ArrayList<Turma> turmas) {
        // fallback sem classe: usa contagem global do curso (comportamento anterior)
        String sigla = gerarSiglaCurso(nomeCurso);
        int max = 0;
        for (Turma t : turmas) {
            if (t.idTurma != null && t.idTurma.toUpperCase().startsWith(sigla.toUpperCase())) {
                try {
                    int n = Integer.parseInt(t.idTurma.substring(sigla.length()));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return sigla + (max + 1);
    }

    public static String gerarNomeTurma(String nomeCurso, ArrayList<Turma> turmas) {
        String sigla = gerarSiglaCurso(nomeCurso);
        int count = 0;
        for (Turma t : turmas) {
            if (t.idTurma != null && t.idTurma.toUpperCase().startsWith(sigla.toUpperCase())) {
                count++;
            }
        }
        char letra = (char) ('A' + count);
        return "Turma " + letra;
    }

    public static String gerarSiglaCurso(String nomeCurso) {
        String limpo = nomeCurso == null ? "TRM" : nomeCurso.replaceAll("[^A-Za-z]", "").toUpperCase();
        if (limpo.length() >= 3) return limpo.substring(0, 3);
        while (limpo.length() < 3) limpo += "X";
        return limpo;
    }

    public static String gerarNumeroMatricula(ArrayList<Aluno> alunos) {
        int anoAtual = LocalDate.now().getYear();
        String prefixo = String.valueOf(anoAtual);
        int max = 0;
        for (Aluno a : alunos) {
            String mat = a.numeroMatricula;
            if (mat != null && mat.length() == 8 && mat.startsWith(prefixo)) {
                try {
                    int n = Integer.parseInt(mat.substring(4));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return prefixo + String.format("%04d", max + 1);
    }

    public static String dataParaString(LocalDate d) {
        if (d == null) return "N/A";
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static LocalDate stringParaData(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return null;
        }
    }

    public static void limparConsola() {
        System.out.println("\n\n\n\n\n");
    }

    public static void pausar(Scanner teclado) {
        System.out.println("\nPressione ENTER para continuar.");
        teclado.nextLine();
    }
    
    public static String garantirPasta(String caminho) {
    File pasta = new File(caminho);
    if (!pasta.exists()) {
        pasta.mkdirs(); // cria a pasta e todas as intermédias necessárias
    }
    return caminho;
}
}



