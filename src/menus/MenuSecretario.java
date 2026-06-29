package menus;

import entidades.*;
import servicos.GestaoMatriculas;
import servicos.GestaoCursos;
import servicos.GestaoTurmas;
import utilitarios.Utilitarios;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuSecretario {

    Scanner teclado;
    GestaoMatriculas gestaoMat;
    GestaoCursos     gestaoCursos;
    GestaoTurmas     gestaoTurmas;
    CalendarioMatricula calendario;
    // REQ 3: para validação cross-entity de BI
    ArrayList<Funcionario> listaFuncionarios;
    ArrayList<Curso>       listaCursos;

    // Construtor completo (com funcionarios — REQ 3)
    public MenuSecretario(Scanner teclado, ArrayList<Aluno> alunos, ArrayList<Curso> cursos,
                          ArrayList<Turma> turmas, ArrayList<Funcionario> funcionarios,
                          CalendarioMatricula calendario) {
        this.teclado           = teclado;
        this.listaFuncionarios = funcionarios;
        this.listaCursos       = cursos;
        this.gestaoMat         = new GestaoMatriculas(alunos, cursos, turmas, funcionarios);
        this.gestaoCursos      = new GestaoCursos(cursos, turmas, alunos);
        this.gestaoTurmas      = new GestaoTurmas(turmas, alunos);
        this.calendario        = calendario;
    }

    // Construtor de compatibilidade
    public MenuSecretario(Scanner teclado, ArrayList<Aluno> alunos, ArrayList<Curso> cursos,
                          ArrayList<Turma> turmas, CalendarioMatricula calendario) {
        this(teclado, alunos, cursos, turmas, new ArrayList<Funcionario>(), calendario);
    }

    public void mostrar() {
        int opcao;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|        MENU SECRETARIO         |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Matricular Aluno            |");
            System.out.println("| 2. Trancar Matricula           |");
            System.out.println("| 3. Reingresso de Aluno         |");
            System.out.println("| 4. Buscar Aluno                |");
            System.out.println("| 5. Listar Todos os Alunos      |");
            System.out.println("| 6. Trocar Aluno de Turma       |");
            System.out.println("| 7. Gerar Relatorio Aluno       |");
            System.out.println("| 0. Sair                        |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            opcao = lerInteiro();

            switch (opcao) {
                case 1: matricularAluno();   break;
                case 2: trancarMatricula();  break;
                case 3: reingressarAluno();  break;
                case 4: buscarAluno();       break;
                case 5: listarAlunos();      break;
                case 6: trocarTurmaAluno();  break;
                case 7: relatorioAluno();    break;
                case 0: System.out.println("Logout efectuado."); break;
                default: System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    void matricularAluno() {
        System.out.println("\n--- NOVA MATRICULA ---");
        if (calendario == null) {
            System.out.println("ATENCAO: Calendario de matriculas nao configurado. Contacte o Administrador.");
            Utilitarios.pausar(teclado); return;
        }
        LocalDate hoje = LocalDate.now();
        if (hoje.isAfter(calendario.dataFimTotal)) {
            System.out.println("ATENCAO: Prazo de matriculas encerrado!");
            Utilitarios.pausar(teclado); return;
        }
        if (hoje.isAfter(calendario.dataFimSemMulta)) {
            System.out.println("ATENCAO: Matricula com multa de 10% sobre a propina.");
        }

        // REQ 2 + REQ 3: loop BI — 14 chars + não pode ser funcionário
        String bi;
        while (true) {
            System.out.print("BI do aluno (exactamente 14 caracteres alfanumericos): ");
            bi = teclado.nextLine().trim();
            if (!Utilitarios.validarFormatoBI(bi)) {
                System.out.println("Erro: BI invalido. Deve ter exactamente 14 caracteres alfanumericos. Tente novamente.");
                continue;
            }
            boolean biFuncionario = false;
            for (Funcionario f : listaFuncionarios) {
                if (f.bi.equalsIgnoreCase(bi)) { biFuncionario = true; break; }
            }
            if (biFuncionario) {
                System.out.println("Erro: Este BI ja esta registado no sistema. Insira o BI correcto do aluno.");
                continue;
            }
            break;
        }

        System.out.print("Nome completo: ");
        String nome = teclado.nextLine();

        // REQ 2: loop idade
        int idade;
        while (true) {
            System.out.print("Idade: ");
            idade = lerInteiro();
            if (idade >= 15) break;
            System.out.println("Erro: Idade minima para matricula e 15 anos. Tente novamente.");
        }

        // Seleccionar curso
        System.out.println("\n=== CURSOS DISPONIVEIS ===");
        ArrayList<Curso> cursos = gestaoMat.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso disponivel. Contacte o Administrador.");
            Utilitarios.pausar(teclado); return;
        }
        String deptActual = "";
        for (Curso c : cursos) {
            if (!c.departamento.equals(deptActual)) {
                deptActual = c.departamento;
                System.out.println("\n  [" + deptActual + "]");
            }
            System.out.println("  " + c.idCurso + " | " + c.nome
                    + " | Propina: " + String.format("%.2f", c.valorPropina) + " Kz"
                    + " | Classes: 10.a a " + c.classeMaxima + ".a");
        }

        Curso cursoSel;
        while (true) {
            System.out.print("\nID do Curso: ");
            String idCurso = teclado.nextLine().trim();
            cursoSel = gestaoCursos.buscarCurso(idCurso);
            if (cursoSel != null) break;
            System.out.println("Erro: Curso nao encontrado. Tente novamente.");
        }

        // REQ 4: classes por departamento
        int[] classesPermitidas = CursosEstaticos.classesPermitidas(cursoSel);
        System.out.println("\nClasses disponiveis para " + cursoSel.nome
                + " [" + cursoSel.departamento + "]:");
        for (int i = 0; i < classesPermitidas.length; i++) {
            System.out.println("  " + (i + 1) + " -> " + classesPermitidas[i] + ".a Classe");
        }

        int classe;
        while (true) {
            System.out.print("Seleccione a classe: ");
            int classeOp = lerInteiro();
            if (classeOp >= 1 && classeOp <= classesPermitidas.length) {
                classe = classesPermitidas[classeOp - 1];
                break;
            }
            System.out.println("Erro: Opcao invalida. Escolha entre 1 e " + classesPermitidas.length + ".");
        }

        // Turmas filtradas pela classe
        ArrayList<Turma> turmasDisp = gestaoMat.listarTurmasDisponiveisDoCurso(cursoSel.idCurso, classe);
        if (turmasDisp.isEmpty()) {
            System.out.println("Nenhuma turma com vagas disponiveis para " + classe + ".a Classe neste curso.");
            Utilitarios.pausar(teclado); return;
        }
        System.out.println("\n=== TURMAS DISPONIVEIS — " + classe + ".a CLASSE ===");
        for (Turma t : turmasDisp) {
            System.out.println("  " + t.idTurma + " | " + t.nome
                    + " | Vagas livres: " + (t.vagas - t.vagasOcupadas));
        }

        String idTurma;
        while (true) {
            System.out.print("ID da Turma: ");
            idTurma = teclado.nextLine().trim();
            boolean found = false;
            for (Turma t : turmasDisp) {
                if (t.idTurma.equalsIgnoreCase(idTurma)) { found = true; break; }
            }
            if (found) break;
            System.out.println("Erro: Turma invalida ou sem vagas. Escolha da lista acima.");
        }

        gestaoMat.matricularAluno(bi, nome, idade, cursoSel.idCurso, idTurma, classe, calendario);
        Utilitarios.pausar(teclado);
    }

    void trancarMatricula() {
        System.out.println("\n--- TRANCAR MATRICULA ---");
        System.out.print("Numero de matricula ou BI do aluno: ");
        gestaoMat.trancarMatricula(teclado.nextLine());
        Utilitarios.pausar(teclado);
    }

    void reingressarAluno() {
        System.out.println("\n--- REINGRESSO DE ALUNO ---");
        System.out.println("(Altera estado de TRANCADO para ACTIVO)");
        System.out.print("Numero de Matricula do aluno: ");
        gestaoMat.reingressarAluno(teclado.nextLine());
        Utilitarios.pausar(teclado);
    }

    void buscarAluno() {
        System.out.println("\n--- BUSCAR ALUNO ---");
        System.out.print("Nome, matricula ou BI: ");
        Aluno a = gestaoMat.buscarAluno(teclado.nextLine());
        if (a != null) {
            System.out.println("\n=== DADOS DO ALUNO ===");
            System.out.println("Matricula : " + a.numeroMatricula);
            System.out.println("Nome      : " + a.nomeCompleto);
            System.out.println("BI        : " + a.idBI);
            System.out.println("Idade     : " + a.idade);
            System.out.println("Classe    : " + a.classe + ".a Classe");
            System.out.println("Curso     : " + gestaoMat.obterNomeCurso(a.idCurso));
            System.out.println("Turma     : " + gestaoMat.obterNomeTurma(a.idTurma));
            System.out.println("Situacao  : " + a.situacao);
            System.out.println("Data Mat. : " + Utilitarios.dataParaString(a.dataMatricula));
            if (a.multaAplicada > 0) {
                System.out.println("Multa     : " + String.format("%.2f", a.multaAplicada) + " Kz");
            }
        } else { System.out.println("Aluno nao encontrado."); }
        Utilitarios.pausar(teclado);
    }

    void listarAlunos() {
        System.out.println("\n=== LISTA DE ALUNOS MATRICULADOS ===");
        ArrayList<Aluno> alunos = gestaoMat.listarAlunos();
        if (alunos.isEmpty()) { System.out.println("Nenhum aluno matriculado."); }
        for (Aluno a : alunos) {
            System.out.println("Matricula: " + a.numeroMatricula
                    + " | " + a.nomeCompleto
                    + " | " + a.classe + ".a Classe"
                    + " | Curso: " + gestaoMat.obterNomeCurso(a.idCurso)
                    + " | Turma: " + gestaoMat.obterNomeTurma(a.idTurma)
                    + " | " + a.situacao);
        }
        System.out.print("\nExportar lista? (S/N): ");
        if (teclado.nextLine().trim().equalsIgnoreCase("S")) {
            gestaoMat.gerarRelatorioTodosAlunos("TXT");
        }
        Utilitarios.pausar(teclado);
    }

    void relatorioAluno() {
        System.out.println("\n--- RELATORIO DE ALUNO ---");
        System.out.print("Matricula ou BI do aluno: ");
        gestaoMat.gerarRelatorioAluno(teclado.nextLine(), "TXT");
        Utilitarios.pausar(teclado);
    }

    // ===================== REQ 3: TROCAR ALUNO DE TURMA =====================

    void trocarTurmaAluno() {
        System.out.println("\n--- TROCAR ALUNO DE TURMA ---");
        System.out.print("Numero de Matricula do aluno: ");
        Aluno aluno = gestaoMat.buscarAluno(teclado.nextLine());
        if (aluno == null) {
            System.out.println("Aluno nao encontrado.");
            Utilitarios.pausar(teclado); return;
        }
        System.out.println("Aluno  : " + aluno.nomeCompleto);
        System.out.println("Turma  : " + aluno.idTurma);
        System.out.println("Classe : " + aluno.classe + ".a Classe");

        ArrayList<Turma> opcoes = gestaoTurmas.listarTurmasParaTroca(aluno);
        if (opcoes.isEmpty()) {
            System.out.println("Nao existem outras turmas com vagas para a mesma classe e curso.");
            Utilitarios.pausar(teclado); return;
        }
        System.out.println("\nTurmas disponiveis para troca:");
        for (Turma t : opcoes) {
            System.out.println("  " + t.idTurma + " | " + t.nome
                    + " | Vagas livres: " + (t.vagas - t.vagasOcupadas));
        }
        String novaId;
        while (true) {
            System.out.print("ID da nova turma: ");
            novaId = teclado.nextLine().trim();
            boolean valid = false;
            for (Turma t : opcoes) { if (t.idTurma.equalsIgnoreCase(novaId)) { valid = true; break; } }
            if (valid) break;
            System.out.println("Erro: Turma invalida ou sem vagas. Escolha da lista acima.");
        }
        gestaoTurmas.trocarTurmaAluno(aluno, novaId);
        Utilitarios.pausar(teclado);
    }

    int lerInteiro() {
        try { return Integer.parseInt(teclado.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }
}
