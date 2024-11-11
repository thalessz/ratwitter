![Banner](https://github.com/AnonJV/ratwitter/blob/main/Images/Banner_Ratwitter.png)
<p align='center'>
  <img alt="Static Badge" src="https://img.shields.io/badge/Open-Source-%237d15a8">
  <img alt="Static Badge" src="https://img.shields.io/badge/Licen%C3%A7a-GPL-%23e02c2d">
  <img alt="GitHub contributors" src="https://img.shields.io/github/contributors-anon/thalessz/ratwitter?label=Contribuidores&color=%23100f99">
  <img alt="GitHub Downloads (all assets, all releases)" src="https://img.shields.io/github/downloads/thalessz/ratwitter/total?label=Downloads&color=%23f4f30d">
  <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/thalessz/ratwitter?label=%C3%9Altimo%20commit&color=rgb(244%2C%20243%2C%2013)">
</p>
<h1 align='center'>Ratwitter</h1>

**Ratwitter** é um projeto acadêmico desenvolvido na disciplina de **Programação para Dispositivos Móveis**, orientado pelo professor **Edivaldo Serafim**. Este projeto é um aplicativo Android que simula uma rede social de textos, onde os usuários podem compartilhar ideias, pensamentos, e sentimentos, criando uma experiência de troca e interação.

### :rocket: Tecnologias Utilizadas
- **Android** com **Java**: Aplicativo Android nativo, que faz a comunicação com a API.
- **Node.js ou Python** para API: Back-end construído para gerenciar rotas e operações do banco de dados.
- **Retrofit**: Utilizado no app Android para facilitar a comunicação com a API RESTful.
- **MySQL**: Banco de dados que armazena toda as informações do aplicativo.

### :star: Funcionalidades

- **Sistema de Login e Cadastro**: Usuários podem criar contas e fazer login para acessar a plataforma.
- **Feed de Publicações**: Veja posts de outros usuários e compartilhe suas próprias ideias na forma de texto.
- **Curtir Publicações**: Expresse sua aprovação com o botão de curtir em cada post.
- **API Integrada**: Comunicação via API RESTful para envio e recebimento de dados, garantindo a atualização do feed e das curtidas em tempo real.

### :electric_plug: Integração com a API

O aplicativo utiliza **Retrofit** para consumir a **API RESTful** desenvolvida em Node.js ou Python. Essa API gerencia o back-end do projeto, sendo responsável pelo cadastro de usuários, autenticação e manipulação de dados, como criação e visualização dos posts.

### :open_file_folder: Acesso ao Repositório

Este projeto é **código aberto**, então você pode ver, modificar e contribuir com o código! Como parte da integração do repositório com o aplicativo, dados dinâmicos, como atualizações de versões ou funcionalidades, podem ser carregados diretamente do próprio repositório, aprimorando a experiência do usuário.

### :gear: Instruções para Uso e Contribuição

1. Clone o repositório:
    ```bash
    git clone https://github.com/thalessz/ratwitter.git
    ```
2. Configure a API (Node.js ou Python) e inicie o servidor, presente dentro da pasta do projeto android, no diretório **/api**.
3. Configure o aplicativo Android para utilizar o endereço da API. Por padrão definido como localhost:5000; 
4. Use o Android Studio para compilar e executar o app em um dispositivo ou emulador Android.

### :clap: Contribuições

Sinta-se à vontade para contribuir! Abra uma **issue** para sugestões ou relatórios de bug, ou submeta um **pull request** para melhorias.
