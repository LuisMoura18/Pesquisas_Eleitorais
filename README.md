# Sistema de Análise de Pesquisas Eleitorais

Projeto acadêmico desenvolvido na disciplina de Técnicas de Programação 1, do curso de Ciência da Computação da Universidade de Brasília (UnB).

## Sobre o projeto

O projeto tem como objetivo desenvolver um sistema para coletar, organizar, processar e analisar dados de pesquisas eleitorais simuladas, utilizando o contexto das Eleições Gerais.

A proposta é reunir funcionalidades relacionadas ao cadastro de pesquisas, coleta de respostas, gerenciamento de candidatos e partidos e análise estatística dos resultados.

O sistema possui finalidade exclusivamente acadêmica e utiliza dados fictícios, sem relação com pesquisas eleitorais reais.

## Modelagem do sistema

Nesta etapa, o foco do projeto está na modelagem do sistema, na identificação das entidades e na definição de seus relacionamentos, servindo como base para o desenvolvimento das funcionalidades.

### Diagrama de Classes

O diagrama abaixo representa a modelagem atual do sistema e poderá ser atualizado conforme o desenvolvimento do projeto.

![Diagrama de Classes do Sistema](img/Datagrama_Classes.drawio%20(1).png)

## Tecnologias e ferramentas

- **Java (JDK 21):** linguagem de programação utilizada no projeto.
- **JavaFX & Scene Builder:** tecnologias utilizadas para o desenvolvimento e estruturação da interface gráfica.
- **IntelliJ IDEA:** ambiente de desenvolvimento.
- **Git e GitHub:** controle de versão e colaboração entre os integrantes.
- **Overleaf:** elaboração da documentação acadêmica.

## Como configurar e executar o projeto

Como o projeto não utiliza um gerenciador de dependências externo (`pom.xml`), é necessário vincular as bibliotecas do **JavaFX** e suporte ao **Scene Builder (FXML)** na IDE antes de executar o sistema pela primeira vez.

### 1. Configuração do JavaFX e Scene Builder (IntelliJ IDEA)

Ao clonar o repositório, adicione as dependências do JavaFX pelo repositório Maven integrado da IDE:

1. Abra o projeto no IntelliJ IDEA e acesse **File > Project Structure...** (ou pressione `Ctrl + Alt + Shift + S`).
2. No menu lateral esquerdo, clique em **Libraries**.
3. Clique no ícone **+** (New Project Library) e selecione **From Maven...**.
4. Busque e adicione a biblioteca principal de controles do JavaFX, clicando em **OK** para confirmar:
   ```text
   org.openjfx:javafx-controls:21
   ```
5. Clique novamente em **+ > From Maven...**, busque e adicione a biblioteca de integração com FXML / Scene Builder, clicando em **OK**:
   ```text
   org.openjfx:javafx-fxml:21
   ```
6. Clique em **Apply** e **OK** para salvar as configurações.

*(Opcional: caso deseje visualizar ou editar arquivos `.fxml` graficamente, instale o **Scene Builder** e vincule o executável em **File > Settings > Languages & Frameworks > JavaFX**).*

### 2. Executando o sistema pela classe `Launcher`

Para evitar erros de carregamento dos componentes de runtime do JavaFX (`JavaFX runtime components are missing`), a execução deve ser feita pela classe inicializadora **`Launcher`** (e não diretamente pela classe que estende `Application`):

1. Localize a classe `Launcher.java` dentro do pacote `presentation.view`.
2. Clique com o botão direito sobre o arquivo `Launcher.java` e selecione **Run 'Launcher.main()'**.
3. A tela de login será aberta e a pasta `dados/` será criada automaticamente para persistência dos arquivos.

### 3. Credenciais de primeiro acesso

Na primeira execução, o sistema garante automaticamente a criação de um usuário administrador padrão para acesso inicial e cadastro de novos usuários:

- **E-mail:** `admin@email.com`
- **Senha:** `admin1234`

## Organização do projeto

O sistema está planejado para ser dividido nos seguintes módulos:

- Administração
- Pesquisas
- Análise Estatística e Relatórios

A implementação desses módulos será desenvolvida progressivamente ao longo das próximas etapas.

## Equipe e responsabilidades

- **Faruq:** Análise Estatística
- **João Rafael:** Administração
- **Luís:** Pesquisas

A distribuição das responsabilidades poderá ser ajustada conforme as necessidades do projeto.

## Status do desenvolvimento

**Etapa atual:** Entrega final.

O sistema encontra-se em fase final de desenvolvimento. Os módulos de Administração, Pesquisas e Análise Estatística e Relatórios foram implementados em Java, com interface gráfica em JavaFX e persistência dos dados por serialização de objetos.

O módulo de Coleta e Amostra não foi concluído. A estrutura do sistema já está preparada para receber esse módulo em uma evolução futura.

---

Projeto acadêmico — Ciência da Computação | Universidade de Brasília (UnB).

## Contribuidores

<a href="https://github.com/LuisMoura18"><img src="https://github.com/LuisMoura18.png?size=80" width="80" alt="Luís André"></a>
<a href="https://github.com/Silverlux846"><img src="https://github.com/Silverlux846.png?size=80" width="80" alt="Faruq Kolapo"></a>
<a href="https://github.com/Rafael-Joao"><img src="https://github.com/Rafael-Joao.png?size=80" width="80" alt="João Rafael"></a>
