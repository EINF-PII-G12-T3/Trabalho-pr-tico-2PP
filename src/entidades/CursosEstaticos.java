package entidades;


import servicos.Fabricas;
import java.util.ArrayList;

/**
 * REQ 1: Cursos são estáticos, divididos por departamentos.
 * DET  — vai até 13.a Classe.
 * DCSA — vai até 12.a Classe.
 */
public class CursosEstaticos {

    public static final String DEPT_TECNICOS = "CURSOS TECNICOS";
    public static final String DEPT_PUNIV = "PUNIV";

    // Propina padrão (pode ser ajustada pelo Admin via edição directa de propina)
    public static final double PROPINA_PADRAO = 15000.00;

    /**
     * Retorna a lista completa dos cursos estáticos.
     * Deve ser chamada em Sgmf.java para popular a lista global de cursos
     * caso ela esteja vazia (primeira execução ou após reset).
     */
    public static ArrayList<Curso> obterTodos() {
        ArrayList<Curso> lista = new ArrayList<>();

        // --- CURSOS TECNICOS (10.a a 13.a Classe) ---
        lista.add(Fabricas.criarCurso("INF", "Informatica", PROPINA_PADRAO, DEPT_TECNICOS, 13));
        lista.add(Fabricas.criarCurso("CTB", "Contabilidade", PROPINA_PADRAO, DEPT_TECNICOS, 13));
        lista.add(Fabricas.criarCurso("CIV", "Construcao Civil", PROPINA_PADRAO, DEPT_TECNICOS, 13));
        lista.add(Fabricas.criarCurso("QUI", "Quimica", PROPINA_PADRAO, DEPT_TECNICOS, 13));
        lista.add(Fabricas.criarCurso("MEC", "Mecanica", PROPINA_PADRAO, DEPT_TECNICOS, 13));
        lista.add(Fabricas.criarCurso("GEP", "Gestao Empresarial", PROPINA_PADRAO, DEPT_TECNICOS, 13));
        lista.add(Fabricas.criarCurso("PET", "Petroleos", PROPINA_PADRAO, DEPT_TECNICOS, 13));

        // --- PUNIV (10.a a 12.a Classe) ---
        lista.add(Fabricas.criarCurso("CFB", "Ciencias Fisicas e Biologicas", PROPINA_PADRAO, DEPT_PUNIV, 12));
        lista.add(Fabricas.criarCurso("CEJ", "Ciencias Economicas e Juridicas", PROPINA_PADRAO, DEPT_PUNIV, 12));

    return lista;

    }

    /** Verifica se um curso pertence aos Cursos Técnicos. */
    public static boolean isTecnico(Curso c) {
        return DEPT_TECNICOS.equals(c.departamento);
    }


    /** Verifica se um curso pertence ao PUNIV. */
    public static boolean isPUNIV(Curso c) {
        return DEPT_PUNIV.equals(c.departamento);
    }


    /** Retorna as classes válidas para o curso (10, 11, 12 e opcionalmente 13). */
    public static int[] classesPermitidas(Curso c) {
        if (DEPT_TECNICOS.equals(c.departamento)) {
            return new int[]{10, 11, 12, 13};
        } else {
            return new int[]{10, 11, 12};
        }
    }

}
