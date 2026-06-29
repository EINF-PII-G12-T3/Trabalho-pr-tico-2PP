package menus;

import entidades.*;
import java.time.LocalDate;
import persistencia.Persistencia;
import servicos.SistemaAutenticacao;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuPrincipal {

    Scanner teclado;
    ArrayList<Funcionario>         funcionarios;
    ArrayList<Curso>               cursos;
    ArrayList<Turma>               turmas;
    ArrayList<Emolumento>          emolumentos;
    ArrayList<Aluno>               alunos;
    ArrayList<PagamentoEmolumento> historicoPagamentos;
    ArrayList<CalendarioMatricula> calendario;

    public MenuPrincipal(Scanner teclado,
                         ArrayList<Funcionario>         funcionarios,
                         ArrayList<Curso>               cursos,
                         ArrayList<Turma>               turmas,
                         ArrayList<Emolumento>          emolumentos,
                         ArrayList<Aluno>               alunos,
                         ArrayList<PagamentoEmolumento> historicoPagamentos,
                         ArrayList<CalendarioMatricula> calendario) {
        this.teclado             = teclado;
        this.funcionarios        = funcionarios;
        this.cursos              = cursos;
        this.turmas              = turmas;
        this.emolumentos         = emolumentos;
        this.alunos              = alunos;
        this.historicoPagamentos = historicoPagamentos;
        this.calendario          = calendario;
    }
    
    private void trancarAlunosInactivos() {
        int contador = 0;
        for (Aluno a : alunos) {
            if (a.situacao.equals("INACTIVO")) {
                a.situacao = "TRANCADO";
                contador++;
            }
        }
        if (contador > 0) {
            System.out.println("AVISO: " + contador + " aluno(s) inactivo(s) foram automaticamente trancados.");
            System.out.println("(Prazo de matricula encerrado)");
        }
    }


    public void mostrar() {
        System.out.println("====  SGMF  ====");

        SistemaAutenticacao auth = new SistemaAutenticacao(funcionarios);

        boolean continuar = true;
        boolean verificouInicializacao = false;
        while (continuar) {
            System.out.println("\n===== LOGIN =====");
            System.out.print("Usuario (ID ou email institucional): ");
            String usuarioLogin = teclado.nextLine();
            System.out.print("Senha: ");
            String senha = teclado.nextLine();

            Funcionario usuario = auth.login(usuarioLogin, senha);

            if (usuario != null) {
                System.out.println("\nBem-vindo, " + usuario.nome + " [" + NivelAcesso.nome(usuario.nivelAcesso) + "]");

                CalendarioMatricula cal = calendario.isEmpty() ? null : calendario.get(0);
                if (cal != null && LocalDate.now().isAfter(cal.dataFimTotal)) {
                    trancarAlunosInactivos();
                }
                
                if (!verificouInicializacao && plataformaSemDados()) {
                    perguntarInicializacaoDados();
                    verificouInicializacao = true;
                }


                int nivel = usuario.nivelAcesso;
                if (nivel == NivelAcesso.ADMIN) {
                    new MenuAdmin(teclado, funcionarios, cursos, turmas, emolumentos,
                            alunos, historicoPagamentos, calendario, usuario).mostrar();
                } else if (nivel == NivelAcesso.SECRETARIO) {
                    if (cal == null) {
                        System.out.println("Atencao: Calendario de matriculas nao configurado. Contacte o Administrador.");
                    }
                    // REQ 3: passar funcionarios para validacao cross-entity de BI
                    new MenuSecretario(teclado, alunos, cursos, turmas, funcionarios, cal).mostrar();
                } else if (nivel == NivelAcesso.FINANCEIRO) {
                    new MenuFinanceiro(teclado, alunos, emolumentos, historicoPagamentos).mostrar();
                }

                Persistencia.gravarDados(funcionarios, cursos, turmas, emolumentos,
                        alunos, historicoPagamentos, calendario);

                System.out.print("\nDeseja fazer login novamente? (S/N): ");
                String resp = teclado.nextLine();
                if (!resp.equalsIgnoreCase("S")) continuar = false;

            } else {
                System.out.println("Credenciais invalidas.");
            }
        }

        System.out.println("\nSistema SGMF encerrado.");
    }

    public boolean plataformaSemDados() {
        return turmas.isEmpty() && emolumentos.isEmpty()
                && alunos.isEmpty() && historicoPagamentos.isEmpty() && calendario.isEmpty();
    }

    public void perguntarInicializacaoDados() {
        int opcao;
        do {
            System.out.println("\nSistema sem dados operacionais.");
            System.out.println("Deseja inicializar com dados predefinidos?");
            System.out.println("1 - Sim");
            System.out.println("2 - Nao");
            System.out.print("Opcao: ");
            opcao = lerInteiro();
            if (opcao == 1) {
                Persistencia.carregarDadosIniciais(funcionarios, cursos, turmas,
                        emolumentos, alunos, historicoPagamentos, calendario);
                System.out.println("Inicializacao concluida.");
            } else if (opcao == 2) {
                System.out.println("Sistema continuara sem dados predefinidos.");
            } else {
                System.out.println("Opcao invalida.");
            }
        } while (opcao != 1 && opcao != 2);
    }

    public int lerInteiro() {
        try { return Integer.parseInt(teclado.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }
}
