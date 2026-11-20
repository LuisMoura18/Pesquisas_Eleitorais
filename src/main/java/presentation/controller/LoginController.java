package presentation.controller;

import application.admin.AdminService;
import application.admin.SessaoUsuario;
import domain.exceptions.AcessoNegadoException;
import domain.exceptions.RegraDeNegocioException;
import domain.model.admin.Usuario;

public class LoginController {

    private final AdminService adminService;

    public LoginController(AdminService adminService) {
        this.adminService = adminService;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new RegraDeNegocioException("Preencha o e-mail e a senha.");
        }
        adminService.autenticar(email.trim(), senha);
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null) {
            throw new AcessoNegadoException("Não foi possível iniciar a sessão do usuário.");
        }
        return logado;
    }

    public void logout() {
        SessaoUsuario.getInstancia().encerrarSessao();
    }
}