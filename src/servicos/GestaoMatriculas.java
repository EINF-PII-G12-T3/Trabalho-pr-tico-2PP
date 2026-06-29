package servicos;

import entidades.Aluno;
import entidades.CalendarioMatricula;
import entidades.Curso;
import entidades.Funcionario;
import entidades.Turma;
import relatorios.GeradorRelatorio;
import utilitarios.Utilitarios;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestaoMatriculas {

    private static ArrayList<Aluno> listaAlunos;
    private static ArrayList<Curso> listaCursos;
    private static ArrayList<Turma> listaTurmas;
    private static ArrayList<Funcionario> listaFuncionarios;

    public GestaoMatriculas(ArrayList<Aluno> listaAlunos, ArrayList<Curso> listaCursos, ArrayList<Turma> listaTurmas) {
        this(listaAlunos, listaCursos, listaTurmas, new ArrayList<Funcionario>());
    }

    public GestaoMatriculas(ArrayList<Aluno> listaAlunos, ArrayList<Curso> listaCursos,
                             ArrayList<Turma> listaTurmas, ArrayList<Funcionario> listaFuncionarios) {
        this.listaAlunos = listaAlunos;
        this.listaCursos = listaCursos;
        this.listaTurmas = listaTurmas;
        this.listaFuncionarios = listaFuncionarios;
    }

    public static boolean validarBI(String bi) {
        if (!Utilitarios.validarFormatoBI(bi)) {
            System.out.println("Erro: BI invalido. Deve ter exactamente 14 caracteres alfanumericos.");
            return false;
        }
        // REQ 1: BI nao pode pertencer a nenhum funcionario/admin
        for (Funcionario f : listaFuncionarios) {
            if (f.bi.equalsIgnoreCase(bi)) {
                System.out.println("Erro: Este BI ja esta registado. Matricula bloqueada.");
                return false;
            }
        }
        // BI duplicado entre alunos
        for (Aluno a : listaAlunos) {
            if (a.idBI.equalsIgnoreCase(bi)) {
                System.out.println("Erro: BI ja cadastrado no sistema.");
                return false;
            }
        }
        return true;
    }

    public static boolean validarIdadeParaMatricula(int idade, int classe) {
        if (idade < 15) {
            System.out.println("Erro: Idade minima para matricula e 15 anos.");
            return false;
        }
        /*if (classe == 10) {
            if (idade < 15 || idade > 18) {
                System.out.println("Erro: Para a 10.a classe, a idade deve estar entre 15 e 18 anos.");
                return false;
            }
        } else if (classe == 11) {
            if (idade < 16 || idade > 19) {
                System.out.println("Erro: Para a 11.a classe, a idade deve estar entre 16 e 19 anos.");
                return false;
            }
        } else if (classe == 12) {
            if (idade < 17 || idade > 20) {
                System.out.println("Erro: Para a 12.a classe, a idade deve estar entre 17 e 20 anos.");
                return false;
            }
        }*/
        return true;
    }

    public static boolean matriculaPermitida(LocalDate data, CalendarioMatricula cal) {
        return !data.isAfter(cal.dataFimTotal);
    }

    public static double calcularMulta(LocalDate data, CalendarioMatricula cal, double valorPropina) {
        if (data.isAfter(cal.dataFimSemMulta)) {
            return valorPropina * 0.10;
        }
        return 0;
    }

    // REQ 2: matricularAluno agora recebe 'classe' explicitamente
    public static boolean matricularAluno(String bi, String nomeCompleto, int idade,
                                    String idCurso, String idTurma, int classe,
                                    CalendarioMatricula cal) {
        if (!validarBI(bi)) return false;

        if (!matriculaPermitida(LocalDate.now(), cal)) {
            System.out.println("Erro: Prazo de matricula encerrado.");
            return false;
        }

        Curso curso = null;
        for (Curso c : listaCursos) {
            if (c.idCurso.equalsIgnoreCase(idCurso)) { curso = c; break; }
        }
        if (curso == null) { System.out.println("Erro: Curso nao encontrado."); return false; }

        Turma turma = null;
        for (Turma t : listaTurmas) {
            if (t.idTurma.equalsIgnoreCase(idTurma) && t.idCurso.equalsIgnoreCase(idCurso)) {
                turma = t; break;
            }
        }
        if (turma == null) { System.out.println("Erro: Turma nao encontrada neste curso."); return false; }

        // Validar que a classe seleccionada coincide com a classe da turma
        if (turma.classe != classe) {
            System.out.println("Erro: A turma seleccionada pertence a " + turma.classe
                    + ".a Classe, mas a classe escolhida foi " + classe + ".a Classe.");
            return false;
        }

        if (!validarIdadeParaMatricula(idade, classe)) return false;

        if ((turma.vagas - turma.vagasOcupadas) <= 0) {
            System.out.println("Erro: Turma sem vagas disponiveis.");
            return false;
        }

        double multa = calcularMulta(LocalDate.now(), cal, curso.valorPropina);
        String numeroMatricula = Utilitarios.gerarNumeroMatricula(listaAlunos);
        Aluno novoAluno = Fabricas.criarAluno(numeroMatricula, bi, nomeCompleto, idade,
                idCurso, idTurma, classe, multa, LocalDate.now(), curso.valorPropina);
        listaAlunos.add(novoAluno);
        turma.vagasOcupadas++;

        if (multa > 0) {
            System.out.println("Atencao: Multa de " + String.format("%.2f", multa) + " Kz aplicada por atraso.");
        }
        System.out.println("Matricula efectuada. Numero: " + numeroMatricula);
        System.out.println("Classe matriculada: " + classe + ".a Classe");
        System.out.println("Situacao inicial: INACTIVO");
        System.out.println("Para activar, pague a 1.a mensalidade e o Cartao de Estudante.");
        return true;
        
        
    }

    public static boolean trancarMatricula(String termoAluno) {
        for (Aluno a : listaAlunos) {
            if (corresponde(a, termoAluno)) {
                if (a.situacao.equals("TRANCADO")) {
                    System.out.println("Matricula ja esta trancada.");
                    return false;
                }
                if (a.situacao.equals("INACTIVO")) {
                    System.out.println("Erro: Aluno INACTIVO nao pode ser trancado.");
                    System.out.println("O aluno deve estar ACTIVO para ser trancado.");
                    return false;
                }
                a.situacao = "TRANCADO";
                System.out.println("Matricula trancada.");
                return true;
            }
        }
        System.out.println("Aluno nao encontrado.");
        return false;
    }

    public static boolean reingressarAluno(String termoAluno) {
        for (Aluno a : listaAlunos) {
            if (corresponde(a, termoAluno)) {
                if (!a.situacao.equalsIgnoreCase("TRANCADO")) {
                    System.out.println("Erro: O aluno nao esta TRANCADO (estado actual: " + a.situacao + ").");
                    return false;
                }
                a.situacao = "ACTIVO";
                System.out.println("Reingresso efectuado. Aluno " + a.nomeCompleto + " esta agora ACTIVO.");
                return true;
            }
        }
        System.out.println("Aluno nao encontrado.");
        return false;
    }

    public static boolean anularMatricula(String termoAluno) {
        for (Aluno a : listaAlunos) {
            if (corresponde(a, termoAluno)) {
                a.situacao = "ANULADO";
                System.out.println("Matricula anulada.");
                return true;
            }
        }
        System.out.println("Aluno nao encontrado.");
        return false;
    }

    public static Aluno buscarAluno(String termo) {
        for (Aluno a : listaAlunos) {
            if (corresponde(a, termo)) return a;
        }
        return null;
    }

    public static boolean corresponde(Aluno a, String termo) {
        return (a.numeroMatricula != null && a.numeroMatricula.equalsIgnoreCase(termo))
                || a.idBI.equalsIgnoreCase(termo)
                || a.nomeCompleto.equalsIgnoreCase(termo);
    }

    // Listar turmas de um curso filtradas opcionalmente por classe (0 = sem filtro)
    public static ArrayList<Turma> listarTurmasDisponiveisDoCurso(String idCurso, int classe) {
        ArrayList<Turma> disponiveis = new ArrayList<>();
        for (Turma t : listaTurmas) {
            if (t.idCurso.equalsIgnoreCase(idCurso) && (t.vagas - t.vagasOcupadas) > 0) {
                if (classe == 0 || t.classe == classe) {
                    disponiveis.add(t);
                }
            }
        }
        return disponiveis;
    }

    // Overload sem filtro de classe (compatibilidade)
    public static ArrayList<Turma> listarTurmasDisponiveisDoCurso(String idCurso) {
        return listarTurmasDisponiveisDoCurso(idCurso, 0);
    }

    public static ArrayList<Curso> listarCursos() { return listaCursos; }
    public static ArrayList<Aluno> listarAlunos() { return listaAlunos; }

    public static String obterNomeCurso(String idCurso) {
        for (Curso c : listaCursos) {
            if (c.idCurso.equalsIgnoreCase(idCurso)) return c.nome;
        }
        return idCurso;
    }

    public static String obterNomeTurma(String idTurma) {
        for (Turma t : listaTurmas) {
            if (t.idTurma.equalsIgnoreCase(idTurma)) return t.nome;
        }
        return idTurma;
    }

    public static void gerarRelatorioAluno(String bi, String formato) {
        Aluno a = buscarAluno(bi);
        if (a == null) { System.out.println("Aluno nao encontrado."); return; }
        GeradorRelatorio.exportar(
        GeradorRelatorio.formatarAluno(a),
        GeradorRelatorio.PASTA_RELATORIOS_ALUNOS,
        "relatorio_aluno_" + a.numeroMatricula,
        "TXT");

    }

    public static void gerarRelatorioTodosAlunos(String formato) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== LISTAGEM GERAL DE ALUNOS =====\n");
        sb.append("Data: ").append(Utilitarios.dataParaString(LocalDate.now())).append("\n\n");
        for (Aluno a : listaAlunos) {
            sb.append(GeradorRelatorio.formatarAluno(a)).append("\n");
        }
        GeradorRelatorio.exportar(
        sb.toString(),
        GeradorRelatorio.PASTA_RELATORIOS_ALUNOS,
        "relatorio_todos_alunos_" + LocalDate.now(),
        "TXT");

    }
    
    
}
