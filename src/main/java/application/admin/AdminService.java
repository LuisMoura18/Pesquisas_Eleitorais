package application.admin;

import domain.exceptions.AcessoNegadoException;
import domain.exceptions.UsuarioInvalidoException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.StatusUsuario;
import domain.model.admin.Usuario;
import domain.repository.UsuarioRepository;
import java.util.List;
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
            throw new AcessoNegadoException("Usuários inativos não podem acessar as funcionalidades do sistema.");
        }

        sessao.setUsuarioLogado(usuario);
    }

    public void cadastrarUsuario(String nome, String email, String senha, Set<PerfilAcesso> perfis) {
        verificarPermissaoAdministrador();
        validarDadosUsuario(email, senha);

        if (repository.buscarPorEmail(email).isPresent()) {
            throw new UsuarioInvalidoException("O e-mail já está associado a outro usuário.");
        }

        Usuario novoUsuario = new Usuario(nome, email, senha, perfis);
        repository.salvar(novoUsuario);
    }

    public void alterarStatusUsuario(String email, StatusUsuario novoStatus) {
        verificarPermissaoAdministrador();
        Usuario usuario = repository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioInvalidoException("Usuário não encontrado."));

        if (novoStatus == StatusUsuario.INATIVO) {
            if (ehUsuarioLogado(usuario))
                throw new UsuarioInvalidoException("Você não pode desativar o próprio usuário.");
            if (ehUltimoAdministradorAtivo(usuario))
                throw new UsuarioInvalidoException("Não é possível desativar o último administrador ativo.");
        }
        usuario.setStatus(novoStatus);
        repository.atualizar(usuario);
    }

    //Administração de usuários

    public List<Usuario> listarUsuarios() {
        verificarPermissaoAdministrador();
        return repository.listarTodos();
    }

    public void editarUsuario(String email, String novoNome, String novoEmail) {
        verificarPermissaoAdministrador();
        Usuario usuario = buscarUsuario(email);
        if (novoNome == null || novoNome.isBlank())
            throw new UsuarioInvalidoException("O nome é obrigatório.");
        if (!usuario.getEmail().equalsIgnoreCase(novoEmail)) {
            validarFormatoEmail(novoEmail);
            if (repository.buscarPorEmail(novoEmail).isPresent())
                throw new UsuarioInvalidoException("O e-mail já está associado a outro usuário.");
        }
        usuario.setNome(novoNome);
        usuario.setEmail(novoEmail);
        repository.atualizar(usuario);
    }

    public void excluirUsuario(String email) {
        verificarPermissaoAdministrador();
        Usuario usuario = buscarUsuario(email);
        if (ehUsuarioLogado(usuario))
            throw new UsuarioInvalidoException("Você não pode excluir o próprio usuário.");
        if (ehUltimoAdministradorAtivo(usuario))
            throw new UsuarioInvalidoException("Não é possível excluir o último administrador ativo.");
        repository.deletar(usuario);
    }

    public void concederPerfil(String email, PerfilAcesso perfil) {
        verificarPermissaoAdministrador();
        Usuario usuario = buscarUsuario(email);
        if (usuario.getPerfis().contains(perfil))
            throw new UsuarioInvalidoException("O usuário já possui o perfil " + perfil + ".");
        usuario.adicionarPerfil(perfil);
        repository.atualizar(usuario);
    }

    public void revogarPerfil(String email, PerfilAcesso perfil) {
        verificarPermissaoAdministrador();
        Usuario usuario = buscarUsuario(email);
        if (!usuario.getPerfis().contains(perfil))
            throw new UsuarioInvalidoException("O usuário não possui o perfil " + perfil + ".");
        if (usuario.getPerfis().size() == 1)
            throw new UsuarioInvalidoException("O usuário precisa ter ao menos um perfil.");
        if (perfil == PerfilAcesso.ADMINISTRADOR && ehUltimoAdministradorAtivo(usuario))
            throw new UsuarioInvalidoException("Não é possível remover o perfil do último administrador ativo.");
        usuario.removerPerfil(perfil);
        repository.atualizar(usuario);
    }

    private Usuario buscarUsuario(String email) {
        return repository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioInvalidoException("Usuário não encontrado."));
    }

    private boolean ehUsuarioLogado(Usuario usuario) {
        Usuario logado = sessao.getUsuarioLogado();
        return logado != null && logado.getId().equals(usuario.getId());
    }

    private boolean ehUltimoAdministradorAtivo(Usuario usuario) {
        if (!usuario.getPerfis().contains(PerfilAcesso.ADMINISTRADOR)) return false;
        long outros = repository.listarTodos().stream()
                .filter(u -> !u.getId().equals(usuario.getId()))
                .filter(u -> u.getStatus() == StatusUsuario.ATIVO)
                .filter(u -> u.getPerfis().contains(PerfilAcesso.ADMINISTRADOR))
                .count();
        return outros == 0;
    }

    private void validarFormatoEmail(String email) {
        if (email == null || !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email))
            throw new UsuarioInvalidoException("Formato de e-mail inválido.");
    }

    private void validarDadosUsuario(String email, String senha) {
        //validação do email
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new UsuarioInvalidoException("Formato de e-mail inválido.");
        }

        //Validação da senha
        if (senha.length() < 8 || !senha.matches(".*[a-zA-Z]+.*") || !senha.matches(".*[0-9]+.*")) {
            throw new UsuarioInvalidoException("A senha deve conter no mínimo 8 caracteres, incluindo letras e números.");
        }
    }

    private void verificarPermissaoAdministrador() {
        Usuario logado = sessao.getUsuarioLogado();
        if (logado == null || !logado.getPerfis().contains(PerfilAcesso.ADMINISTRADOR)) {
            throw new AcessoNegadoException("Funcionalidade restrita a Administradores.");
        }
    }
}