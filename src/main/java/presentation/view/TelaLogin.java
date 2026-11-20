package presentation.view;

import application.admin.AdminService;
import application.analise.RelatorioController;
import application.pesquisa.PesquisaService;
import domain.exceptions.AcessoNegadoException;
import domain.exceptions.RegraDeNegocioException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import presentation.controller.LoginController;

public class TelaLogin {

    private final Stage stage;
    private final AdminService adminService;
    private final PesquisaService pesquisaService;
    private final RelatorioController relatorioController;
    private final LoginController controller;

    public TelaLogin(Stage stage,
                     AdminService adminService,
                     PesquisaService pesquisaService,
                     RelatorioController relatorioController) {
        this.stage = stage;
        this.adminService = adminService;
        this.pesquisaService = pesquisaService;
        this.relatorioController = relatorioController;
        this.controller = new LoginController(adminService);
    }

    public void mostrar() {
        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F4F4F6;");

        Label lblTitulo = new Label("Sistema de Pesquisas Eleitorais");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E1E1E;");

        Label lblSubtitulo = new Label("Acesso ao Sistema");
        lblSubtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        VBox card = new VBox(12);
        card.setMaxWidth(380);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #D8DCE3; "
                + "-fx-border-radius: 6; -fx-background-radius: 6;");

        Label lblEmail = new Label("E-mail");
        lblEmail.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Ex.: admin@email.com");
        txtEmail.setPrefHeight(32);

        Label lblSenha = new Label("Senha");
        lblSenha.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Digite sua senha");
        txtSenha.setPrefHeight(32);

        Label lblMensagem = new Label();
        lblMensagem.setWrapText(true);
        lblMensagem.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 12px;");

        Button btnLimpar = new Button("Limpar");
        btnLimpar.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #CED4DA; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 14;");
        btnLimpar.setOnAction(e -> {
            txtEmail.clear();
            txtSenha.clear();
            lblMensagem.setText("");
        });

        Button btnEntrar = new Button("Entrar");
        btnEntrar.setDefaultButton(true);
        btnEntrar.setStyle("-fx-background-color: #B3D9FF; -fx-border-color: #8EC5FC; "
                + "-fx-text-fill: #0F2942; -fx-font-weight: bold; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 18;");

        btnEntrar.setOnAction(e -> {
            try {
                controller.autenticar(txtEmail.getText(), txtSenha.getText());
                new TelaMenuPrincipal(stage, adminService, pesquisaService, relatorioController).mostrar();
            } catch (RegraDeNegocioException ex) {
                lblMensagem.setText(ex.getMessage());
            }
        });

        HBox botoes = new HBox(10, btnLimpar, btnEntrar);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(lblEmail, txtEmail, lblSenha, txtSenha, lblMensagem, botoes);
        root.getChildren().addAll(lblTitulo, lblSubtitulo, card);

        stage.setTitle("Login - Pesquisas Eleitorais");
        stage.setScene(new Scene(root, 520, 400));
        stage.centerOnScreen();
        stage.show();
    }
}