package presentation.view;

import application.analise.RelatorioController;
import domain.exceptions.AcessoNegadoException;
import domain.model.analise.Relatorio;
import domain.model.analise.ResultadoEstatistico;

import java.io.IOException;
import java.util.Scanner;

public class MenuAnalise {

    private final RelatorioController controller;
    private final Scanner in;

    public MenuAnalise(RelatorioController controller, Scanner in) {
        this.controller = controller;
        this.in = in;
    }

    public void executar() {
        String opcao;
        do {
            System.out.println("\n===== MÓDULO DE ANÁLISE E RELATÓRIOS =====");
            System.out.println("1 - Calcular resultado estatístico");
            System.out.println("2 - Gerar relatório consolidado");
            System.out.println("3 - Visualizar relatório (Tabela)");
            System.out.println("4 - Exportar relatório para CSV");
            System.out.println("0 - Voltar ao menu principal");
            opcao = ler("Opção: ");

            try {
                switch (opcao) {
                    case "1" -> calcularResultado();
                    case "2" -> gerarRelatorio();
                    case "3" -> visualizarTabela();
                    case "4" -> exportarCSV();
                    case "0" -> System.out.println("Retornando ao menu principal...");
                    default -> System.out.println("Opção inexistente.");
                }
            } catch (AcessoNegadoException e) {
                System.out.println(">> Acesso negado: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(">> Aviso: " + e.getMessage());
            } catch (IOException e) {
                System.out.println(">> Erro de arquivo ao exportar CSV: " + e.getMessage());
            }
        } while (!opcao.equals("0"));
    }

    private void calcularResultado() {
        String categoria = ler("Categoria / Opção de resposta: ");
        int quantidade = lerInteiro("Quantidade de respostas válidas para esta categoria: ");
        int totalValidos = lerInteiro("Total geral de respostas válidas: ");

        ResultadoEstatistico resultado = controller.calcularResultado(quantidade, totalValidos, categoria);
        System.out.println("Resultado calculado: " + resultado);
    }

    private void gerarRelatorio() {
        int id = lerInteiro("ID do relatório: ");
        String titulo = ler("Título do relatório: ");

        Relatorio relatorio = controller.gerarRelatorio(id, titulo);
        if (relatorio != null) {
            System.out.println("Relatório '" + titulo + "' (ID: " + id + ") gerado com sucesso!");
        }
    }

    private void visualizarTabela() {
        String tabela = controller.visualizarTabela();
        System.out.println("\n" + tabela);
    }

    private void exportarCSV() throws IOException {
        String caminho = ler("Caminho do arquivo CSV (ex: dados/relatorio.csv): ");
        controller.exportarCSV(caminho);
        System.out.println("Relatório exportado com sucesso para: " + caminho);
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);
        return in.nextLine().trim();
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(ler(mensagem));
            } catch (NumberFormatException e) {
                System.out.println(">> Digite um número inteiro válido.");
            }
        }
    }
}