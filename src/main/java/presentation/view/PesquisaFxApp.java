package presentation.view;

import application.admin.AdminService;
import application.admin.SessaoUsuario;
import application.pesquisa.PesquisaService;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.Usuario;
import domain.repository.UsuarioRepository;
import infrastructure.persistance.UsuarioFileRepository;
import infrastructure.persistence.PesquisaRepositoryArquivo;
import javafx.application.Application;
import javafx.stage.Stage;
import presentation.controller.PesquisaFxController;
import java.util.Set;

public class PesquisaFxApp extends Application {
    private PesquisaService pesquisaService;

    @Override
    public void init() {
        //verifica se o usuário realmente é admin
        UsuarioRepository usuarioRepo = new UsuarioFileRepository();
        if (usuarioRepo.buscarPorEmail("admin@email.com").isEmpty()) {
            usuarioRepo.salvar(new Usuario(
                    "Administrador",
                    "admin@email.com",
                    "admin1234",
                    Set.of(PerfilAcesso.ADMINISTRADOR)
            ));
        }
        AdminService adminService = new AdminService(usuarioRepo);
        adminService.autenticar("admin@email.com", "admin1234");

        this.pesquisaService = new PesquisaService(
                new PesquisaRepositoryArquivo("dados/pesquisas.dat")
        );
    }
    //mostra a tela inicial da pesquisa
    @Override
    public void start(Stage primaryStage) {
        PesquisaFxController controller = new PesquisaFxController(pesquisaService);
        TelaConsultaPesquisa tela = new TelaConsultaPesquisa(controller);
        tela.exibir(primaryStage);
    }
    @Override
    public void stop() {
        SessaoUsuario.getInstancia().encerrarSessao();
    }
    public static void main(String[] args) {
        launch(args);
    }
}