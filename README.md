# 📚 BiblioQueue - Sistema de Gerenciamento de Biblioteca Escolar

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

O **BiblioQueue** é um sistema de gerenciamento de biblioteca escolar desenvolvido em Java com JavaFX. O projeto permite o controle completo de usuários, acervo, empréstimos e reservas, oferecendo diferentes níveis de acesso customizados para alunos, professores e bibliotecários.

O sistema foi construído inicialmente utilizando estruturas de dados em memória e, posteriormente, evoluiu para incluir persistência de dados em arquivos locais, garantindo a consistência e a manutenção dos registros entre as execuções da aplicação.

---

## 🔗 Links Úteis e Documentação

* 📄 [Documentação Geral](docs/)
* 🧪 [📖 Guia de Testes](docs/GUIA_DE_TESTES.md)
* 🌿 [Manual de Trabalho com GitHub](docs/GITHUB_MANUAL.md) — *Ajustar caminho se necessário*
* 📋 [Guia de Tarefas LP2](docs/TAREFAS_LP2.md) — *Ajustar caminho se necessário*

---

## 🎓 Contexto Acadêmico

Este projeto nasceu e está sendo expandido como parte prática das disciplinas do curso de **Sistemas de Informação** no **Instituto Federal da Bahia (IFBA), Campus Vitória da Conquista**.

O histórico de desenvolvimento do sistema está dividido em marcos acadêmicos bem definidos:

### 1. Estrutura de Dados (2026.1)
* **Objetivo:** Implementação da lógica de negócios, gerenciamento de acervo e controle de filas de reserva utilizando persistência em memória. Com o propósito de consolidar os conceitos teóricos da disciplina, as estruturas de dados (como listas e filas de prioridade) foram implementadas de forma totalmente personalizada pela equipe. Os dados iniciais da aplicação eram povoados em tempo de execução por meio de uma classe semente (`DatabaseSeed`), simulando um banco pré-carregado com livros e usuários.
* **Docente:** Prof. Claudio Rodolfo Santos de Oliveira ([@claudiorodolfo](https://github.com/claudiorodolfo)).
* **Nota Histórica:** O estado final e estável desta entrega foi congelado e pode ser acessado diretamente na branch [`archive/ed-final`](https://github.com/Nuillexe/school-library-manager/tree/archive/ed-final).

### 2. Linguagem de Programação II (2026.2 - Em Andamento)
* **Objetivo:** Evoluir a arquitetura do sistema com a implementação de **persistência de dados real** através da manipulação e armazenamento de arquivos locais, além de refatorar o backend para utilizar exclusivamente as coleções nativas da API do Java (*Java Collections*).
* **Docente:** Prof. Alexandro dos Santos Silva ([@alexandrossilva](https://github.com/alexandrossilva)).

---

## 🚀 Funcionalidades Principais

### 👤 Gerenciamento de Usuários
* Cadastro de novos usuários no sistema.
* Login dinâmico utilizando e-mail e senha.
* Níveis de acesso e permissões diferenciadas por perfil: **Aluno**, **Professor** e **Bibliotecário**.

### 📚 Gerenciamento de Acervo
* Cadastro completo de novos títulos e livros.
* Controle rigoroso de estoque e exemplares (Total no acervo vs. Disponíveis).
* Consulta e busca rápida por título ou **ISBN**.

### 🔄 Empréstimos e Devoluções
* Registro e validação de empréstimos em tempo real.
* Controle de devoluções e geração de histórico.
* Atualização automática dos cards de disponibilidade de exemplares.

### ⏳ Reservas com Fila de Prioridade
* Permite a reserva de títulos que estão totalmente indisponíveis no momento.
* Organização automática de uma **fila de prioridade** baseada no tipo de usuário (ex: Professores possuem prioridade).
* Liberação e gerenciamento automático do acervo conforme os livros retornam.

---

## 💾 Persistência de Dados (Fase LP2)

Para garantir que os dados não sejam perdidos ao fechar a aplicação, o sistema agora manipula arquivos de texto locais salvos automaticamente na pasta: `data/`.

**Arquivos persistidos e restaurados automaticamente na inicialização:**
* `livros.txt` — Dados cadastrais do acervo.
* `usuarios.txt` — Registro de usuários e credenciais.
* `emprestimos.txt` — Histórico e transações ativas de empréstimos.
* `reservas.txt` — Fila de espera e gerenciamento de prioridades.
* `ids.txt` — Controle sequencial de chaves primárias e autoincremento.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17+** (Linguagem base do projeto)
* **JavaFX / FXML** (Construção de interfaces modernas e responsivas)
* **CSS** (Estilização customizada das views)
* **Git & GitHub** (Controle de versão e colaboração)

🏗️ Arquitetura do Projeto

O projeto adota estritamente o padrão arquitetural MVC (Model-View-Controller) com uma organização modular de pacotes focada em domínios de negócio:

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

## 🌿 Estrutura de Branches do Repositório

O histórico do projeto está segmentado estrategicamente para facilitar a navegação e a avaliação por parte dos respectivos docentes:

* [`main`](https://github.com/Nuillexe/school-library-manager/tree/main): Estágio atual e final do projeto unificado, contendo a persistência em arquivos e todas as regras de negócio integradas.
* [`archive/ed`](https://github.com/Nuillexe/school-library-manager/tree/archive/ed): Código estável e congelado referente à entrega final da disciplina de **Estrutura de Dados** (execução e persistência exclusivamente em memória).
* [`archive/Lp2`](https://github.com/Nuillexe/school-library-manager/tree/archive/Lp2): Ramificação focada no desenvolvimento e nos requisitos da disciplina de **Linguagem de Programação II**, com ênfase na arquitetura de persistência via manipulação de arquivos locais e desacoplamento da interface JavaFX.

---

## ⚙️ Como Executar o Projeto

### 📋 Pré-requisitos
* **Java JDK 17** ou superior instalado e configurado nas variáveis de ambiente.
* **JavaFX SDK** configurado na sua máquina ou integrado diretamente na sua IDE de preferência.

### 🚀 Passo a Passo
1. Clone o repositório em sua máquina local utilizando o terminal:
   ```bash
   git clone [https://github.com/Nuillexe/school-library-manager.git](https://github.com/Nuillexe/school-library-manager.git)

2. Abra o diretório clonado na sua IDE de preferência (*IntelliJ IDEA*, *Eclipse* ou *VS Code*).
3. Certifique-se de vincular as bibliotecas nativas do **JavaFX** ao *Build Path* ou às dependências do projeto.
4. Execute a classe principal do sistema (localizada no pacote raiz `br.edu.ifba.biblioqueue`).

---

## 👨‍💻 Equipe e Desenvolvimento

Projeto desenvolvido como atividade prática acadêmica por alunos do curso de **Sistemas de Informação** do **Instituto Federal da Bahia (IFBA) – Campus Vitória da Conquista**.

📄 *Este software foi construído e disponibilizado exclusivamente para fins didáticos e educacionais.*
