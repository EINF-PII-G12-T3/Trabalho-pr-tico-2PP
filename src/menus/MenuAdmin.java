package menus;


import servicos.Fabricas;
import entidades.*;
import servicos.*;
import persistencia.Persistencia;
import utilitarios.Utilitarios;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuAdmin {

    Scanner teclado;
    GestaoFuncionarios gestaoFunc;
    GestaoCursos       gestaoCursos;
    GestaoTurmas       gestaoTurmas;
    GestaoEmolumentos  gestaoEmol;
    GestaoMatriculas   gestaoMat;
    GestaoFinancas     gestaoFin;
    ArrayList<Emolumento>          listaEmolumentos;
    ArrayList<CalendarioMatricula> calendario;
    // REQ 3: referência aos funcionarios para validação cross-entity de BI
    ArrayList<Funcionario>         listaFuncionarios;
    // REQ 2: listas necessárias para reset total
    ArrayList<Turma>               listaTurmas;
    ArrayList<Aluno>               listaAlunos;
    ArrayList<PagamentoEmolumento> listaPagamentos;
    Funcionario                    adminLogado;

    public MenuAdmin(Scanner teclado, ArrayList<Funcionario> funcionarios, ArrayList<Curso> cursos,
                     ArrayList<Turma> turmas, ArrayList<Emolumento> emolumentos, ArrayList<Aluno> alunos,
                     ArrayList<PagamentoEmolumento> pagamentos, ArrayList<CalendarioMatricula> calendario,
                     Funcionario adminLogado) {
        this.teclado          = teclado;
        this.listaFuncionarios = funcionarios;
        this.listaTurmas       = turmas;
        this.listaAlunos       = alunos;
        this.listaPagamentos   = pagamentos;
        this.adminLogado       = adminLogado;
        this.gestaoFunc        = new GestaoFuncionarios(funcionarios);
        this.gestaoCursos      = new GestaoCursos(cursos, turmas, alunos);
        this.gestaoTurmas      = new GestaoTurmas(turmas, alunos);
        this.gestaoEmol        = new GestaoEmolumentos(emolumentos, pagamentos);
        this.gestaoMat         = new GestaoMatriculas(alunos, cursos, turmas, funcionarios);
        this.gestaoFin         = new GestaoFinancas(alunos, emolumentos, pagamentos);
        this.listaEmolumentos  = emolumentos;
        this.calendario        = calendario;
    }

    public void mostrar() {
        int opcao;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|       MENU ADMINISTRADOR       |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Gerir Funcionarios          |");
            System.out.println("| 2. Consultar Cursos            |");
            System.out.println("| 3. Gerir Turmas                |");
            System.out.println("| 4. Gerir Emolumentos           |");
            System.out.println("| 5. Gerir Matriculas            |");
            System.out.println("| 6. Gerir Financas              |");
            System.out.println("| 7. Gerir Calendario            |");
            System.out.println("| 8. Eliminar Todos os Dados     |");
            System.out.println("| 0. Sair                        |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            opcao = lerInteiro();
            switch (opcao) {
                case 1: menuFuncionarios(); break;
                case 2: menuCursos();       break;
                case 3: menuTurmas();       break;
                case 4: menuEmolumentos();  break;
                case 5: menuMatriculas();   break;
                case 6: menuFinancas();     break;
                case 7: menuCalendario();   break;
                case 8: eliminarTodosDados(); break;
                case 0: System.out.println("Logout efectuado."); break;
                default: System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    // ===================== FUNCIONARIOS =====================
    void menuFuncionarios() {
        int op;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|     GESTAO DE FUNCIONARIOS     |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Cadastrar Funcionario       |");
            System.out.println("| 2. Listar Funcionarios         |");
            System.out.println("| 3. Buscar Funcionario          |");
            System.out.println("| 4. Editar Funcionario          |");
            System.out.println("| 5. Eliminar Funcionario        |");
            System.out.println("| 6. Gerar Relatorio             |");
            System.out.println("| 0. Voltar                      |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            op = lerInteiro();
            switch (op) {
                case 1: cadastrarFuncionario(); break;
                case 2: listarFuncionarios();   break;
                case 3: buscarFuncionario();    break;
                case 4: editarFuncionario();    break;
                case 5: eliminarFuncionario();  break;
                case 6:
                    gestaoFunc.gerarRelatorioFuncionarios("TXT");
                    Utilitarios.pausar(teclado);
                    break;
            }
        } while (op != 0);
    }

    void cadastrarFuncionario() {
        System.out.println("\n--- CADASTRAR FUNCIONARIO ---");
        System.out.print("Nome: ");
        String nome = teclado.nextLine();

        // REQ 2: loop até BI válido
        String bi;
        while (true) {
            System.out.print("BI (exactamente 14 caracteres alfanumericos): ");
            bi = teclado.nextLine().trim();
            if (Utilitarios.validarFormatoBI(bi)) break;
            System.out.println("Erro: BI invalido. Deve ter exactamente 14 caracteres alfanumericos. Tente novamente.");
        }

        // REQ 2: loop até email válido
        String email;
        while (true) {
            System.out.print("Email institucional (usuario@dominio.com): ");
            email = teclado.nextLine().trim().toLowerCase();
            if (Utilitarios.validarEmail(email)) break;
            System.out.println("Erro: Email invalido. Formato esperado: usuario@dominio.com. Tente novamente.");
        }

        System.out.println("Nivel de Acesso:");
        System.out.println("  1 - ADMIN");
        System.out.println("  2 - SECRETARIO");
        System.out.println("  3 - FINANCEIRO");
        int nivel;
        while (true) {
            System.out.print("Opcao: ");
            int nivelCod = lerInteiro();
            nivel = NivelAcesso.fromCodigo(nivelCod);
            if (nivel != 0) break;
            System.out.println("Erro: Nivel invalido. Escolha 1 (ADMIN), 2 (SECRETARIO) ou 3 (FINANCEIRO).");
        }

        System.out.print("Senha: ");
        String senha = teclado.nextLine();

        int novoId = gestaoFunc.listarFuncionarios().size() + 1;
        String codigo = String.format("FUNC%03d", novoId);
        Funcionario novoFunc = Fabricas.criarFuncionario(novoId, codigo, nome, bi, email, nivel, senha);
        if (gestaoFunc.cadastrarFuncionario(novoFunc)) {
            System.out.println("Codigo de acesso atribuido: " + codigo);
        }
        Utilitarios.pausar(teclado);
    }

    void listarFuncionarios() {
        System.out.println("\n=== LISTA DE FUNCIONARIOS ===");
        ArrayList<Funcionario> lista = gestaoFunc.listarFuncionarios();
        if (lista.isEmpty()) { System.out.println("Nenhum funcionario cadastrado."); }
        for (Funcionario f : lista) {
            System.out.println("Codigo: " + f.codigoFuncionario
                    + " | Nome: " + f.nome
                    + " | Email: " + f.emailInstitucional
                    + " | Nivel: " + NivelAcesso.nome(f.nivelAcesso));
        }
        Utilitarios.pausar(teclado);
    }

    void buscarFuncionario() {
        System.out.print("Nome, codigo, email ou BI do funcionario: ");
        String termo = teclado.nextLine();
        Funcionario encontrado = gestaoFunc.buscarFuncionario(termo);
        if (encontrado != null) {
            System.out.println("\nFuncionario encontrado:");
            System.out.println("ID interno : " + encontrado.id);
            System.out.println("Codigo     : " + encontrado.codigoFuncionario);
            System.out.println("Nome       : " + encontrado.nome);
            System.out.println("BI         : " + encontrado.bi);
            System.out.println("Email      : " + encontrado.emailInstitucional);
            System.out.println("Nivel      : " + NivelAcesso.nome(encontrado.nivelAcesso));
        } else {
            System.out.println("Funcionario nao encontrado.");
        }
        Utilitarios.pausar(teclado);
    }

    void editarFuncionario() {
        System.out.print("ID, codigo, email ou BI do funcionario a editar: ");
        String termoEdit = teclado.nextLine();
        Funcionario f = gestaoFunc.buscarFuncionario(termoEdit);
        if (f == null) {
            System.out.println("Funcionario nao encontrado.");
            Utilitarios.pausar(teclado); return;
        }
        System.out.print("Novo nome (ENTER para manter): ");
        String novoNome = teclado.nextLine();
        System.out.print("Novo email (ENTER para manter): ");
        String novoEmail = teclado.nextLine();
        System.out.println("Novo nivel: 1-ADMIN / 2-SECRETARIO / 3-FINANCEIRO");
        System.out.print("Opcao (ENTER para manter): ");
        String nivelStr = teclado.nextLine().trim();
        System.out.print("Nova senha (ENTER para manter): ");
        String novaSenha = teclado.nextLine();
        gestaoFunc.editarFuncionario(termoEdit,
            novoNome.trim().isEmpty() ? null : novoNome.trim(),
            novoEmail.trim().isEmpty() ? null : novoEmail.trim().toLowerCase(),
            nivelStr.isEmpty() ? null : nivelStr,
            novaSenha.trim().isEmpty() ? null : novaSenha.trim());
        Utilitarios.pausar(teclado);
    }

    void eliminarFuncionario() {
        System.out.print("Codigo, email ou BI do funcionario a eliminar: ");
        String termoElim = teclado.nextLine();
        Funcionario f = gestaoFunc.buscarFuncionario(termoElim);
        if (f == null) {
            System.out.println("Funcionario nao encontrado.");
            Utilitarios.pausar(teclado); return;
        }
        gestaoFunc.eliminarFuncionario(termoElim, teclado);
        Utilitarios.pausar(teclado);
    }

    // ===================== CURSOS (REQ 1 — apenas consulta e edição de propina) =====================
    void menuCursos() {
        int op;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|       CONSULTA DE CURSOS       |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Listar todos os Cursos      |");
            System.out.println("| 2. Listar por Departamento     |");
            System.out.println("| 3. Buscar Curso                |");
            System.out.println("| 4. Editar Propina do Curso     |");
            System.out.println("| 0. Voltar                      |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            op = lerInteiro();

            switch (op) {
                case 1:
                    listarCursos(gestaoCursos.listarCursos());
                    Utilitarios.pausar(teclado);
                    break;

                case 2:
                    System.out.println("Departamentos:");
                    System.out.println("  1 - DET  (Engenharias e Tecnologias)");
                    System.out.println("  2 - DCSA (Ciencias Sociais Aplicadas)");
                    System.out.print("Opcao: ");
                    int dOp = lerInteiro();
                    String dept = (dOp == 1) ? CursosEstaticos.DEPT_TECNICOS : CursosEstaticos.DEPT_PUNIV;
                    ArrayList<Curso> filtrados = new ArrayList<>();
                    for (Curso c : gestaoCursos.listarCursos()) {
                        if (dept.equalsIgnoreCase(c.departamento)) filtrados.add(c);
                    }
                    listarCursos(filtrados);
                    Utilitarios.pausar(teclado);
                    break;

                case 3:
                    System.out.print("Nome ou ID do curso: ");
                    Curso cEnc = gestaoCursos.buscarCurso(teclado.nextLine());
                    if (cEnc != null) {
                        imprimirCurso(cEnc);
                    } else {
                        System.out.println("Curso nao encontrado.");
                    }
                    Utilitarios.pausar(teclado);
                    break;

                case 4:
                    listarCursos(gestaoCursos.listarCursos());
                    System.out.print("\nID do curso a editar propina: ");
                    String idEdit = teclado.nextLine().trim();
                    Curso cEdit = gestaoCursos.buscarCurso(idEdit);
                    if (cEdit == null) {
                        System.out.println("Curso nao encontrado.");
                        Utilitarios.pausar(teclado); break;
                    }
                    double novaP;
                    while (true) {
                        System.out.print("Novo valor de propina (Kz) [actual: "
                                + String.format("%.2f", cEdit.valorPropina) + "]: ");
                        String s = teclado.nextLine().trim();
                        try {
                            novaP = Double.parseDouble(s);
                            if (novaP > 0) break;
                            System.out.println("Erro: O valor deve ser maior que zero.");
                        } catch (NumberFormatException e) {
                            System.out.println("Erro: Valor invalido. Insira um numero. Tente novamente.");
                        }
                    }
                    gestaoCursos.editarCurso(idEdit, null, novaP);
                    Utilitarios.pausar(teclado);
                    break;
            }
        } while (op != 0);
    }

    public void listarCursos(ArrayList<Curso> lista) {
        System.out.println("\n=== CURSOS ===");
        if (lista.isEmpty()) { System.out.println("Nenhum curso disponivel."); return; }
        String deptActual = "";
        for (Curso c : lista) {
            if (!c.departamento.equals(deptActual)) {
                deptActual = c.departamento;
                System.out.println("\n  [" + deptActual + "]");
            }
            System.out.println("  ID: " + c.idCurso
                    + " | " + c.nome
                    + " | Propina: " + String.format("%.2f", c.valorPropina) + " Kz"
                    + " | Classes: 10.a ate " + c.classeMaxima + ".a");
        }
    }

    public void imprimirCurso(Curso c) {
        System.out.println("ID          : " + c.idCurso);
        System.out.println("Nome        : " + c.nome);
        System.out.println("Departamento: " + c.departamento);
        System.out.println("Propina     : " + String.format("%.2f", c.valorPropina) + " Kz");
        System.out.println("Classes     : 10.a a " + c.classeMaxima + ".a Classe");
    }

    // ===================== TURMAS (REQ 5 — multi-classe com loop) =====================
    void menuTurmas() {
        int op;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|        GESTAO DE TURMAS        |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Cadastrar Turma(s)          |");
            System.out.println("| 2. Listar Turmas               |");
            System.out.println("| 3. Buscar Turma                |");
            System.out.println("| 4. Editar Turma                |");
            System.out.println("| 5. Eliminar Turma              |");
            System.out.println("| 6. Gerar Relatorio             |");
            System.out.println("| 0. Voltar                      |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            op = lerInteiro();

            switch (op) {
                case 1: cadastrarTurmasMultiClasse(); break;

                case 2:
                    System.out.println("\n=== LISTA DE TURMAS ===");
                    for (Turma t : gestaoTurmas.listarTurmas()) {
                        System.out.println("ID: " + t.idTurma
                                + " | Nome: " + t.nome
                                + " | Curso: " + t.idCurso
                                + " | " + t.classe + ".a Classe"
                                + " | Vagas: " + (t.vagas - t.vagasOcupadas) + "/" + t.vagas);
                    }
                    Utilitarios.pausar(teclado);
                    break;

                case 3:
                    System.out.print("Nome ou ID da turma: ");
                    Turma tEnc = gestaoTurmas.buscarTurma(teclado.nextLine());
                    if (tEnc != null) {
                        System.out.println("ID: " + tEnc.idTurma
                                + " | " + tEnc.nome
                                + " | Classe: " + tEnc.classe
                                + " | Vagas livres: " + (tEnc.vagas - tEnc.vagasOcupadas));
                    } else { System.out.println("Turma nao encontrada."); }
                    Utilitarios.pausar(teclado);
                    break;

                case 4:
                    ArrayList<Turma> turmasEditar = gestaoTurmas.listarTurmas();
                    if (turmasEditar.isEmpty()) {
                        System.out.println("Nenhuma turma cadastrada.");
                        Utilitarios.pausar(teclado); break;
                    }
                    System.out.println("\n=== TURMAS EXISTENTES ===");
                    for (Turma t : turmasEditar) {
                        System.out.println("ID: " + t.idTurma
                                + " | " + t.nome
                                + " | " + t.classe + ".a Classe"
                                + " | Vagas: " + (t.vagas - t.vagasOcupadas) + "/" + t.vagas);
                    }
                    System.out.print("\nID da turma a editar: ");
                    String idEditT = teclado.nextLine().trim();
                    Turma tEdit = gestaoTurmas.buscarTurma(idEditT);
                    if (tEdit == null) {
                        System.out.println("Turma nao encontrada.");
                        Utilitarios.pausar(teclado); break;
                    }
                    System.out.println("Valores actuais -> Nome: " + tEdit.nome
                            + " | Vagas: " + tEdit.vagas
                            + " | Classe: " + tEdit.classe);
                    System.out.print("Novo nome (ENTER para manter): ");
                    String novoNomeT = teclado.nextLine().trim();
                    System.out.print("Novas vagas (ENTER para manter): ");
                    String novasVagasStr = teclado.nextLine().trim();
                    int novasVagas = novasVagasStr.isEmpty() ? 0 : parseInteiroSeguro(novasVagasStr);
                    gestaoTurmas.editarTurma(idEditT,
                            novoNomeT.isEmpty() ? null : novoNomeT,
                            novasVagas, 0);
                    Utilitarios.pausar(teclado);
                    break;

                case 5:
                    ArrayList<Turma> turmasElim = gestaoTurmas.listarTurmas();
                    if (turmasElim.isEmpty()) {
                        System.out.println("Nenhuma turma cadastrada.");
                        Utilitarios.pausar(teclado); break;
                    }
                    System.out.println("\n=== TURMAS EXISTENTES ===");
                    for (Turma t : turmasElim) {
                        System.out.println("ID: " + t.idTurma
                                + " | " + t.nome
                                + " | " + t.classe + ".a Classe"
                                + " | Vagas: " + (t.vagas - t.vagasOcupadas) + "/" + t.vagas);
                    }
                    System.out.print("\nID da turma a eliminar: ");
                    gestaoTurmas.eliminarTurma(teclado.nextLine().trim(), teclado);
                    Utilitarios.pausar(teclado);
                    break;

                case 6:
                    gestaoTurmas.gerarRelatorioTurmas("TXT");
                    Utilitarios.pausar(teclado);
                    break;
            }
        } while (op != 0);
    }

    /**
     * REQ 5: Cadastro de turmas multi-classe com loop por cada classe seleccionada.
     * ID da Turma: 3 chars do Curso + Classe + Letra  (ex: MEC10A, MEC11B)
     */
    void cadastrarTurmasMultiClasse() {
        System.out.println("\n--- CADASTRAR TURMAS ---");

        // Seleccionar curso
        ArrayList<Curso> cursoLista = gestaoCursos.listarCursos();
        if (cursoLista.isEmpty()) {
            System.out.println("Nenhum curso disponivel.");
            Utilitarios.pausar(teclado); return;
        }
        listarCursos(cursoLista);

        Curso cursoSel;
        while (true) {
            System.out.print("\nID do Curso: ");
            String idCursoT = teclado.nextLine().trim();
            cursoSel = gestaoCursos.buscarCurso(idCursoT);
            if (cursoSel != null) break;
            System.out.println("Erro: Curso nao encontrado. Tente novamente.");
        }

        // Determinar classes disponíveis para o departamento do curso
        int[] classesDisponiveis = CursosEstaticos.classesPermitidas(cursoSel);
        System.out.println("\nClasses disponiveis para " + cursoSel.nome
                + " [" + cursoSel.departamento + "]:");
        for (int i = 0; i < classesDisponiveis.length; i++) {
            System.out.println("  " + (i + 1) + " -> " + classesDisponiveis[i] + ".a Classe");
        }
        System.out.println("  0 -> Todas as classes acima");

        System.out.print("\nSeleccione as classes (separadas por virgula, ex: 1,3 ou 0 para todas): ");
        String seleccao = teclado.nextLine().trim();

        // Resolver quais classes foram seleccionadas
        ArrayList<Integer> classesSel = new ArrayList<>();
        if (seleccao.equals("0")) {
            for (int c : classesDisponiveis) classesSel.add(c);
        } else {
            for (String part : seleccao.split(",")) {
                try {
                    int idx = Integer.parseInt(part.trim()) - 1;
                    if (idx >= 0 && idx < classesDisponiveis.length) {
                        int classeVal = classesDisponiveis[idx];
                        if (!classesSel.contains(classeVal)) classesSel.add(classeVal);
                    } else {
                        System.out.println("Aviso: Opcao '" + (idx + 1) + "' ignorada (fora do intervalo).");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Aviso: Valor '" + part.trim() + "' ignorado.");
                }
            }
        }

        if (classesSel.isEmpty()) {
            System.out.println("Nenhuma classe valida seleccionada.");
            Utilitarios.pausar(teclado); return;
        }

        int totalCriadas = 0;

        // Para cada classe seleccionada: pedir qtd de turmas e vagas
        for (int classe : classesSel) {
            System.out.println("\n--- " + classe + ".a CLASSE ---");

            int qtdTurmas;
            while (true) {
                System.out.print("Quantidade de turmas para a " + classe + ".a Classe: ");
                qtdTurmas = lerInteiro();
                if (qtdTurmas >= 1) break;
                System.out.println("Erro: Quantidade invalida. Deve ser pelo menos 1.");
            }

            int vagasPorTurma;
            while (true) {
                System.out.print("Vagas por turma: ");
                vagasPorTurma = lerInteiro();
                if (vagasPorTurma >= 1) break;
                System.out.println("Erro: Numero de vagas invalido. Deve ser pelo menos 1.");
            }

            for (int i = 0; i < qtdTurmas; i++) {
                // REQ 5: ID = sigla(3) + classe + letra sequencial por classe
                String idTurma  = Utilitarios.gerarIdTurma(cursoSel.nome, classe, gestaoTurmas.listarTurmas());
                String nomeTurma = Utilitarios.gerarNomeTurma(cursoSel.nome, classe, gestaoTurmas.listarTurmas());
                boolean ok = gestaoTurmas.cadastrarTurma(
                        Fabricas.criarTurma(idTurma, nomeTurma, cursoSel.idCurso, vagasPorTurma, classe));
                if (ok) totalCriadas++;
            }
        }

        System.out.println("\nTotal de turmas criadas: " + totalCriadas);
        Utilitarios.pausar(teclado);
    }

    // ===================== EMOLUMENTOS =====================
    void menuEmolumentos() {
        int op;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|      GESTAO DE EMOLUMENTOS     |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Cadastrar Emolumento        |");
            System.out.println("| 2. Listar Emolumentos          |");
            System.out.println("| 3. Buscar Emolumento           |");
            System.out.println("| 4. Editar Emolumento           |");
            System.out.println("| 5. Eliminar Emolumento         |");
            System.out.println("| 0. Voltar                      |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            op = lerInteiro();

            switch (op) {
                case 1:
                    System.out.print("Descricao do Emolumento: ");
                    String desc = teclado.nextLine();
                    double preco;
                    while (true) {
                        System.out.print("Preco (Kz): ");
                        try {
                            preco = Double.parseDouble(teclado.nextLine().trim());
                            if (preco > 0) break;
                            System.out.println("Erro: O preco deve ser maior que zero.");
                        } catch (NumberFormatException e) {
                            System.out.println("Erro: Valor invalido. Insira um numero.");
                        }
                    }
                    String idEmol = Utilitarios.gerarIdEmolumento(listaEmolumentos);
                    gestaoEmol.cadastrarEmolumento(Fabricas.criarEmolumento(idEmol, desc, preco));
                    System.out.println("ID gerado: " + idEmol);
                    Utilitarios.pausar(teclado);
                    break;

                case 2:
                    System.out.println("\n=== LISTA DE EMOLUMENTOS ===");
                    for (Emolumento e : gestaoEmol.listarEmolumentos()) {
                        System.out.println("ID: " + e.idEmolumento
                                + " | " + e.descricao
                                + " | " + String.format("%.2f", e.preco) + " Kz");
                    }
                    Utilitarios.pausar(teclado);
                    break;

                case 3:
                    System.out.print("ID ou descricao: ");
                    Emolumento eEnc = gestaoEmol.buscarEmolumento(teclado.nextLine());
                    if (eEnc != null) {
                        System.out.println("ID: " + eEnc.idEmolumento
                                + " | " + eEnc.descricao
                                + " | " + String.format("%.2f", eEnc.preco) + " Kz");
                    } else { System.out.println("Emolumento nao encontrado."); }
                    Utilitarios.pausar(teclado);
                    break;

                case 4:
                    if (gestaoEmol.listarEmolumentos().isEmpty()) {
                        System.out.println("Nenhum emolumento cadastrado.");
                        Utilitarios.pausar(teclado);
                        break;
                    }

                    System.out.println("\n=== EMOLUMENTOS DISPONIVEIS ===");
                    for (Emolumento e : gestaoEmol.listarEmolumentos()) {
                        System.out.println("ID: " + e.idEmolumento
                                + " | " + e.descricao
                                + " | " + String.format("%.2f", e.preco) + " Kz");
                    }
                    
                    System.out.print("ID do emolumento a editar: ");
                    String idEditE = teclado.nextLine().trim();
                    Emolumento eEdit = gestaoEmol.buscarEmolumento(idEditE);
                    if (eEdit == null) {
                        System.out.println("Emolumento nao encontrado.");
                        Utilitarios.pausar(teclado); break;
                    }
                    System.out.println("Actual -> " + eEdit.descricao
                            + " | " + String.format("%.2f", eEdit.preco) + " Kz");
                    System.out.print("Nova descricao (ENTER para manter): ");
                    String novaDesc = teclado.nextLine().trim();
                    System.out.print("Novo preco (ENTER para manter): ");
                    String novoPrecoStr = teclado.nextLine().trim();
                    double novoPreco = novoPrecoStr.isEmpty() ? 0 : parseDoubleSeguro(novoPrecoStr);
                    gestaoEmol.editarEmolumento(idEditE,
                            novaDesc.isEmpty() ? null : novaDesc, novoPreco);
                    Utilitarios.pausar(teclado);
                    break;

                case 5:
                    if (gestaoEmol.listarEmolumentos().isEmpty()) {
                        System.out.println("Nenhum emolumento cadastrado.");
                        Utilitarios.pausar(teclado);
                        break;
                    }
                    
                    System.out.println("\n=== EMOLUMENTOS DISPONIVEIS ===");
                    for (Emolumento e : gestaoEmol.listarEmolumentos()) {
                        System.out.println("ID: " + e.idEmolumento
                                + " | " + e.descricao
                                + " | " + String.format("%.2f", e.preco) + " Kz");
                    }
                    System.out.print("ID do emolumento a eliminar: ");
                    gestaoEmol.eliminarEmolumento(teclado.nextLine().trim(), teclado);
                    Utilitarios.pausar(teclado);
                    break;
            }
        } while (op != 0);
    }

    // ===================== MATRICULAS =====================
    void menuMatriculas() {
        int op;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|       GESTAO DE MATRICULAS     |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Matricular Aluno            |");
            System.out.println("| 2. Trancar Matricula           |");
            System.out.println("| 3. Editar Matricula            |");
            System.out.println("| 4. Reingresso de Aluno         |");
            System.out.println("| 5. Anular Matricula            |");
            System.out.println("| 6. Buscar Aluno                |");
            System.out.println("| 7. Listar Todos os Alunos      |");
            System.out.println("| 8. Trocar Aluno de Turma       |");
            System.out.println("| 9. Gerar Relatorio Aluno       |");
            System.out.println("| 0. Voltar                      |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            op = lerInteiro();

            switch (op) {
                case 1: matricularAluno(); break;
                case 2:
                    System.out.print("Numero de matricula do aluno: ");
                    gestaoMat.trancarMatricula(teclado.nextLine());
                    Utilitarios.pausar(teclado); break;
                case 3: editarAluno(); break;
                
                case 4:
                    System.out.println("(TRANCADO -> ACTIVO)");
                    System.out.print("Matricula, BI ou nome do aluno: ");
                    gestaoMat.reingressarAluno(teclado.nextLine());
                    Utilitarios.pausar(teclado); break;
                case 5:
                    System.out.print("Numero de matricula a anular: ");
                    gestaoMat.anularMatricula(teclado.nextLine());
                    Utilitarios.pausar(teclado); break;
                case 6:
                    System.out.print("Nome, matricula ou BI: ");
                    Aluno a = gestaoMat.buscarAluno(teclado.nextLine());
                    if (a != null) {
                        System.out.println("Matricula : " + a.numeroMatricula);
                        System.out.println("Nome      : " + a.nomeCompleto);
                        System.out.println("BI        : " + a.idBI);
                        System.out.println("Classe    : " + a.classe + ".a Classe");
                        System.out.println("Curso     : " + gestaoMat.obterNomeCurso(a.idCurso));
                        System.out.println("Turma     : " + gestaoMat.obterNomeTurma(a.idTurma));
                        System.out.println("Situacao  : " + a.situacao);
                    } else { System.out.println("Aluno nao encontrado."); }
                    Utilitarios.pausar(teclado); break;
                case 7:
                    System.out.println("\n=== LISTA DE ALUNOS ===");
                    for (Aluno al : gestaoMat.listarAlunos()) {
                        System.out.println("Matricula: " + al.numeroMatricula
                                + " | " + al.nomeCompleto
                                + " | " + al.classe + ".a Classe"
                                + " | Curso: " + gestaoMat.obterNomeCurso(al.idCurso)
                                + " | Turma: " + gestaoMat.obterNomeTurma(al.idTurma)
                                + " | " + al.situacao);
                    }
                    Utilitarios.pausar(teclado); break;
                case 8: trocarTurmaAluno(); break;
                case 9:
                    System.out.print("Numero de Matricula do Aluno: ");
                    gestaoMat.gerarRelatorioAluno(teclado.nextLine(), "TXT");
                    Utilitarios.pausar(teclado); break;
            }
        } while (op != 0);
    }

    /**
     * REQ 2, 3, 4: Fluxo de matrícula com loops de validação e departamento dinâmico.
     */
    void matricularAluno() {
        System.out.println("\n--- NOVA MATRICULA ---");
        CalendarioMatricula cal = calendario.isEmpty() ? null : calendario.get(0);
        if (cal == null) {
            System.out.println("ATENCAO: Calendario de matriculas nao configurado.");
            Utilitarios.pausar(teclado); return;
        }
        LocalDate hoje = LocalDate.now();
        if (hoje.isAfter(cal.dataFimTotal)) {
            System.out.println("ATENCAO: Prazo de matriculas encerrado.");
            Utilitarios.pausar(teclado); return;
        }
        if (hoje.isAfter(cal.dataFimSemMulta)) {
            System.out.println("ATENCAO: Matricula com multa de 10%.");
        }

        // REQ 2 + REQ 3: loop BI (14 chars + não pode ser funcionário)
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
                System.out.println("Erro: Este BI ja esta registado como Funcionario/Admin. Insira o BI do aluno.");
                continue;
            }
            break;
        }

        System.out.print("Nome completo: ");
        String nome = teclado.nextLine();

        int idade;
        while (true) {
            System.out.print("Idade: ");
            idade = lerInteiro();
            if (idade >= 15) break;
            System.out.println("Erro: Idade minima e 15 anos. Tente novamente.");
        }

        // Seleccionar curso
        System.out.println("\n=== CURSOS DISPONIVEIS ===");
        listarCursos(gestaoMat.listarCursos());

        Curso cursoSel;
        while (true) {
            System.out.print("\nID do Curso: ");
            String idCurso = teclado.nextLine().trim();
            cursoSel = gestaoCursos.buscarCurso(idCurso);
            if (cursoSel != null) break;
            System.out.println("Erro: Curso nao encontrado. Tente novamente.");
        }

        // REQ 4: classes dependem do departamento
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
            System.out.println("Nenhuma turma com vagas para " + classe + ".a Classe neste curso.");
            Utilitarios.pausar(teclado); return;
        }
        System.out.println("\n=== TURMAS DISPONIVEIS — " + classe + ".a CLASSE ===");
        for (Turma t : turmasDisp) {
            System.out.println("  " + t.idTurma + " | " + t.nome
                    + " | Vagas: " + (t.vagas - t.vagasOcupadas));
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

        gestaoMat.matricularAluno(bi, nome, idade, cursoSel.idCurso, idTurma, classe, cal);
        Utilitarios.pausar(teclado);
    }

    // ===================== FINANCAS =====================
    void menuFinancas() {
        int op;
        do {
            Utilitarios.limparConsola();
            System.out.println("+--------------------------------+");
            System.out.println("|        GESTAO FINANCEIRA       |");
            System.out.println("+--------------------------------+");
            System.out.println("| 1. Mensalidades em Aberto      |");
            System.out.println("| 2. Pagar Propina               |");
            System.out.println("| 3. Pagar Emolumento            |");
            System.out.println("| 4. Situacao Financeira Aluno   |");
            System.out.println("| 5. Pagamento Anual (desc. 15%) |");
            System.out.println("| 6. Historico do Aluno          |");
            System.out.println("| 7. Relatorio Geral             |");
            System.out.println("| 0. Voltar                      |");
            System.out.println("+--------------------------------+");
            System.out.print("Opcao: ");
            op = lerInteiro();

            switch (op) {
                case 1:
                    System.out.print("Numero de Matricula: ");
                    ArrayList<Mensalidade> abertas = gestaoFin.verificarMensalidades(teclado.nextLine());
                    if (abertas.isEmpty()) { System.out.println("Nenhuma mensalidade em aberto."); }
                    else { for (Mensalidade m : abertas) System.out.println("  Mes " + m.numero + ": " + String.format("%.2f", m.valor) + " Kz"); }
                    Utilitarios.pausar(teclado); break;
                case 2:
                    System.out.print("Numero de Matricula: ");
                    String t2 = teclado.nextLine();
                    ArrayList<Mensalidade> ab2 = gestaoFin.verificarMensalidades(t2);
                    if (ab2.isEmpty()) { System.out.println("Todas as mensalidades ja pagas."); Utilitarios.pausar(teclado); break; }
                    System.out.println("Mensalidades em aberto:");
                    for (Mensalidade m : ab2) System.out.println("  Mes " + m.numero + ": " + String.format("%.2f", m.valor) + " Kz");
                    System.out.print("Numeros a pagar (ex: 1,2,3): ");
                    gestaoFin.registarPagamentoMultiplasMensalidades(t2, teclado.nextLine());
                    Utilitarios.pausar(teclado); break;
                case 3:
                    System.out.print("Numero de Matricula: ");
                    String t3 = teclado.nextLine();
                    for (Emolumento e : listaEmolumentos)
                    System.out.println(" " + e.idEmolumento + " | " + e.descricao
                    + " | " + String.format("%.2f", e.preco) + " Kz");
                    System.out.print("IDs a pagar (ex: EM0001,EM0002): ");
                    gestaoFin.registarPagamentoMultiplosEmolumentos(t3, teclado.nextLine());
                    Utilitarios.pausar(teclado); break;
                case 4:
                    System.out.print("Numero de Matricula: ");
                    System.out.println(gestaoFin.verificarSituacaoPropinas(teclado.nextLine()));
                    Utilitarios.pausar(teclado); break;
                case 5:
                    System.out.print("Numero de Matricula: ");
                    String termoAnual = teclado.nextLine();
                    gestaoFin.pagarPropinasAnualComDesconto(termoAnual, teclado);
                case 6:
                    System.out.print("Numero de Matricula: ");
                    gestaoFin.gerarHistoricoPagamentos(teclado.nextLine(), "TXT");
                    Utilitarios.pausar(teclado); break;
                case 7:
                    gestaoFin.gerarRelatorioGeralPagamentos("TXT");
                    Utilitarios.pausar(teclado); break;
            }
        } while (op != 0);
    }

    // ===================== CALENDARIO =====================
    void menuCalendario() {
        Utilitarios.limparConsola();
        System.out.println("+--------------------------------+");
        System.out.println("|     CALENDARIO DE MATRICULAS   |");
        System.out.println("+--------------------------------+");
        if (!calendario.isEmpty()) {
            CalendarioMatricula cal = calendario.get(0);
            System.out.println("Inicio        : " + Utilitarios.dataParaString(cal.dataInicio));
            System.out.println("Fim sem multa : " + Utilitarios.dataParaString(cal.dataFimSemMulta));
            System.out.println("Fim total     : " + Utilitarios.dataParaString(cal.dataFimTotal));
        } else {
            System.out.println("Nenhum calendario configurado.");
        }
        System.out.println("\n1. Definir novo calendario");
        System.out.println("0. Voltar");
        System.out.print("Opcao: ");
        int op = lerInteiro();
        if (op == 1) {
            LocalDate inicio;
            while (true) {
                System.out.print("Data de inicio (dd/MM/yyyy): ");
                inicio = Utilitarios.stringParaData(teclado.nextLine());
                if (inicio != null) break;
                System.out.println("Erro: Data invalida. Formato esperado: dd/MM/yyyy.");
            }
            CalendarioMatricula novo = Fabricas.criarCalendarioMatricula(inicio);
            System.out.print("Data fim sem multa (ENTER = inicio +1 mes): ");
            String fsmInput = teclado.nextLine().trim();
            if (!fsmInput.isEmpty()) {
                LocalDate fsm = Utilitarios.stringParaData(fsmInput);
                if (fsm != null) novo.dataFimSemMulta = fsm;
                else System.out.println("Data invalida. Usado padrao (+1 mes).");
            }
            System.out.print("Data fim total (ENTER = inicio +3 meses): ");
            String ftInput = teclado.nextLine().trim();
            if (!ftInput.isEmpty()) {
                LocalDate ft = Utilitarios.stringParaData(ftInput);
                if (ft != null) novo.dataFimTotal = ft;
                else System.out.println("Data invalida. Usado padrao (+3 meses).");
            }
            calendario.clear();
            calendario.add(novo);
            System.out.println("Calendario configurado.");
        }
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
        System.out.println("Aluno   : " + aluno.nomeCompleto);
        System.out.println("Curso   : " + gestaoMat.obterNomeCurso(aluno.idCurso));
        System.out.println("Turma   : " + aluno.idTurma);
        System.out.println("Classe  : " + aluno.classe + ".a Classe");

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

    // ===================== REQ 2: ELIMINAR TODOS OS DADOS =====================

    void eliminarTodosDados() {
        System.out.println("\n!!! AVISO CRITICO !!!");
        System.out.println("Esta operacao ira apagar PERMANENTEMENTE todos os alunos,");
        System.out.println("funcionarios, turmas e emolumentos do sistema.");
        System.out.println("Os cursos NAO serao afectados (sao dados estaticos).");
        System.out.print("\nDeseja continuar? (S/N): ");
        if (!teclado.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Operacao cancelada.");
            Utilitarios.pausar(teclado); return;
        }
        System.out.print("CONFIRMACAO FINAL: Escreva 'CONFIRMAR' para prosseguir: ");
        String confirmacao = teclado.nextLine().trim();
        if (!confirmacao.equals("CONFIRMAR")) {
            System.out.println("Confirmacao incorrecta. Operacao cancelada.");
            Utilitarios.pausar(teclado); return;
        }
        Persistencia.resetTotal(listaFuncionarios, listaTurmas,
                listaEmolumentos, listaAlunos, listaPagamentos, calendario, adminLogado);
        System.out.println("A sua conta de Administrador foi mantida. Pode continuar a usar o sistema.");
        Utilitarios.pausar(teclado);
    }
    
    void editarAluno() {
        System.out.println("\n--- EDITAR ALUNO ---");
        System.out.print("Matricula ou BI do aluno: ");
        String termo = teclado.nextLine();
        
        Aluno a = gestaoMat.buscarAluno(termo);
        if (a == null) {
            System.out.println("Aluno nao encontrado.");
            Utilitarios.pausar(teclado);
            return;
        }
        System.out.println("Aluno: " + a.nomeCompleto);
        System.out.println("Email atual: " + a.email);
        System.out.print("Novo email (ENTER para manter): ");
        String novoEmail = teclado.nextLine().trim();
        if (!novoEmail.isEmpty()) {
            if (Utilitarios.validarEmail(novoEmail)) {
                a.email = novoEmail.toLowerCase();
                System.out.println("Email actualizado.");
            } else {
                System.out.println("Erro: Email invalido. Nao alterado.");
            }
        }
        Utilitarios.pausar(teclado);
    }


    // ===================== HELPERS =====================
    int lerInteiro() {
        try { return Integer.parseInt(teclado.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    double parseDoubleSeguro(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { System.out.println("Valor invalido, campo nao alterado."); return 0; }
    }

    int parseInteiroSeguro(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { System.out.println("Valor invalido, campo nao alterado."); return 0; }
    }
}
