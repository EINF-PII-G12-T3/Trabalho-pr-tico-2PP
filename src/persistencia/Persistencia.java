package persistencia;


import servicos.Fabricas;
import entidades.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Persistencia {

    public static final String ARQUIVO_DADOS       = "dados_sgmf.txt";
    public static final String ARQUIVO_DADOS_INICIO = "dados_inicio.txt";

    public static void gravarDados(ArrayList<Funcionario> funcionarios,
                                   ArrayList<Curso>       cursos,
                                   ArrayList<Turma>       turmas,
                                   ArrayList<Emolumento>  emolumentos,
                                   ArrayList<Aluno>       alunos,
                                   ArrayList<PagamentoEmolumento> pagamentos,
                                   ArrayList<CalendarioMatricula> calendario) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_DADOS));

            pw.println("[FUNCIONARIOS]");
            for (Funcionario f : funcionarios) {
                pw.println(f.id + "|" + f.codigoFuncionario + "|" + f.nome + "|"
                        + f.bi + "|" + f.emailInstitucional + "|"
                        + NivelAcesso.nome(f.nivelAcesso) + "|" + f.senha);
            }

            // Cursos: gravar departamento e classeMaxima
            pw.println("[CURSOS]");
            for (Curso c : cursos) {
                pw.println(c.idCurso + "|" + c.nome + "|"
                        + c.valorPropina + "|" + c.departamento + "|" + c.classeMaxima);
            }

            pw.println("[TURMAS]");
            for (Turma t : turmas) {
                pw.println(t.idTurma + "|" + t.nome + "|" + t.idCurso + "|"
                        + t.vagas + "|" + t.classe + "|" + t.vagasOcupadas);
            }

            pw.println("[EMOLUMENTOS]");
            for (Emolumento e : emolumentos) {
                pw.println(e.idEmolumento + "|" + e.descricao + "|" + e.preco);
            }

            // Alunos: incluir campo 'classe' (posição base+5 no novo formato)
            pw.println("[ALUNOS]");
            for (Aluno a : alunos) {
                
                pw.print(a.numeroMatricula + "|" + a.idBI + "|" + a.nomeCompleto + "|" + a.idade + 
                            "|" + a.idCurso + "|" + a.idTurma + "|" + a.classe + "|" + a.situacao + 
                            "|" + a.multaAplicada + "|" + a.dataMatricula + "|" + (a.email != null ? a.email : ""));

                for (Mensalidade m : a.mensalidades) {
                    pw.print("|" + m.numero + "," + m.valor + "," + m.pago + ","
                            + (m.dataPagamento != null ? m.dataPagamento : "null"));
                }
                pw.println();
            }

            pw.println("[PAGAMENTOS_EMOL]");
            for (PagamentoEmolumento pe : pagamentos) {
                pw.println(pe.idAluno + "|" + pe.idEmolumento + "|"
                        + pe.valorPago + "|" + pe.dataPagamento);
            }

            pw.println("[CALENDARIO]");
            for (CalendarioMatricula cal : calendario) {
                pw.println(cal.dataInicio + "|" + cal.dataFimSemMulta + "|" + cal.dataFimTotal);
            }

            pw.close();
            System.out.println("Dados gravados.");
        } catch (IOException e) {
            System.out.println("Erro ao gravar dados: " + e.getMessage());
        }
    }

    public static void carregarDados(ArrayList<Funcionario> funcionarios, ArrayList<Curso> cursos,
                                     ArrayList<Turma> turmas, ArrayList<Emolumento> emolumentos,
                                     ArrayList<Aluno> alunos, ArrayList<PagamentoEmolumento> pagamentos,
                                     ArrayList<CalendarioMatricula> calendario) {
        carregarDadosDeArquivo(ARQUIVO_DADOS, funcionarios, cursos, turmas, emolumentos, alunos, pagamentos, calendario);
    }

    public static void carregarDadosIniciais(ArrayList<Funcionario> funcionarios, ArrayList<Curso> cursos,
                                             ArrayList<Turma> turmas, ArrayList<Emolumento> emolumentos,
                                             ArrayList<Aluno> alunos, ArrayList<PagamentoEmolumento> pagamentos,
                                             ArrayList<CalendarioMatricula> calendario) {
        carregarDadosDeArquivo(ARQUIVO_DADOS_INICIO, funcionarios, cursos, turmas, emolumentos, alunos, pagamentos, calendario);
    }

    // ===================== helpers de deduplicação =====================

    public static boolean existeFuncionario(ArrayList<Funcionario> list, String bi) {
        for (Funcionario f : list) if (f.bi.equalsIgnoreCase(bi)) return true;
        return false;
    }
    public static boolean existeCurso(ArrayList<Curso> list, String id) {
        for (Curso c : list) if (c.idCurso.equalsIgnoreCase(id)) return true;
        return false;
    }
    public static boolean existeTurma(ArrayList<Turma> list, String id) {
        for (Turma t : list) if (t.idTurma.equalsIgnoreCase(id)) return true;
        return false;
    }
    public static boolean existeEmolumento(ArrayList<Emolumento> list, String id) {
        for (Emolumento e : list) if (e.idEmolumento.equalsIgnoreCase(id)) return true;
        return false;
    }
    public static boolean existeAluno(ArrayList<Aluno> list, String mat, String bi) {
        for (Aluno a : list) {
            if ((a.numeroMatricula != null && a.numeroMatricula.equalsIgnoreCase(mat))
                    || a.idBI.equalsIgnoreCase(bi)) return true;
        }
        return false;
    }
    public static boolean existePagamento(ArrayList<PagamentoEmolumento> list, String idAluno, String idEmol) {
        for (PagamentoEmolumento p : list)
            if (p.idAluno.equalsIgnoreCase(idAluno) && p.idEmolumento.equalsIgnoreCase(idEmol)) return true;
        return false;
    }
    public static boolean existeCalendario(ArrayList<CalendarioMatricula> list, LocalDate inicio) {
        for (CalendarioMatricula c : list) if (c.dataInicio.equals(inicio)) return true;
        return false;
    }

    public static String gerarNumeroMatricula(ArrayList<Aluno> alunos) {
        String numero;
        boolean existe;
        do {
            numero = String.format("20%06d", (int)(Math.random() * 1000000));
            existe = false;
            for (Aluno a : alunos) {
                if (numero.equals(a.numeroMatricula)) { existe = true; break; }
            }
        } while (existe);
        return numero;
    }

    // ===================== leitura do ficheiro =====================

    public static void carregarDadosDeArquivo(String nomeArquivo,
            ArrayList<Funcionario> funcionarios, ArrayList<Curso> cursos,
            ArrayList<Turma> turmas, ArrayList<Emolumento> emolumentos,
            ArrayList<Aluno> alunos, ArrayList<PagamentoEmolumento> pagamentos,
            ArrayList<CalendarioMatricula> calendario) {

        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            System.out.println("Ficheiro nao encontrado: " + nomeArquivo);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            String secao = "";

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                if (linha.startsWith("[")) { secao = linha; continue; }

                String[] p = linha.split("\\|");

                switch (secao) {
                    case "[FUNCIONARIOS]":
                        if (p.length >= 7 && !existeFuncionario(funcionarios, p[3])) {
                            funcionarios.add(Fabricas.criarFuncionario(
                                    Integer.parseInt(p[0]), p[1], p[2], p[3], p[4], p[5], p[6]));
                        } else if (p.length >= 5 && !existeFuncionario(funcionarios, p[2])) {
                            funcionarios.add(Fabricas.criarFuncionario(
                                    Integer.parseInt(p[0]), p[1], p[2], p[3], p[4]));
                        }
                        break;

                    case "[CURSOS]":
                        // Cursos são carregados exclusivamente de cursos.txt.
                        // Ignorar esta secção para evitar duplicados com departamentos antigos.
                        break;

                    case "[TURMAS]":
                        if (p.length >= 6 && !existeTurma(turmas, p[0])) {
                            Turma t = Fabricas.criarTurma(p[0], p[1], p[2],
                                    Integer.parseInt(p[3]), Integer.parseInt(p[4]));
                            t.vagasOcupadas = Integer.parseInt(p[5]);
                            turmas.add(t);
                        }
                        break;

                    case "[EMOLUMENTOS]":
                        if (p.length >= 3 && !existeEmolumento(emolumentos, p[0])) {
                            emolumentos.add(Fabricas.criarEmolumento(p[0], p[1], Double.parseDouble(p[2])));
                        }
                        break;

                    case "[ALUNOS]":
                        carregarAluno(p, alunos);
                        break;

                    case "[PAGAMENTOS_EMOL]":
                        if (p.length >= 4 && !existePagamento(pagamentos, p[0], p[1])) {
                            pagamentos.add(Fabricas.criarPagamentoEmolumento(
                                    p[0], p[1], Double.parseDouble(p[2]), LocalDate.parse(p[3])));
                        }
                        break;

                    case "[CALENDARIO]":
                        if (p.length >= 1) {
                            LocalDate dataInicio = LocalDate.parse(p[0]);
                            if (!existeCalendario(calendario, dataInicio)) {
                                CalendarioMatricula cal = Fabricas.criarCalendarioMatricula(dataInicio);
                                if (p.length >= 3) {
                                    cal.dataFimSemMulta = LocalDate.parse(p[1]);
                                    cal.dataFimTotal = LocalDate.parse(p[2]);
                                }
                                calendario.add(cal);
                            }
                        }
                        break;
                }
            }
            System.out.println("Dados carregados de " + nomeArquivo + ".");
        } catch (IOException e) {
            System.out.println("ERRO CRITICO: Nao foi possivel ler o ficheiro " + nomeArquivo + ".");
            System.out.println("Detalhe: " + e.getMessage());
            System.out.println("O sistema iniciara com os dados que foram carregados ate ao momento.");
        } catch (Exception e) {
            System.out.println("ERRO: Ficheiro de dados corrompido ou formato invalido.");
            System.out.println("Detalhe: " + e.getMessage());
            System.out.println("Recomendacao: Verifique o ficheiro " + nomeArquivo + " ou elimine-o para reiniciar.");
}

    }

    /**
     * Carrega um registo de Aluno, suportando dois formatos:
     *
     * NOVO (v4+): mat|bi|nome|idade|idCurso|idTurma|CLASSE|situacao|multa|dataMat|mens…
     *   — campo CLASSE na posição 6 (índice 6)
     *
     * LEGADO (v3):  mat|bi|nome|idade|idCurso|idTurma|situacao|multa|dataMat|mens…
     *   — sem campo CLASSE; situacao na posição 6
     *
     * Distinção: no novo formato, p[6] é um inteiro (10/11/12/13);
     *            no legado, p[6] é uma string ("ACTIVO", "INACTIVO", etc.)
     */
    public static void carregarAluno(String[] p, ArrayList<Aluno> alunos) {
        try {
            // Mínimo: mat(0)+bi(1)+nome(2)+idade(3)+idCurso(4)+idTurma(5)+???+situacao+multa+data+10 mens = 19
            if (p.length < 19) return;

            String numeroMatricula = p[0];
            String bi   = p[1];
            String nome = p[2];
            int    idade  = Integer.parseInt(p[3]);
            String idCurso = p[4];
            String idTurma = p[5];

            int    classe;
            String situacao;
            double multa;
            LocalDate dataMat;
            int    mensOffset; // índice do primeiro bloco de mensalidade
            
            


            // Detectar formato: p[6] inteiro = novo formato com classe
            boolean novoFormato = p[6].matches("\\d+") && (p[6].equals("10") || p[6].equals("11")
                    || p[6].equals("12") || p[6].equals("13"));

            if (novoFormato) {
                classe    = Integer.parseInt(p[6]);
                situacao  = p[7];
                multa     = Double.parseDouble(p[8]);
                dataMat   = LocalDate.parse(p[9]);
                mensOffset = 10;
            } else {
                // Legado: sem campo classe — inferir a partir da turma (não disponível aqui, usa 10 como default)
                classe    = 10;
                situacao  = p[6];
                multa     = Double.parseDouble(p[7]);
                dataMat   = LocalDate.parse(p[8]);
                mensOffset = 9;
            }

            String email = "";
            if (p.length > mensOffset && !ehMensalidadeSerializada(p[mensOffset])) {
                email = p[mensOffset];
                mensOffset++;
            }

            if (p.length < mensOffset + 10) return;
            if (existeAluno(alunos, numeroMatricula, bi)) return;

            String[] mDados0 = p[mensOffset].split(",");
            double valorBase = Double.parseDouble(mDados0[1]) - multa;
            

            Aluno a = Fabricas.criarAluno(numeroMatricula, bi, nome, idade, idCurso, idTurma,
                    classe, multa, dataMat, valorBase);
            a.situacao = situacao;
            a.email = email;

            for (int i = 0; i < 10; i++) {
                String[] md = p[mensOffset + i].split(",");
                a.mensalidades[i].valor = Double.parseDouble(md[1]);
                a.mensalidades[i].pago = Boolean.parseBoolean(md[2]);
                if (!md[3].equals("null")) {
                    a.mensalidades[i].dataPagamento = LocalDate.parse(md[3]);
                }
            }
            alunos.add(a);
        } catch (Exception e) {
            System.out.println("Aviso: Registo de aluno ignorado (dados incompletos ou corrompidos).");
            System.out.println("  Causa: " + e.getMessage());
            System.out.println("  Dados: " + (p.length > 0 ? p[0] : "desconhecido"));
}

    }

    private static boolean ehMensalidadeSerializada(String valor) {
        if (valor == null) return false;
        String[] partes = valor.split(",");
        return partes.length >= 4 && partes[0].matches("\\d+");
    }

    // ===================== REQ 1: cursos.txt =====================

    public static final String ARQUIVO_CURSOS = "cursos.txt";

    /**
     * Carrega cursos do ficheiro cursos.txt (estático, nunca apagado no reset).
     * Suporta linhas de secção [DET]/[DCSA] e comentários com #.
     */
    public static void carregarCursos(ArrayList<Curso> cursos) {
        File f = new File(ARQUIVO_CURSOS);
        if (!f.exists()) {
            System.out.println("Aviso: cursos.txt nao encontrado. Usando cursos em memoria.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#") || linha.startsWith("[")) continue;
                String[] p = linha.split("\\|");
                if (p.length >= 5 && !existeCurso(cursos, p[0])) {
                    cursos.add(Fabricas.criarCurso(p[0], p[1], Double.parseDouble(p[2]), p[3], Integer.parseInt(p[4])));
                }
            }
            System.out.println("Cursos carregados de cursos.txt.");
        } catch (IOException e) {
            System.out.println("Erro ao carregar cursos.txt: " + e.getMessage());
        }
    }

    /**
     * Grava as propinas actualizadas de volta em cursos.txt,
     * preservando a estrutura de secções.
     */
    public static void gravarCursos(ArrayList<Curso> cursos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_CURSOS))) {
            pw.println("# Cursos estaticos do SGMF — NAO ELIMINAR ESTE FICHEIRO");
            pw.println("# Formato: ID|Nome|Propina|Departamento|ClasseMaxima");
            pw.println("[CURSOS TECNICOS]");
            for (Curso c : cursos) {
                if ("CURSOS TECNICOS".equals(c.departamento)) {
                    pw.println(c.idCurso + "|" + c.nome + "|"
                            + c.valorPropina + "|" + c.departamento + "|" + c.classeMaxima);
                }
            }
            pw.println("[PUNIV]");
            for (Curso c : cursos) {
                if ("PUNIV".equals(c.departamento)) {
                    pw.println(c.idCurso + "|" + c.nome + "|"
                            + c.valorPropina + "|" + c.departamento + "|" + c.classeMaxima);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao gravar cursos.txt: " + e.getMessage());
        }
    }

    // ===================== REQ 2: Reset Total =====================

    /**
     * Apaga todos os dados operacionais (funcionarios, turmas, alunos, emolumentos,
     * pagamentos, calendario). Cursos NAO sao apagados.
     * As listas em memoria sao esvaziadas e dados_sgmf.txt e sobrescrito vazio.
     */
    public static void resetTotal(ArrayList<Funcionario> funcionarios,
                                   ArrayList<Turma>       turmas,
                                   ArrayList<Emolumento>  emolumentos,
                                   ArrayList<Aluno>       alunos,
                                   ArrayList<PagamentoEmolumento> pagamentos,
                                   ArrayList<CalendarioMatricula> calendario,
                                   Funcionario adminLogado) {

        // Preservar apenas os administradores (nunca apagar quem está logado)
        ArrayList<Funcionario> adminsAPreservar = new ArrayList<>();
        for (Funcionario f : funcionarios) {
            if (f.nivelAcesso == NivelAcesso.ADMIN) {
                adminsAPreservar.add(f);
            }
        }
        // Se por algum motivo nenhum admin foi encontrado, preservar o logado
        if (adminsAPreservar.isEmpty() && adminLogado != null) {
            adminsAPreservar.add(adminLogado);
        }

        funcionarios.clear();
        funcionarios.addAll(adminsAPreservar);
        turmas.clear();
        emolumentos.clear();
        alunos.clear();
        pagamentos.clear();
        calendario.clear();

        // Gravar ficheiro com apenas os admins preservados
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO_DADOS))) {
            pw.println("[FUNCIONARIOS]");
            for (Funcionario f : adminsAPreservar) {
                pw.println(f.id + "|" + f.codigoFuncionario + "|" + f.nome + "|"
                        + f.bi + "|" + f.emailInstitucional + "|"
                        + NivelAcesso.nome(f.nivelAcesso) + "|" + f.senha);
            }
            pw.println("[CURSOS]");
            pw.println("[TURMAS]");
            pw.println("[EMOLUMENTOS]");
            pw.println("[ALUNOS]");
            pw.println("[PAGAMENTOS_EMOL]");
            pw.println("[CALENDARIO]");
        } catch (IOException e) {
            System.out.println("Erro ao limpar ficheiro de dados: " + e.getMessage());
        }
        System.out.println("Reset concluido. " + adminsAPreservar.size()
                + " conta(s) de Administrador preservada(s).");
    }
}
