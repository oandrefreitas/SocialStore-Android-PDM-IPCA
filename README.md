//******************************PORTUGUÊS******************************//

# LojaSocial-Android-IPCA

Aplicação móvel Android desenvolvida em Kotlin com Jetpack Compose para apoio à gestão de uma loja social. A aplicação permite gerir beneficiários, visitas, fluxo de caixa e utilizadores/voluntários, recorrendo ao Firebase (Authentication e Firestore) para autenticação e persistência de dados. Foi desenvolvida em contexto académico no IPCA.

## Funcionalidades principais

* **Autenticação e sessão**
  * Login com email e palavra-passe (e suporte para login com telefone).
  * Gestão de sessão através de `userSession`.
  * Redirecionamento automático consoante o estado de autenticação (ecrãs de login / home).

* **Ecrã inicial (Home)**
  * Navegação centralizada para as principais áreas da aplicação.
  * Acesso rápido a perfil, gestão de voluntários, fluxo de caixa e beneficiários.

* **Gestão de beneficiários**
  * Registo de novos beneficiários com dados como nome, contacto, referência, agregado familiar, crianças e nacionalidade.
  * Listagem e consulta de beneficiários ativos.
  * Atualização de dados de beneficiários através de `BeneficiarioRepository` (Firestore).

* **Gestão de visitas**
  * Registo e listagem de visitas associadas a beneficiários.
  * Pesquisa e filtragem de visitas.
  * Utilização de `VisitaRepository` para leitura e escrita de dados no Firestore.
  * Componentes de UI dedicados (por exemplo, `VisitaListScreen`, `VisitaDialog`, `SearchRowView`).

* **Gestão de fluxo de caixa (Cash Flow)**
  * Registo de transações (entradas e saídas) com `CashRepository`.
  * Visualização de histórico de transações em lista (`CashFlowView`).
  * Cálculo de saldo e resumo financeiro em componentes como `BalanceRow`.
  * Filtros por intervalo de datas para análise de movimentos.

* **Gestão de utilizadores/voluntários**
  * Registo e listagem de utilizadores (voluntários).
  * Operações suportadas pelo `UserRepository` sobre coleções do Firestore.
  * Interfaces específicas para gestão interna da equipa.

* **Perfil e edição de dados**
  * Ecrãs de perfil e edição de utilizador (`EditProfileView`, `EditProfileViewModel`).
  * Atualização de informação pessoal diretamente na base de dados.

* **Interface moderna com Jetpack Compose**
  * Navegação entre ecrãs com `NavHost` e rotas dedicadas.
  * Utilização de Material 3 e theming (ficheiros `Color.kt`, `Theme.kt`, `Type.kt`).
  * Componentes reutilizáveis para formulários, listas e diálogos.

//******************************ENGLISH******************************//

# LojaSocial-Android-IPCA

Android mobile application developed in Kotlin with Jetpack Compose to support the management of a community social store. The app allows managing beneficiaries, visits, cash flow and volunteer users, relying on Firebase (Authentication and Firestore) for user authentication and data persistence. It was developed in an academic context at IPCA.

## Main Features

* **Authentication and session management**
  * Email and password login (with support for phone-based login).
  * Session handling through `userSession`.
  * Automatic redirection based on authentication state (login vs home screens).

* **Home screen**
  * Central navigation hub to the main areas of the app.
  * Quick access to profile, volunteer management, cash flow and beneficiaries.

* **Beneficiary management**
  * Registration of new beneficiaries with data such as name, contact, reference, household size, children and nationality.
  * Listing and viewing of active beneficiaries.
  * Update of beneficiary records via `BeneficiarioRepository` (Firestore).

* **Visit management**
  * Registration and listing of visits associated with beneficiaries.
  * Search and filtering of visits.
  * Use of `VisitaRepository` for reading and writing visit data in Firestore.
  * Dedicated UI components (e.g. `VisitaListScreen`, `VisitaDialog`, `SearchRowView`).

* **Cash flow management**
  * Recording of income and expense transactions using `CashRepository`.
  * Display of transaction history in `CashFlowView`.
  * Balance calculation and financial summary via components such as `BalanceRow`.
  * Date range filters for analysing cash movements.

* **User/volunteer management**
  * Registration and listing of users (volunteers).
  * Operations supported by `UserRepository` on Firestore collections.
  * Specific interfaces for internal team management.

* **Profile and user data editing**
  * Profile and edit screens (`EditProfileView`, `EditProfileViewModel`).
  * Update of personal information directly in the database.

* **Modern UI with Jetpack Compose**
  * Navigation between screens using `NavHost` and dedicated routes.
  * Use of Material 3 and theming (`Color.kt`, `Theme.kt`, `Type.kt`).
  * Reusable components for forms, lists and dialogs.
