
# Teste da SISAUDCON 11/2024

## Descrição do Projeto

SISAUDCON é uma aplicação Java desenvolvida para gerenciar dados de usuários e funcionários, com funcionalidades como controle de acesso e operações CRUD. O projeto utiliza PostgreSQL como banco de dados e inclui scripts de migração para facilitar a configuração inicial do banco.

## Requisitos

- **Java Development Kit (JDK)**: versão compatível com a aplicação.
- **PostgreSQL**: é necessário ter um banco de dados PostgreSQL vazio para a aplicação.

## Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL vazio.
2. Configure o acesso ao banco no arquivo de configuração `config/Database.java`, que deve conter os parâmetros de conexão como `host`, `port`, `username`, `password`, e `databaseName`.
3. A aplicação utilizará as migrações SQL localizadas em `database/migrations/` para criar as tabelas necessárias e populá-las automaticamente.

## Deploy

1. Compile o projeto, caso ainda não esteja compilado.
2. Execute a aplicação, que se conectará ao banco de dados e aplicará as migrations necessárias para inicializar as tabelas.

**Observação:** O banco de dados deve estar vazio para que as migrations sejam executadas.

## Primeiro Acesso

Para o primeiro acesso, utilize as seguintes credenciais de administrador:

- **Email**: admin@test.com
- **Senha**: password

## Estrutura do Projeto

```bash
sisaudcon/
├── config/
│   └── Database.java               # Classe de configuração para conexão com o banco de dados PostgreSQL
├── Controllers/
│   ├── EmployeeController.java     # Controller responsável por operações de funcionários
│   ├── UserController.java         # Controller responsável por operações de usuários
│   └── Dtos/
│       ├── EmployeeDto.java        # DTO para dados de funcionário
│       ├── EmployeeResponse.java   # Resposta de API para funcionários
│       ├── PaginateResponse.java   # Resposta de paginação para listagens
│       ├── UserDto.java            # DTO para dados de usuário
│       └── UserResponse.java       # Resposta de API para usuários
├── database/
│   ├── DatabaseConnection.java     # Classe de conexão com o banco de dados
│   ├── DataAccessObject.java       # Objeto de Acesso a Dados para operações de banco
│   ├── MigrationManager.java       # Gerenciador de migrações para aplicação das tabelas no banco
│   └── migrations/
│       ├── 20241102-00001-usuarios.sql # Script SQL para criar a tabela de usuários
│       └── 20241102-00002-funcionarios.sql # Script SQL para criar a tabela de funcionários
├── Models/
│   ├── Employee.java               # Model representando a entidade de funcionário
│   └── User.java                   # Model representando a entidade de usuário
└── Views/
    ├── LoginView.java              # Tela de login para entrada de credenciais do usuário
    ├── EmployeeListView.java       # Tela de listagem e gerenciamento de funcionários
    ├── EmployeeFormView.java       # Formulário para criação e edição de dados de funcionários
    ├── UserListView.java           # Tela de listagem e gerenciamento de usuários
    └── UserFormView.java           # Formulário para criação e edição de usuários
```

### Descrição dos Componentes

- **Config**: Define as configurações de conexão ao banco de dados PostgreSQL.
- **Controllers**: Gerenciam as operações de backend e comunicação com os models. Realizam operações CRUD e regras de negócio para as entidades do sistema.
  - **EmployeeController**: Controla a lógica de operações sobre funcionários.
  - **UserController**: Gerencia operações de usuário, incluindo autenticação e permissões.
  - **Dtos**: Objetos de transferência que padronizam os dados de resposta.
- **database**: Classes e scripts SQL que configuram e conectam o banco de dados.
  - **DatabaseConnection**: Realiza a conexão com o PostgreSQL.
  - **MigrationManager**: Aplica migrações ao banco de dados.
- **Models**: Representam as entidades de dados do sistema e definem os atributos das tabelas do banco de dados.
  - **Employee**: Model de funcionário com atributos como nome, cargo, etc.
  - **User**: Model de usuário que gerencia as informações de acesso.
- **Views**: Camada de apresentação com as interfaces de interação do usuário.
  - **LoginView**: Interface de login para autenticação.
  - **EmployeeListView**: Exibe uma lista de funcionários e permite filtragem.
  - **EmployeeFormView**: Formulário de criação e edição de funcionários.
  - **UserListView**: Exibe uma lista de usuários e gerencia permissões.
  - **UserFormView**: Formulário para criação e edição de usuários.

---

Este README fornece uma visão geral da estrutura do projeto construido para o teste da SISAUDCON, cobrindo o propósito, requisitos, configuração, deploy e descrição dos principais componentes.
