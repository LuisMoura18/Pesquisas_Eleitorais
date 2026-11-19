package presentation.view;

import application.admin.AdminService;
import application.admin.SessaoUsuario;
import application.pesquisa.PesquisaService;
import domain.exceptions.PersistenciaException;
import application.analise.RelatorioController;
import domain.exceptions.RegraDeNegocioException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.Usuario;
import domain.repository.UsuarioRepository;
import infrastructure.persistance.UsuarioFileRepository;
import infrastructure.persistence.PesquisaRepositoryArquivo;

import java.io.File;
import java.util.Set;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Garante que a pasta dados/ exista antes de abrir os arquivos
            new File("dados").mkdirs();

            UsuarioRepository usuarios = new UsuarioFileRepository();
            garantirAdministradorInicial(usuarios);

            AdminService admin = new AdminService(usuarios);
            if (!fazerLogin(admin)) {
                System.out.println("Número máximo de tentativas excedido. Encerrando.");
                return;
            }

            PesquisaService pesquisas = new PesquisaService(
                    new PesquisaRepositoryArquivo("dados/pesquisas.dat"));
            RelatorioController relatorioController = new RelatorioController();
            menuPrincipal(admin, pesquisas, relatorioController);

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
        while (SessaoUsuario.getInstancia().getUsuarioLogado() == null) {
            System.out.println("\n===== LOGIN =====");
            System.out.println("[0] Encerrar Programa");
            String email = ler("E-mail: ");

            if (email.equals("0")) {
                return false;
            }

            String senha = ler("Senha: ");
            try {
                admin.autenticar(email, senha);
                System.out.println("Bem-vindo(a), "
                        + SessaoUsuario.getInstancia().getUsuarioLogado().getNome() + "!");
                return true;
            } catch (RegraDeNegocioException e) {
                System.out.println(">> " + e.getMessage());
            }
        }
        return true;
    }

    private static void menuPrincipal(AdminService admin, PesquisaService pesquisas, RelatorioController relatorioController) {
        String opcao;
        do {
            Set<PerfilAcesso> perfis = SessaoUsuario.getInstancia().getUsuarioLogado().getPerfis();
            boolean isAdmin = perfis.contains(PerfilAcesso.ADMINISTRADOR);
            boolean isAnalista = perfis.contains(PerfilAcesso.ANALISTA);

            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1 - Pesquisas");
            if (isAdmin)
                System.out.println("2 - Menu administrativo");
            if (isAdmin || isAnalista)
                System.out.println("3 - Análise e Relatórios");
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
                    case "3" -> {
                        if (!(isAdmin || isAnalista))
                            System.out.println("Opção inexistente.");
                        else new MenuAnalise(relatorioController, in).executar();
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