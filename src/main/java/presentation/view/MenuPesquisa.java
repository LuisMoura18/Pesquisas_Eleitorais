package presentation.view;

import application.pesquisa.PesquisaService;
import domain.exceptions.PersistenciaException;
import domain.exceptions.RegraDeNegocioException;
import domain.model.pesquisa.Abrangencia;
import domain.model.pesquisa.OpcaoResposta;
import domain.model.pesquisa.Pergunta;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.TipoPergunta;
import domain.model.pesquisa.TipoPesquisa;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MenuPesquisa {
    //TESTES NO TERMINAL!!
    private final PesquisaService service;
    private final Scanner in;

    public MenuPesquisa(PesquisaService service, Scanner in) {
        this.service = service;
        this.in = in;
    }

    public void executar() {
        String opcao;
        do {
            System.out.println("\n===== MÓDULO DE PESQUISAS =====");
            System.out.println("1 - Cadastrar pesquisa");
            System.out.println("2 - Listar pesquisas");
            System.out.println("3 - Adicionar pergunta");
            System.out.println("4 - Associar candidato");
            System.out.println("5 - Associar região");
            System.out.println("6 - Iniciar coleta");
            System.out.println("7 - Acompanhar andamento");
            System.out.println("0 - Sair");
            opcao = ler("Opção: ");
            try {
                tratar(opcao);
            } catch (RegraDeNegocioException e) {
                System.out.println(">> Aviso: " + e.getMessage());
            } catch (PersistenciaException e) {
                System.out.println(">> Erro de arquivo: " + e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println(">> Data inválida. Use o formato AAAA-MM-DD.");
            } catch (IllegalArgumentException e) {
                System.out.println(">> Valor inválido. Confira o que foi digitado.");
            }
        } while (!opcao.equals("0"));
    }

    private void tratar(String opcao) {
        switch (opcao) {
            case "1" -> {
                service.cadastrar(ler("Código: "), ler("Nome: "), ler("Cargo: "),
                        LocalDate.parse(ler("Início (AAAA-MM-DD): ")),
                        LocalDate.parse(ler("Fim (AAAA-MM-DD): ")),
                        Abrangencia.valueOf(ler("Abrangência (NACIONAL, ESTADUAL, MUNICIPAL): ").toUpperCase()),
                        TipoPesquisa.valueOf(ler("Tipo (INTENCAO_VOTO, REJEICAO, CONHECIMENTO, SEGUNDO_TURNO, OUTRO): ").toUpperCase()),
                        Integer.parseInt(ler("Quantidade prevista: ")));
                System.out.println("Pesquisa cadastrada.");
            }
            case "2" -> service.listar().forEach(this::imprimir);
            case "3" -> adicionarPergunta();
            case "4" -> {
                service.associarCandidato(ler("Código da pesquisa: "), ler("Número do candidato: "), ler("Nome do candidato: "));
                System.out.println("Candidato associado.");
            }
            case "5" -> {
                service.associarRegiao(ler("Código da pesquisa: "), ler("Região: "));
                System.out.println("Região associada.");
            }
            case "6" -> {
                service.iniciarColeta(ler("Código da pesquisa: "));
                System.out.println("Coleta iniciada.");
            }
            case "7" -> {
                var a = service.acompanhar(ler("Código da pesquisa: "));
                System.out.printf("Previstas: %d | Realizadas: %d | Conclusão: %.1f%%%n",
                        a.quantidadePrevista(), a.quantidadeRealizada(), a.percentualConclusao());
            }
            case "0" -> System.out.println("Encerrando...");
            default -> System.out.println("Opção inexistente.");
        }
    }

    private void adicionarPergunta() {
        String codigo = ler("Código da pesquisa: ");
        Pergunta pergunta = new Pergunta(ler("Enunciado: "),
                Integer.parseInt(ler("Ordem: ")),
                TipoPergunta.valueOf(ler("Tipo (ESCOLHA_UNICA, ESCOLHA_MULTIPLA): ").toUpperCase()),
                ler("Obrigatória? (s/n): ").equalsIgnoreCase("s"));
        int n = Integer.parseInt(ler("Quantas opções de resposta? "));
        for (int i = 1; i <= n; i++)
            pergunta.adicionarOpcao(new OpcaoResposta(ler("Opção " + i + ": "), i));
        service.adicionarPergunta(codigo, pergunta);
        System.out.println("Pergunta adicionada.");
    }

    private void imprimir(Pesquisa p) {
        System.out.printf("[%s] %s | %s | %s | %s | %d/%d | candidatos: %s | regiões: %s%n",
                p.getCodigo(), p.getNome(), p.getCargo(), p.getTipo(), p.getSituacao(),
                p.getQuantidadeRealizada(), p.getQuantidadePrevista(),
                p.getCandidatos(), p.getRegioes());
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);
        return in.nextLine().trim();
    }
}