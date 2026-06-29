package servicos;

import entidades.Aluno;
import entidades.Turma;
import java.time.LocalDate;
import relatorios.GeradorRelatorio;
import utilitarios.Utilitarios;
import java.util.ArrayList;
import java.util.Scanner;

public class GestaoTurmas {

    private static ArrayList<Turma> lista;
    private static ArrayList<Aluno> alunos;


    public GestaoTurmas(ArrayList<Turma> lista) {
        this(lista, new ArrayList<Aluno>());
    }

    public GestaoTurmas(ArrayList<Turma> lista, ArrayList<Aluno> alunos) {
        this.lista = lista;
        this.alunos = alunos;
    }

    public static boolean cadastrarTurma(Turma t) {
        for (Turma existente : lista) {
            
              if (existente.idTurma.equalsIgnoreCase(t.idTurma)) {
                System.out.println("Erro: Já existe uma turma cadastrada com o ID " + t.idTurma);
                return false;
            }
        }
        lista.add(t);
        System.out.println("Turma cadastrada: " + t.idTurma + " | " + t.nome);
        return true;
    }

    public boolean editarTurma(String idTurma, String novoNome, int novasVagas, int novaClasse) {
    for (Turma t : lista) {
        if (t.idTurma.equalsIgnoreCase(idTurma)) {
            if (novoNome != null && !novoNome.isEmpty()) t.nome = novoNome;
            if (novasVagas > 0) {
                if (novasVagas < t.vagasOcupadas) {
                    System.out.println("Erro: As novas vagas nao podem ser menores que as vagas ocupadas (" + t.vagasOcupadas + ").");
                    return false;
                }
                t.vagas = novasVagas;
            } else if (novasVagas == 0) {
                // o Campo foi mantido
                System.out.println("Aviso: Campo de vagas mantido (" + t.vagas + ") — nenhum valor introduzido.");
            }
            if (novaClasse >= 10 && novaClasse <= 13) t.classe = novaClasse;
            System.out.println("Turma actualizada.");
            return true;
        }
    }
    System.out.println("Turma nao encontrada.");
    return false;
}


    public static boolean eliminarTurma(String idTurma, Scanner teclado) {
        Turma alvo = null;
        for (Turma t : lista) {
            if (t.idTurma.equalsIgnoreCase(idTurma)) { alvo = t; break; }
        }
        if (alvo == null) { System.out.println("Turma nao encontrada."); return false; }

        // REQ 4: recolher alunos afectados
        ArrayList<Aluno> afectados = new ArrayList<>();
        for (Aluno a : alunos) {
            if (a.idTurma.equalsIgnoreCase(idTurma)) afectados.add(a);
        }

        if (!afectados.isEmpty()) {
            // Encontrar turmas gemeas: mesma classe + mesmo curso + com vagas livres
            ArrayList<Turma> gemeas = new ArrayList<>();
            for (Turma t : lista) {
                if (!t.idTurma.equalsIgnoreCase(idTurma)
                        && t.idCurso.equalsIgnoreCase(alvo.idCurso)
                        && t.classe == alvo.classe
                        && (t.vagas - t.vagasOcupadas) > 0) {
                    gemeas.add(t);
                }
            }
            // Verificar capacidade total
            int vagasDisponiveis = 0;
            for (Turma g : gemeas) vagasDisponiveis += (g.vagas - g.vagasOcupadas);
            if (vagasDisponiveis < afectados.size()) {
                System.out.println("Erro: Nao ha vagas suficientes nas turmas gemeas para realocar todos os "
                        + afectados.size() + " aluno(s). Operacao abortada.");
                System.out.println("Vagas disponiveis nas turmas gemeas: " + vagasDisponiveis);
                return false;
            }
            System.out.println("Turma tem " + afectados.size() + " aluno(s). Sera(ao) realocado(s) automaticamente.");
            System.out.print("Confirmar eliminacao e realocacao automatica? (S/N): ");
            if (!teclado.nextLine().trim().equalsIgnoreCase("S")) {
                System.out.println("Operacao cancelada.");
                return false;
            }
            // Realocar: distribuir pelos gemeos em ordem
            int idxGemea = 0;
            for (Aluno a : afectados) {
                // Avançar para gemea com vaga
                while (idxGemea < gemeas.size() && (gemeas.get(idxGemea).vagas - gemeas.get(idxGemea).vagasOcupadas) == 0) idxGemea++;
                Turma destino = gemeas.get(idxGemea);
                String turmaAntiga = a.idTurma;
                a.idTurma = destino.idTurma;
                destino.vagasOcupadas++;
                System.out.println("  Aluno " + a.nomeCompleto + " realocado: "
                        + turmaAntiga + " -> " + destino.idTurma);
            }
        } else {
            System.out.print("Tem a certeza que deseja eliminar a turma '" + alvo.nome + "'? (S/N): ");
            if (!teclado.nextLine().trim().equalsIgnoreCase("S")) {
                System.out.println("Operacao cancelada.");
                return false;
            }
        }

        lista.remove(alvo);
        System.out.println("Turma eliminada com sucesso.");
        return true;
    }

    // ===================== REQ 3: Trocar Aluno de Turma =====================

    /**
     * Lista turmas com vagas livres da mesma classe e curso do aluno.
     */
    public static ArrayList<Turma> listarTurmasParaTroca(Aluno aluno) {
        ArrayList<Turma> opcoes = new ArrayList<>();
        for (Turma t : lista) {
            if (!t.idTurma.equalsIgnoreCase(aluno.idTurma)
                    && t.idCurso.equalsIgnoreCase(aluno.idCurso)
                    && t.classe == aluno.classe
                    && (t.vagas - t.vagasOcupadas) > 0) {
                opcoes.add(t);
            }
        }
        return opcoes;
    }

    /**
     * Troca o aluno da turma actual para novaIdTurma,
     * ajustando as vagas nas duas turmas.
     */
    public static boolean trocarTurmaAluno(Aluno aluno, String novaIdTurma) {
        Turma turmaAntiga = buscarTurma(aluno.idTurma);
        Turma turmaNova   = buscarTurma(novaIdTurma);

        if (turmaNova == null) {
            System.out.println("Erro: Turma de destino nao encontrada.");
            return false;
        }
        if ((turmaNova.vagas - turmaNova.vagasOcupadas) <= 0) {
            System.out.println("Erro: A turma de destino nao tem vagas livres.");
            return false;
        }
        if (turmaNova.idCurso.equalsIgnoreCase(aluno.idCurso) == false
                || turmaNova.classe != aluno.classe) {
            System.out.println("Erro: A turma de destino e de outro curso ou classe.");
            return false;
        }
        // Efectuar troca
        String idAnterior = aluno.idTurma;
        aluno.idTurma = novaIdTurma;
        turmaNova.vagasOcupadas++;
        if (turmaAntiga != null) if (turmaAntiga.vagasOcupadas > 0) turmaAntiga.vagasOcupadas--;
        System.out.println("Troca efectuada: " + idAnterior + " -> " + novaIdTurma
                + " para o aluno " + aluno.nomeCompleto);
        return true;
    }

    public static Turma buscarTurma(String termo) {
        for (Turma t : lista) {
            if (t.idTurma.equalsIgnoreCase(termo) || t.nome.equalsIgnoreCase(termo)) {
                return t;
            }
        }
        return null;
    }

    public static ArrayList<Turma> listarTurmas() {
        return lista;
    }

    public static void gerarRelatorioTurmas(String formato) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATORIO DE TURMAS =====\n");
        sb.append("Data: ").append(Utilitarios.dataParaString(java.time.LocalDate.now())).append("\n\n");
        for (Turma t : lista) {
            sb.append("ID: ").append(t.idTurma).append("\n");
            sb.append("Nome: ").append(t.nome).append("\n");
            sb.append("Classe: ").append(t.classe).append(".a\n");
            sb.append("Vagas: ").append(t.vagas).append(" | Ocupadas: ").append(t.vagasOcupadas).append("\n");
            sb.append("Livres: ").append((t.vagas - t.vagasOcupadas)).append("\n");
            sb.append("--------------------------------\n");
        }
        GeradorRelatorio.exportar(
        sb.toString(),
        GeradorRelatorio.PASTA_RELATORIOS_TURMAS,
        "relatorio_turmas_" + LocalDate.now(),
        formato);

    }
}
