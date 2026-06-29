package servicos;

import entidades.Emolumento;
import entidades.PagamentoEmolumento;
import java.util.ArrayList;
import java.util.Scanner;

public class GestaoEmolumentos {

    private static ArrayList<Emolumento> lista;
    private static ArrayList<PagamentoEmolumento> pagamentos;


    public GestaoEmolumentos(ArrayList<Emolumento> lista) {
        this(lista, new ArrayList<PagamentoEmolumento>());
    }

    public GestaoEmolumentos(ArrayList<Emolumento> lista, ArrayList<PagamentoEmolumento> pagamentos) {
        this.lista = lista;
        this.pagamentos = pagamentos;
    }

    public static boolean cadastrarEmolumento(Emolumento e) {
        for (Emolumento existente : lista) {
            if (existente.descricao.equalsIgnoreCase(e.descricao)) {
                System.out.println("Erro: Emolumento ja existe.");
                return false;
            }
        }
        lista.add(e);
        System.out.println("Emolumento cadastrado.");
        return true;
    }

    public static boolean editarEmolumento(String id, String novaDescricao, double novoPreco) {
         
        for (Emolumento e : lista) {
            if (e.idEmolumento.equalsIgnoreCase(id)) {
                if (novaDescricao != null && !novaDescricao.isEmpty()) e.descricao = novaDescricao;
                if (novoPreco > 0) e.preco = novoPreco;
                System.out.println("Emolumento actualizado.");
                return true;
            }
        }
        System.out.println("Emolumento nao encontrado.");
        return false;
    }

    public static boolean eliminarEmolumento(String id, Scanner teclado) {
        Emolumento alvo = null;
        
        for (Emolumento e : lista) {
            if (e.idEmolumento.equalsIgnoreCase(id)) { alvo = e; break; }
        }
        if (alvo == null) { System.out.println("Emolumento nao encontrado."); return false; }

        for (PagamentoEmolumento p : pagamentos) {
            if (p.idEmolumento.equalsIgnoreCase(id)) {
                System.out.println("Erro: Nao e possivel eliminar emolumento com pagamentos associados.");
                return false;
            }
        }

        System.out.print("Tem a certeza que deseja eliminar o emolumento '" + alvo.descricao + "'? (S/N): ");
        String conf = teclado.nextLine().trim();
        if (!conf.equalsIgnoreCase("S")) {
            System.out.println("Operacao cancelada.");
            return false;
        }

        lista.remove(alvo);
        System.out.println("Emolumento eliminado.");
        return true;
    }

    public static Emolumento buscarEmolumento(String id) {
        for (Emolumento e : lista) {
            if (e.idEmolumento.equalsIgnoreCase(id) || e.descricao.equalsIgnoreCase(id)) {
                return e;
            }
        }
        return null;
    }

    public static ArrayList<Emolumento> listarEmolumentos() {
        return lista;
    }
}
