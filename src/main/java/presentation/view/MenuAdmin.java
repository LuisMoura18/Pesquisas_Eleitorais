package presentation.view;

import application.admin.AdminService;
import domain.exceptions.RegraDeNegocioException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.StatusUsuario;

import java.util.Scanner;
import java.util.Set;

public class MenuAdmin {

    private final AdminService admin;
    private final Scanner in;

    public MenuAdmin(AdminService admin, Scanner in) {
        this.admin = admin;
        this.in = in;
    }

    public void executar() {
        String opcao;
        do {
            System.out.println("\n===== MENU ADMINISTRATIVO =====");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Listar usuários");
            System.out.println("3 - Editar usuário");
            System.out.println("4 - Ativar/desativar usuário");
            System.out.println("5 - Conceder perfil");
            System.out.println("6 - Revogar perfil");
            System.out.println("7 - Excluir usuário");
            System.out.println("0 - Voltar");
            opcao = ler("Opção: ");
            try {
                tratar(opcao);
            } catch (RegraDeNegocioException e) {
                System.out.println(">> Aviso: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(">> Valor inválido. Confira o que foi digitado.");
            }
        } while (!opcao.equals("0"));
    }

    private void tratar(String opcao) {
        switch (opcao) {
            case "1" -> {
                admin.cadastrarUsuario(ler("Nome: "), ler("E-mail: "),
                        ler("Senha (mín. 8, letras e números): "), Set.of(lerPerfil()));
                System.out.println("Usuário cadastrado.");
            }
            case "2" -> admin.listarUsuarios().forEach(u -> System.out.printf("%s | %s | %s | %s%n",
                    u.getNome(), u.getEmail(), u.getStatus(), u.getPerfis()));
            case "3" -> {
                admin.editarUsuario(ler("E-mail atual do usuário: "), ler("Novo nome: "), ler("Novo e-mail: "));
                System.out.println("Usuário atualizado.");
            }
            case "4" -> {
                admin.alterarStatusUsuario(ler("E-mail do usuário: "),
                        StatusUsuario.valueOf(ler("Novo status (ATIVO, INATIVO): ").toUpperCase()));
                System.out.println("Status atualizado.");
            }
            case "5" -> {
                admin.concederPerfil(ler("E-mail do usuário: "), lerPerfil());
                System.out.println("Perfil concedido.");
            }
            case "6" -> {
                admin.revogarPerfil(ler("E-mail do usuário: "), lerPerfil());
                System.out.println("Perfil revogado.");
            }
            case "7" -> {
                String email = ler("E-mail do usuário: ");
                if (ler("Confirma a exclusão de " + email + "? (s/n): ").equalsIgnoreCase("s")) {
                    admin.excluirUsuario(email);
                    System.out.println("Usuário excluído.");
                }
            }
            case "0" -> { }
            default -> System.out.println("Opção inexistente.");
        }
    }

    private PerfilAcesso lerPerfil() {
        return PerfilAcesso.valueOf(ler("Perfil (ADMINISTRADOR, PESQUISADOR, ANALISTA): ").toUpperCase());
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);
        return in.nextLine().trim();
    }
}