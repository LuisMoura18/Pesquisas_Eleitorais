package presentation.controller;

import application.admin.AdminService;
import domain.exceptions.UsuarioInvalidoException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.StatusUsuario;
import domain.model.admin.Usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminFxController {

    private final AdminService adminService;
    private final Map<String, List<String>> parametrosGerais = new LinkedHashMap<>();

    public AdminFxController(AdminService adminService) {
        this.adminService = adminService;
        inicializarParametrosPadrao();
    }

    private void inicializarParametrosPadrao() {
        parametrosGerais.put("Região", new ArrayList<>(List.of("Norte", "Sul", "Centro", "Leste", "Oeste")));
        parametrosGerais.put("Faixa Etária", new ArrayList<>(List.of("16-24 anos", "25-34 anos", "35-44 anos", "45-59 anos", "60+ anos")));
        parametrosGerais.put("Escolaridade", new ArrayList<>(List.of("Fundamental", "Médio", "Superior", "Pós-graduação")));
        parametrosGerais.put("Faixa de Renda", new ArrayList<>(List.of("Até 2 SM", "2 a 5 SM", "5 a 10 SM", "Acima de 10 SM")));
    }

    public List<Usuario> listarUsuarios() {
        return adminService.listarUsuarios();
    }

    public List<Usuario> filtrarUsuarios(String nome, PerfilAcesso perfil, StatusUsuario status) {
        return listarUsuarios().stream()
                .filter(u -> nome == null || nome.isBlank()
                        || u.getNome().toLowerCase().contains(nome.trim().toLowerCase()))
                .filter(u -> perfil == null || u.getPerfis().contains(perfil))
                .filter(u -> status == null || u.getStatus() == status)
                .toList();
    }

    public void cadastrarUsuario(String nome, String email, String senha, Set<PerfilAcesso> perfis) {
        if (nome == null || nome.isBlank()) {
            throw new UsuarioInvalidoException("O nome é obrigatório.");
        }
        if (perfis == null || perfis.isEmpty()) {
            throw new UsuarioInvalidoException("Selecione pelo menos um perfil de acesso.");
        }
        adminService.cadastrarUsuario(nome.trim(), email.trim(), senha, perfis);
    }

    public void editarUsuario(String emailOriginal, String novoNome, Set<PerfilAcesso> novosPerfis) {
        if (novosPerfis == null || novosPerfis.isEmpty()) {
            throw new UsuarioInvalidoException("O usuário precisa ter ao menos um perfil.");
        }

        // 1. Atualiza o nome mantendo o e-mail original
        adminService.editarUsuario(emailOriginal, novoNome.trim(), emailOriginal);

        // 2. Busca os perfis atuais do usuário
        Usuario usuarioAtual = listarUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(emailOriginal))
                .findFirst()
                .orElseThrow(() -> new UsuarioInvalidoException("Usuário não encontrado."));

        Set<PerfilAcesso> perfisAtuais = new HashSet<>(usuarioAtual.getPerfis());

        // 3. Concede primeiro os perfis recém-marcados (evita zerar os perfis durante a troca)
        for (PerfilAcesso perfil : novosPerfis) {
            if (!perfisAtuais.contains(perfil)) {
                adminService.concederPerfil(emailOriginal, perfil);
            }
        }

        // 4. Revoga os perfis que foram desmarcados
        for (PerfilAcesso perfil : perfisAtuais) {
            if (!novosPerfis.contains(perfil)) {
                adminService.revogarPerfil(emailOriginal, perfil);
            }
        }
    }

    public void alternarStatus(Usuario usuario) {
        StatusUsuario novoStatus = (usuario.getStatus() == StatusUsuario.ATIVO)
                ? StatusUsuario.INATIVO
                : StatusUsuario.ATIVO;
        adminService.alterarStatusUsuario(usuario.getEmail(), novoStatus);
    }

    public void excluirUsuario(String email) {
        adminService.excluirUsuario(email);
    }

    public List<String> listarParametrosPorCategoria(String categoria) {
        return parametrosGerais.getOrDefault(categoria, List.of());
    }

    public void adicionarParametro(String categoria, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe um valor para o parâmetro.");
        }
        List<String> lista = parametrosGerais.computeIfAbsent(categoria, k -> new ArrayList<>());
        if (!lista.contains(valor.trim())) {
            lista.add(valor.trim());
        }
    }

    public void removerParametro(String categoria, String valor) {
        List<String> lista = parametrosGerais.get(categoria);
        if (lista != null) {
            lista.remove(valor);
        }
    }

    public String gerarResumoAdministrativo() {
        List<Usuario> todos = listarUsuarios();
        long ativos = todos.stream().filter(u -> u.getStatus() == StatusUsuario.ATIVO).count();
        long inativos = todos.stream().filter(u -> u.getStatus() == StatusUsuario.INATIVO).count();
        long admins = todos.stream().filter(u -> u.getPerfis().contains(PerfilAcesso.ADMINISTRADOR)).count();
        long pesquisadores = todos.stream().filter(u -> u.getPerfis().contains(PerfilAcesso.PESQUISADOR)).count();
        long analistas = todos.stream().filter(u -> u.getPerfis().contains(PerfilAcesso.ANALISTA)).count();

        return String.format(
                "Total de Usuários Cadastrados: %d%n"
                        + "• Usuários Ativos: %d%n"
                        + "• Usuários Inativos: %d%n%n"
                        + "Distribuição por Perfil:%n"
                        + "• Administradores: %d%n"
                        + "• Pesquisadores: %d%n"
                        + "• Analistas: %d",
                todos.size(), ativos, inativos, admins, pesquisadores, analistas
        );
    }
}