package servicos;

import entidades.*;
import java.time.LocalDate;

public class Fabricas {

    public static Mensalidade criarMensalidade(int numero, double valor) {
        Mensalidade m = new Mensalidade();
        m.numero = numero;
        m.valor = valor;
        m.pago = false;
        m.dataPagamento = null;
        return m;
    }

    public static Aluno criarAluno(String numeroMatricula, String idBI, String nomeCompleto, int idade,
        String idCurso, String idTurma, int classe,
        double multaAplicada, LocalDate dataMatricula, double valorPropina) {
        Aluno a = new Aluno();
        a.numeroMatricula = numeroMatricula;
        a.idBI = idBI;
        a.nomeCompleto = nomeCompleto;
        a.idade = idade;
        a.idCurso = idCurso;
        a.idTurma = idTurma;
        a.classe = classe;
        a.situacao = "INACTIVO";
        a.multaAplicada = multaAplicada;
        a.dataMatricula = dataMatricula;
        a.email = ""; // Inicializa vazio
        a.mensalidades = new Mensalidade[10];
        double valorMes = valorPropina + multaAplicada;
        for (int i = 0; i < 10; i++) {
            a.mensalidades[i] = criarMensalidade(i + 1, valorMes);
        }
        return a;
    }

    public static Aluno criarAluno(String idBI, String nomeCompleto, int idade,
                                   String idCurso, String idTurma, int classe,
                                   double multaAplicada, LocalDate dataMatricula, double valorPropina) {
        return criarAluno(null, idBI, nomeCompleto, idade, idCurso, idTurma, classe,
                multaAplicada, dataMatricula, valorPropina);
    }

    public static Curso criarCurso(String idCurso, String nome, double valorPropina, String departamento, int classeMaxima) {
        Curso c = new Curso();
        c.idCurso = idCurso;
        c.nome = nome;
        c.valorPropina = valorPropina;
        c.departamento = departamento;
        c.classeMaxima = classeMaxima;
        return c;
    }

    public static Curso criarCurso(String idCurso, String nome, double valorPropina) {
        return criarCurso(idCurso, nome, valorPropina, inferirDepartamentoCurso(idCurso), 12);
    }

    public static String inferirDepartamentoCurso(String idCurso) {
        if (idCurso == null) return CursosEstaticos.DEPT_PUNIV;
        switch (idCurso.toUpperCase()) {
            case "INF": case "CTB": case "CIV": case "QUI":
            case "MEC": case "GEP": case "PET":
                return CursosEstaticos.DEPT_TECNICOS;
            default:
                return CursosEstaticos.DEPT_PUNIV;
        }
    }

    public static Turma criarTurma(String idTurma, String nome, String idCurso, int vagas, int classe) {
        Turma t = new Turma();
        t.idTurma = idTurma;
        t.nome = nome;
        t.idCurso = idCurso;
        t.vagas = vagas;
        t.classe = classe;
        t.vagasOcupadas = 0;
        return t;
    }

    public static Funcionario criarFuncionario(int id, String nome, String bi, String nivelAcesso, String senha) {
        return criarFuncionario(id, String.format("FUNC%03d", id), nome, bi, "", nivelAcesso, senha);
    }

    public static Funcionario criarFuncionario(int id, String codigoFuncionario, String nome, String bi,
                                               String emailInstitucional, String nivelAcesso, String senha) {
        return criarFuncionario(id, codigoFuncionario, nome, bi, emailInstitucional, NivelAcesso.fromTexto(nivelAcesso), senha);
    }

    public static Funcionario criarFuncionario(int id, String codigoFuncionario, String nome, String bi,
                                               String emailInstitucional, int nivelAcesso, String senha) {
        Funcionario f = new Funcionario();
        f.id = id;
        f.codigoFuncionario = codigoFuncionario;
        f.nome = nome;
        f.bi = bi;
        f.emailInstitucional = emailInstitucional;
        f.nivelAcesso = nivelAcesso;
        f.senha = senha;
        return f;
    }

    public static Emolumento criarEmolumento(String idEmolumento, String descricao, double preco) {
        Emolumento e = new Emolumento();
        e.idEmolumento = idEmolumento;
        e.descricao = descricao;
        e.preco = preco;
        return e;
    }

    public static PagamentoEmolumento criarPagamentoEmolumento(String idAluno, String idEmolumento, double valorPago, LocalDate dataPagamento) {
        PagamentoEmolumento p = new PagamentoEmolumento();
        p.idAluno = idAluno;
        p.idEmolumento = idEmolumento;
        p.valorPago = valorPago;
        p.dataPagamento = dataPagamento;
        return p;
    }

    public static Comprovativo criarComprovativo(String idAluno, String nomeAluno, String tipoDocumento,
                                                 double totalPago, LocalDate dataEmissao, String[] itensPagos) {
        Comprovativo c = new Comprovativo();
        c.idAluno = idAluno;
        c.nomeAluno = nomeAluno;
        c.tipoDocumento = tipoDocumento;
        c.totalPago = totalPago;
        c.dataEmissao = dataEmissao;
        c.itensPagos = itensPagos;
        return c;
    }

    public static CalendarioMatricula criarCalendarioMatricula(LocalDate dataInicio) {
        CalendarioMatricula c = new CalendarioMatricula();
        c.dataInicio = dataInicio;
        c.dataFimSemMulta = dataInicio.plusMonths(1);
        c.dataFimTotal = dataInicio.plusMonths(3);
        return c;
    }
}
