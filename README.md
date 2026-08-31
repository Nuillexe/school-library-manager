# 📚 LibQueue — Sistema de Gerenciamento de Biblioteca Escolar
![logo do LibQueue](docs/Telas/logo.png)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

---

## 📑 Índice

- [Descrição do Projeto](#-descrição-do-projeto)
- [Status do Projeto](#-status-do-projeto)
- [Funcionalidades e Demonstração da Aplicação](#-funcionalidades-e-demonstração-da-aplicação)
- [Acesso ao Projeto](#-acesso-ao-projeto)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pessoas Contribuidoras](#-pessoas-contribuidoras)
- [Pessoas Desenvolvedoras do Projeto](#-pessoas-desenvolvedoras-do-projeto)
- [Licença](#-licença)

---

## 📖 Descrição do Projeto

O **LibQueue** é um sistema de gerenciamento de biblioteca escolar desenvolvido em **Java** e **JavaFX**, destinado ao controle de usuários, acervo, empréstimos, devoluções e reservas.

O projeto começou a ser desenvolvido no curso de Sistemas de Informação do Instituto Federal da Bahia (IFBA) — Campus Vitória da Conquista, evoluindo ao longo das disciplinas de Estrutura de Dados e Linguagem de Programação II. Atualmente, o projeto é mantido e aprimorado como um projeto pessoal.

Durante seu desenvolvimento, o sistema passou de uma implementação baseada em estruturas de dados desenvolvidas manualmente e persistência em memória para uma arquitetura com utilização das coleções da Java Collections Framework e persistência permanente em arquivos locais.

Entre seus principais recursos, destacam-se o gerenciamento do acervo, o controle de empréstimos e devoluções e o gerenciamento de reservas por meio de **filas de prioridade**, nas quais professores possuem prioridade sobre alunos.

---


## 🚧 Status do Projeto

**Em desenvolvimento — versão estável para demonstração**

O projeto foi concluído como atividade acadêmica e atualmente é mantido como projeto pessoal, recebendo melhorias, refatorações e novas funcionalidades.

---

## ⚙️ Funcionalidades e Demonstração da Aplicação

### 👤 Gerenciamento de Usuários

- Cadastro de novos usuários;
- Login por e-mail e senha;
- Controle de acesso por perfil;
- Perfis de:
  - Aluno;
  - Professor;
  - Bibliotecário;
- Validação de identificadores institucionais.

### 📚 Gerenciamento do Acervo

- Cadastro de novos Exemplares;
- Consulta de obras por ISBN;
- Agrupamento de exemplares pertencentes à mesma obra;
- Controle de disponibilidade;
- Inclusão e remoção de exemplares.

### 🔄 Empréstimos e Devoluções

- Registro de empréstimos;
- Registro de devoluções;
- Controle de prazos;
- Identificação de empréstimos atrasados;
- Histórico de empréstimos;
- Atualização da disponibilidade dos exemplares.

### ⏳ Reservas com Prioridade

- Solicitação de reservas;
- Filas independentes para cada título;
- Prioridade para professores em relação a alunos;
- Organização das reservas pela prioridade e ordem de solicitação;
- Atendimento automático da primeira reserva após a devolução de um exemplar.

### 🖥️ Demonstração

As capturas de tela a seguir apresentam o funcionamento do sistema utilizando a base de dados de demonstração disponibilizada no projeto. Os dados utilizados são fictícios e foram criados exclusivamente para fins acadêmicos e de demonstração.
#### Tela de Autenticação

![tela de login](docs/Telas/login.png)

![tela de cadastro](docs/Telas/cadastro.png)

#### Interfaces do Usuário

![Tela de catálogo](docs/Telas/Usuario/catalogo.png)

*Catálogo de obras disponíveis no acervo.*

![Tela de empréstimos](docs/Telas/Usuario/emprestimos%20com%20emprestimos.png)

*Empréstimos ativos do usuário.*

![Tela de reservas](docs/Telas/Usuario/reservas_com_reservas.png)

*Reservas realizadas pelo usuário.*
##### Detalhes de uma Obra

![Detalhes de uma obra](docs/Telas/Usuario/dodos_do_livro.png)

*Detalhes de uma obra e suas informações de disponibilidade.*

![Empréstimo atrasado](docs/Telas/Usuario/dados_do_livro_impedindo_o_usuario_de_pegalo.png)

*Restrição de empréstimo quando o usuário possui uma devolução atrasada ou ja tenha pegado emprestado um exemplar daquela obra.*)

#### Interfaces do Bibliotecário

![Dashboard](docs/Telas/Bibliotecario/dashboard_com_dados.png)

*Dashboard com indicadores gerais do sistema.*

![Inventário](docs/Telas/Bibliotecario/inventario.png)

*Gerenciamento do inventário do acervo.*

![Detalhes do título](docs/Telas/Bibliotecario/detalhe_titulo_com_livro_indisponivel.png)

*Gerenciamento dos exemplares associados a uma obra.*

![Controle de empréstimos](docs/Telas/Bibliotecario/controle_de_devoluçoes_com_emprestimos.png)

*Controle de empréstimos e devoluções.*

![Controle de reservas](docs/Telas/Bibliotecario/controle_das_reservas.png)

*Gerenciamento das filas de reservas.*
---

## 🔗 Acesso ao Projeto

**Repositório:** https://github.com/Nuillexe/school-library-manager

### Documentação

- [Guia de Testes](docs/GUIA_DE_TESTES.md)
- [Manual de Trabalho com GitHub](docs/MANUAL_GITHUB.md)
- [Guia de Tarefas LP2](docs/GUIA_DE_TAREFAS_Lp2.md)

### Documentação Acadêmica

- [Artigo Final de Estrutura de Dados](docs/docs_academicos/Artigo_ED.pdf)
- [Relatório Final de LP2](docs/docs_academicos/Relatorio_LP2.pdf)

### Versões Históricas

- `archive/ed` — versão final da disciplina de Estrutura de Dados;
- `archive/lp2` — versão final da disciplina de Linguagem de Programação II.

---

## 🛠️ Tecnologias Utilizadas

- **Java 17+**
- **JavaFX**
- **FXML**
- **CSS**
- **Maven**
- **Git**
- **GitHub**

### Arquitetura

O projeto utiliza uma arquitetura baseada nos princípios do padrão **Model-View-Controller (MVC)**, complementada pelas camadas de **Service**, **Repository/DAO** e **Util**.

A organização geral dos componentes é:

```text
Model → entidades e estruturas de domínio
View → interfaces FXML e estilização CSS
Controller → controle das interações da interface
Service → regras de negócio e validações
Repository/DAO → gerenciamento e persistência dos dados
Util → funcionalidades auxiliares