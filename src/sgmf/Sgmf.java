package sgmf;


import servicos.Fabricas;
import entidades.*;
import menus.MenuPrincipal;
import persistencia.Persistencia;
import java.util.ArrayList;
import java.util.Scanner;

public class Sgmf {

    public static ArrayList<Funcionario>         funcionarios        = new ArrayList<>();
    public static ArrayList<Curso>               cursos              = new ArrayList<>();
    public static ArrayList<Turma>               turmas              = new ArrayList<>();
    public static ArrayList<Emolumento>          emolumentos         = new ArrayList<>();
    public static ArrayList<Aluno>               alunos              = new ArrayList<>();
    public static ArrayList<PagamentoEmolumento> historicoPagamentos = new ArrayList<>();
    public static ArrayList<CalendarioMatricula> calendario          = new ArrayList<>();

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        // REQ 1: cursos carregados do ficheiro estatico cursos.txt
        Persistencia.carregarCursos(cursos);
        if (cursos.isEmpty()) {
            System.out.println("cursos.txt vazio ou nao encontrado. Carregando cursos estaticos padrao.");
            cursos.addAll(CursosEstaticos.obterTodos());
        }


        // Dados operacionais do ficheiro principal
        Persistencia.carregarDados(funcionarios, cursos, turmas, emolumentos, alunos, historicoPagamentos, calendario);

        if (funcionarios.isEmpty()) {
            System.out.println("Nenhum funcionario encontrado. Administrador inicial criado.");
            funcionarios.add(Fabricas.criarFuncionario(1, "FUNC001", "Sabino Gaspar",
                    "123456789LA001", "sabino.gaspar@sgmf.co.ao", "ADMIN", "admin1234"));
            System.out.println("Login inicial: Usuario FUNC001 | Senha admin1234");
        }

        MenuPrincipal menu = new MenuPrincipal(teclado, funcionarios, cursos, turmas,
                emolumentos, alunos, historicoPagamentos, calendario);
        menu.mostrar();

        teclado.close();
    }
}
