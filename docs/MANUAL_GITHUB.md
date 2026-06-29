
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



# Fluxo Alternativo — Fork + Pull Request

Caso algum integrante não possua permissão de escrita no repositório principal ou prefira trabalhar de forma isolada, pode utilizar o fluxo de **Fork + Pull Request**.

## 1. Criar um Fork do Repositório

Acesse o repositório:

```text
https://github.com/Nuillexe/school-library-manager
```

Clique em:

```text
Fork
```

Isso criará uma cópia do projeto em sua conta do GitHub.

Exemplo:

```text
https://github.com/SEU_USUARIO/school-library-manager
```

---

## 2. Clonar o Fork

```bash
git clone https://github.com/SEU_USUARIO/school-library-manager.git

cd school-library-manager
```

---

## 3. Acessar a Branch do Projeto

```bash
git checkout archive/Lp2

git pull origin archive/Lp2
```

---

## 4. Realizar as Alterações

Faça normalmente as modificações relacionadas à sua tarefa.

---

## 5. Salvar as Alterações

```bash
git add .

git commit -m "Descrição da alteração realizada"
```

Exemplo:

```bash
git commit -m "Implementa carregarUsuarios no PersistenceManager"
```

---

## 6. Enviar para o Seu Fork

```bash
git push origin archive/Lp2
```

Neste caso, o envio será feito para o seu próprio repositório, portanto não será necessária permissão no repositório principal.

---

## 7. Abrir um Pull Request

Acesse seu Fork no GitHub.

Clique em:

```text
Compare & Pull Request
```

ou

```text
Contribute
→ Open Pull Request
```

Configure:

```text
Base Repository: Nuillexe/school-library-manager
Base Branch: archive/Lp2

Head Repository: seu-fork/school-library-manager
Head Branch: archive/Lp2
```

---

## 8. Aguardar Revisão

O Pull Request será enviado para o repositório principal.

Após a revisão, as alterações poderão ser incorporadas à branch:

```text
archive/Lp2
```

---

## Vantagens

- Evita conflitos diretos na branch compartilhada.
- Permite revisão de código antes da integração.
- Nenhum integrante precisa de permissão de escrita no repositório principal.
- Mantém um histórico organizado das contribuições de cada membro.


