# Projeto POO - Gerenciador de Álbuns (nome a definir)

Este é um projeto desenvolvido para a disciplina de Programação Orientada a Objetos (POO). O sistema consiste em um gerenciador de álbuns musicais utilizando a linguagem **Java**, a biblioteca gráfica **JavaFX** para a interface de usuário, e persistência de dados em arquivo binário.

---

## 📂 Estrutura do Projeto

O projeto adota o padrão de arquitetura **MVC (Model-View-Controller)** de forma simplificada e direta:

- **`.vscode/`**: Configurações de execução do editor (parâmetros do JavaFX).
- **`dados/`**: Local de armazenamento do arquivo de dados (`biblioteca.dat`).
- **`src/`**: Código-fonte do sistema.
  - `Main.java`: Ponto de entrada que inicializa a aplicação JavaFX.
  - `model/`: Classes que representam as entidades de dados (Álbuns, Herança, Polimorfismo).
  - `view/`: Componentes visuais e layouts da tela.
  - `controller/`: Lógica de controle que conecta a interface (`view`) às regras de negócio e dados (`model`).

---

## 🛠️ Pré-requisitos e Tecnologias

Para executar este projeto localmente, você precisará ter configurado no seu ambiente:
- **JDK 21** (Java Development Kit)
- **JavaFX SDK 21** (Configurado via argumentos da máquina virtual no `launch.json`)
- **VS Code** com o *Extension Pack for Java*

---

## ⚙️ Como Executar o Projeto

1. Abra a pasta raiz `POO` no seu VS Code.
2. Certifique-se de que a biblioteca do JavaFX está referenciada no seu projeto (em *Referenced Libraries* apontando para a pasta `lib` do seu SDK).
3. Abra o arquivo `src/Main.java`.
4. Clique em **Run** (ou selecione a configuração de execução no menu *Run and Debug*).

---

## 👥 Autora
- **Júlia Bechaire**
