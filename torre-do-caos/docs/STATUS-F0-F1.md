# Evidências — laboratório F0/F1.1 (16/11/2026)

## Entregue

- `build.gradle`, `settings.gradle`, Java 21 / Paper API 1.21.4 (baseline provisório).
- JavaPlugin de bootstrap com gameplay inativo.
- Migração SQL V001 contendo 6 tabelas e índice de recuperação.
- Repositórios Java para moedas e copy-on-write do estado original do bloco.
- Regras da máquina de estados em Java puro.
- Testes JUnit 5 para migração e reabertura de DB, consistência do journal, integridade referencial e regras de run.
- Dependência MockBukkit registrada para testes Paper subsequentes; testes MockBukkit ainda não escritos.

## Executado de verdade neste ambiente

```text
$ python3 scripts/verify-sql-offline.py
PASS 1/6: six expected SQLite tables
PASS 2/6: permanent progression survives reconnect
PASS 3/6: first original block is not overwritten
PASS 4/6: one active run enforced
PASS 5/6: foreign key enforcement
PASS 6/6: journal survives reconnect
OFFLINE_SQL_CHECKS: 6/6 PASS

$ bash scripts/verify-jdk-domain.sh
6/6 JDK domain checks PASS

$ javac -d build/offline-storage src/main/java/br/com/hallancraft/torredocaos/storage/*.java
PASS: Java storage sources compiled with javac 21
```

## NÃO executado / NÃO homologado

- `gradle test`, `gradle jar`: Gradle e dependências não instalados neste ambiente; acesso de rede indisponível.
- Executar servidor Paper real, testes com MockBukkit, Spark, `/iniciar`, waves e morte.
- Criar/validar segunda instância Shulkerboxes (nenhuma ação externa efetuada).
- F1.2...F1.6, reset durável da arena, arena blueprint e testes de crash.
- O jar do plugin final não foi gerado; apenas classes Java puras foram compiladas.

## Próximo aceite

1. Executar `gradle test` e `gradle jar` com internet/Gradle disponíveis.
2. Verificar o SQLite abrindo dentro de Paper de teste 1.21.4.
3. Implementar F1.2/F1.3 com blueprint e journal ligados aos eventos Paper.
4. Implementar F1.4 e testes de queda forçada antes de liberar mobs/construção.

**Sem deploy. Servidor do José intocado.**