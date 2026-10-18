# HallanCraft 🌿🏡

**Um universo familiar de Minecraft Bedrock, construído com engenharia de software, automação e criatividade.**

O **HallanCraft** é um projeto independente de Minecraft **Bedrock Edition** que combina um servidor dedicado para a família, uma vila rústico-fantástica, pacotes visuais autorais e mecânicas de gameplay desenvolvidas de forma incremental. A experiência prioriza exploração, construção livre, segurança e compatibilidade com dispositivos móveis.

A direção artística une a atmosfera de vilas de fantasia à arquitetura rústica do interior brasileiro. Obras como *Tensei Shitara Slime Datta Ken* servem apenas de **inspiração temática**; não utilizamos nem autorizamos a cópia de ativos protegidos de terceiros.

> **Estado do projeto — 18/10/2026:** desenvolvimento ativo. A apresentação visual da Variante D foi aprovada em teste no Galaxy A54; as correções arquitetônicas mais recentes da vila e o núcleo de proteção territorial ainda **não foram homologados no Minecraft**. Não há autorização para implantar essas entregas no servidor familiar.

## 🧭 O que estamos construindo

- **Vila rústico-fantástica:** setor inicial de **32 × 20 × 32 blocos**, com casas, posto de guarda, praça, Grande Árvore, horta, ruas, ponte e riacho. O carregamento de teste utiliza `/function vila`.
- **HallanCraft Realista:** Resource Pack com texturas 32 × 32, materiais PBR e água cristalina; a **Variante D** é a referência visual aprovada.
- **Core de proteção:** Behavior Pack com regras de território, permissões, infrações e prisão, desenvolvido separadamente e ainda pendente de homologação e integração.
- **Experimentação controlada:** ideias como o **Pesadelo do Nether**, a **Variante E — Floresta Viva**, slimes azuis e uma cidade expandida permanecem em estágios diferentes de estudo ou planejamento, sem autorização automática para implantação.

## 📍 Estado das entregas

| Frente | Situação em 18/10/2026 |
| --- | --- |
| Resource Pack Realista 1.2.0 / Variante D | Aparência homologada pelo Product Owner no Galaxy A54; referência visual preservada. |
| Vila inicial e `/function vila` | Carregamento e geometria principal testados no jogo; arquitetura anterior apresentou defeitos. |
| Vila — Sprint AGY 03.2 | Correções de portas, telhados, acessos e estados de blocos **reportadas**; BP 1.0.3, aguardando reteste no A54. |
| Proteção territorial — Sprint Codex 02.2 | Código e 45 testes automatizados **reportados**; sem homologação real no Bedrock. |
| Pesadelo do Nether | Protótipo de domínio **offline**, isolado do gameplay ativo. |
| Integração e produção | **Não autorizadas** para as entregas em desenvolvimento. |

**Como interpretamos resultados:** implementação de agente, testes automatizados e homologação pelo Product Owner são evidências diferentes. Os detalhes, commits e pendências ficam em [`docs/STATUS.md`](docs/STATUS.md).

## 🛠️ Tecnologias e formatos

| Área | Tecnologias / formatos |
| --- | --- |
| Jogo | Minecraft Bedrock Edition |
| Arte | Resource Packs, texturas, materiais PBR e configurações visuais compatíveis |
| Mundo e estruturas | Behavior Packs, `.mcstructure` (NBT) e funções `.mcfunction` |
| Ferramentas | Python para geração, empacotamento e testes de estruturas/recursos |
| Gameplay | JavaScript e APIs de script do Minecraft Bedrock |
| Infraestrutura | Bedrock Dedicated Server e Docker Compose, em ambiente de produção separado e protegido |
| Engenharia | Git, worktrees, testes automatizados e revisões por sprint |

## 🌳 Organização do desenvolvimento

O projeto utiliza **um repositório Git com worktrees independentes**. A branch `main` concentra a documentação e somente conteúdo cuja integração tenha sido aprovada pelo Product Owner; funcionalidades em desenvolvimento permanecem isoladas.

| Branch | Responsabilidade |
| --- | --- |
| `main` | Base oficial e documentação; integração controlada. |
| `feature/visual-rustico` | **AGY:** direção artística, Resource Pack, estruturas, geradores e testes visuais. |
| `feature/bedrock-core` | **Codex:** gameplay, proteção, permissões, prisão, testes e estudos técnicos. |

Nos worktrees de desenvolvimento, os principais diretórios são:

```text
HallanCraft-AGY/                  # Worktree de arte (não integrado à main)
├── resource-pack/               # Texturas, PBR e água
├── structures/                  # Estrutura da vila e funções
├── tools/                       # Geradores Python e testes
├── docs/                        # Relatórios e guias especializados
├── exports/                     # Pacotes instaláveis gerados
└── archives/                    # Diagnósticos e versões anteriores

HallanCraft-Codex/                # Worktree de gameplay (não integrado à main)
├── behavior_packs/hallancraft_core/
├── test/                        # Testes JavaScript
├── prototypes/nether_nightmare/ # Estudo offline, não ativado
├── scripts/                     # Verificações e ferramentas
├── homologacao/                 # Arquivos de ambiente isolado, não iniciado
└── docs/                        # Auditorias e estudos
```

Essa árvore descreve **as áreas de trabalho das branches**; não significa que todos esses diretórios estejam publicados ou integrados à `main`.

## 🧪 Qualidade e homologação

O fluxo de desenvolvimento segue etapas separadas:

1. **Proposta e autorização:** o Product Owner delimita a missão.
2. **Implementação isolada:** cada agente atua somente na própria branch/worktree.
3. **Validação estática:** scripts, manifestos, integridade de pacotes, regressões e testes automatizados.
4. **Homologação no Minecraft:** teste em **mundo novo e descartável**, especialmente no Galaxy A54 para os pacotes visuais e a vila.
5. **Integração e implantação:** somente mediante **autorização expressa**, avaliação de riscos, backup verificável e plano de reversão.

A vila é carregada, quando o pacote correspondente está ativado em um mundo de **teste**, pelo comando:

```mcfunction
/function vila
```

> **Atenção:** este comando não é uma instrução para uso no mundo da família. O pacote mais recente da vila ainda aguarda homologação. Consulte o guia de instalação na branch visual e [`docs/STATUS.md`](docs/STATUS.md) antes de testar.

## 🔐 Segurança e limites

- O **servidor familiar e seus mundos são protegidos**: desenvolvimento, testes e scripts não podem modificá-los automaticamente.
- Não realizar merges entre as branches nem aplicar packs em produção sem decisão do Product Owner.
- Não publicar backups de mundos, credenciais, `.env`, dados pessoais nem configurações administrativas sensíveis.
- Não confundir worktrees Git com isolamento de segurança do sistema operacional.
- Não ativar globalmente `keepInventory` ou gamerules para contornar riscos do protótipo Pesadelo do Nether.
- Priorizar compatibilidade com Bedrock, desempenho em Android, originalidade dos ativos e entregas pequenas e reversíveis.

## 👥 Papéis e governança

- **Product Owner:** define visão, prioridades, critérios de aceite e autorizações de integração/deploy.
- **AGY:** implementa a frente visual e arquitetônica no escopo autorizado.
- **Codex:** implementa a frente de gameplay e conduz auditorias e protótipos isolados.
- **ChatGPT:** atua como conselheiro técnico do PO, apoiando análise de riscos, relatórios e documentação; não substitui os testes reais nem a autorização humana.

## 📚 Documentação

- [`docs/PROJETO.md`](docs/PROJETO.md) — visão, arquitetura, responsabilidades e decisões duradouras.
- [`docs/STATUS.md`](docs/STATUS.md) — sprints, versões, evidências, impedimentos e próximos passos.
- Documentação especializada de cada frente: disponível nas respectivas branches de desenvolvimento.

## 🚧 Próximos marcos

A prioridade imediata é **homologar no Galaxy A54 as correções arquitetônicas da Sprint AGY 03.2**. Em paralelo, o núcleo de segurança do Codex permanece aguardando testes e eventual autorização específica de integração. Os demais itens do backlog não estão automaticamente autorizados.

## 📄 Direitos e referências

Este é um projeto independente, **não oficial e não afiliado à Mojang ou à Microsoft**. *Minecraft* e marcas relacionadas pertencem aos seus respectivos titulares. Recursos e homenagens temáticas devem respeitar direitos autorais e utilizar conteúdo próprio ou devidamente licenciado.

**Licença do código e dos ativos:** ainda não definida. A disponibilização pública deste repositório, por si só, não constitui concessão de licença para reutilização.
