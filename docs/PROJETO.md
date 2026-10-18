# HallanCraft — Documento Mestre do Projeto

**Versão deste documento:** 1.0  
**Atualizado em:** 18/10/2026  
**Responsável pelas decisões:** Product Owner (PO)  
**Documento complementar:** [`STATUS.md`](STATUS.md) — situação operacional, entregas, testes e próximos passos.

> **Regra de leitura:** este documento registra a visão e as decisões duradouras. Para saber o que está efetivamente pronto **hoje**, consulte `STATUS.md`. Ideias, implementações de agentes, testes automatizados e homologações feitas pelo PO não são a mesma coisa.

## 1. Propósito e identidade

O **HallanCraft** é um projeto familiar de Minecraft **Bedrock Edition**, com servidor dedicado, conteúdos autorais e uma vila rústico-fantástica. O objetivo é oferecer uma experiência divertida e segura para a família, especialmente para as crianças, sem perder espaço para exploração, construção livre e novas mecânicas.

A direção artística é inspirada **na atmosfera** de vilas fantásticas como Tempest, de *Tensei Shitara Slime Datta Ken*, misturada com arquitetura rústica do interior brasileiro. A inspiração não autoriza copiar modelos, imagens, texturas ou outros ativos de terceiros. Preferimos desenvolver recursos próprios e compatíveis com Bedrock.

**Princípios do projeto:**

- **Família em primeiro lugar:** proteger a experiência e os mundos já utilizados pelas crianças.
- **Uma melhoria por vez:** entregas pequenas, testáveis e reversíveis.
- **Autoridade do PO:** novos escopos, integrações e deploys dependem de autorização expressa.
- **Funcionalidade antes de espetáculo:** corrigir falhas estruturais e jogáveis antes de expandir visual ou gameplay.
- **Originalidade e compatibilidade:** packs autorais, desempenho viável em Android e preferencialmente APIs estáveis.
- **Evidência real:** testes automatizados não substituem os testes no Minecraft.

## 2. Pessoas, agentes e responsabilidades

| Papel | Responsabilidade principal | Limites |
|---|---|---|
| **Product Owner** | Visão, prioridades, aceites, decisões de arquitetura funcional e autorizações | Aprova integração e alterações de produção |
| **AGY** | Direção de arte, Resource Pack, PBR, água, vegetação, arquitetura, geradores e estruturas da vila | Não modifica núcleo de gameplay do Codex ou produção |
| **Codex** | Engenharia de gameplay, Behavior Pack de proteção, permissões, prisão, pesquisa de novas mecânicas | Não edita os ativos do AGY ou produção |
| **ChatGPT** | Conselheiro técnico do PO: analisar relatórios, revisar riscos, organizar documentação e preparar orientações | Relatórios do agente não equivalem a verificação independente; não aprova deploy pelo PO |

**Regras de coordenação:** cada agente trabalha na própria branch/worktree; não edita a área do outro, não faz merge entre branches e não integra funcionalidades sem a autorização do PO. Um worktree Git **não** é, por si só, uma sandbox de segurança do sistema operacional.

## 3. Ambientes e organização do código

### 3.1. Produção — área protegida

- **Diretório:** `~/HallanCraft`
- **Tecnologia:** Minecraft Bedrock Dedicated Server em Docker Compose, com contêiner `hallancraft`.
- **Mundo familiar:** `Mundo da Familia` (nome registrado na configuração).
- **Ambiente:** CachyOS/Linux no notebook; capacidade de memória limitada.
- **Política:** **não acessar para escrita, reiniciar, parar, alterar arquivos, aplicar packs ou modificar configurações sem aprovação explícita do PO**.

Os mundos, dados Docker, redes, regras de firewall, allowlist, permissões e configurações de operação não devem ser alterados como efeito colateral de tarefas de desenvolvimento. Mudanças autorizadas em produção exigem planejamento, backup verificável, janela apropriada e roteiro de reversão.

### 3.2. Desenvolvimento

```text
~/Projetos/HallanCraft/            # Repositório principal / integração controlada
~/Projetos/HallanCraft-AGY/        # Worktree: feature/visual-rustico
~/Projetos/HallanCraft-Codex/      # Worktree: feature/bedrock-core
~/HallanCraft/                     # PRODUÇÃO: separado e protegido
```

Este documento e `STATUS.md` devem residir idealmente em `docs/` do repositório principal e estar disponíveis para os agentes. Até que haja autorização para integrar branches, a documentação compartilhada pode ser distribuída ou copiada de forma controlada, sem um merge implícito.

### 3.3. Homologação

- **Primeiro nível:** testes locais, mocks e validações estáticas, sem Docker e sem tocar em produção.
- **Segundo nível:** mundos locais **novos e descartáveis** no Galaxy A54; uso apenas de packs de teste e comandos explicitamente aceitos.
- **Terceiro nível (futuro):** ambiente Bedrock dedicado isolado, com portas, dados e contêiner separados, mediante aprovação específica e avaliação de memória/saúde da produção.
- **Quarto nível:** integração/deploy em produção, com aprovação expressa, backup, plano de retorno e validação multijogador.

**Não assumir que o ambiente de homologação Docker está ligado ou liberado.**

## 4. Arquitetura funcional

| Componente | Finalidade | Responsável |
|---|---|---|
| **Resource Pack Realista** | Texturas 32×32, aparência rústica, materiais PBR, iluminação e água cristalina | AGY |
| **Behavior Pack da Vila** | Distribuição e carregamento das estruturas `.mcstructure` via função simples | AGY |
| **Behavior Pack Core** | Proteções, permissões, infrações e prisão; futuras mecânicas | Codex |
| **Servidor Bedrock dedicado** | Hospedagem do mundo da família e, futuramente, conteúdo aprovado | Produção sob aprovação do PO |
| **Testes/geradores/documentação** | Reprodutibilidade, regressões, validação de NBT e entrega | Cada agente em seu domínio |

**Interfaces não devem ser confundidas:** um Resource Pack altera apresentação; um Behavior Pack implementa comportamentos e/ou distribui estruturas; o arquivo `.mcstructure` guarda blocos, não garante qualidade arquitetônica; a função `/function vila` é uma interface de carregamento e não um substituto para a estrutura.

## 5. Visão da vila

### 5.1. Setor inicial de homologação

- **Dimensão nominal:** 32 × 20 × 32 blocos (X × Y × Z).
- **Elementos visados:** riacho transparente, ponte rústica, ruas de terra, residência do artesão, posto de guarda, praça da Grande Árvore e horta irrigada.
- **Comando de teste desejado:** `/function vila`, sem exigir comandos extensos ao jogador.
- **Condição de aceite:** estruturas montadas, acessíveis e estáveis em um mundo plano novo, sem blocos inválidos ou posicionamento incorreto.

O setor inicial é **um protótipo**, não a cidade completa nem a área já protegida pelo sistema do Codex.

### 5.2. Visão de longo prazo

- Cidade rústico-fantástica de aproximadamente **180 × 180 blocos**, com referência inicial de núcleo urbano/protegido de aproximadamente **140 × 140**; limites exatos ainda exigem projeto e homologação.
- Grande Árvore Guardiã, ruas e pontes, casas, áreas residenciais, mercado, prefeitura, hospital, delegacia, prisão e espaços de encontro.
- Áreas externas com liberdade de mineração e construção, respeitando demais regras do servidor.
- Futuras mecânicas de resgate de itens, NPCs e serviços da cidade são **ideias**, não implementações aprovadas.

## 6. Sistemas de gameplay planejados

### 6.1. Proteção territorial e punições

Requisitos aprovados para o núcleo, sujeitos a integração e testes reais:

- Bloquear quebra e colocação indevidas de blocos em áreas protegidas para jogadores comuns.
- Preservar interações legítimas, como portas, portões e botões.
- Diferenciar administrador e jogadores comuns com identificação robusta; não confiar apenas no nome exibido.
- **10 tentativas de violação → prisão por 60 segundos.**
- Impedir evasão da prisão por desconexão, morte ou tentativa de fuga, sem punir jogadores inocentes.
- Permitir proteção territorial configurável, vinculada às coordenadas **efetivas** da vila; **não** ativá-la automaticamente em coordenadas presumidas.
- Não aplicar o pacote de proteção ao mundo da família sem homologação dedicada.

### 6.2. Pesadelo do Nether — conceito novo

**Ideia aprovada para investigação/protótipo offline; ainda NÃO aprovada para uso no jogo:** ao tentar dormir, o jogador é transportado individualmente ao Nether como em um pesadelo. Quando sofreria dano fatal, desperta perto da cama de origem com inventário/equipamentos preservados; a noite não deve ser pulada por essa tentativa.

Questões técnicas obrigatórias: detecção/cancelamento do sono, teleporte dimensional seguro, localização da cama, multiplayer, persistência, reconexão, morte real versus simulada, prevenção de duplicação/perda de itens, conflitos com proteção/prisão e compatibilidade com a API estável efetivamente usada. **Não habilitar `keepInventory` global nem lançar a mecânica sem aprovação.**

## 7. Identidade visual e backlog artístico

**Referência já aprovada em dispositivo real:** configuração visual da **Variante D**, que apresentou água cristalina com boa visibilidade do fundo e dos peixes no Galaxy A54 com Visuais Vibrantes.

**Trabalhos futuros, em ordem de prioridade sujeita ao PO:**

1. Corrigir defeitos arquitetônicos da vila atual — entradas, portas, telhados, blocos inválidos e estabilidade.
2. **Variante E — Floresta Viva:** melhorar copas, folhas, troncos e, depois, grama mais natural; experimentar de modo independente e reversível.
3. Slimes **azuis**, visual original inspirado na ideia de Rimuru, inicialmente com a alternativa de substituição puramente visual se for suficiente.
4. Altar monumental, alto, com uma slime azul em destaque no topo, como homenagem temática, com proposta visual antes de implementação.

**Regra:** não alterar os recursos da água já aprovados quando a tarefa não exigir isso. Uma homenagem deve utilizar criação própria, sem copiar ativos protegidos de anime/jogo.

## 8. Versionamento, empacotamento e distribuição

### Regras

- **UUIDs estáveis** para atualizar um mesmo pacote; UUIDs diferentes para experimentos realmente independentes.
- Toda mudança no conteúdo de um pacote distribuído exige **incremento da versão interna** (`manifest.json`: header, módulos e dependências relevantes).
- **Renomear o arquivo não basta** para atualizar um pacote no Minecraft.
- Versionar Resource Pack e Behavior Pack **independentemente**. O `.mcaddon` é o contêiner de distribuição e pode ter uma versão externa distinta das versões internas de seus componentes.
- Rebuild determinístico, testes de integridade ZIP, verificação de manifesto, hashes e testes de regressão antes de entregar.
- Preservar histórico/artefatos anteriores fora de `exports/`; **não apagar definitivamente** versões antigas úteis.

### Política de `exports/`

`~/Projetos/HallanCraft-AGY/exports/` deve conter **somente três pacotes oficiais correntes**:

```text
HC_Vila_v<versao>.mcaddon      # Instalador integrado
HC_Realista_v<versao>.mcpack   # Resource Pack avulso
HC_Vila_BP_v<versao>.mcpack    # Behavior Pack avulso da vila
```

Versões exatas e hashes vigentes ficam no `STATUS.md`. Builds de diagnóstico, artefatos antigos e temporários pertencem a diretórios separados. Alterações devem atualizar os scripts para não recriar nomes antigos.

## 9. Critérios de qualidade e aceitação

Uma entrega pode ter quatro estados distintos:

1. **Proposta:** conceito, pesquisa ou documentação, sem execução autorizada.
2. **Implementada:** código/arquivos produzidos pelo agente.
3. **Validada estaticamente:** testes automatizados reportados/aprovados.
4. **Homologada pelo PO no Minecraft:** comportamento e aparência observados no dispositivo/ambiente designado.

Para a vila: verificar carregamento, posição do piso, geometria de telhados/portas, acesso, blocos inválidos, suporte e atualizações de blocos, água, iluminação, desempenho e ausência de crashes. Para gameplay: testar permissões, limites, reconexão, persistência e multiplayer. **Não chamar algo de homologado sem teste apropriado.**

## 10. Rotina de trabalho e documentação

- O PO envia missão delimitada → o agente desenvolve na própria branch → apresenta commit, diffs, testes, artefatos e riscos → o PO testa no Bedrock quando aplicável → decide aprovar, devolver para correção ou pausar.
- Ao concluir **cada entrega relevante**, atualizar `docs/STATUS.md` (versões, commits, testes reais, defeitos abertos, próximas decisões).
- Atualizar `docs/PROJETO.md` **somente** quando houver mudança duradoura de visão, arquitetura ou regra.
- Antes de qualquer integração: reconciliar relatórios dos dois agentes, resolver conflitos de versões/manifestos, realizar teste isolado e solicitar autorização expressa.
- Em novas conversas com o ChatGPT: fornecer **ambos os documentos**, mais os relatórios recentes ainda não refletidos no `STATUS.md`.
- Quando a conversa acumular muitas entregas/decisões, realizar um **checkpoint de contexto** e iniciar outra conversa com a documentação atualizada.

## 11. Restrições inegociáveis

- Nunca modificar ou testar conteúdos novos diretamente em `~/HallanCraft` por iniciativa do agente.
- Nunca alterar Docker, rede, firewall, mundos ou permissões de produção sem aprovação expressa.
- Nunca presumir que um teste no Galaxy A54 autoriza deploy para a família.
- Nunca apagar dados do mundo para resolver problema de cache de add-on.
- Nunca fazer merge entre branches dos agentes sem decisão de integração do PO.
- Nunca iniciar a Variante E ou a mecânica do Pesadelo do Nether como recurso ativo apenas porque há planejamento.
- Na dúvida entre velocidade e preservação do mundo, preservar o mundo e pedir autorização.

---

**Manutenção:** o PO é a autoridade sobre decisões; agentes propõem e implementam dentro do escopo autorizado. O documento `STATUS.md` é o quadro corrente e deve ser revisado após cada novo relatório.
