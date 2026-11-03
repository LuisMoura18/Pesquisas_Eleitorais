package infrastructure.persistence;

import domain.exceptions.PersistenciaException;
import domain.model.pesquisa.Pesquisa;
import domain.repository.PesquisaRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PesquisaRepositoryArquivo implements PesquisaRepository {

    private final Path arquivo;
    private final Map<String, Pesquisa> pesquisas = new LinkedHashMap<>();

    public PesquisaRepositoryArquivo(String caminho) {
        this.arquivo = Paths.get(caminho);
        carregar();
    }

    @Override
    public void salvar(Pesquisa pesquisa) {
        pesquisas.put(pesquisa.getCodigo(), pesquisa);
        gravar();
    }

    @Override
    public Optional<Pesquisa> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(pesquisas.get(codigo));
    }

    @Override
    public List<Pesquisa> listar() {
        return new ArrayList<>(pesquisas.values());
    }

    @Override
    public void excluir(String codigo) {
        pesquisas.remove(codigo);
        gravar();
    }

    @SuppressWarnings("unchecked")
    private void carregar() {
        if (!Files.exists(arquivo)) return;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(arquivo))) {
            pesquisas.putAll((Map<String, Pesquisa>) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("Não foi possível ler o arquivo de pesquisas.", e);
        }
    }

    private void gravar() {
        try {
            if (arquivo.getParent() != null) Files.createDirectories(arquivo.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(arquivo))) {
                out.writeObject(pesquisas);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível gravar o arquivo de pesquisas.", e);
        }
    }
}