package menus;

import entidades.*;
import servicos.GestaoFinancas;
import utilitarios.Utilitarios;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuFinanceiro {

    Scanner teclado;
    GestaoFinancas gestaoFin;
    ArrayList<Emolumento> listaEmolumentos;

    public MenuFinanceiro(Scanner teclado, ArrayList<Aluno> alunos, ArrayList<Emolumento> emolumentos,
                          ArrayList<PagamentoEmolumento> historico) {
        this.teclado = teclado;
        this.gestaoFin = new GestaoFinancas(alunos, emolumentos, historico);
        this.listaEmolumentos = emolumentos;
    }

    public void mostrar() {
        int opcao;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|        MENU FINANCEIRO         |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Mensalidades em Aberto      |");
            System.out.println("| 2. Pagar Propina               |");
            System.out.println("| 3. Pagar Emolumento            |");
            System.out.println("| 4. Situacao Financeira Aluno   |");
            System.out.println("| 5. Pagamento anual da propina  |");
            System.out.println("| 6. Historico do Aluno          |");
            System.out.println("| 7. Relatorio Geral             |");
            System.out.println("| 0. Sair                        |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            opcao = lerInteiro();

            switch (opcao) {
                case 1: verificarMensalidades(); break;
                case 2: pagarPropina(); break;
                case 3: pagarEmolumento(); break;
                case 4: situacaoPropinas(); break;
                case 5: pagamentoAnualPropina(); break;
                case 6: historicoAluno(); break;
                case 7: relatorioGeral(); break;
                case 0: System.out.println("Logout efectuado."); break;
                default: System.out.println("Opcao invalida!");
            }
        } while (opcao != 0);
    }

    void verificarMensalidades() {
        System.out.println("\n--- MENSALIDADES EM ABERTO ---");
        System.out.print("Numero de matricula do aluno: ");
        String termo = teclado.nextLine();
        ArrayList<Mensalidade> abertas = gestaoFin.verificarMensalidades(termo);
        if (abertas.isEmpty()) {
            System.out.println("Nenhuma mensalidade em aberto. Aluno em dia!");
        } else {
            System.out.println("Mensalidades em aberto:");
            for (Mensalidade m : abertas) {
                System.out.println("  Mes " + m.numero + ": " + String.format("%.2f", m.valor) + " Kz");
            }
        }
        Utilitarios.pausar(teclado);
    }

    void pagarPropina() {
        System.out.println("\n--- PAGAMENTO DE PROPINA ---");
        System.out.print("Numero de matricula do aluno: ");
        String termo = teclado.nextLine();
        ArrayList<Mensalidade> abertas = gestaoFin.verificarMensalidades(termo);
        if (abertas.isEmpty()) {
            System.out.println("Todas as mensalidades ja foram pagas!");
            Utilitarios.pausar(teclado); return;
        }
        System.out.println("Mensalidades em aberto:");
        for (Mensalidade m : abertas) {
            System.out.println("  Mes " + m.numero + ": " + String.format("%.2f", m.valor) + " Kz");
        }
        System.out.println("Indique os numeros das mensalidades a pagar (separados por virgula, ex: 1,2,3):");
        System.out.print("> ");
        gestaoFin.registarPagamentoMultiplasMensalidades(termo, teclado.nextLine());
        Utilitarios.pausar(teclado);
    }

    void pagarEmolumento() {
        System.out.println("\n--- PAGAMENTO DE EMOLUMENTO ---");
        System.out.print("Numero de matricula ou BI do aluno: ");
        String termo = teclado.nextLine();
        if (listaEmolumentos.isEmpty()) {
            System.out.println("Nenhum emolumento cadastrado.");
            Utilitarios.pausar(teclado); return;
        }
        System.out.println("Emolumentos disponiveis:");
        for (Emolumento e : listaEmolumentos) {
            System.out.println("  ID: " + e.idEmolumento + " | " + e.descricao
                    + " | " + String.format("%.2f", e.preco) + " Kz");
        }
        System.out.println("Indique os IDs dos emolumentos a pagar (separados por virgula, ex: EMOL001,EMOL002):");
        System.out.print("> ");
        gestaoFin.registarPagamentoMultiplosEmolumentos(termo, teclado.nextLine());
        Utilitarios.pausar(teclado);
    }

    void situacaoPropinas() {
        System.out.println("\n--- SITUACAO FINANCEIRA DO ALUNO ---");
        System.out.print("Numero de matricula ou BI do aluno: ");
        System.out.println(gestaoFin.verificarSituacaoPropinas(teclado.nextLine()));
        Utilitarios.pausar(teclado);
    }

    void pagamentoAnualPropina() {
        System.out.println("\n--- PAGAMENTO ANUAL DA PROPINA ---");
        System.out.print("Numero de matricula ou BI do aluno: ");
        String termo = teclado.nextLine();
        gestaoFin.pagarPropinasAnualComDesconto(termo, teclado);
        Utilitarios.pausar(teclado);
    }

    void historicoAluno() {
        System.out.println("\n--- HISTORICO DE PAGAMENTOS ---");
        System.out.print("Numero de matricula do aluno: ");
        gestaoFin.gerarHistoricoPagamentos(teclado.nextLine(), "TXT");
        Utilitarios.pausar(teclado);
    }

    void relatorioGeral() {
        System.out.println("\n--- RELATORIO GERAL DE PAGAMENTOS ---");
        gestaoFin.gerarRelatorioGeralPagamentos("TXT");
        Utilitarios.pausar(teclado);
    }

    int lerInteiro() {
        try { return Integer.parseInt(teclado.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }
}
