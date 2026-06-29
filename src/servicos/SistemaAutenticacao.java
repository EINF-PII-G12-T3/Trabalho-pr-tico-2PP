package servicos;

import entidades.Funcionario;
import entidades.NivelAcesso;
import java.util.ArrayList;

public class SistemaAutenticacao {

    private static ArrayList<Funcionario> funcionarios;

    public SistemaAutenticacao(ArrayList<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public static Funcionario login(String usuario, String senha) {
        for (Funcionario f : funcionarios) {
            boolean usuarioValido = f.codigoFuncionario.equalsIgnoreCase(usuario)
                    || f.emailInstitucional.equalsIgnoreCase(usuario);

            if (usuarioValido && f.senha.equals(senha)) {
                return f;
            }
        }
        return null;
    }

    public static String obterNivelAcesso(String usuario) {
        for (Funcionario f : funcionarios) {
            if (f.codigoFuncionario.equalsIgnoreCase(usuario)
                    || f.emailInstitucional.equalsIgnoreCase(usuario)) {
                return NivelAcesso.nome(f.nivelAcesso);
            }
        }
        return null;
    }
}

