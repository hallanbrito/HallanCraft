# HallanCraft — Torre do Caos 🏰

**F0/F1.1 — fundação de laboratório, NÃO é ainda um jogo jogável.**

Servidor Minecraft independente do servidor familiar do José Emanuel. Não instalar no servidor do José. Sem deploy na Shulkerboxes até autorização específica e homologação.

## Baseline

- Java 21, Paper 1.21.4 **provisório** (revalidar com a instância da hospedagem)
- Gradle 8.14.3 recomendado; configuração `build.gradle` (Groovy DSL)
- SQLite via driver JDBC incluído no runtime do Paper; Gradle fornece driver para testes locais
- JUnit 5 + dependência MockBukkit para testes de integração Paper posteriores

## Entregues nesta etapa

1. Entrypoint `JavaPlugin` que inicia SQLite, executa migração versionada e desativa gameplay por segurança.
2. `V001__baseline.sql`: seis tabelas de domínio, integridade referencial, journal por sessão/mundo/arena e slot único de run.
3. Repositórios Java para moedas permanentes e journal copy-on-write (persistência apenas).
4. Regras de estados da run isoladas do Bukkit.
5. Testes JUnit planejados para DB/migração/transição; verificações offline executáveis sem Gradle.

**NÃO implementado:** arena real, blocos, respawn, loot, waves, recuperação de chunks, blueprint, comandos de gameplay, multiplayer, testes end-to-end Paper e deploy.

## Windows 11 / PowerShell

```powershell
# Com o Java 21 e Gradle instalados, na pasta do projeto:
.\scripts\setup-gradle-wrapper.ps1
.\gradlew.bat test
.\gradlew.bat jar
```

Se o PowerShell restringir scripts, execute o `gradle wrapper --gradle-version 8.14.3 --distribution-type bin` diretamente (não altere permanentemente a política de execução).

JAR esperado APÓS build bem-sucedido: `build/libs/TorreDoCaos-0.1.0-SNAPSHOT.jar`. Este JAR contém somente a fundação de dados, não o minigame.

## Verificações sem internet/Gradle

```bash
python3 scripts/verify-sql-offline.py
bash scripts/verify-jdk-domain.sh
```

Essas verificações não substituem `gradlew test`, Paper real ou testes de falha de arena.

## Política de segurança

- `enabled-gameplay: false` por padrão; se for habilitado antes de existir gameplay, o plugin se desativa.
- Nunca iniciar a arena sem implementação e teste de recuperação dos blocos em disco.
- Não remover o `arena_block_journal` apenas porque os blocos foram alterados em memória.
- Não aceitar itens/skills de outra dimensão ou servidor: o servidor da Torre será completamente independente.
- Uma única sessão ativa, reforçada no esquema por índice UNIQUE.
- Qualquer evolução do schema deve entrar em arquivo de migração imutável versionado (V002 etc.).

## Documentos

Veja `docs/STATUS-F0-F1.md` e `docs/PLANO-EXECUCAO.md`.