package servicos;

import entidades.Aluno;
import entidades.Comprovativo;
import entidades.Emolumento;
import entidades.Mensalidade;
import entidades.PagamentoEmolumento;
import relatorios.GeradorRelatorio;
import utilitarios.Utilitarios;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class GestaoFinancas {

    private static ArrayList<Aluno> listaAlunos;
    private static ArrayList<Emolumento> listaEmolumentos;
    private static ArrayList<PagamentoEmolumento> historicoPagamentosEmolumentos;


    public GestaoFinancas(ArrayList<Aluno> listaAlunos, ArrayList<Emolumento> listaEmolumentos, ArrayList<PagamentoEmolumento> historico) {
        this.listaAlunos = listaAlunos;
        this.listaEmolumentos = listaEmolumentos;
        this.historicoPagamentosEmolumentos = historico;
    }

    public static Aluno encontrarAluno(String termo) {
        for (Aluno a : listaAlunos) {
            if ((a.numeroMatricula != null && a.numeroMatricula.equalsIgnoreCase(termo)) ||
                a.idBI.equalsIgnoreCase(termo) ||
                a.nomeCompleto.equalsIgnoreCase(termo)) {
                return a;
            }
        }
        return null;
    }

    public static boolean pagamentoPertenceAoAluno(PagamentoEmolumento pe, Aluno aluno) {
        return pe.idAluno.equalsIgnoreCase(aluno.idBI) ||
               (aluno.numeroMatricula != null && pe.idAluno.equalsIgnoreCase(aluno.numeroMatricula));
    }

    public static ArrayList<Mensalidade> verificarMensalidades(String termoAluno) {
        ArrayList<Mensalidade> emAberto = new ArrayList<>();
        Aluno aluno = encontrarAluno(termoAluno);
        if (aluno != null) {
            for (Mensalidade m : aluno.mensalidades) {
                if (!m.pago) emAberto.add(m);
            }
            return emAberto;
        }
        System.out.println("Aluno nao encontrado.");
        return emAberto;
    }

    public static String verificarSituacaoPropinas(String termoAluno) {
        Aluno aluno = encontrarAluno(termoAluno);
        if (aluno == null) return "Aluno nao encontrado.";
        for (Mensalidade m : aluno.mensalidades) {
            if (!m.pago) return "Tem propinas em atraso.";
        }
        return "Propinas regularizadas.";
    }

    public static boolean registarPagamentoMensalidade(String termoAluno, int numeroMes) {
        Aluno aluno = encontrarAluno(termoAluno);
        if (aluno == null) { System.out.println("Aluno nao encontrado."); return false; }

        if (aluno.situacao.equalsIgnoreCase("TRANCADO")) {
            System.out.println("Aluno Trancado - Impossivel efectuar o pagamento.");
            return false;
        }

        if (numeroMes < 1 || numeroMes > 10) {
            System.out.println("Numero de mensalidade invalido (1-10).");
            return false;
        }
        Mensalidade m = aluno.mensalidades[numeroMes - 1];
        if (m.pago) {
            System.out.println("Erro: Mensalidade " + numeroMes + " ja foi paga!");
            return false;
        }
        String idComp = aluno.numeroMatricula != null ? aluno.numeroMatricula : aluno.idBI;
        String[] itens = {"Mensalidade " + numeroMes};
        Comprovativo comp = Fabricas.criarComprovativo(
            idComp, aluno.nomeCompleto, "Comprovativo de Propina",
            m.valor, LocalDate.now(), itens);
        String conteudo = GeradorRelatorio.formatarComprovativo(comp);
        System.out.println("\n" + conteudo);
        GeradorRelatorio.exportar(
        GeradorRelatorio.formatarComprovativo(comp),
        GeradorRelatorio.PASTA_COMPROVATIVOS_PROPINAS,
        "comp_propinas_" + idComp + "_" + LocalDate.now(),
        "TXT");

        return true;

    }

    // REQ 3: Pagamento de multiplas mensalidades numa unica transaccao
    public static void registarPagamentoMultiplasMensalidades(String termoAluno, String seleccao) {
        Aluno aluno = encontrarAluno(termoAluno);
        if (aluno == null) { System.out.println("Aluno nao encontrado."); return; }

        if (aluno.situacao.equalsIgnoreCase("TRANCADO")) {
            System.out.println("Aluno Trancado - Impossivel efectuar o pagamento.");
            return;
        }

        String[] partes = seleccao.split(",");
        ArrayList<Integer> numerosValidos = new ArrayList<>();
        double totalAPagar = 0;
        ArrayList<String> itens = new ArrayList<>();

        // Validar seleccao antes de processar
        for (String parte : partes) {
            String p = parte.trim();
            if (p.isEmpty()) continue;
            try {
                int num = Integer.parseInt(p);
                if (num < 1 || num > 10) {
                    System.out.println("Aviso: Mensalidade " + num + " invalida (1-10). Ignorada.");
                    continue;
                }
                Mensalidade m = aluno.mensalidades[num - 1];
                if (m.pago) {
                    System.out.println("Aviso: Mensalidade " + num + " ja estava paga. Ignorada.");
                    continue;
                }
                numerosValidos.add(num);
                totalAPagar += m.valor;
                itens.add("Mensalidade " + num);
            } catch (NumberFormatException e) {
                System.out.println("Aviso: '" + p + "' nao e um numero valido. Ignorado.");
            }
        }

        if (numerosValidos.isEmpty()) {
            System.out.println("Nenhuma mensalidade valida seleccionada.");
            return;
        }

        System.out.println("\nResumo da transaccao:");
        for (int num : numerosValidos) {
            Mensalidade m = aluno.mensalidades[num - 1];
            System.out.println("  Mes " + num + ": " + String.format("%.2f", m.valor) + " Kz");
        }
        System.out.println("  --------------------------------");
        System.out.println("  TOTAL A PAGAR: " + String.format("%.2f", totalAPagar) + " Kz");

        // Registar pagamentos
        for (int num : numerosValidos) {
            Mensalidade m = aluno.mensalidades[num - 1];
            m.pago = true;
            m.dataPagamento = LocalDate.now();
        }
        atualizarSituacaoAluno(termoAluno);

        // Gerar comprovativo unico com todos os itens
        String idComp = aluno.numeroMatricula != null ? aluno.numeroMatricula : aluno.idBI;
        Comprovativo comp = Fabricas.criarComprovativo(idComp, aluno.nomeCompleto, "Comprovativo de Propinas",
                totalAPagar, LocalDate.now(), itens.toArray(new String[0]));
        System.out.println("\n" + GeradorRelatorio.formatarComprovativo(comp));
        GeradorRelatorio.exportar(
        GeradorRelatorio.formatarComprovativo(comp),
        GeradorRelatorio.PASTA_COMPROVATIVOS_PROPINAS,
        "comp_propinas_" + idComp + "_" + LocalDate.now(),
        "TXT");

    }

    public static boolean registarPagamentoEmolumento(String termoAluno, String idEmolumento) {
        Aluno aluno = encontrarAluno(termoAluno);
        if (aluno == null) { System.out.println("Aluno nao encontrado."); return false; }

        if (aluno.situacao.equalsIgnoreCase("TRANCADO")) {
            System.out.println("Aluno Trancado - Impossivel efectuar o pagamento.");
            return false;
        }

        for (PagamentoEmolumento pe : historicoPagamentosEmolumentos) {
            if (pagamentoPertenceAoAluno(pe, aluno) && pe.idEmolumento.equals(idEmolumento)) {
                System.out.println("Erro: Este emolumento ja foi pago para este aluno.");
                return false;
            }
        }

        Emolumento emol = null;
        for (Emolumento e : listaEmolumentos) {
            if (e.idEmolumento.equalsIgnoreCase(idEmolumento)) { emol = e; break; }
        }
        if (emol == null) { System.out.println("Emolumento nao encontrado."); return false; }

        String idAlunoPagamento = aluno.numeroMatricula != null ? aluno.numeroMatricula : aluno.idBI;
        PagamentoEmolumento pe = Fabricas.criarPagamentoEmolumento(idAlunoPagamento, idEmolumento, emol.preco, LocalDate.now());
        historicoPagamentosEmolumentos.add(pe);
        System.out.println("Emolumento '" + emol.descricao + "' pago. Valor: " + String.format("%.2f", emol.preco) + " Kz");
        atualizarSituacaoAluno(termoAluno);

        String[] itens = {emol.descricao};
        Comprovativo comp = Fabricas.criarComprovativo(idAlunoPagamento, aluno.nomeCompleto, "Comprovativo de Emolumento", emol.preco, LocalDate.now(), itens);
        System.out.println("\n" + GeradorRelatorio.formatarComprovativo(comp));
        return true;
    }

    // REQ 3: Pagamento de multiplos emolumentos numa unica transaccao
    public static void registarPagamentoMultiplosEmolumentos(String termoAluno, String seleccao) {
        Aluno aluno = encontrarAluno(termoAluno);
        if (aluno == null) { System.out.println("Aluno nao encontrado."); return; }

        if (aluno.situacao.equalsIgnoreCase("TRANCADO")) {
            System.out.println("Aluno Trancado - Impossivel efectuar o pagamento.");
            return;
        }

        String[] ids = seleccao.split(",");
        ArrayList<Emolumento> emolumentosValidos = new ArrayList<>();
        double totalAPagar = 0;
        ArrayList<String> itens = new ArrayList<>();

        for (String id : ids) {
            String idTrimmed = id.trim();
            if (idTrimmed.isEmpty()) continue;

            // Verificar se ja foi pago
            boolean jaPago = false;
            for (PagamentoEmolumento pe : historicoPagamentosEmolumentos) {
                if (pagamentoPertenceAoAluno(pe, aluno) && pe.idEmolumento.equalsIgnoreCase(idTrimmed)) {
                    jaPago = true;
                    break;
                }
            }
            if (jaPago) {
                System.out.println("Aviso: Emolumento '" + idTrimmed + "' ja foi pago para este aluno. Ignorado.");
                continue;
            }

            Emolumento emol = null;
            for (Emolumento e : listaEmolumentos) {
                if (e.idEmolumento.equalsIgnoreCase(idTrimmed)) { emol = e; break; }
            }
            if (emol == null) {
                System.out.println("Aviso: Emolumento '" + idTrimmed + "' nao encontrado. Ignorado.");
                continue;
            }

            emolumentosValidos.add(emol);
            totalAPagar += emol.preco;
            itens.add(emol.descricao);
        }

        if (emolumentosValidos.isEmpty()) {
            System.out.println("Nenhum emolumento valido seleccionado.");
            return;
        }

        System.out.println("\nResumo da transaccao:");
        for (Emolumento e : emolumentosValidos) {
            System.out.println("  " + e.descricao + ": " + String.format("%.2f", e.preco) + " Kz");
        }
        System.out.println("  --------------------------------");
        System.out.println("  TOTAL A PAGAR: " + String.format("%.2f", totalAPagar) + " Kz");

        // Registar pagamentos
        String idAlunoPagamento = aluno.numeroMatricula != null ? aluno.numeroMatricula : aluno.idBI;
        for (Emolumento emol : emolumentosValidos) {
            PagamentoEmolumento pe = Fabricas.criarPagamentoEmolumento(idAlunoPagamento, emol.idEmolumento, emol.preco, LocalDate.now());
            historicoPagamentosEmolumentos.add(pe);
        }
        atualizarSituacaoAluno(termoAluno);

        // Comprovativo unico com todos os emolumentos
        Comprovativo comp = Fabricas.criarComprovativo(idAlunoPagamento, aluno.nomeCompleto, "Comprovativo de Emolumentos",
                totalAPagar, LocalDate.now(), itens.toArray(new String[0]));
        System.out.println("\n" + GeradorRelatorio.formatarComprovativo(comp));
        GeradorRelatorio.exportar(
        GeradorRelatorio.formatarComprovativo(comp),
        GeradorRelatorio.PASTA_COMPROVATIVOS_EMOLUMENTOS,
        "comp_emolumentos_" + idAlunoPagamento + "_" + LocalDate.now(),
        "TXT");

    }

    // REQ 4: Pagamento anual da propina com desconto de 15% sobre o total de mensalidades em aberto do ano
    public static void pagarPropinasAnualComDesconto(String termoAluno, Scanner teclado) {
        Aluno aluno = encontrarAluno(termoAluno);
        if (aluno == null) { System.out.println("Aluno nao encontrado."); return; }
        if (aluno.situacao.equalsIgnoreCase("TRANCADO")) {
            System.out.println("Aluno Trancado - Impossivel efectuar o pagamento.");
            return;
        }

        // Verificar se já pagou alguma mensalidade
        boolean jaPagou = false;
        for (Mensalidade m : aluno.mensalidades) {
            if (m.pago) {
                jaPagou = true;
                break;
            }
        }
        if (jaPagou) {
            System.out.println("ERRO: O aluno ja possui mensalidade(s) paga(s).");
            System.out.println("O pagamento anual so pode ser efectuado para TODAS as mensalidades em aberto.");
            System.out.println("Utilize a opcao de pagamento normal para pagar as restantes.");
            return;
        }
        
        // Calcular total de TODAS as mensalidades do ano (pagas ou nao)
        double totalBruto = 0;
        ArrayList<Mensalidade> emAberto = new ArrayList<>();
        for (Mensalidade m : aluno.mensalidades) {
            totalBruto += m.valor;
            if (!m.pago) emAberto.add(m);
        }

        if (emAberto.isEmpty()) {
            System.out.println("Todas as mensalidades deste aluno ja estao pagas.");
            return;
        }

        double desconto = totalBruto * 0.15;
        double totalComDesconto = totalBruto - desconto;

        System.out.println("\n=== PAGAMENTO ANUAL DA PROPINA ===");
        System.out.println("Aluno         : " + aluno.nomeCompleto);
        System.out.println("Total do ano  : " + String.format("%.2f", totalBruto) + " Kz");
        System.out.println("Desconto (15%): " + String.format("%.2f", desconto) + " Kz");
        System.out.println("TOTAL A PAGAR : " + String.format("%.2f", totalComDesconto) + " Kz");
        System.out.println("----------------------------------");
        System.out.println("Ao confirmar, TODAS as mensalidades serao marcadas como Pagas.");
        // Pagamento confirmado pelo menu antes de invocar este metodo

        // Nota: a confirmacao sera lida pelo menu que invoca este metodo.
        // Para nao depender do Scanner aqui, processamos directamente.
        System.out.print("\nConfirmar pagamento anual com desconto de 15%? (S/N): ");
        String confirmacao = teclado.nextLine().trim();
        if (!confirmacao.equalsIgnoreCase("S")) {
            System.out.println("Operacao cancelada.");
            return;
        }

        // Marcar todas as mensalidades como pagas
        ArrayList<String> itens = new ArrayList<>();
        for (Mensalidade m : aluno.mensalidades) {
            if (!m.pago) {
                m.pago = true;
                m.dataPagamento = LocalDate.now();
            }
            itens.add("Mensalidade " + m.numero);
        }
        atualizarSituacaoAluno(termoAluno);

        String idComp = aluno.numeroMatricula != null ? aluno.numeroMatricula : aluno.idBI;
        Comprovativo comp = Fabricas.criarComprovativo(idComp, aluno.nomeCompleto,
                "Comprovativo - Pagamento Anual da Propina (15% desconto)",
                totalComDesconto, LocalDate.now(), itens.toArray(new String[0]));
        System.out.println("\n" + GeradorRelatorio.formatarComprovativo(comp));
        GeradorRelatorio.exportar(
        GeradorRelatorio.formatarComprovativo(comp),
        GeradorRelatorio.PASTA_COMPROVATIVOS_PROPINAS,
        "comp_anual_" + idComp + "_" + LocalDate.now(),
        "TXT");


        System.out.println("Todas as mensalidades marcadas como Pagas.");
    }

    public static void atualizarSituacaoAluno(String termoAluno) {
        Aluno a = encontrarAluno(termoAluno);
        if (a != null) {
            if (a.situacao.equals("TRANCADO") || a.situacao.equals("ANULADO")) return;

            boolean primeiraMesPago = a.mensalidades[0].pago;
            boolean cartaoPago = false;

            for (PagamentoEmolumento pe : historicoPagamentosEmolumentos) {
                if (pagamentoPertenceAoAluno(pe, a)) {
                    for (Emolumento e : listaEmolumentos) {
                        if (e.idEmolumento.equals(pe.idEmolumento) &&
                            e.descricao.toLowerCase().contains("cart")) {
                            cartaoPago = true;
                        }
                    }
                }
            }

            if (primeiraMesPago && cartaoPago) {
                a.situacao = "ACTIVO";
            } else {
                a.situacao = "INACTIVO";
            }
        }
    }

    /*
    public static double calcularDesconto(String termoAluno) {
        Aluno a = encontrarAluno(termoAluno);
        if (a != null) {
            boolean todosPagos = true;
            double total = 0;
            for (Mensalidade m : a.mensalidades) {
                if (!m.pago) { todosPagos = false; break; }
                total += m.valor;
            }
            if (todosPagos) {
                double desconto = total * 0.05;
                System.out.println("Desconto por pagamento integral: " + String.format("%.2f", desconto) + " Kz (5%)");
                return desconto;
            } else {
                System.out.println("O aluno ainda tem mensalidades em aberto.");
                return 0;
            }
        }
        System.out.println("Aluno nao encontrado.");
        return 0;
    }**/

    public static void gerarHistoricoPagamentos(String bi, String formato) {
        Aluno aluno = encontrarAluno(bi);
        if (aluno == null) { System.out.println("Aluno nao encontrado."); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("===== HISTORICO DE PAGAMENTOS =====\n");
        sb.append("Aluno: ").append(aluno.nomeCompleto).append("\n");
        sb.append("Matricula: ").append(aluno.numeroMatricula).append("\n");
        sb.append("BI: ").append(aluno.idBI).append("\n\n");

        sb.append("--- PROPINAS ---\n");
        for (Mensalidade m : aluno.mensalidades) {
            if (m.pago) {
                sb.append("Mensalidade ").append(m.numero)
                  .append(" | Valor: ").append(String.format("%.2f", m.valor))
                  .append(" Kz | Pago em: ").append(Utilitarios.dataParaString(m.dataPagamento)).append("\n");
            }
        }

        sb.append("\n--- EMOLUMENTOS ---\n");
        for (PagamentoEmolumento pe : historicoPagamentosEmolumentos) {
            if (pagamentoPertenceAoAluno(pe, aluno)) {
                sb.append("Emolumento ID: ").append(pe.idEmolumento)
                  .append(" | Valor: ").append(String.format("%.2f", pe.valorPago))
                  .append(" Kz | Pago em: ").append(Utilitarios.dataParaString(pe.dataPagamento)).append("\n");
            }
        }

        // REQ 2: sempre TXT
        GeradorRelatorio.exportar(sb.toString(), GeradorRelatorio.PASTA_HISTORICOS,"historico_" + aluno.numeroMatricula,"TXT");

    }

    public static void gerarRelatorioGeralPagamentos(String formato) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATORIO GERAL DE PAGAMENTOS =====\n");
        sb.append("Data: ").append(Utilitarios.dataParaString(LocalDate.now())).append("\n\n");
        double totalGeral = 0;

        for (Aluno a : listaAlunos) {
            sb.append("Aluno: ").append(a.nomeCompleto)
              .append(" | Matricula: ").append(a.numeroMatricula)
              .append(" | BI: ").append(a.idBI).append("\n");
            double totalAluno = 0;
            for (Mensalidade m : a.mensalidades) {
                if (m.pago) {
                    totalAluno += m.valor;
                    sb.append("  Mensalidade ").append(m.numero).append(": ").append(String.format("%.2f", m.valor)).append(" Kz\n");
                }
            }
            sb.append("  Total Propinas: ").append(String.format("%.2f", totalAluno)).append(" Kz\n");
            totalGeral += totalAluno;
            sb.append("--------------------------------\n");
        }

        sb.append("\n--- EMOLUMENTOS ---\n");
        double totalEmol = 0;
        for (PagamentoEmolumento pe : historicoPagamentosEmolumentos) {
            sb.append("Aluno: ").append(pe.idAluno)
              .append(" | Emolumento: ").append(pe.idEmolumento)
              .append(" | Valor: ").append(String.format("%.2f", pe.valorPago)).append(" Kz\n");
            totalEmol += pe.valorPago;
        }
        sb.append("Total Emolumentos: ").append(String.format("%.2f", totalEmol)).append(" Kz\n");
        totalGeral += totalEmol;

        sb.append("\n===== TOTAL GERAL: ").append(String.format("%.2f", totalGeral)).append(" Kz =====\n");
        // REQ 2: sempre TXT
        GeradorRelatorio.exportar(
        sb.toString(),
        GeradorRelatorio.PASTA_RELATORIOS_PAGAMENTOS,
        "relatorio_geral_pagamentos_" + LocalDate.now(),
        "TXT");

    }
}
