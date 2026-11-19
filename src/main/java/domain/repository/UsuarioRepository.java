package domain.repository;

import domain.model.admin.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    void salvar(Usuario usuario);
    void atualizar(Usuario usuario);
    void deletar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    List<Usuario> listarTodos();
}
