# 🎬 AgCast - Browser com Detecção de Vídeos

Este projeto é um aplicativo Android desenvolvido em **Kotlin** com **Jetpack Compose**, que incorpora um navegador (`WebView`) capaz de detectar vídeos em páginas web e exibir uma lista dos vídeos encontrados em um **ModalBottomSheet**.

---

## 🚀 Funcionalidades

- Navegação web com `WebView`
- Detecção automática de vídeos (`.mp4`, `.m3u8`, `.webm`) via:
  - Injeção de **JavaScript** em páginas carregadas
  - Interceptação de requisições (`shouldInterceptRequest`)
- Exibição dos vídeos detectados em um **BottomSheet**
- Integração com **ViewModel** para gerenciamento de estado:
  - URL atual
  - Lista de vídeos detectados
  - Controle de abertura/fechamento do BottomSheet
- Botão de acesso rápido para abrir a lista de vídeos encontrados

---

## 🛠️ Tecnologias Utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Material3**
- **ViewModel (AndroidX Lifecycle)**
- **WebView**
- **JavaScript Interface**

---

## 📂 Estrutura do Código

- `BrowserScreen.kt` → Composable principal com o `WebView` e UI
- `BrowserViewModel.kt` → Gerencia estados (`url`, `videoList`, `showSheet`)
- `VideoInfo.kt` → Data class para representar vídeos detectados
- `TopBar.kt` → Componente de barra superior com funções para navagação

---

## ▶️ Como Rodar

1. Clone este repositório:
   ```bash
   git clone https://github.com/seuusuario/agcast.git

