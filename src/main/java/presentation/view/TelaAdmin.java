package presentation.view;

import application.admin.AdminService;
import application.analise.RelatorioController;
import application.pesquisa.PesquisaService;
import domain.exceptions.AcessoNegadoException;
import domain.exceptions.RegraDeNegocioException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.StatusUsuario;
import domain.model.admin.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import presentation.controller.AdminFxController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TelaAdmin {

    private final Stage stage;
    private final AdminService adminService;
    private final PesquisaService pesquisaService;
    private final RelatorioController relatorioController;
    private final AdminFxController controller;

    private final ObservableList<Usuario> dadosTabela = FXCollections.observableArrayList();
    private Label lblContador;
    private TextArea txtRelatorioAdmin;

    public TelaAdmin(Stage stage,
                     AdminService adminService,
                     PesquisaService pesquisaService,
                     RelatorioController relatorioController) {
        this.stage = stage;
        this.adminService = adminService;
        this.pesquisaService = pesquisaService;
        this.relatorioController = relatorioController;
        this.controller = new AdminFxController(adminService);
    }

    public void mostrar() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F4F6;");

        HBox topo = new HBox();
        topo.setAlignment(Pos.CENTER_LEFT);
        Label lblTitulo = new Label("Administração do Sistema");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E1E1E;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnVoltar = criarBotaoSecundario("Voltar ao Menu");
        btnVoltar.setOnAction(e ->
                new TelaMenuPrincipal(stage, adminService, pesquisaService, relatorioController).mostrar()
        );
        topo.getChildren().addAll(lblTitulo, spacer, btnVoltar);

        TabPane abas = new TabPane();
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(abas, Priority.ALWAYS);

        Tab abaUsuarios = new Tab("Usuários e Permissões", criarAbaUsuarios());
        Tab abaParametros = new Tab("Parâmetros Gerais", criarAbaParametros());
        Tab abaRelatorios = new Tab("Relatórios Administrativos", criarAbaRelatorios());

        abas.getTabs().addAll(abaUsuarios, abaParametros, abaRelatorios);
        root.getChildren().addAll(topo, abas);

        atualizarTabela(controller.listarUsuarios());

        stage.setTitle("Administração - Pesquisas Eleitorais");
        stage.setScene(new Scene(root, 1020, 680));
        stage.centerOnScreen();
    }

    private VBox criarAbaUsuarios() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 0, 0, 0));

        // 1. Card de Filtros (inspirado diretamente na tela de Consulta de Pesquisas)
        VBox cardFiltro = criarCard();
        GridPane gridFiltro = new GridPane();
        gridFiltro.setHgap(12);
        gridFiltro.setVgap(8);

        TextField txtFiltroNome = new TextField();
        txtFiltroNome.setPromptText("Ex.: Maria Silva...");

        ComboBox<PerfilAcesso> cbFiltroPerfil = new ComboBox<>();
        cbFiltroPerfil.getItems().add(null);
        cbFiltroPerfil.getItems().addAll(PerfilAcesso.values());
        cbFiltroPerfil.setPromptText("Todos os perfis");
        cbFiltroPerfil.setMaxWidth(Double.MAX_VALUE);

        ComboBox<StatusUsuario> cbFiltroStatus = new ComboBox<>();
        cbFiltroStatus.getItems().add(null);
        cbFiltroStatus.getItems().addAll(StatusUsuario.values());
        cbFiltroStatus.setPromptText("Todos os status");
        cbFiltroStatus.setMaxWidth(Double.MAX_VALUE);

        gridFiltro.add(criarLabelCampo("Nome"), 0, 0);
        gridFiltro.add(txtFiltroNome, 0, 1);
        gridFiltro.add(criarLabelCampo("Perfil de Acesso"), 1, 0);
        gridFiltro.add(cbFiltroPerfil, 1, 1);
        gridFiltro.add(criarLabelCampo("Status"), 2, 0);
        gridFiltro.add(cbFiltroStatus, 2, 1);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.3);
            gridFiltro.getColumnConstraints().add(col);
        }

        Button btnLimparFiltro = criarBotaoSecundario("Limpar Filtros");
        btnLimparFiltro.setOnAction(e -> {
            txtFiltroNome.clear();
            cbFiltroPerfil.setValue(null);
            cbFiltroStatus.setValue(null);
            atualizarTabela(controller.listarUsuarios());
        });

        Button btnFiltrar = criarBotaoPrimario("Pesquisar");
        btnFiltrar.setOnAction(e -> atualizarTabela(
                controller.filtrarUsuarios(txtFiltroNome.getText(), cbFiltroPerfil.getValue(), cbFiltroStatus.getValue())
        ));

        HBox barraFiltro = new HBox(10, btnLimparFiltro, btnFiltrar);
        barraFiltro.setAlignment(Pos.CENTER_RIGHT);
        cardFiltro.getChildren().addAll(gridFiltro, barraFiltro);

        // 2. Card de Cadastro / Edição
        VBox cardForm = criarCard();
        GridPane gridForm = new GridPane();
        gridForm.setHgap(12);
        gridForm.setVgap(8);

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome completo");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("usuario@email.com");

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Mín. 8 caracteres (letras e números)");

        CheckBox chkAdmin = new CheckBox("Administrador");
        CheckBox chkPesq = new CheckBox("Pesquisador");
        CheckBox chkAnalista = new CheckBox("Analista");
        HBox boxPerfis = new HBox(12, chkAdmin, chkPesq, chkAnalista);
        boxPerfis.setAlignment(Pos.CENTER_LEFT);

        gridForm.add(criarLabelCampo("Nome"), 0, 0);
        gridForm.add(txtNome, 0, 1);
        gridForm.add(criarLabelCampo("E-mail"), 1, 0);
        gridForm.add(txtEmail, 1, 1);
        gridForm.add(criarLabelCampo("Senha"), 2, 0);
        gridForm.add(txtSenha, 2, 1);
        gridForm.add(criarLabelCampo("Perfis Atribuídos"), 0, 2);
        gridForm.add(boxPerfis, 0, 3, 3, 1);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.3);
            gridForm.getColumnConstraints().add(col);
        }

        // 3. Tabela de Usuários
        TableView<Usuario> tabela = new TableView<>(dadosTabela);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        TableColumn<Usuario, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<Usuario, String> colPerfis = new TableColumn<>("Perfis");
        colPerfis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPerfis().toString()));

        TableColumn<Usuario, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        tabela.getColumns().addAll(colNome, colEmail, colPerfis, colStatus);

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, sel) -> {
            if (sel != null) {
                txtNome.setText(sel.getNome());
                txtEmail.setText(sel.getEmail());
                txtEmail.setDisable(true);
                txtSenha.clear();
                chkAdmin.setSelected(sel.getPerfis().contains(PerfilAcesso.ADMINISTRADOR));
                chkPesq.setSelected(sel.getPerfis().contains(PerfilAcesso.PESQUISADOR));
                chkAnalista.setSelected(sel.getPerfis().contains(PerfilAcesso.ANALISTA));
            }
        });

        Button btnNovo = criarBotaoSecundario("Limpar Formulário");
        btnNovo.setOnAction(e -> {
            tabela.getSelectionModel().clearSelection();
            txtNome.clear();
            txtEmail.clear();
            txtEmail.setDisable(false);
            txtSenha.clear();
            chkAdmin.setSelected(false);
            chkPesq.setSelected(false);
            chkAnalista.setSelected(false);
        });

        Button btnSalvar = criarBotaoPrimario("Cadastrar");
        btnSalvar.setOnAction(e -> {
            try {
                Set<PerfilAcesso> perfisSel = coletarPerfis(chkAdmin, chkPesq, chkAnalista);
                controller.cadastrarUsuario(txtNome.getText(), txtEmail.getText(), txtSenha.getText(), perfisSel);
                atualizarTabela(controller.listarUsuarios());
                btnNovo.fire();
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário cadastrado com sucesso!");
            } catch (RegraDeNegocioException | IllegalArgumentException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro ao Cadastrar", ex.getMessage());
            }
        });

        Button btnEditar = criarBotaoSecundario("Salvar Edição");
        btnEditar.setOnAction(e -> {
            Usuario sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) {
                exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione um usuário na tabela para editar.");
                return;
            }
            try {
                Set<PerfilAcesso> perfisSel = coletarPerfis(chkAdmin, chkPesq, chkAnalista);
                controller.editarUsuario(sel.getEmail(), txtNome.getText(), perfisSel);
                atualizarTabela(controller.listarUsuarios());
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");
            } catch (RegraDeNegocioException | IllegalArgumentException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro ao Editar", ex.getMessage());
            }
        });

        Button btnStatus = criarBotaoSecundario("Ativar / Desativar");
        btnStatus.setOnAction(e -> {
            Usuario sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) {
                exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione um usuário na tabela.");
                return;
            }
            try {
                controller.alternarStatus(sel);
                atualizarTabela(controller.listarUsuarios());
            } catch (RegraDeNegocioException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Operação Não Permitida", ex.getMessage());
            }
        });

        Button btnExcluir = criarBotaoSecundario("Excluir");
        btnExcluir.setOnAction(e -> {
            Usuario sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) {
                exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione um usuário na tabela para excluir.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Exclusão");
            confirm.setHeaderText("Excluir usuário definitivamente?");
            confirm.setContentText("Usuário: " + sel.getNome() + " (" + sel.getEmail() + ")");
            Optional<ButtonType> resp = confirm.showAndWait();
            if (resp.isPresent() && resp.get() == ButtonType.OK) {
                try {
                    controller.excluirUsuario(sel.getEmail());
                    atualizarTabela(controller.listarUsuarios());
                    btnNovo.fire();
                } catch (RegraDeNegocioException ex) {
                    exibirAlerta(Alert.AlertType.ERROR, "Erro ao Excluir", ex.getMessage());
                }
            }
        });

        HBox barraForm = new HBox(10, btnNovo, btnStatus, btnExcluir, btnEditar, btnSalvar);
        barraForm.setAlignment(Pos.CENTER_RIGHT);
        cardForm.getChildren().addAll(gridForm, barraForm);

        lblContador = new Label("0 usuário(s) exibido(s)");
        lblContador.setStyle("-fx-font-size: 12px; -fx-text-fill: #444444;");

        box.getChildren().addAll(cardFiltro, cardForm, tabela, lblContador);
        return box;
    }

    private VBox criarAbaParametros() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 0, 0, 0));

        VBox card = criarCard();
        ComboBox<String> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll("Região", "Faixa Etária", "Escolaridade", "Faixa de Renda");
        cbCategoria.setValue("Região");

        TextField txtValor = new TextField();
        txtValor.setPromptText("Novo valor para a categoria...");
        HBox.setHgrow(txtValor, Priority.ALWAYS);

        ListView<String> listaValores = new ListView<>();
        listaValores.getItems().setAll(controller.listarParametrosPorCategoria("Região"));
        VBox.setVgrow(listaValores, Priority.ALWAYS);

        cbCategoria.setOnAction(e ->
                listaValores.getItems().setAll(controller.listarParametrosPorCategoria(cbCategoria.getValue()))
        );

        Button btnAdicionar = criarBotaoPrimario("Adicionar");
        btnAdicionar.setOnAction(e -> {
            try {
                controller.adicionarParametro(cbCategoria.getValue(), txtValor.getText());
                listaValores.getItems().setAll(controller.listarParametrosPorCategoria(cbCategoria.getValue()));
                txtValor.clear();
            } catch (IllegalArgumentException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Valor Inválido", ex.getMessage());
            }
        });

        Button btnRemover = criarBotaoSecundario("Remover Selecionado");
        btnRemover.setOnAction(e -> {
            String sel = listaValores.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controller.removerParametro(cbCategoria.getValue(), sel);
                listaValores.getItems().setAll(controller.listarParametrosPorCategoria(cbCategoria.getValue()));
            }
        });

        HBox linhaControles = new HBox(10, cbCategoria, txtValor, btnAdicionar, btnRemover);
        linhaControles.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(criarLabelCampo("Configuração de Categorias Utilizadas nas Pesquisas"), linhaControles);
        box.getChildren().addAll(card, listaValores);
        return box;
    }

    private VBox criarAbaRelatorios() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(14, 0, 0, 0));

        VBox card = criarCard();
        txtRelatorioAdmin = new TextArea(controller.gerarResumoAdministrativo());
        txtRelatorioAdmin.setEditable(false);
        txtRelatorioAdmin.setStyle("-fx-font-size: 13px;");
        VBox.setVgrow(txtRelatorioAdmin, Priority.ALWAYS);

        Button btnAtualizar = criarBotaoPrimario("Atualizar Indicadores");
        btnAtualizar.setOnAction(e -> txtRelatorioAdmin.setText(controller.gerarResumoAdministrativo()));

        HBox barra = new HBox(btnAtualizar);
        barra.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(criarLabelCampo("Indicadores Cadastrais de Usuários"), txtRelatorioAdmin, barra);
        box.getChildren().add(card);
        return box;
    }

    private Set<PerfilAcesso> coletarPerfis(CheckBox admin, CheckBox pesq, CheckBox analista) {
        Set<PerfilAcesso> set = new HashSet<>();
        if (admin.isSelected()) set.add(PerfilAcesso.ADMINISTRADOR);
        if (pesq.isSelected()) set.add(PerfilAcesso.PESQUISADOR);
        if (analista.isSelected()) set.add(PerfilAcesso.ANALISTA);
        return set;
    }

    private void atualizarTabela(List<Usuario> lista) {
        dadosTabela.setAll(lista);
        if (lblContador != null) {
            lblContador.setText(lista.size() + " usuário(s) exibido(s)");
        }
        if (txtRelatorioAdmin != null) {
            txtRelatorioAdmin.setText(controller.gerarResumoAdministrativo());
        }
    }

    private VBox criarCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #D8DCE3; "
                + "-fx-border-radius: 6; -fx-background-radius: 6;");
        return card;
    }

    private Label criarLabelCampo(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        return lbl;
    }

    private Button criarBotaoPrimario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: #B3D9FF; -fx-border-color: #8EC5FC; "
                + "-fx-text-fill: #0F2942; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 16;");
        return btn;
    }

    private Button criarBotaoSecundario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #CED4DA; "
                + "-fx-text-fill: #212529; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 14;");
        return btn;
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}