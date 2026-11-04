package infrastructure.persistance;

import domain.model.admin.Usuario;
import domain.repository.UsuarioRepository;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioFileRepository implements UsuarioRepository {
    private static final String FILE_NAME = "usuarios.dat"; // Armazenar dados em arquivos

    @Override
    public void salvar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        usuarios.add(usuario);
        gravarArquivo(usuarios);
    }

    @Override
    public void atualizar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuario.getId())) {
                usuarios.set(i, usuario);
                break;
            }
        }
        gravarArquivo(usuarios);
    }

    @Override
    public void deletar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        usuarios.removeIf(u -> u.getId().equals(usuario.getId()));
        gravarArquivo(usuarios);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return listarTodos().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Usuario> listarTodos() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void gravarArquivo(List<Usuario> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar dados no arquivo", e);
        }
    }
}
