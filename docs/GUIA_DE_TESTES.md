# 📖 Guia de Testes - LibQueue

Este documento apresenta usuários de teste, IDs institucionais disponíveis e sugestões de testes para validar as funcionalidades do sistema.

---

# 🔐 Login no Sistema

O login é realizado utilizando:

* **Email**
* **Senha**

Qualquer usuário cadastrado pode acessar o sistema utilizando suas credenciais.

---

# 👥 Usuários Disponíveis para Teste

## Alunos

| Nome           | Email                                                       | Senha    |
| -------------- | ----------------------------------------------------------- | -------- |
| João Silva     | [joao.silva@email.com](mailto:joao.silva@email.com)         | 123456   |
| Maria Oliveira | [maria.oliveira@email.com](mailto:maria.oliveira@email.com) | abc123   |
| Ana Silva      | [ana.silva@email.com](mailto:ana.silva@email.com)           | senha123 |
| Lucas Souza    | [lucas.souza@email.com](mailto:lucas.souza@email.com)       | aluno4   |
| Beatriz Santos | [beatriz.santos@email.com](mailto:beatriz.santos@email.com) | aluno5   |
| Gabriel Rocha  | [gabriel.rocha@email.com](mailto:gabriel.rocha@email.com)   | aluno6   |
| Zeca Camargo   | [zeca.c@email.com](mailto:zeca.c@email.com)                 | aluno50  |

---

## Professores

| Nome            | Email                                                         | Senha    |
| --------------- | ------------------------------------------------------------- | -------- |
| Alexandro Silva | [alexandro.silva@email.com](mailto:alexandro.silva@email.com) | prof123  |
| Ana Pereira     | [ana.pereira@email.com](mailto:ana.pereira@email.com)         | senha456 |
| Marcos Antônio  | [marcos.antonio@email.com](mailto:marcos.antonio@email.com)   | prof3    |
| Fernanda Carmo  | [fernanda.carmo@email.com](mailto:fernanda.carmo@email.com)   | prof4    |
| Roberto Mendes  | [roberto.mendes@email.com](mailto:roberto.mendes@email.com)   | prof5    |

---

## Bibliotecários

| Nome              | Email                                                         | Senha    |
| ----------------- | ------------------------------------------------------------- | -------- |
| Admin BiblioQueue | [admin@biblioteca.com](mailto:admin@biblioteca.com)           | admin    |
| Kaique Oliveira   | [kaique.oliveira@email.com](mailto:kaique.oliveira@email.com) | admin123 |
| Maria Eduarda     | [maria.eduarda@email.com](mailto:maria.eduarda@email.com)     | admin456 |

---

# 🆔 IDs Institucionais Disponíveis para Novos Cadastros

Os IDs abaixo são válidos e ainda não estão associados a nenhum usuário cadastrado.

## IDs de Alunos Livres

```text
s000007
s000008
s000009
s000010
...
s000050
```

---

## IDs de Professores Livres

```text
p000006
p000007
p000008
p000009
...
p000025
```

> Os IDs `p000001` até `p000005` já estão cadastrados.

---

## IDs de Bibliotecários Livres

```text
l000004
l000005
l000006
l000007
```

> Os IDs `l000001`, `l000002` e `l000003` já estão cadastrados.

---

# 🧪 Testes Recomendados

## Teste de Login

1. Abrir o sistema.
2. Selecionar a opção de login.
3. Utilizar qualquer usuário listado neste documento.
4. Verificar se o acesso ocorre corretamente.

---

## Teste de Cadastro

1. Selecionar a opção de cadastro.
2. Utilizar um ID institucional livre.
3. Informar nome, email e senha.
4. Confirmar o cadastro.
5. Realizar login com a conta criada.

### Exemplo

```text
ID: s000007
Nome: Usuário Teste
Email: teste@email.com
Senha: 123456
```

---

## Teste de Empréstimo

1. Entrar como aluno ou professor.
2. Buscar um livro disponível.
3. Solicitar empréstimo.
4. Verificar se o livro deixa de aparecer como disponível.

---

## Teste de Reserva

1. Escolher um título sem exemplares disponíveis.
2. Realizar uma reserva.
3. Verificar se a reserva é adicionada à fila do título.

---

## Teste de Persistência

1. Realizar empréstimos, reservas ou cadastros.
2. Encerrar a aplicação.
3. Executar novamente o sistema.
4. Confirmar que os dados foram preservados nos arquivos `.txt`.

---

# 📚 Acervo

O sistema já acompanha diversos livros cadastrados para testes de:

* Pesquisa
* Empréstimos
* Reservas
* Controle de disponibilidade
* Gerenciamento de títulos

Fique à vontade para criar novos usuários e explorar todas as funcionalidades do sistema.
