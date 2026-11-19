package presentation.view;

import domain.exceptions.PersistenciaException;
import domain.exceptions.RegraDeNegocioException;
import domain.model.pesquisa.GrupoAmostral;
import domain.model.pesquisa.OpcaoResposta;
import domain.model.pesquisa.Pergunta;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.PlanoAmostral;
import domain.model.pesquisa.SituacaoPesquisa;
import domain.model.pesquisa.TipoPergunta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import presentation.controller.PesquisaFxController;
import java.util.Map;

public class TelaConfiguracaoPesquisa {

    private final PesquisaFxController controller;
    private final String codigoPesquisa;
    private final Runnable aoAtualizar;
    private Pesquisa pesquisaAtual;
    private final ObservableList<Pergunta> listaPerguntas = FXCollections.observableArrayList();
    private final ObservableList<OpcaoResposta> listaOpcoes = FXCollections.observableArrayList();
    private final ObservableList<String> listaCandidatos = FXCollections.observableArrayList();
    private final ObservableList<String> listaRegioes = FXCollections.observableArrayList();
    private final ObservableList<GrupoAmostral> listaGrupos = FXCollections.observableArrayList();

    public TelaConfiguracaoPesquisa(PesquisaFxController controller, String codigoPesquisa, Runnable aoAtualizar) {
        this.controller = controller;
        this.codigoPesquisa = codigoPesquisa;
        this.aoAtualizar = aoAtualizar;
    }

    public void exibir(Window owner) {
        recarregarPesquisa();

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Configurar Pesquisa - " + pesquisaAtual.getCodigo());

        VBox raiz = new VBox(12);
        raiz.setPadding(new Insets(16));

        Label lblHeader = new Label(pesquisaAtual.getCodigo() + " — " + pesquisaAtual.getNome()
                + " [" + pesquisaAtual.getSituacao() + "]");
        lblHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblAvisoSituacao = new Label();
        if (pesquisaAtual.getSituacao() != SituacaoPesquisa.PLANEJADA) {
            lblAvisoSituacao.setText("Pesquisa em situação " + pesquisaAtual.getSituacao()
                    + ": alterações estruturais só são permitidas enquanto PLANEJADA.");
            lblAvisoSituacao.setStyle("-fx-text-fill: #b45309; -fx-font-weight: bold;");
        }

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabs, Priority.ALWAYS);

        tabs.getTabs().addAll(
                criarAbaPerguntas(),
                criarAbaCandidatos(),
                criarAbaRegioes(),
                criarAbaPlanoAmostral()
        );

        Button btnFechar = new Button("Fechar");
        btnFechar.setOnAction(e -> dialog.close());
        HBox rodape = new HBox(btnFechar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        raiz.getChildren().addAll(lblHeader, lblAvisoSituacao, tabs, rodape);

        dialog.setScene(new Scene(raiz, 860, 560));
        dialog.showAndWait();
    }

    private void recarregarPesquisa() {
        this.pesquisaAtual = controller.buscarPorCodigo(codigoPesquisa);
        listaPerguntas.setAll(pesquisaAtual.getPerguntas());

        listaCandidatos.clear();
        for (Map.Entry<String, String> entry : pesquisaAtual.getCandidatos().entrySet()) {
            listaCandidatos.add(entry.getKey() + " — " + entry.getValue());
        }
        listaRegioes.setAll(pesquisaAtual.getRegioes());
        if (pesquisaAtual.getPlanoAmostral() != null) {
            listaGrupos.setAll(pesquisaAtual.getPlanoAmostral().getGrupos());
        } else {
            listaGrupos.clear();
        }
        if (aoAtualizar != null) aoAtualizar.run();
    }

    @SuppressWarnings("unchecked")
    private Tab criarAbaPerguntas() {
        TableView<Pergunta> tabelaPerguntas = new TableView<>(listaPerguntas);
        tabelaPerguntas.setPlaceholder(new Label("Nenhuma pergunta cadastrada."));
        tabelaPerguntas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Pergunta, String> colOrdem = new TableColumn<>("Ordem");
        colOrdem.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getOrdem())));

        TableColumn<Pergunta, String> colEnunciado = new TableColumn<>("Enunciado");
        colEnunciado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnunciado()));

        TableColumn<Pergunta, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));

        TableColumn<Pergunta, String> colObrig = new TableColumn<>("Obrigatória");
        colObrig.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isObrigatoria() ? "Sim" : "Não"));

        tabelaPerguntas.getColumns().addAll(colOrdem, colEnunciado, colTipo, colObrig);

        //formulário nova pergunta
        TextField txtOrdem = new TextField();
        txtOrdem.setPromptText("Ordem");
        txtOrdem.setPrefWidth(70);

        TextField txtEnunciado = new TextField();
        txtEnunciado.setPromptText("Enunciado da pergunta");
        HBox.setHgrow(txtEnunciado, Priority.ALWAYS);

        ComboBox<TipoPergunta> cbTipo = new ComboBox<>();
        cbTipo.getItems().setAll(TipoPergunta.values());
        cbTipo.setValue(TipoPergunta.ESCOLHA_UNICA);

        CheckBox chkObrigatoria = new CheckBox("Obrigatória");
        chkObrigatoria.setSelected(true);

        Button btnAddPergunta = new Button("Adicionar");
        btnAddPergunta.setOnAction(e -> {
            try {
                int ordem = Integer.parseInt(txtOrdem.getText().trim());
                Pergunta p = new Pergunta(txtEnunciado.getText().trim(), ordem, cbTipo.getValue(), chkObrigatoria.isSelected());
                controller.adicionarPergunta(codigoPesquisa, p);
                txtOrdem.clear();
                txtEnunciado.clear();
                recarregarPesquisa();
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "A ordem da pergunta deve ser um inteiro.");
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        Button btnRemPergunta = new Button("Remover Selecionada");
        btnRemPergunta.setOnAction(e -> {
            Pergunta sel = tabelaPerguntas.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            try {
                controller.removerPergunta(codigoPesquisa, sel.getOrdem());
                listaOpcoes.clear();
                recarregarPesquisa();
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        HBox linhaNovaPergunta = new HBox(8, txtOrdem, txtEnunciado, cbTipo, chkObrigatoria, btnAddPergunta, btnRemPergunta);
        linhaNovaPergunta.setAlignment(Pos.CENTER_LEFT);

        //opçoes da pergunta selecionada
        ListView<OpcaoResposta> lvOpcoes = new ListView<>(listaOpcoes);
        lvOpcoes.setPrefHeight(130);

        tabelaPerguntas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, nova) -> {
            if (nova != null) {
                listaOpcoes.setAll(nova.getOpcoes());
            } else {
                listaOpcoes.clear();
            }
        });

        TextField txtOrdemOpcao = new TextField();
        txtOrdemOpcao.setPromptText("Ordem");
        txtOrdemOpcao.setPrefWidth(70);

        TextField txtTextoOpcao = new TextField();
        txtTextoOpcao.setPromptText("Texto da opção de resposta");
        HBox.setHgrow(txtTextoOpcao, Priority.ALWAYS);

        Button btnAddOpcao = new Button("Adicionar Opção");
        btnAddOpcao.setOnAction(e -> {
            Pergunta sel = tabelaPerguntas.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma pergunta",
                        "Selecione uma pergunta na tabela acima antes de adicionar opções.");
                return;
            }
            try {
                int ordemOp = Integer.parseInt(txtOrdemOpcao.getText().trim());
                controller.adicionarOpcao(codigoPesquisa, sel.getOrdem(),
                        new OpcaoResposta(txtTextoOpcao.getText().trim(), ordemOp));
                txtOrdemOpcao.clear();
                txtTextoOpcao.clear();
                recarregarPesquisa();
                Pergunta atualizada = pesquisaAtual.buscarPergunta(sel.getOrdem());
                listaOpcoes.setAll(atualizada.getOpcoes());
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "A ordem da opção deve ser um inteiro.");
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        Button btnRemOpcao = new Button("Remover Opção");
        btnRemOpcao.setOnAction(e -> {
            Pergunta selP = tabelaPerguntas.getSelectionModel().getSelectedItem();
            OpcaoResposta selOp = lvOpcoes.getSelectionModel().getSelectedItem();
            if (selP == null || selOp == null) return;
            try {
                controller.removerOpcao(codigoPesquisa, selP.getOrdem(), selOp.getOrdem());
                recarregarPesquisa();
                listaOpcoes.setAll(pesquisaAtual.buscarPergunta(selP.getOrdem()).getOpcoes());
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        HBox linhaOpcoes = new HBox(8, txtOrdemOpcao, txtTextoOpcao, btnAddOpcao, btnRemOpcao);
        linhaOpcoes.setAlignment(Pos.CENTER_LEFT);

        VBox conteudo = new VBox(10,
                new Label("Perguntas da Pesquisa:"),
                tabelaPerguntas,
                linhaNovaPergunta,
                new Label("Opções de Resposta da Pergunta Selecionada:"),
                lvOpcoes,
                linhaOpcoes
        );
        conteudo.setPadding(new Insets(12));
        VBox.setVgrow(tabelaPerguntas, Priority.ALWAYS);

        return new Tab("Perguntas e Opções", conteudo);
    }

    private Tab criarAbaCandidatos() {
        ListView<String> lvCandidatos = new ListView<>(listaCandidatos);
        VBox.setVgrow(lvCandidatos, Priority.ALWAYS);

        TextField txtNumero = new TextField();
        txtNumero.setPromptText("Número (ex.: 10)");
        txtNumero.setPrefWidth(130);

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome fictício do candidato");
        HBox.setHgrow(txtNome, Priority.ALWAYS);

        Button btnAssociar = new Button("Associar Candidato");
        btnAssociar.setOnAction(e -> {
            try {
                controller.associarCandidato(codigoPesquisa, txtNumero.getText().trim(), txtNome.getText().trim());
                txtNumero.clear();
                txtNome.clear();
                recarregarPesquisa();
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        HBox form = new HBox(8, txtNumero, txtNome, btnAssociar);
        VBox conteudo = new VBox(10, new Label("Candidatos associados à pesquisa:"), lvCandidatos, form);
        conteudo.setPadding(new Insets(12));
        return new Tab("Candidatos", conteudo);
    }

    private Tab criarAbaRegioes() {
        ListView<String> lvRegioes = new ListView<>(listaRegioes);
        VBox.setVgrow(lvRegioes, Priority.ALWAYS);

        TextField txtRegiao = new TextField();
        txtRegiao.setPromptText("Região (ex.: Norte, Sul, Plano Piloto...)");
        HBox.setHgrow(txtRegiao, Priority.ALWAYS);

        Button btnAssociar = new Button("Associar Região");
        btnAssociar.setOnAction(e -> {
            try {
                controller.associarRegiao(codigoPesquisa, txtRegiao.getText().trim());
                txtRegiao.clear();
                recarregarPesquisa();
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        HBox form = new HBox(8, txtRegiao, btnAssociar);
        VBox conteudo = new VBox(10, new Label("Regiões associadas à pesquisa:"), lvRegioes, form);
        conteudo.setPadding(new Insets(12));
        return new Tab("Regiões", conteudo);
    }

    private Tab criarAbaPlanoAmostral() {
        ListView<GrupoAmostral> lvGrupos = new ListView<>(listaGrupos);
        VBox.setVgrow(lvGrupos, Priority.ALWAYS);

        TextField txtIdPlano = new TextField(
                pesquisaAtual.getPlanoAmostral() != null ? String.valueOf(pesquisaAtual.getPlanoAmostral().getId()) : "1");
        txtIdPlano.setPrefWidth(90);

        TextField txtIdGrupo = new TextField();
        txtIdGrupo.setPromptText("ID Grupo");
        txtIdGrupo.setPrefWidth(90);

        TextField txtDescGrupo = new TextField();
        txtDescGrupo.setPromptText("Descrição (ex.: Região Norte / Jovens 16-24)");
        HBox.setHgrow(txtDescGrupo, Priority.ALWAYS);

        TextField txtMetaGrupo = new TextField();
        txtMetaGrupo.setPromptText("Meta mín.");
        txtMetaGrupo.setPrefWidth(100);

        Button btnAddGrupo = new Button("Adicionar à lista");
        btnAddGrupo.setOnAction(e -> {
            try {
                long idGrupo = Long.parseLong(txtIdGrupo.getText().trim());
                int meta = Integer.parseInt(txtMetaGrupo.getText().trim());
                GrupoAmostral g = new GrupoAmostral(idGrupo, txtDescGrupo.getText().trim(), meta);
                if (listaGrupos.stream().anyMatch(x -> x.getId() == idGrupo)) {
                    mostrarAlerta(Alert.AlertType.WARNING, "ID duplicado", "Já existe um grupo com ID " + idGrupo);
                    return;
                }
                listaGrupos.add(g);
                txtIdGrupo.clear();
                txtDescGrupo.clear();
                txtMetaGrupo.clear();
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "ID e meta mínima devem ser números.");
            } catch (RegraDeNegocioException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        Button btnSalvarPlano = new Button("Salvar Plano Amostral");
        btnSalvarPlano.setOnAction(e -> {
            try {
                long idPlano = Long.parseLong(txtIdPlano.getText().trim());
                PlanoAmostral plano = new PlanoAmostral(idPlano);
                for (GrupoAmostral g : listaGrupos) {
                    plano.adicionarGrupo(g);
                }
                controller.definirPlanoAmostral(codigoPesquisa, plano);
                recarregarPesquisa();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Plano amostral salvo na pesquisa.");
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "ID do plano deve ser numérico.");
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro ao salvar plano", ex.getMessage());
            }
        });

        HBox linhaPlano = new HBox(8, new Label("ID do Plano:"), txtIdPlano, btnSalvarPlano);
        linhaPlano.setAlignment(Pos.CENTER_LEFT);

        HBox linhaGrupo = new HBox(8, txtIdGrupo, txtDescGrupo, txtMetaGrupo, btnAddGrupo);
        linhaGrupo.setAlignment(Pos.CENTER_LEFT);

        VBox conteudo = new VBox(10,
                linhaPlano,
                new Label("Grupos do Plano Amostral (meta total não pode ultrapassar "
                        + pesquisaAtual.getQuantidadePrevista() + " entrevistas previstas):"),
                lvGrupos,
                linhaGrupo
        );
        conteudo.setPadding(new Insets(12));
        return new Tab("Plano Amostral", conteudo);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}