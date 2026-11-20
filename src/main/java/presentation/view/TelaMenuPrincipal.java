package presentation.view;

import application.admin.AdminService;
import application.admin.SessaoUsuario;
import application.analise.RelatorioController;
import application.pesquisa.PesquisaService;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Set;

public class TelaMenuPrincipal {

    private final Stage stage;
    private final AdminService adminService;
    private final PesquisaService pesquisaService;
    private final RelatorioController relatorioController;

    public TelaMenuPrincipal(Stage stage,
                             AdminService adminService,
                             PesquisaService pesquisaService,
                             RelatorioController relatorioController) {
        this.stage = stage;
        this.adminService = adminService;
        this.pesquisaService = pesquisaService;
        this.relatorioController = relatorioController;
    }

    public void mostrar() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        Set<PerfilAcesso> perfis = logado.getPerfis();

        boolean isAdmin = perfis.contains(PerfilAcesso.ADMINISTRADOR);
        boolean isPesquisador = perfis.contains(PerfilAcesso.PESQUISADOR);
        boolean isAnalista = perfis.contains(PerfilAcesso.ANALISTA);

        VBox root = new VBox(18);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #F4F4F6;");

        Label lblTitulo = new Label("Painel Principal");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E1E1E;");

        Label lblUsuario = new Label("Usuário: " + logado.getNome() + "  |  Perfis: " + perfis);
        lblUsuario.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        VBox cardModulos = new VBox(12);
        cardModulos.setPadding(new Insets(20));
        cardModulos.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #D8DCE3; "
                + "-fx-border-radius: 6; -fx-background-radius: 6;");

        if (isAdmin) {
            Button btnAdmin = criarBotaoModulo(
                    "Módulo de Administração",
                    "Gerenciar usuários, perfis de acesso, parâmetros gerais e relatórios administrativos"
            );
            btnAdmin.setOnAction(e ->
                    new TelaAdmin(stage, adminService, pesquisaService, relatorioController).mostrar()
            );
            cardModulos.getChildren().add(btnAdmin);
        }

        if (isAdmin || isPesquisador) {
            Button btnPesquisas = criarBotaoModulo(
                    "Módulo de Pesquisas Eleitorais",
                    "Consultar, cadastrar e configurar pesquisas, perguntas e plano amostral"
            );
            btnPesquisas.setOnAction(e -> abrirTelaPesquisas());
            cardModulos.getChildren().add(btnPesquisas);
        }

        if (isAdmin || isAnalista) {
            Button btnAnalise = criarBotaoModulo(
                    "Módulo de Análise Estatística e Relatórios",
                    "Calcular resultados percentuais, visualizar tabelas e exportar CSV"
            );
            btnAnalise.setOnAction(e ->
                    new TelaAnalise(stage, adminService, pesquisaService, relatorioController).mostrar()
            );
            cardModulos.getChildren().add(btnAnalise);
        }

        Button btnLogout = new Button("Sair (Logout)");
        btnLogout.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #CED4DA; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 14;");
        btnLogout.setOnAction(e -> {
            SessaoUsuario.getInstancia().encerrarSessao();
            new TelaLogin(stage, adminService, pesquisaService, relatorioController).mostrar();
        });

        HBox rodape = new HBox(btnLogout);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(lblTitulo, lblUsuario, cardModulos, rodape);

        stage.setTitle("Sistema de Análise de Pesquisas Eleitorais");
        stage.setScene(new Scene(root, 680, 440));
        stage.centerOnScreen();
    }

    private Button criarBotaoModulo(String titulo, String descricao) {
        Button btn = new Button(titulo + "\n" + descricao);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: #B3D9FF; -fx-border-color: #8EC5FC; "
                + "-fx-text-fill: #0F2942; -fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 12 16;");
        return btn;
    }

    private void abrirTelaPesquisas() {
        try {
            new PesquisaFxApp(this.pesquisaService).start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}