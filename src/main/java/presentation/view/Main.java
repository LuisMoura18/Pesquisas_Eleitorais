package presentation.view;

import application.admin.AdminService;
import application.admin.SessaoUsuario;
import application.pesquisa.PesquisaService;
import domain.exceptions.PersistenciaException;
import domain.exceptions.RegraDeNegocioException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.Usuario;
import domain.repository.UsuarioRepository;
import infrastructure.persistance.UsuarioFileRepository;
import infrastructure.persistence.PesquisaRepositoryArquivo;

import java.util.Set;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            UsuarioRepository usuarios = new UsuarioFileRepository();
            garantirAdministradorInicial(usuarios);

            AdminService admin = new AdminService(usuarios);
            if (!fazerLogin(admin)) {
                System.out.println("Número máximo de tentativas excedido. Encerrando.");
                return;
            }

            PesquisaService pesquisas = new PesquisaService(
                    new PesquisaRepositoryArquivo("dados/pesquisas.dat"));
            menuPrincipal(admin, pesquisas);

            SessaoUsuario.getInstancia().encerrarSessao();
        } catch (PersistenciaException e) {
            System.out.println("Erro ao acessar o arquivo de dados: " + e.getMessage());
        }
    }

    //para cadastrar precisa ter um admin pré-existente
    private static void garantirAdministradorInicial(UsuarioRepository usuarios) {
        if (usuarios.buscarPorEmail("admin@email.com").isEmpty()) {
            usuarios.salvar(new Usuario("Administrador", "admin@email.com", "admin1234",
                    Set.of(PerfilAcesso.ADMINISTRADOR)));
        }
    }

    private static boolean fazerLogin(AdminService admin) {
        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            System.out.println("\n===== LOGIN =====");
            String email = ler("E-mail: ");
            String senha = ler("Senha: ");
            try {
                admin.autenticar(email, senha);
                System.out.println("Bem-vindo(a), " + SessaoUsuario.getInstancia().getUsuarioLogado().getNome() + "!");
                return true;
            } catch (RegraDeNegocioException e) {
                System.out.println(">> " + e.getMessage());
            }
        }
        return false;
    }

    private static void menuPrincipal(AdminService admin, PesquisaService pesquisas) {
        String opcao;
        do {
            Set<PerfilAcesso> perfis = SessaoUsuario.getInstancia().getUsuarioLogado().getPerfis();
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1 - Pesquisas");
            if (perfis.contains(PerfilAcesso.ADMINISTRADOR))
                System.out.println("2 - Menu administrativo");
            System.out.println("0 - Sair");
            opcao = ler("Opção: ");
            try {
                switch (opcao) {
                    case "1" -> new MenuPesquisa(pesquisas, in).executar();
                    case "2" -> {
                        if (!perfis.contains(PerfilAcesso.ADMINISTRADOR))
                            System.out.println("Opção inexistente.");
                        else new MenuAdmin(admin, in).executar();
                    }
                    case "0" -> System.out.println("Encerrando...");
                    default -> System.out.println("Opção inexistente.");
                }
            } catch (RegraDeNegocioException e) {
                System.out.println(">> Aviso: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(">> Valor inválido. Confira o que foi digitado.");
            }
        } while (!opcao.equals("0"));
    }

    private static String ler(String mensagem) {
        System.out.print(mensagem);
        return in.nextLine().trim();
    }
}