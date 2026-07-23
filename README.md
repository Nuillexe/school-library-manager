# 📚 BiblioQueue - Sistema de Gerenciamento de Biblioteca Escolar

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

O **BiblioQueue** é um sistema de gerenciamento de biblioteca escolar desenvolvido em **Java** e **JavaFX**, permitindo o controle completo de usuários, acervo, empréstimos e reservas.

O projeto evoluiu ao longo de duas disciplinas do curso de **Sistemas de Informação do IFBA**, passando de uma implementação baseada exclusivamente em estruturas de dados em memória para uma arquitetura com persistência permanente em arquivos locais.

---

## 🔗 Links Úteis

- 📖 [Guia de Testes](docs/GUIA_DE_TESTES.md)
- 🌿 [Manual de Trabalho com GitHub](docs/MANUAL_GITHUB.md)
- 📋 [Guia de Tarefas LP2](docs/GUIA_DE_TAREFAS_Lp2.md)

---

# 🎓 Contexto Acadêmico e Documentação

Este projeto foi desenvolvido como atividade prática das disciplinas do curso de **Sistemas de Informação** do **Instituto Federal da Bahia (IFBA) – Campus Vitória da Conquista**.

## Estrutura de Dados (2026.1)

### Objetivo

Implementação da lógica de negócios, gerenciamento de acervo e controle de filas de reserva utilizando persistência em memória.

Com o propósito de consolidar os conceitos teóricos da disciplina, as estruturas de dados (listas, filas e filas de prioridade) foram implementadas manualmente pela equipe, sem utilização das coleções nativas da linguagem.

Os dados iniciais eram carregados em tempo de execução por meio da classe `DatabaseSeed`, responsável por simular um banco de dados previamente populado.

**Docente:** Prof. Claudio Rodolfo Santos de Oliveira ([claudiorodolfo](https://github.com/claudiorodolfo))

### Documentação

- [Artigo Final de Estrutura de Dados](docs/docs_academicos/Artigo_ED.pdf)

### Nota Histórica

A entrega final da disciplina encontra-se preservada na branch:

- [`archive/ed`](https://github.com/Nuillexe/school-library-manager/tree/archive/ed)

---

## Linguagem de Programação II (2026.1)

### Objetivo

Evoluir a arquitetura do sistema através da implementação de persistência permanente em arquivos locais, adoção das coleções da Java Collections Framework e aprimoramento da interface gráfica utilizando JavaFX.

Nesta etapa foram implementados:

- Persistência de dados em arquivos `.txt`;
- Recuperação automática dos relacionamentos entre objetos;
- Refatoração dos repositórios;
- Melhor organização arquitetural;
- Documentação e guias de utilização;
- Aprimoramentos na interface gráfica.

**Docente:** Prof. Alexandro dos Santos Silva ([alexandrossilva](https://github.com/alexandrossilva))

### Documentação

- [Relatório Final de LP2](docs/docs_academicos/Relatorio_LP2.pdf)
- [Guia de Testes](docs/GUIA_DE_TESTES.md)

### Nota Histórica

A entrega final da disciplina encontra-se preservada na branch:

- [`archive/lp2`](https://github.com/Nuillexe/school-library-manager/tree/archive/Lp2)

---

## Versão Atual

A branch **main** reúne a versão mais recente e estável do projeto, consolidando as funcionalidades desenvolvidas ao longo das disciplinas e servindo como referência principal para demonstração e portfólio.

---

# 🌿 Estrutura de Branches

O repositório mantém versões históricas do projeto associadas às disciplinas em que foi desenvolvido.

| Branch | Descrição |
|----------|----------|
| `main` | Versão consolidada e atual do sistema |
| `archive/ed` | Entrega final da disciplina de Estrutura de Dados |
| `archive/lp2` | Entrega final da disciplina de Linguagem de Programação II |

---

# 🚀 Funcionalidades Principais

## 👤 Gerenciamento de Usuários

- Cadastro de novos usuários;
- Login por e-mail e senha;
- Controle de permissões por perfil;
- Perfis disponíveis:
  - Aluno;
  - Professor;
  - Bibliotecário.

## 📚 Gerenciamento de Acervo

- Cadastro de títulos;
- Cadastro de exemplares;
- Consulta por ISBN;
- Controle de disponibilidade;
- Controle de estoque.

## 🔄 Empréstimos e Devoluções

- Registro de empréstimos;
- Registro de devoluções;
- Histórico de movimentações;
- Atualização automática de disponibilidade.

## ⏳ Reservas com Prioridade

- Reserva de títulos indisponíveis;
- Fila de prioridade para professores;
- Gerenciamento automático de espera;
- Liberação automática após devoluções.

---

# 💾 Persistência de Dados

Para garantir que os dados permaneçam disponíveis entre diferentes execuções da aplicação, o sistema utiliza persistência baseada em arquivos texto armazenados na pasta:

```text
data/
```

Arquivos utilizados:

```text
livros.txt
usuarios.txt
emprestimos.txt
reservas.txt
ids.txt
```

A leitura e escrita são centralizadas pela classe:

```java
PersistenceManager
```

localizada no pacote:

```text
repository
```

---

# 📚 Dados de Demonstração

O projeto é distribuído com uma base inicial de dados para facilitar testes e demonstrações.

Os arquivos da pasta `data/` contêm:

- Usuários de exemplo;
- Livros e exemplares;
- Empréstimos;
- Reservas;
- Identificadores institucionais válidos.

> Os dados presentes em `livros.txt` e `ids.txt` foram gerados com auxílio de Inteligência Artificial exclusivamente para fins acadêmicos e de simulação. Nenhuma informação corresponde a registros reais de uma instituição de ensino.

## Sobre os IDs Institucionais

O sistema exige um identificador institucional válido para permitir o cadastro de novos usuários.

A lista de identificadores válidos encontra-se em:

```text
data/ids.txt
```

Os IDs foram gerados artificialmente para simular uma instituição de ensino e seguem o padrão:

```text
s = Student (Aluno)
p = Professor
l = Bibliotecário
```

Exemplos:

```text
s000007
p000006
l000004
```

Esse mecanismo foi implementado para impedir o cadastro de usuários externos à instituição simulada.

---

# 🛠️ Tecnologias Utilizadas

- Java 17+
- JavaFX
- FXML
- CSS
- Git
- GitHub

---

## 🏗️ Arquitetura do Projeto

O projeto adota uma arquitetura inspirada no padrão MVC (Model-View-Controller), complementada por camadas de serviços e repositórios para promover melhor separação de responsabilidades, organização do código e facilidade de manutenção.

A estrutura dos pacotes está organizada da seguinte forma:
```text
src/main/java/br/edu/ifba/
├── controller/
│   ├── auth/
│   │   ├── Cadastro.java
│   │   └── Login.java
│   └── features/
│       ├── bibliotecario/
│       │   ├── AdicionarLivroController.java
│       │   ├── BottomMenuController.java
│       │   ├── ControleDeEmprestimosController.java
│       │   ├── ControleDeReservasController.java
│       │   ├── DashboardController.java
│       │   ├── InventarioController.java
│       │   └── TopBarController.java
│       └── usuario/
│           ├── CatalogoController.java
│           ├── DetalheLivroController.java
│           ├── EmprestimosController.java
│           └── ReservasController.java
├── enums/
│   └── TipoUsuario.java
├── models/
│   ├── Emprestimo.java
│   ├── Livro.java
│   ├── Reserva.java
│   ├── Titulo.java
│   └── Usuario.java
├── repository/
│   ├── dao/
│   ├── BibliotecaRepository.java
│   └── PersistenceManager.java
├── service/
│   ├── AuthService.java
│   ├── BibliotecarioService.java
│   └── UsuarioService.java
├── util/
├── Launcher.java
└── MainApp.java

src/main/resources/
├── images/
└── views/
├── AuthViews/
│   ├── css/
│   ├── cadastro.fxml
│   └── login.fxml
├── bibliotecarioViews/
│   ├── css/
│   ├── telaPadrao/
│   ├── adicionarLivro.fxml
│   ├── controleDeEmprestimos.fxml
│   ├── controleDeReservas.fxml
│   ├── dashboard.fxml
│   └── inventario.fxml
└── usuarioViews/
├── css/
├── Catalogo.fxml
├── DetalheLivro.fxml
├── Emprestimos.fxml
└── Reservas.fxml
```

---

### Organização das Camadas

- **Model:** entidades centrais do domínio, como Usuário, Livro, Empréstimo e Reserva.
- **View:** interfaces gráficas desenvolvidas com JavaFX e arquivos FXML.
- **Controller:** responsáveis por receber eventos da interface e coordenar as ações do sistema.
- **Service:** implementação das regras de negócio e validações.
- **Repository/DAO:** manipulação e persistência dos dados da aplicação.
- **Util:** funcionalidades auxiliares utilizadas por diferentes módulos.

  
# ⚙️ Como Executar

## Pré-requisitos

- Java JDK 17+
- JavaFX SDK

## Execução

Clone o projeto:

```bash
git clone https://github.com/Nuillexe/school-library-manager.git
```

Abra o projeto em sua IDE preferida:

- IntelliJ IDEA
- Eclipse
- VS Code

Configure o JavaFX e execute a classe principal da aplicação.

---

# 👨‍💻 Equipe e Desenvolvimento

Projeto desenvolvido como atividade acadêmica do curso de **Sistemas de Informação** do **Instituto Federal da Bahia (IFBA) – Campus Vitória da Conquista**.

📄 *Software desenvolvido exclusivamente para fins educacionais e acadêmicos.*
