package domain.model.admin;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String nome;
    private String email;
    private String senha;
    private Set<PerfilAcesso> perfis; // Um usuário pode ter um ou mais perfis
    private StatusUsuario status;

    public Usuario(String nome, String email, String senha, Set<PerfilAcesso> perfis) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfis = perfis != null ? perfis : new HashSet<>();
        this.status = StatusUsuario.ATIVO;
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Set<PerfilAcesso> getPerfis() { return perfis; }
    public StatusUsuario getStatus() { return status; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setStatus(StatusUsuario status) { this.status = status; }

    public void adicionarPerfil(PerfilAcesso perfil) { this.perfis.add(perfil); }
    public void removerPerfil(PerfilAcesso perfil) { this.perfis.remove(perfil); }
}
