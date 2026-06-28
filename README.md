# LibQueue - Pendências de Desenvolvimento

## 🔗 Acesso Rápido

- [📋 Quadro de Tarefas](#-quadro-de-tarefas)
- [🎯 Objetivo da Atualização](#-objetivo-da-atualização)
- [📝 Estrutura dos Arquivos](#-estrutura-dos-arquivos)
- [📖 Manual de Trabalho com GitHub](#manual-de-trabalho-com-github---libqueue)

## 📊 Status

| Status | Significado |
|----------|----------|
| ⬜ | Não iniciado |
| 🟨 | Em andamento |
| ✅ | Concluído |
| ❌ | Bloqueado |

---

# 📋 Quadro de Tarefas

| ID | Tarefa | Responsável | Status | Observação |
|----|---------|-------------|---------|---------|
| T01 | Estruturar arquivos iniciais | Indaia | ✅ ||
| T02 | Adicionar mais dados de teste | Indaia | ✅ ||
| T03 | Implementar carregarLivros() | Charles | 🟨 ||
| T04 | Implementar carregarUsuarios() | Charles | 🟨 ||
| T05 | Implementar carregarEmprestimos() | Charles | 🟨 ||
| T06 | Implementar carregarReservas() | Charles | 🟨 ||
| T07 | Implementar carregarIds() | Charles | ✅ ||
| T08 | Implementar salvarLivro() | Maria Eduarda | ⬜ ||
| T09 | Implementar salvarUsuario() | Maria Eduarda | ⬜ ||
| T10 | Implementar salvarEmprestimo() | Maria Eduarda | ⬜ ||
| T11 | Implementar salvarReserva() | Maria Eduarda | ⬜ ||
| T12 | Implementar sobrescreverLivros() | Maria Eduarda | ⬜ ||
| T13 | Implementar sobrescreverUsuarios() | Maria Eduarda | ⬜ ||
| T14 | Implementar sobrescreverEmprestimos() | Maria Eduarda | ⬜ ||
| T15 | Implementar sobrescreverReservas() | Maria Eduarda | ⬜ ||
| T16 | Criar construtores auxiliares | Indaia | 🟨 ||
| T17 | Implementar reconstrução dos relacionamentos | Emanuel | ✅ ||
| T18 | Criar métodos de persistência na BibliotecaRepository | Emanuel | ✅ ||
| T19 | Ajustar Services para persistência em arquivos | Emanuel | ✅ ||
| T20 | Substituir EDs por Collections Framework | Ana Clara | ⬜ ||
| T21 | Ajustar menu superior do Inventário | Kaique | 🟨 | Falta adicionar ação ao botão de deslogar e trocar "nome aqui" pelo nome do usuario logado|
| T22 | Reduzir efeito de intermitência das telas | Kaique | 🟨 |
| T23 | Ajustar login na tela de bibliotecario, n é possivel entrar nessa tela | Kaique |🟨 

---

# Objetivo da Atualização

O sistema deixará de utilizar a classe `DataBaseSeed` como mecanismo de persistência em memória e passará a utilizar arquivos `.txt` para armazenamento permanente dos dados.

A nova arquitetura será composta principalmente por:

- `PersistenceManager` → responsável pela leitura e escrita dos arquivos.
- `BibliotecaRepository` → responsável por manter os dados carregados, reconstruir relacionamentos entre objetos e fornecer acesso às informações do sistema.

Arquivos utilizados:

- `usuarios.txt`
- `livros.txt`
- `emprestimos.txt`
- `reservas.txt`
- `ids.txt`

---

# Tarefas Detalhadas

## T01 - Estruturar arquivos iniciais
**Responsável:** Indaia

Migrar os dados atualmente presentes no `DataBaseSeed` para:

- `usuarios.txt`
- `livros.txt`
- `ids.txt`

Também validar se os arquivos estão compatíveis com os métodos de leitura que serão implementados.

---

## T02 - Adicionar mais dados de teste
**Responsável:** Indaia

Adicionar:

- Novos usuários.
- Novos livros.
- Novos exemplares.
- Casos para testes de empréstimo.
- Casos para testes de reserva.

Objetivo: aumentar a cobertura de testes da aplicação.

---

## T03 - Implementar carregarLivros()
**Responsável:** Charles
**Classe:** PeristenceManager

Implementar:

```java
carregarLivros()
```

O método deverá:

- Ler `livros.txt`.
- Instanciar objetos Livro.
- Retornar uma estrutura contendo todos os livros carregados.

---

## T04 - Implementar carregarUsuarios()
**Responsável:** Charles
**Classe:** PeristenceManagerImplementar:

```java
carregarUsuarios()
```

O método deverá:

- Ler `usuarios.txt`.
- Instanciar objetos Usuario.
- Retornar a lista carregada.

---

## T05 - Implementar carregarEmprestimos()
**Responsável:** Charles
**Classe:** PeristenceManagerImplementar:

```java
carregarEmprestimos()
```

Observação:

- Inicialmente poderá utilizar usuários e livros temporários para permitir a reconstrução posterior dos relacionamentos.

---

## T06 - Implementar carregarReservas()
**Responsável:** Charles
**Classe:** PeristenceManagerImplementar:

```java
carregarReservas()
```

Observação:

- Inicialmente poderá utilizar títulos temporários.

---

## T07 - Implementar carregarIds()
**Responsável:** Charles
**Classe:** PeristenceManagerImplementar:

```java
carregarIds()
```

Responsável por carregar todos os IDs válidos da instituição.

---

## T08 - Implementar salvarLivro()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:

```java
salvarLivro(Livro livro)
```

Utilizar escrita incremental (`append`) para adicionar novos registros sem apagar os existentes.

---

## T09 - Implementar salvarUsuario()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:

Implementar:

```java
salvarUsuario(Usuario usuario)
```

Utilizar escrita incremental.

---

## T10 - Implementar salvarEmprestimo()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:
Implementar:

```java
salvarEmprestimo(Emprestimo emprestimo)
```

Utilizar escrita incremental.

---

## T11 - Implementar salvarReserva()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:
Implementar:

```java
salvarReserva(Reserva reserva)
```

Utilizar escrita incremental.

---

## T12 - Implementar sobrescreverLivros()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:
Implementar:

```java
sobrescreverLivros(...)
```

Utilizado principalmente em operações de remoção ou atualização.

---

## T13 - Implementar sobrescreverUsuarios()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:
Implementar:

```java
sobrescreverUsuarios(...)
```

Utilizado principalmente em operações de remoção ou atualização.

---

## T14 - Implementar sobrescreverEmprestimos()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:

Implementar:

```java
sobrescreverEmprestimos(...)
```

Utilizado principalmente em operações de remoção ou atualização.

---

## T15 - Implementar sobrescreverReservas()
**Responsável:** Maria Eduarda
**Classe:** PeristenceManagerImplementar:

Implementar:

```java
sobrescreverReservas(...)
```

Utilizado principalmente em operações de remoção ou atualização.

---

## T16 - Criar construtores auxiliares
**Responsável:** Indaia

Criar construtores simplificados para auxiliar no carregamento dos dados.

Exemplos:

```java
Usuario(String id)

Livro(long id)

Titulo(String isbn)
```

Esses construtores serão utilizados para criação de objetos temporários durante a leitura dos arquivos.

---

## T17 - Implementar reconstrução dos relacionamentos
**Responsável:** Emanuel

Implementar no `BibliotecaRepository`.

Objetivos:

- Substituir referências temporárias pelas referências reais.
- Associar empréstimos aos respectivos usuários.
- Associar reservas aos respectivos usuários.
- Associar empréstimos aos respectivos livros.
- Associar reservas aos respectivos títulos.
- Reconstruir a lista de títulos.
- Reconstruir as filas de reserva.

---

## T18 - Criar métodos de persistência na BibliotecaRepository
**Responsável:** Emanuel 

Criar métodos responsáveis por manter sincronizados os arquivos e as estruturas em memória.

Exemplos:

```java
adicionarLivro(...)
removerLivro(...)

adicionarUsuario(...)
removerUsuario(...)

adicionarEmprestimo(...)
removerEmprestimo(...)

adicionarReserva(...)
removerReserva(...)
```

---

## T19 - Ajustar Services com base em Biblioteca Repositori 
**Responsável:** Charles

Atualizar os Services para utilizar a nova camada de persistência.

Impactos:

- Cadastro de usuários.
- Cadastro de livros.
- Empréstimos.
- Devoluções.
- Reservas.

---

## T20 - Substituir EDs por Collections Framework
**Responsável:** Ana Clara

Avaliar substituição das estruturas implementadas manualmente por estruturas nativas do Java.

Principais candidatas:

```java
List
ArrayList
Queue
PriorityQueue
Map
```

Garantir compatibilidade com os DAOs existentes.

---

## T21 - Ajustar menu superior do Inventário
**Responsável:** Kaique

Corrigir:

- Alinhamento.
- Espaçamento.
- Organização visual.
- Responsividade.

---

## T22 - Reduzir efeito de intermitência das telas
**Responsável:** Kaique

Investigar e corrigir o efeito de "piscar" durante a navegação entre telas.

Possíveis causas:

- Recarregamento excessivo de FXML.
- Troca completa de Scene.
- Recriação desnecessária de componentes.

---

# Estrutura dos Arquivos

## usuarios.txt

```text
id | nome | email | senha | tipo
```

---

## livros.txt

```text
id | nome | autor | isbn | genero | descricao | dataPublicacao | disponivel
```

---

## emprestimos.txt

```text
id | dataEmprestimo | dataDevolucao | atrasado | idUsuario | idLivro
```

---

## reservas.txt

```text
id | idUsuario | isbnTitulo | dataReserva
```

---

## ids.txt

Lista contendo todos os IDs institucionais válidos.

Exemplo:

```text
s000001
s000002
s000003
p000001
p000002
l000001
```

---

# Dependências entre Tarefas

1. T01 deve ser concluída antes da validação dos métodos de leitura.
2. T16 deve ser concluída antes da implementação completa dos carregamentos.
3. T03–T07 devem ser concluídas antes da reconstrução dos relacionamentos (T17).
4. T17 depende da conclusão dos carregamentos.
5. T18 depende da conclusão dos métodos de leitura.
6. T19 depende da implementação da nova persistência.
7. As tarefas de interface (T21 e T22) podem ser realizadas independentemente.

---

# Observações

- A classe `DataBaseSeed` será removida.
- Toda persistência passará a ser feita via arquivos.
- O `PersistenceManager` será responsável apenas pela leitura e escrita dos arquivos.
- O `BibliotecaRepository` será responsável por montar o estado completo do sistema.
- Os relacionamentos entre objetos deverão ser reconstruídos após o carregamento dos dados.
- Novas funcionalidades deverão utilizar o `BibliotecaRepository` como ponto central de acesso aos dados.


# Manual de Trabalho com GitHub - LibQueue

## 1. Clonar o Repositório

```bash
git clone https://github.com/Nuillexe/school-library-manager.git
cd school-library-manager
```

---

## 2. Entrar na Branch do Projeto

Todo o desenvolvimento desta etapa será realizado na branch:

```bash
git checkout archive/Lp2
```

Caso necessário:

```bash
git checkout -b archive/Lp2 origin/archive/Lp2
```

⚠️ Não realizar alterações diretamente na `main`.

---

## 3. Atualizar Antes de Começar

Sempre execute:

```bash
git pull origin archive/Lp2
```

Isso garante que você está trabalhando na versão mais recente do projeto.

---

## 4. Realizar Alterações

Implemente apenas as tarefas sob sua responsabilidade e evite modificar arquivos de outros membros sem necessidade.

---

## 5. Criar um Commit

Verifique os arquivos alterados:

```bash
git status
```

Adicione as alterações:

```bash
git add .
```

Realize o commit:

```bash
git commit -m "Descrição da alteração"
```

Exemplos:

```bash
git commit -m "Implementa carregarUsuarios"
git commit -m "Implementa salvarEmprestimo"
git commit -m "Refatora tela de inventario"
```

---

## 6. Enviar para o GitHub

```bash
git push origin archive/Lp2
```

---

## 7. Atualizar o README

Ao iniciar ou concluir uma tarefa, atualizar o status correspondente:

* ⬜ Não iniciado
* 🟨 Em andamento
* ✅ Concluído
* ❌ Bloqueado

---

## Regras da Equipe

* Não realizar push na `main`.
* Sempre executar `git pull` antes de começar.
* Utilizar mensagens de commit claras.
* Atualizar o README conforme o andamento das tarefas.
* Em caso de conflito, comunicar a equipe antes de realizar alterações.

---

## Fluxo Resumido

```bash
git clone https://github.com/Nuillexe/school-library-manager.git

cd school-library-manager

git checkout archive/Lp2

git pull origin archive/Lp2

# realizar alterações

git add .

git commit -m "Descrição da alteração"

git push origin archive/Lp2
```

Ao final do desenvolvimento, será realizado um Pull Request da branch `archive/Lp2` para a `main`.

