# HallanCraft — Estado Atual, Sprints e Próximas Decisões

**Snapshot:** 18/10/2026  
**Versão deste documento:** 1.2 (consolidação dos relatórios AGY Sprint 03.2 e Codex Sprint 02.2)  
**Documento mestre:** [`PROJETO.md`](PROJETO.md)  
**Origem:** relatórios AGY e Codex apresentados pelo PO, observações anteriores do PO no Minecraft e consulta pontual à documentação oficial Bedrock. Os repositórios locais, commits, arquivos binários e resultados de execução **não foram inspecionados diretamente** nesta atualização.

> **Legenda de evidência:** **[PO]** teste/relato do Product Owner no Minecraft; **[AGENTE]** implementação ou teste relatado pelo agente; **[PENDENTE]** não validado; **[PLANEJADO]** escopo ainda não autorizado para implementação.

## 1. Resumo executivo

**O que está funcionando:** o Resource Pack HallanCraft Realista derivado da Variante D foi aprovado visualmente pelo PO no Galaxy A54 (Minecraft Bedrock 1.26.52, Visuais Vibrantes). A água é transparente e permite observar peixes. A estrutura da vila 32×32 agora carrega em formato reconhecível após a correção de transposição dos eixos. **[PO]**

**O que ainda impede homologação da vila:** esses defeitos foram observados anteriormente pelo PO — entrada parcialmente obstruída, porta ausente, telhado incompleto, bloco `?` e perda de blocos sobre caminhos. O AGY **relatou corrigi-los na Sprint 03.2**, mas ainda falta **reteste real do novo BP 1.0.3 no Galaxy A54**. **[PO]** para defeitos anteriores; **[AGENTE]** para as correções.

**Entregas recentes:** AGY **relatou concluir a Sprint 03.2** no commit `9efbae2` com correções arquitetônicas e novo pacote integrado `1.2.2` (RP 1.2.0 + BP da vila 1.0.3); a contagem declarada é **5/5 suítes**, embora quatro scripts principais apareçam listados. Codex **relatou concluir a Sprint 02.2** no commit `94afd4d`, com correção de bordas, auditoria e protótipo offline do Pesadelo do Nether (**45/45 testes**). **Nenhuma das duas novas entregas foi homologada no Bedrock pelo PO.** Integração e produção bloqueadas.

**Produção:** **não autorizada** a receber novos pacotes ou integrações. O mundo da família deve permanecer intocado.

## 2. Quadro de entregas

| Área / entrega | Estado | Evidência / pendência |
|---|---|---|
| Variantes visuais A, B, C e D | **Homologadas no A54** | Todas abriram; C e D exibiram a água aprovada **[PO]** |
| Resource Pack HallanCraft Realista 1.2.0 | **Referência visual congelada** | Hash invariável declarado pelo AGY; aparência D anterior aprovada no A54 **[AGENTE]/[PO]** |
| Vila 32×20×32 — arquivos de estrutura | **Correções arquitetônicas reportadas** | Telhado, portas, acessos, caminhos e estados NBT; falta inspeção in-game **[AGENTE]/[PENDENTE]** |
| Correção de transposição Y/Z | **Homologação funcional parcial anterior** | Cidade reconhecível; nova revisão arquitetônica 1.0.3 ainda não retestada **[PO]/[PENDENTE]** |
| Comando único `/function vila` | **Funcional para carregar estrutura** | O PO executou e visualizou a vila **[PO]** |
| Sprint AGY 03.2 — portas/telhados/bloco `?` | **Concluída pelo agente, aguardando homologação** | Commit `9efbae2`; BP vila 1.0.3, integrado 1.2.2; testes somente relatados **[AGENTE]/[PENDENTE]** |
| Variante E — Floresta Viva | **Planejamento aprovado** | Implementação ainda não autorizada **[PLANEJADO]** |
| Slimes azuis e altar monumental | **Backlog criativo** | Ainda não autorizado para implementação **[PLANEJADO]** |
| Codex Core — proteção/permissões/prisão | **Sprint 02.2 implementada, segundo Codex** | Correção da borda + validador territorial; falta inspeção independente, integração e homologação real **[AGENTE]/[PENDENTE]** |
| Codex Sprint 02.2 | **Concluída pelo agente; 45/45 testes reportados** | Commit `94afd4d`, Core `1.1.1`, pacote `0.2.1`; sem execução independente dos testes **[AGENTE]** |
| Pesadelo do Nether | **Protótipo offline isolado reportado** | `prototypes/nether_nightmare/domain.js` não integrado; viabilidade parcial, sem ativação/deploy autorizado **[AGENTE]/[PENDENTE]** |
| Ambiente Docker de homologação | **Preparado em arquivos, não iniciado** | Requer autorização e preflight de recursos/saúde **[PENDENTE]** |
| Implantação no servidor familiar | **Bloqueada** | Exige autorização do PO, backup e testes isolados **[PENDENTE]** |

## 3. AGY — histórico relevante e situação

**Worktree:** `~/Projetos/HallanCraft-AGY`  
**Branch:** `feature/visual-rustico`

### 3.1. Linha do tempo reportada

| Commit AGY | Marco |
|---|---|
| `e74d3e2` | Investigação do crash do Resource Pack; variantes A/B/C/D e correções de compatibilidade |
| `7777b55` | Sprint 03: candidata visual 1.2 e setor 32×20×32 gerado |
| `c41b065` | Sprint 03.1: pacote Android com Resource Pack + Behavior Pack da vila |
| `80078ac` | Organização de `exports/` e padronização dos nomes curtos |
| `0479639` | Correção do embaralhamento Y/Z na serialização NBT e testes assimétricos |
| `5098ebc` | Remoção do teleporte intermediário; função `/function vila` simplificada |
| `443fe1e` | Hotfix de versionamento do Behavior Pack para 1.0.2 e instalador integrado 1.2.1 |

O histórico acima precede o **novo commit `9efbae2` da Sprint 03.2**, informado pelo AGY em seu relatório posterior. Commits não foram verificados diretamente no repositório.

### 3.2. Artefatos oficiais mais recentes relatados

**Diretório:** `~/Projetos/HallanCraft-AGY/exports/`

| Arquivo | Conteúdo | Versão interna |
|---|---|---|
| `HC_Vila_v1.2.2.mcaddon` | Distribuição integrada | RP 1.2.0 + BP 1.0.3 |
| `HC_Realista_v1.2.mcpack` | Resource Pack avulso (visual D) | 1.2.0 |
| `HC_Vila_BP_v1.0.3.mcpack` | Behavior Pack avulso da vila | 1.0.3 |

**Hashes SHA-256 relatados pelo AGY:**

```text
HC_Vila_v1.2.2.mcaddon    f6552dc985ce0ebd9f51774d35402863ef83fecd3256eecd62c32557721fe1e7
HC_Realista_v1.2.mcpack  7c73185ab6e0d7fe2dea50d0fc87a6a60a47b9ba85f7031b72c24fa3c3ef5d60
HC_Vila_BP_v1.0.3.mcpack 531349cbac07bf2e273deb8b7bd0bd2cfd776c3d390dc99d14cf70a4bfaba82e
```

Hashes, tamanhos e presença dos arquivos são **reportados**, não verificados diretamente aqui. AGY declarou que `exports/` contém apenas os três artefatos novos e que versões anteriores foram preservadas em `archives/exports_anteriores/`. O checksum do RP coincide com a referência anterior, segundo o relatório; confirmar em arquivo real quando possível.

**Observação de versionamento:** AGY informou ter incrementado o BP da vila de `1.0.2` para `1.0.3`, preservando UUIDs, e atualizado o `.mcaddon` externo para `1.2.2`, sem modificar o RP 1.2.0. Qualquer nova alteração do BP da vila requer nova versão interna acima de `1.0.3`; jamais reutilizar a mesma versão para payload distinto. Não confundir versão do BP da vila (AGY) com BP Core 1.1.1 (Codex).

### 3.3. Testes reais no Galaxy A54 — fatos importantes

1. **Crash original do visual 1.1:** o jogo funcionava sem o pack, mas fechava com o pack mesmo sem Visuais Vibrantes. **[PO]**
2. **A, B, C, D:** todas abriram em testes sucessivos; B/C/D foram testadas com Visuais Vibrantes; o PO elogiou especialmente a água transparente com peixes visíveis. **[PO]**
3. **Primeira vila carregada:** o Minecraft reconheceu a estrutura, mas o mundo mostrou terreno suspenso, blocos separados e quedas d'água verticais. **[PO]**
4. **Raiz do problema apontada e corrigida:** AGY identificou loops NBT em ordem X→Z→Y em vez de X→Y→Z; relatou regressão 3×4×5 e conferência de 20.480 voxels sem diferenças. **[AGENTE]**
5. **Reteste após a correção:** a vila passou a aparecer como conjunto arquitetônico reconhecível, porém o PO encontrou **entrada quase fechada, porta ausente, telhado ausente/incompleto, textura com `?` e um bloco que desaparece/reage anormalmente quando outro é colocado sobre ele**. **[PO]**
6. **Conclusão histórica:** renderização visual aprovada; a revisão antiga da vila foi reprovada arquitetonicamente. As **correções reportadas em 1.0.3** ainda exigem homologação no Minecraft antes de alterar essa conclusão.

### 3.4. Escopo da missão Sprint 03.2 (autorização original)

**Autorizado ao AGY:** investigar/corrigir portas, acessos, telhados, blocos inválidos ou com suporte/estado inadequado; adicionar testes de regressão; preservar o carregamento por `/function vila`, a geometria Y/Z, as texturas e a água aprovadas; gerar artefato versionado; arquivar versão anterior; manter apenas três arquivos atuais em `exports/`.

**Não autorizado nesta missão:** Variante E, mudanças na grama, slimes azuis, altar monumental, expansão da vila, edição de produção ou integração com o Codex.

**Evidência recebida:** o relatório do AGY descreve causas, correções, testes, commit, versões internas e nomes/hashes dos pacotes. **Ainda faltam o arquivo binário real, inspeção independente dos diffs e novas fotos/relato do teste pelo PO no Galaxy A54.**


### 3.5. Entrega Sprint 03.2 — novo relato do AGY (18/10/2026)

**Evidência:** conteúdo integral do relatório colado pelo PO, sem análise direta de commit, scripts ou arquivos de pacote. **[AGENTE]**

- **Branch/commit declarados:** `feature/visual-rustico` / `9efbae2`.
- **Diagnóstico declarado:** (a) laço do telhado da Casa do Artesão parava antes de fechar cumeeira/frontões; (b) aberturas da casa e do posto eram apenas ar, sem estados de portas; (c) degraus/aberturas de acesso ausentes; (d) o ID `minecraft:dirt_path` (Java) produzia bloco desconhecido no Bedrock; (e) apoios de postes/soleiras sobre caminho de terra e serialização `states: {}` geravam instabilidade de alguns blocos.
- **Correções declaradas:** telhado contínuo em Y=6..10 e frontões selados; portas `minecraft:wooden_door` com `upper_block_bit` e orientação; escadas de varanda e sacada do posto; troca por `minecraft:grass_path`; suportes `stone`/`cobblestone`; serialização NBT Little-Endian com estados preservados.
- **Arquivos de implementação/teste citados:** `generate_village_sector.py`, `build_installable_village.py`, `test_construction.py`, `test_package_versioning.py`, `GUIA_INSTALACAO_GALAXY_A54.md`, `README.md` (caminhos relativos conforme relato).
- **Testes declarados:** regressão assimétrica 3×4×5, validação do RP, validação de 20.480 voxels da `.mcstructure`, testes de telhado/portas/acessos/suporte, 1.846 comandos no gerador `.mcfunction`, integridade/versionamento dos pacotes. Relatório anuncia **5/5 suítes**, mas o roteiro mostra **quatro scripts de teste**; registrar diferença sem inferir reprovação. **Não reexecutados aqui.**
- **Versões novas declaradas:** RP 1.2.0 congelado; BP da Vila 1.0.3; pacote de distribuição integrado 1.2.2. Os três pacotes oficiais e hashes estão na seção 3.2.
- **Verificação documental externa:** o catálogo Bedrock da Microsoft contém `minecraft:grass_path` e `minecraft:wooden_door`, com `upper_block_bit` e `minecraft:cardinal_direction` para a porta. Isso respalda os identificadores, **não prova que os blocos carregam corretamente no dispositivo**.
- **Comando preservado:** `/function vila`, usando `execute at @s run structure load hallancraft_setor_32x32 ~2 ~-1 ~2` (deslocamentos nos eixos globais +X, -Y, +Z, não automaticamente relativos ao olhar). O PO já testou versões anteriores da função, mas não a nova estrutura 1.0.3.
- **Escopo declarado:** sem iniciar Variante E, slimes/altar, extensão da cidade, merge/deploy ou edição da produção. Confirmar somente com eventual inspeção direta.

**Parecer:** aceitar como **entrega implementada e testada estaticamente, segundo agente**. **Homologação arquitetônica continua pendente**. Não alterar a prioridade ou liberar integração.

**Roteiro de homologação recomendado ao PO — mundo novo e descartável no Galaxy A54:**

1. Importar `HC_Vila_BP_v1.0.3.mcpack` quando o RP 1.2.0 já estiver disponível, ou importar o `.mcaddon` integrado apenas para um teste novo; verificar qual BP foi realmente ativado no mundo. **Não apagar mundos nem dados para resolver cache**.
2. Criar **mundo plano novo**, criativo, cheats habilitados; ativar os packs de teste e executar **`/function vila`** em terreno livre. A estrutura carrega deslocada em +X/+Z em relação à posição, independentemente da direção do olhar.
3. Inspecionar visualmente o telhado inteiro (também por cima), abrir e fechar todas as portas; atravessar os vãos; percorrer degraus da varanda e escada interna da guarda.
4. Conferir ausência de blocos `?` nas trilhas, verificar que lanternas/cercas/soleiras permanecem intactas após atualizações, remoção/colocação de blocos próximos e algum tempo de jogo.
5. Conferir riacho/água/peixes e estabilidade visual/performance da Variante D. Capturar prints e anotar falhas exatas. **Não instalar nem reconstruir a vila no mundo familiar**.

**Pontos em aberto:** os binários do AGY não foram anexados; o comportamento de portas, suporte de blocos e teto continua por demonstrar no Bedrock real. A descrição de garantia de suporte/0 blocos frágeis decorre de testes do agente, não prova ausência de problemas após *neighbor updates*.

## 4. Codex — histórico e missão atual

**Worktree:** `~/Projetos/HallanCraft-Codex`  
**Branch:** `feature/bedrock-core`

### 4.1. Base existente, conforme relatos anteriores

- Sprint 01: núcleo de proteções, permissões, infrações e prisão.
- Commit `734900b`: endurecimento da persistência/prisão e testes.
- Commit `b1806f7`: preparação da Sprint 02 e homologação isolada; o Codex relatou **27 testes aprovados** nessa fase.
- Requisitos principais: impedir alterações indevidas em área protegida, permitir interações legítimas, contar 10 tentativas para prisão de 60 segundos, persistir punições e diferenciar administrador de jogador comum.
- O Codex preparou arquivos de homologação Docker isolada, mas **não foi autorizado a iniciar o ambiente**.

**Não confundir testes unitários com validação real dentro do Bedrock.** O estado posterior está reportado na seção 4.3, sem inspeção direta da branch.

### 4.2. Sprint 02.2 — prompt enviado pelo PO

**Frente A:** auditoria do núcleo de proteção, permissões e prisão; testes offline, fronteiras territoriais e contrato de integração futuro com coordenadas reais da vila.

**Frente B:** viabilidade e protótipo **offline isolado** da mecânica **Pesadelo do Nether**:

- Tentativa de dormir deve iniciar pesadelo no Nether, sem pular a noite.
- Morte fatal simulada/retorno seguro à cama com inventário preservado, se tecnicamente viável.
- Suporte individual a múltiplos jogadores e tratamento de reconexão, cama destruída, riscos de inventário e exceções.
- Avaliar eventos e APIs estáveis reais da versão utilizada; apresentar limitações sem prometer mecanismos não comprovados.
- **Não integrar, ativar ou publicar** a mecânica nesta sprint.

**Situação:** relatório de conclusão recebido do Codex, resumido na seção 4.3. Os dois arquivos de relatório citados pelo agente ainda não foram abertos/revisados diretamente nesta conversa.

### 4.3. Entrega da Sprint 02.2 — novo relato do Codex (18/10/2026)

**Evidência:** texto enviado pelo PO, sem inspeção direta de código ou diffs. **[AGENTE]**

- **Branch/worktree:** `feature/bedrock-core` / `~/Projetos/HallanCraft-Codex`, declarados limpos ao final.
- **Commit:** `94afd4d` (relatado, não verificado independentemente).
- **Versões:** Behavior Pack Core `1.1.1`; pacote externo `0.2.1` (informadas pelo agente; manifesto não inspecionado).
- **Validações informadas:** `npm test` **45/45**, `node --check` dos scripts e do protótipo, `npm run check`, `bash -n scripts/homologacao/*.sh` e `git diff --check` — todos aprovados segundo o Codex.
- **Segurança territorial:** checagem do bloco clicado e do bloco adjacente por `blockFace`; tentativa de colocação de fora para dentro bloqueada; interação externa e portas/botões/alavancas/alçapões/portões públicos preservados. Validador rejeita IDs ausentes/duplicados, nível de proteção inválido, dimensão vazia, prioridade inválida e intervalo com mínimo maior que máximo.
- **Preservações informadas:** quebra preventiva, bloqueio antes do consumo, 10 infrações, deduplicação, prisão por 1.200 ticks *conectados* na cela, persistência/recondução, vinculação administrativa robusta, políticas pública e administrativa por prioridade. A equivalência entre ticks conectados e 60 segundos depende da taxa de 20 ticks/s, que deve ser observada no teste real.
- **Riscos remanescentes:** pistões, fluidos, fogo/raios/mobs, entidades em explosões e futuros blocos mutáveis; tratar no desenho da cidade e em homologação, não por gamerules globais automáticas.
- **Pesadelo do Nether:** máquina de estados individual, janela noturna, prioridade da prisão, cama de origem, destino seguro, desconexão/reconexão, fuga por portal, persistência e retorno emergencial; protótipo `prototypes/nether_nightmare/domain.js` deliberadamente desacoplado de `@minecraft/server`, `main.js` e do manifesto.
- **Estratégia proposta:** cancelar dano fatal antes da morte, não tocar nos slots nem ativar `keepInventory`, teleportar a mesma entidade; **ainda não validado em Bedrock**. Riscos: `/kill`, causas não canceláveis, danos simultâneos, Totem/absorção, chunks descarregados, crashes e portais.
- **Arquivos principais citados:** `behavior_packs/hallancraft_core/scripts/dominio.js`, `.../main.js`, `scripts/validar-pack.js`, `test/dominio.test.js`, `test/nether_nightmare.test.js`, os dois relatórios em `docs/` e `prototypes/nether_nightmare/domain.js`.
- **Relatórios citados:** `docs/AUDITORIA_SEGURANCA_SPRINT_02_2.md` e `docs/ESTUDO_PESADELO_NETHER_SPRINT_02_2.md` — não disponibilizados integralmente como anexos nesta atualização.
- **Limites declarados:** produção, Docker e mundos não acessados; nenhuma integração/deploy autorizados.

### 4.4. Parecer do conselheiro técnico — pendências

1. **Estado:** entrega **reportada como implementada e validada estaticamente**; ainda **não homologada no Minecraft pelo PO**. A recomendação é aceitar o relatório de desenvolvimento, **sem promover o Core à produção**.
2. **Segurança:** testar em mundo descartável, no momento apropriado e sob autorização, as seis faces de borda, interações permitidas, contagem exata, punição, evasão, reconexão e multiplayer. Nunca presumir coordenadas finais antes da integração com a vila do AGY.
3. **Pesadelo:** provar interceptação de sono sem avançar horário; validar o cálculo de dano efetivamente fatal, Totem, absorção, danos concorrentes, `/kill`, persistência, teleporte com destino/chunk e isolamento entre jogadores. Eventos *before* impedem modificar o estado do mundo diretamente; cancelamento deve acontecer antes, operações como teleporte devem ser diferidas e testadas quanto à janela entre ambas.
4. **Nota de precisão API:** a documentação estável confirma `playerInteractWithBlock` com `blockFace`/`cancel`/`isFirstEvent` e confirma `WorldBeforeEvents.entityHurt` e seu `cancel`. **Correção documental:** changelog oficial introduz `EntityHurtBeforeEvent` na seção `2.6.0`, e não `2.8.0`. Compatibilidade prática do módulo `2.9.0` ainda requer teste in-game.
5. **Decisão de escopo:** o pesadelo continua restrito a estudo/protótipo offline e **não pode ser importado, ativado, empacotado como recurso ativo nem implantado** sem autorização expressa do PO.

**Referências de documentação API (consulta externa, não prova de comportamento em jogo):**
- https://learn.microsoft.com/en-us/minecraft/creator/scriptapi/minecraft/server/playerinteractwithblockbeforeevent?view=minecraft-bedrock-stable
- https://learn.microsoft.com/en-us/minecraft/creator/scriptapi/minecraft/server/entityhurtbeforeevent?view=minecraft-bedrock-stable
- https://learn.microsoft.com/en-us/minecraft/creator/scriptapi/minecraft/server/worldbeforeevents?view=minecraft-bedrock-stable
- https://learn.microsoft.com/en-us/minecraft/creator/scriptapi/minecraft/server/changelog?view=minecraft-bedrock-stable

## 5. Produção e ambiente de homologação — última observação conhecida

O último diagnóstico informado pelo PO em Konsole exibiu:

```text
Contêiner: hallancraft
Estado: Up, mas healthcheck "unhealthy" naquele momento
Imagem: itzg/minecraft-bedrock-server:stable
Imagem local reportada: sha256:6ff3abf645a88c5aafd88b7cf96d35cdfb1e0977a0c45c465a5b536cb1f1c4f9
Memória disponível no notebook: ~3,2 GiB
Swap em uso: ~1,9 GiB
```

**Atenção:** essa medição é anterior ao snapshot e **não prova indisponibilidade do Minecraft**; pode haver problema específico no healthcheck. **Não afirmar que continua `unhealthy` sem nova verificação.** Não iniciar outro servidor automaticamente em hardware restrito. Qualquer diagnóstico adicional de produção é somente leitura, salvo aprovação do PO.

O servidor familiar continua fora do escopo de alterações dos agentes.

## 6. Backlog priorizado e bloqueios

| Ordem | Tema | Condição para avançar |
|---|---|---|
| **1 — Agora** | Homologar correções da vila (Sprint 03.2) | Entrega recebida; validar BP da vila 1.0.3 no A54 em mundo descartável |
| **Paralelo — aguardando autorização futura** | Core territorial Codex + protótipo do pesadelo | Entrega recebida; futuras provas no jogo somente em ambiente isolado e mediante escopo aprovado, sem integração |
| **2** | Variante E: copas, troncos e grama mais realista | Vila atual aprovada + autorização específica |
| **3** | Slimes azuis com visual próprio, inspiradas em Rimuru | Definir escopo visual, performance e aprovação |
| **4** | Altar monumental com slime azul no topo | Aprovar projeto arquitetônico/localização |
| **5** | Integração de vila + proteção + multiplayer | Ambiente isolado, coordenadas fixadas, testes e aprovação do PO |
| **6** | Expansão para cidade completa e serviços (hospital, polícia etc.) | Roadmap e sprints específicos |

A ordem dos itens 2–6 pode mudar por decisão expressa do PO. Nenhuma dessas etapas está automaticamente autorizada por constar do backlog.

## 7. Checklist de retomada da próxima conversa

Quando um agente entregar novo relatório:

- [ ] Conferir **branch, commit, escopo e arquivos alterados**.
- [ ] Registrar o que o agente **afirma ter testado**, separando de teste real do PO.
- [ ] Conferir versões internas, UUIDs, dependências e nomes externos dos packs.
- [ ] Confirmar se `exports/` contém apenas os três pacotes oficiais atuais.
- [ ] Confirmar preservação dos recursos visuais aprovados quando o escopo for arquitetura/gameplay.
- [ ] Verificar se a entrega exige mundo descartável no Galaxy A54.
- [ ] Registrar defeitos encontrados, prints e o aceite/reprovação do PO.
- [ ] Evitar prompt novo desnecessário; consolidar correções de um mesmo problema.
- [ ] Não autorizar integração ou deploy sem aprovação explícita.
- [ ] Atualizar **este `STATUS.md`** antes de recomendar checkpoint de conversa.

## 8. Próximas perguntas objetivas ao PO

1. **AGY 03.2:** o PO conseguiu importar o BP 1.0.3 no **mundo novo descartável** e verificar telhado, portas, acesso, bloco `?` e estabilidade? Relatar prints/erros.
2. **Codex 02.2:** o PO deseja apenas arquivar a entrega reportada até a vila estar corrigida ou autorizar posteriormente uma homologação Bedrock isolada do Core? O pesadelo permanece protótipo offline.
3. Há alguma necessidade real de usar Docker/homologação dedicada agora, ou continuaremos com testes locais até as entregas estarem prontas?

**Próxima decisão esperada:** priorizar a homologação da **Sprint AGY 03.2 no A54**. Após o teste real, decidir se a vila requer mais correção ou se está apta a avançar para planejamento futuro. Preservar o Core Codex isolado; **nenhum merge/deploy** se presume autorizado.

---

**Controle de mudança v1.2:** consolidação dos relatórios do Codex Sprint 02.2 e AGY Sprint 03.2, com versões, hashes reportados e recomendação de homologação isolada. **PROJETO.md v1.0 permanece inalterado**; nenhum pacote foi instalado e nenhuma autorização de integração/deploy foi concedida. Nunca promover testes de agente à homologação pelo PO sem evidência in-game.
