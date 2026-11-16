# Torre do Caos — F1.2.2: lobby + 3 mundos vazios + 12 arenas

**Escopo:** protótipo navegável de mundos/torres. Não habilita gameplay, hordas, progressão nem recuperação de sessões.

## Instalação simples no servidor novo

1. Crie um servidor **Paper 1.21.4 / Java 21** exclusivamente para Torre do Caos na Shulkerboxes.
2. Envie o arquivo `TorreDoCaos-Paper-1.21.4-WorldsPreview.jar` para Plugins/Upload, e inicie esse servidor novo.
3. O plugin cria automaticamente `torre_overworld`, `torre_nether`, `torre_end` com gerador de chunks vazios e 4 torres/ambientes (total 12). No Overworld também cria o lobby.
4. No jogo, teste `/torre status`, `/torre jogar`, `/torre lobby`.
5. **Não copie mundos manualmente. Não instale ferramentas de desenvolvimento no PC.**

O servidor Paper poderá manter seus próprios mundos padrão (`world`, `world_nether`, `world_the_end`) por exigência da inicialização vanilla. Eles **não são nossos mundos jogáveis**. Novos jogadores são levados ao lobby da Torre e a navegação comum entre mundos da Torre é controlada pelo plugin. Comandos administrativos podem acessar mundos padrão.

## Layout

- Mundo Normal: `torre_overworld` — lobby em (0,80,0) e arenas `tc-o-01` a `tc-o-04`.
- Nether: `torre_nether` — arenas `tc-n-01` a `tc-n-04`.
- End: `torre_end` — arenas `tc-e-01` a `tc-e-04`.
- Em cada dimensão, as torres ficam em `(512,512), (1024,512), (512,1024), (1024,1024)`; separação mínima de 512 blocos.
- Cada torre tem área 25×25, paredes e iluminação, sem terreno natural. O panorama visual varia por dimensão.

## Comportamento

- Join -> lobby. `/torre jogar` -> reserva **exclusiva** de uma torre e teleporta para o centro do nível 1, sem disparar mobs ou progressão.
- `/torre lobby` (ou `/torre sair`) -> libera a torre e volta ao lobby.
- `/torre status` -> vagas ocupadas e situação de inicialização.
- Não é possível reservar a mesma torre para duas pessoas ao mesmo tempo. Quando todas estiverem ocupadas, novas entradas são recusadas de forma segura.
- Ao desconectar, a reserva de **prévia** é liberada; no reconnect o jogador retorna ao lobby. Progressão de jogadores e equipes será gerida por partidas persistentes na F1.5/F3.
- Proteção temporária: impedir quebra/colocação/dano nos mundos da Torre. Construção jogável autorizada pela F1.3 somente após journal e recuperação seguros.
- O SQLite V001 ainda limita sessões de combate a uma global; precisa de migração V002 para N sessões antes de liberar multiplayer real. **Não usar este protótipo para waves.**

## Segurança e recuperação

- O plugin só cria pastas `torre_*` novas; nunca altera mundos `world*` existentes nem opera no servidor do José.
- Se encontrar uma pasta `torre_*` pré-existente sem seu manifesto de propriedade, **falha fechada** e não tenta sobrescrevê-la.
- Manifesto `plugins/TorreDoCaos/managed-worlds.properties` contém UUID de cada mundo, versão e status de preparação. Instalação interrompida retorna de `INITIALIZING` sem apagar mundos.
- Arena original registrada em blueprints `arena_blueprint_tc-*-0*.dat` imutáveis com checksum SHA-256. A estrutura nunca é sobrescrita se o snapshot não combinar.
- O bootstrap divide gravações em lotes de 384 blocos/tick; sem geração de cavernas, estruturas e terreno. **RAM/MSPT reais ainda precisam ser medidos na hospedagem.**
- Se estiver ocupado durante a preparação inicial, `/torre jogar` não inicia nenhuma partida.
- Se o mundo ou blueprint falhar, a inicialização deve ser interrompida, sem restauração/destruição automáticas.

## Aceite manual pendente (Shulkerboxes)

- [ ] Três mundos novos + lobby + 12 torres existem e visualizam corretamente.
- [ ] Reiniciar o servidor não recria nem sobrepõe torres.
- [ ] Dois jogadores usam torres diferentes, nunca invadem arena um do outro.
- [ ] Jogador com reserva consegue retornar ao lobby; slot reutilizado.
- [ ] Jogador recém-conectado vai ao lobby, e não ao nível de outra partida.
- [ ] Conferir com `/torre admin verificarena tc-o-01` e os demais IDs.
- [ ] Confirmar Paper, plugins permitidos, uso de memória e MSPT em produção isolada.

**Sem deploy automático. Sem merge. Servidor do José permanece intocado.**
