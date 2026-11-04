package application.admin;

import domain.exceptions.AcessoNegadoException;
import domain.exceptions.UsuarioInvalidoException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.StatusUsuario;
import domain.model.admin.Usuario;
import domain.repository.UsuarioRepository;
import java.util.Set;
import java.util.regex.Pattern;

public class AdminService {
    private final UsuarioRepository repository;
    private final SessaoUsuario sessao = SessaoUsuario.getInstancia();

    public AdminService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void autenticar(String email, String senha) {
        Usuario usuario = repository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioInvalidoException("E-mail não encontrado."));

        if (!usuario.getSenha().equals(senha)) {
            throw new UsuarioInvalidoException("Senha incorreta.");
        }

        if (usuario.getStatus() == StatusUsuario.INATIVO) {
            throw new AcessoNegadoException("Usuários inativos não podem acessar as funcionalidades do sistema."); //[cite: 4, 17]
        }

        sessao.setUsuarioLogado(usuario);
    }

    public void cadastrarUsuario(String nome, String email, String senha, Set<PerfilAcesso> perfis) {
        verificarPermissaoAdministrador(); // Apenas administradores podem criar contas[cite: 4, 17]
        validarDadosUsuario(email, senha);

        if (repository.buscarPorEmail(email).isPresent()) {
            throw new UsuarioInvalidoException("O e-mail já está associado a outro usuário."); //[cite: 4, 17]
        }

        Usuario novoUsuario = new Usuario(nome, email, senha, perfis);
        repository.salvar(novoUsuario);
    }

    public void alterarStatusUsuario(String email, StatusUsuario novoStatus) {
        verificarPermissaoAdministrador();
        Usuario usuario = repository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioInvalidoException("Usuário não encontrado."));

        usuario.setStatus(novoStatus);
        repository.atualizar(usuario);
    }

    private void validarDadosUsuario(String email, String senha) {
        // Validação de formato de E-mail[cite: 2, 15]
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new UsuarioInvalidoException("Formato de e-mail inválido."); //[cite: 4, 17]
        }

        // Validação de Senha (mínimo 8 caracteres, letras e números)[cite: 4, 17]
        if (senha.length() < 8 || !senha.matches(".*[a-zA-Z]+.*") || !senha.matches(".*[0-9]+.*")) {
            throw new UsuarioInvalidoException("A senha deve conter no mínimo 8 caracteres, incluindo letras e números."); //[cite: 4, 17]
        }
    }

    private void verificarPermissaoAdministrador() {
        Usuario logado = sessao.getUsuarioLogado();
        if (logado == null || !logado.getPerfis().contains(PerfilAcesso.ADMINISTRADOR)) {
            throw new AcessoNegadoException("Funcionalidade restrita a Administradores."); //[cite: 4, 17]
        }
    }
}
