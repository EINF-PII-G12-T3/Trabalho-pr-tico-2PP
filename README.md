SGMF — Sistema de Gestão Escolar e Financeira

O **SGMF** é um sistema em Java desenvolvido para a automatização de processos académicos e controlo de fluxos financeiros institucionais, operando de forma eficiente através de manipulação de ficheiros de texto.

---

Funcionalidades Principais

  - Controlo de Acesso:** Autenticação baseada em perfis com níveis de acesso para **Administrador**, **Secretário** e **Financeiro**.
  - Gestão Académica:** Registo de matrículas, inscrições de estudantes, alocação de turmas e controlo de cursos.
  - Gestão Financeira:** Pagamento de propinas mensais, liquidação anual, pagamento de emolumentos e emissão automática de comprovativos.
  - Relatórios Automáticos:** Exportação de históricos de alunos, balanço de pagamentos e listagens oficiais de funcionários e turmas.

---

Divisão do Sistema (Estrutura)

  src/entidades/` — Estruturas de dados principais (Aluno, Curso, Funcionario, Turma, etc.).
  src/menus/` — Interfaces de consola dinâmicas para cada perfil de utilizador.
  src/servicos/` — Motores de regras de negócio (Gestão de Finanças, Autenticação, Matrículas).
  src/persistencia/` — Camada responsável pela leitura e escrita dos dados nos ficheiros `.txt`.
  src/relatorios/` — Módulo de geração e exportação de relatórios estruturados.
  saidas/` — Pasta onde o sistema grava os comprovativos, recibos e históricos gerados.


Tecnologias Utilizadas:

* **Linguagem:** Java (Modo Estruturado)
* **Armazenamento:** Ficheiros Planos (`.txt`)
* **Ambiente:** Apache NetBeans IDE

---

Autores:

* **Welliton Costa** 
* **Sabino Gaspar**
* **Luis Dalton**
