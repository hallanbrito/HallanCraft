# Plano de entregas — escopo autorizado, deploy bloqueado

| ID | Implementação | Estado |
|---|---|---|
| F0.1 | Baseline Java 21 + Paper 1.21.4 no Gradle | Configurado; servidor não iniciado |
| F0.2 | Medição Spark | Pendente: depende de instância Paper rodando |
| F0.3 | Segundo servidor Shulkerboxes | Pendente; não tocar no servidor do José |
| F0.4 | Projeto Gradle + JUnit + MockBukkit | Arquivos criados; resolução online e Gradle a executar |
| F1.1 | SQLite + migrações + repositórios | Código criado; SQL verificado offline |
| F1.2 | Arena blueprint imutável | Não iniciado |
| F1.3 | Journal conectado a eventos de blocos | Não iniciado; persistência isolada preparada |
| F1.4 | Reset incremental e confirmação de world save | Não iniciado |
| F1.5 | Lifecycle real, grace period, cleanup pendente | Não iniciado; dados preparados |
| F1.6 | Waves, loot, morte e respawn | Não iniciado |

## Bloqueios de segurança antes de ligar gameplay

- Arena original definida e blueprint com verificação independente;
- Bloqueio na origem de líquidos/containers/pistões/física destrutiva;
- Journal persistido ANTES das mutações de blocos;
- Reset idempotente com confirmação do salvamento dos chunks;
- Morte nativa e dano fatal tratados; jogadores offline recuperados;
- Testes de crash em múltiplos pontos e testes Paper reais.

**Proibido deploy na Shulkerboxes antes da homologação local.**
