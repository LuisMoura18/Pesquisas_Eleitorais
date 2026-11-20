package presentation.view;

import application.admin.AdminService;
import application.analise.RelatorioController;
import application.pesquisa.PesquisaService;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.Usuario;
import domain.repository.UsuarioRepository;
import infrastructure.persistance.UsuarioFileRepository;
import infrastructure.persistence.PesquisaRepositoryArquivo;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.Set;

public class SistemaFxApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        new File("dados").mkdirs();

        UsuarioRepository usuarioRepository = new UsuarioFileRepository();
        if (usuarioRepository.buscarPorEmail("admin@email.com").isEmpty()) {
            usuarioRepository.salvar(new Usuario(
                    "Administrador",
                    "admin@email.com",
                    "admin1234",
                    Set.of(PerfilAcesso.ADMINISTRADOR)
            ));
        }

        AdminService adminService = new AdminService(usuarioRepository);
        PesquisaService pesquisaService = new PesquisaService(
                new PesquisaRepositoryArquivo("dados/pesquisas.dat")
        );
        RelatorioController relatorioController = new RelatorioController();

        new TelaLogin(primaryStage, adminService, pesquisaService, relatorioController).mostrar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}