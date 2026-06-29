package servicos;

import entidades.Funcionario;
import entidades.NivelAcesso;
import java.time.LocalDate;
import relatorios.GeradorRelatorio;
import utilitarios.Utilitarios;
import java.util.ArrayList;
import java.util.Scanner;

public class GestaoFuncionarios {

    private static ArrayList<Funcionario> lista;

    public GestaoFuncionarios(ArrayList<Funcionario> lista) {
        this.lista = lista;
    }

    public static boolean cadastrarFuncionario(Funcionario f) {
        if (!Utilitarios.validarFormatoBI(f.bi)) {
            System.out.println("Erro: BI deve ter exactamente 14 caracteres alfanumericos.");
            return false;
        }
        if (!Utilitarios.validarEmail(f.emailInstitucional)) {
            System.out.println("Erro: Email institucional invalido (formato esperado: usuario@dominio.com).");
            return false;
        }
        if (f.nivelAcesso == 0) {
            System.out.println("Erro: Nivel de acesso invalido. Use ADMIN, SECRETARIO ou FINANCEIRO.");
            return false;
        }
        for (Funcionario existente : lista) {
            if (existente.bi.equalsIgnoreCase(f.bi)) {
                System.out.println("Erro: BI ja cadastrado.");
                return false;
            }
            if (existente.codigoFuncionario.equalsIgnoreCase(f.codigoFuncionario)) {
                System.out.println("Erro: Codigo de funcionario ja cadastrado.");
                return false;
            }
            if (!f.emailInstitucional.isEmpty()
                    && existente.emailInstitucional.equalsIgnoreCase(f.emailInstitucional)) {
                System.out.println("Erro: Email institucional ja cadastrado.");
                return false;
            }
        }
        lista.add(f);
        System.out.println("Funcionario cadastrado.");
        return true;
    }

    public static boolean editarFuncionario(String termo, String novoNome, String novoEmail, int novoNivel, String novaSenha) {
        Funcionario f = buscarFuncionario(termo);
        if (f == null) {
            System.out.println("Funcionario nao encontrado.");
            return false;
        }
        if (novoNome != null && !novoNome.isEmpty()) f.nome = novoNome;
        if (novoEmail != null && !novoEmail.isEmpty()) {
            if (Utilitarios.validarEmail(novoEmail)) {
                f.emailInstitucional = novoEmail.toLowerCase();
            } else {
                System.out.println("Erro: Email invalido. Mantido o anterior.");
            }
        }
        if (novoNivel != 0) f.nivelAcesso = novoNivel;
        if (novaSenha != null && !novaSenha.isEmpty()) f.senha = novaSenha;
        System.out.println("Funcionario actualizado.");
        return true;
    }

    public static boolean editarFuncionario(String termo, String novoNome, String novoEmail, String novoNivelTexto, String novaSenha) {
        int nivel = (novoNivelTexto == null || novoNivelTexto.isEmpty()) ? 0 : NivelAcesso.fromTexto(novoNivelTexto);
        return editarFuncionario(termo, novoNome, novoEmail, nivel, novaSenha);
    }

    public static boolean eliminarFuncionario(String termo, Scanner teclado) {
        Funcionario alvo = buscarFuncionario(termo);
        if (alvo == null) { System.out.println("Funcionario nao encontrado."); return false; }

        System.out.print("Tem a certeza que deseja eliminar o funcionario '" + alvo.nome + "'? (S/N): ");
        String conf = teclado.nextLine().trim();
        if (!conf.equalsIgnoreCase("S")) {
            System.out.println("Operacao cancelada.");
            return false;
        }

        lista.remove(alvo);
        System.out.println("Funcionario eliminado.");
        return true;
    }

    public static Funcionario buscarFuncionario(String termo) {
        for (Funcionario f : lista) {
            if (f.codigoFuncionario.equalsIgnoreCase(termo)
                    || f.emailInstitucional.equalsIgnoreCase(termo)
                    || f.bi.equalsIgnoreCase(termo)
                    || f.nome.equalsIgnoreCase(termo)) {
                return f;
            }
        }
        return null;
    }

    public static ArrayList<Funcionario> listarFuncionarios() {
        return lista;
    }

    public static void gerarRelatorioFuncionarios(String formato) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATORIO DE FUNCIONARIOS =====\n");
        sb.append("Data: ").append(Utilitarios.dataParaString(java.time.LocalDate.now())).append("\n\n");
        for (Funcionario f : lista) {
            sb.append("ID interno: ").append(f.id).append("\n");
            sb.append("Codigo: ").append(f.codigoFuncionario).append("\n");
            sb.append("Nome: ").append(f.nome).append("\n");
            sb.append("BI: ").append(f.bi).append("\n");
            sb.append("Email: ").append(f.emailInstitucional).append("\n");
            sb.append("Nivel de Acesso: ").append(NivelAcesso.nome(f.nivelAcesso)).append("\n");
            sb.append("--------------------------------\n");
        }
        GeradorRelatorio.exportar(
        sb.toString(),
        GeradorRelatorio.PASTA_RELATORIOS_FUNCIONARIOS,
        "relatorio_funcionarios_" + LocalDate.now(),
        formato);

    }
}
