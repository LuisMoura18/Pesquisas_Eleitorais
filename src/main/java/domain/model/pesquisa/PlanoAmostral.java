package domain.model.pesquisa;

import domain.exceptions.PesquisaInvalidaException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlanoAmostral implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final List<GrupoAmostral> grupos = new ArrayList<>();

    public PlanoAmostral(long id) {
        this.id = id;
    }

    public void adicionarGrupo(GrupoAmostral novo) {
        for (GrupoAmostral g : grupos) {
            if (g.getId() == novo.getId())
                throw new PesquisaInvalidaException("Já existe um grupo com o id " + novo.getId() + ".");
        }
        grupos.add(novo);
    }

    public int totalEntrevistas() {
        return grupos.stream().mapToInt(GrupoAmostral::getQuantidadeMinima).sum();
    }

    public long getId() { return id; }
    public List<GrupoAmostral> getGrupos() { return Collections.unmodifiableList(grupos); }
}