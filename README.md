# Controle de Gastos

Aplicativo Android desenvolvido em Kotlin utilizando **Jetpack Compose** para a interface e **Room** para persistência de dados. O objetivo do projeto é ajudar os usuários a registrar seus gastos mensais, definir limites de despesas por categoria e mensal, e acompanhar esses limites.

## Funcionalidades

- ✅ Cadastro de gastos com descrição, valor e categoria.
- ✅ Edição e exclusão de gastos registrados.
- ✅ Limites de gastos por categoria e mensal.
- ✅ Validação de limite: impede novos registros se o valor ultrapassar o limite definido.
- ✅ Visualização de todos os gastos.
- ✅ Consulta dos limites já definidos.
- ✅ Categorias com menu suspenso (dropdown) pré-definido para padronizar os registros.
- ✅ Interface 100% construída com Jetpack Compose.

## Tecnologias utilizadas

- Kotlin
- Jetpack Compose
- Room (banco de dados local)
- Coroutines
- Android Studio

## Estrutura do Projeto

- `MainActivity.kt` – Tela principal e navegação entre telas (Cadastro, Lista de Gastos, Definir Limite, Consultar Limites)
- `AppDatabase.kt` – Configuração do Room
- `Gasto.kt` – Data class representando um gasto
- `Limite.kt` – Data class representando um limite por categoria
- `GastoDao.kt` – DAO com operações CRUD para os gastos
- `LimiteDao.kt` – DAO com operações de inserção e consulta de limites
- `ui.theme/` – Cores e estilos do Material 3

## Como rodar o projeto

1. **Clone este repositório**
   ```bash
   git clone https://github.com/seu-usuario/controle-de-gastos.git
