# F1.2 — Blueprint imutável da arena

**Status:** Implementação inicial. `gradle test`, Paper/MockBukkit e homologação in-game pendentes.

## Uso (somente laboratório Paper independente)

1. Construa a arena em mundo próprio, limite máximo de **20 mil blocos** no volume.
2. Não inclua containers ou block entities: não são suportados pelo blueprint F1.2.
3. Em `plugins/TorreDoCaos/config.yml`, configure `arenas.arena-1.world`, `min` e `max`.
4. Reinicie o servidor e carregue os chunks da região.
5. No console, rode:

```
torre admin exportarena arena-1
torre admin verificarena arena-1
```

O comando exige `torredocaos.admin` (padrão OP); não concede OP a ninguém.
O exportador captura no máximo 128 blocos por tick, registra UUID do mundo,
coordenadas inclusivas e `BlockData#getAsString()` em arquivo versionado com checksum SHA-256.
O caminho é `plugins/TorreDoCaos/arena-blueprints/arena_blueprint_arena-1.dat`.

**Segurança:** o arquivo usa `CREATE_NEW` e não pode ser sobrescrito pelo comando.
Uma queda durante escrita pode deixar um arquivo inválido; falha fechada, backup e revisão
administrativa são necessários antes de usar a arena. Integridade por checksum não significa
imunidade a falha física de armazenamento.

A arena fica bloqueada contra alterações por jogadores e eventos comuns após exportada;
a verificação apenas conta divergências e **não restaura** os blocos.
Se o SQLite indicar run ativa ou journal pendente, a exportação é proibida.
A F1.3 implementará construções temporárias e a F1.4, a restauração confiável.

**Não instalar no servidor do José Emanuel. Não publicar na Shulkerboxes.**
