package servicos;

import entidades.Aluno;
import entidades.Curso;
import entidades.Turma;
import utilitarios.Utilitarios;
import java.util.ArrayList;
import java.util.Scanner;

public class GestaoCursos {

    private static ArrayList<Curso> lista;
    private static ArrayList<Turma> turmas;
    private static ArrayList<Aluno> alunos;


    public GestaoCursos(ArrayList<Curso> lista) {
        this(lista, new ArrayList<Turma>(), new ArrayList<Aluno>());
    }

    public GestaoCursos(ArrayList<Curso> lista, ArrayList<Turma> turmas, ArrayList<Aluno> alunos) {
        this.lista = lista;
        this.turmas = turmas;
        this.alunos = alunos;
    }

    public static boolean cadastrarCurso(Curso c) {
        // REQ 2: verificar duplicidade de nome
        for (Curso existente : lista) {
            if (existente.nome.equalsIgnoreCase(c.nome)) {
                System.out.println("Erro: Curso ja cadastrado com o nome '" + c.nome + "'.");
                return false;
            }
            // REQ 2: verificar colisão de ID (ex: Contabilidade e Contribuicoes -> ambos CON)
            if (existente.idCurso.equalsIgnoreCase(c.idCurso)) {
                System.out.println("Erro: O ID gerado '" + c.idCurso
                        + "' ja esta em uso pelo curso '" + existente.nome + "'.");
                System.out.println("Dica: Use um nome com iniciais diferentes.");
                return false;
            }
        }
        lista.add(c);
        System.out.println("Curso cadastrado. ID: " + c.idCurso + " | Nome: " + c.nome);
        return true;
    }

    public static boolean editarCurso(String idCurso, String novoNome, double novoValor) {
        for (Curso c : lista) {
            if (c.idCurso.equalsIgnoreCase(idCurso)) {
                if (novoNome != null && !novoNome.isEmpty()) c.nome = novoNome;
                if (novoValor > 0) c.valorPropina = novoValor;
                System.out.println("Curso actualizado.");
                return true;
            }
        }
        System.out.println("Curso nao encontrado.");
        return false;
    }

    public static boolean eliminarCurso(String idCurso, Scanner teclado) {
        Curso alvo = null;
        for (Curso c : lista) {
            if (c.idCurso.equalsIgnoreCase(idCurso)) { alvo = c; break; }
        }
        if (alvo == null) { System.out.println("Curso nao encontrado."); return false; }

        for (Turma t : turmas) {
            if (t.idCurso.equalsIgnoreCase(idCurso)) {
                System.out.println("Erro: Nao e possivel eliminar curso com turmas associadas.");
                return false;
            }
        }
        for (Aluno a : alunos) {
            if (a.idCurso.equalsIgnoreCase(idCurso)) {
                System.out.println("Erro: Nao e possivel eliminar curso com alunos associados.");
                return false;
            }
        }

        System.out.print("Tem a certeza que deseja eliminar o curso '" + alvo.nome + "'? (S/N): ");
        if (!teclado.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Operacao cancelada.");
            return false;
        }
        lista.remove(alvo);
        System.out.println("Curso eliminado.");
        return true;
    }

    public static Curso buscarCurso(String termo) {
        for (Curso c : lista) {
            if (c.idCurso.equalsIgnoreCase(termo) || c.nome.equalsIgnoreCase(termo)) {
                return c;
            }
        }
        return null;
    }

    public static ArrayList<Curso> listarCursos() { return lista; }
}
