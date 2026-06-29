/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package relatorios;

import entidades.Aluno;
import entidades.Comprovativo;
import entidades.Mensalidade;
import utilitarios.Utilitarios;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class GeradorRelatorio {

    // ===== PASTAS DO SISTEMA =====
    public static final String PASTA_COMPROVATIVOS_PROPINAS   = "saidas/comprovativos/propinas/";
    public static final String PASTA_COMPROVATIVOS_EMOLUMENTOS = "saidas/comprovativos/emolumentos/";
    public static final String PASTA_RELATORIOS_ALUNOS        = "saidas/relatorios/alunos/";
    public static final String PASTA_RELATORIOS_FUNCIONARIOS  = "saidas/relatorios/funcionarios/";
    public static final String PASTA_RELATORIOS_TURMAS        = "saidas/relatorios/turmas/";
    public static final String PASTA_RELATORIOS_PAGAMENTOS    = "saidas/relatorios/pagamentos/";
    public static final String PASTA_HISTORICOS               = "saidas/historicos/";
 
    public static void exportar(String conteudo, String pasta, String nomeArquivo, String formato, Scanner teclado) {
        // Mostrar na consola
        System.out.println("\n" + conteudo);

        System.out.print("\nDeseja gerar relatorio em TXT? (S/N): ");
        String resp = teclado.nextLine().trim();
        if (resp.equalsIgnoreCase("S")) {
            Utilitarios.garantirPasta(pasta);
            String caminhoCompleto = pasta + nomeArquivo + ".txt";
            exportarTXT(conteudo, caminhoCompleto);
        } else {
            System.out.println("Relatorio nao foi salvo em arquivo.");
        }
    }

    // Sobrecarga para manter compatibilidade com chamadas existentes
    public static void exportar(String conteudo, String pasta, String nomeArquivo, String formato) {
        // Esta versão mantém o comportamento anterior (salva sempre)
        Utilitarios.garantirPasta(pasta);
        String caminhoCompleto = pasta + nomeArquivo + ".txt";
        exportarTXT(conteudo, caminhoCompleto);
    }


    public static void exportarTXT(String conteudo, String caminhoCompleto) {
        try {
            FileWriter fw = new FileWriter(caminhoCompleto);
            fw.write(conteudo);
            fw.close();
            System.out.println("Ficheiro gravado em: " + caminhoCompleto);
        } catch (IOException e) {
            System.out.println("Erro ao exportar: " + e.getMessage());
        }
    }

 
    public static String formatarAluno(Aluno a) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DADOS DO ALUNO ===\n");
        sb.append("Matricula: ").append(a.numeroMatricula).append("\n");
        sb.append("Nome: ").append(a.nomeCompleto).append("\n");
        sb.append("BI: ").append(a.idBI).append("\n");
        sb.append("Idade: ").append(a.idade).append(" anos\n");
        sb.append("Classe: ").append(a.classe).append(".a Classe\n");
        sb.append("Curso ID: ").append(a.idCurso).append("\n");
        sb.append("Turma ID: ").append(a.idTurma).append("\n");
        sb.append("Situacao: ").append(a.situacao).append("\n");
        sb.append("Data de Matricula: ").append(Utilitarios.dataParaString(a.dataMatricula)).append("\n");
        if (a.multaAplicada > 0) {
            sb.append("Multa Aplicada: ").append(String.format("%.2f", a.multaAplicada)).append(" Kz\n");
        }
        sb.append("\n--- MENSALIDADES ---\n");
        for (Mensalidade m : a.mensalidades) {
            sb.append("Mes ").append(m.numero).append(": ")
              .append(String.format("%.2f", m.valor)).append(" Kz | ")
              .append(m.pago ? "PAGO em " + Utilitarios.dataParaString(m.dataPagamento) : "EM ABERTO")
              .append("\n");
        }
        return sb.toString();
    }
 
    public static String formatarComprovativo(Comprovativo c) {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("         COMPROVATIVO DE PAGAMENTO          \n");
        sb.append("============================================\n");
        sb.append("Tipo: ").append(c.tipoDocumento).append("\n");
        sb.append("Aluno: ").append(c.nomeAluno).append("\n");
        sb.append("Identificador: ").append(c.idAluno).append("\n");
        sb.append("Data de Emissao: ").append(Utilitarios.dataParaString(c.dataEmissao)).append("\n");
        sb.append("\nItens Pagos:\n");
        for (String item : c.itensPagos) {
            sb.append("  - ").append(item).append("\n");
        }
        sb.append("\nTotal Pago: ").append(String.format("%.2f", c.totalPago)).append(" Kz\n");
        sb.append("============================================\n");
        return sb.toString();
    }
}


