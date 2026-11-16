# Shulkerboxes — Torre do Caos (somente servidor novo)

**F1.2 é um protótipo administrativo: ainda não há hordas, construção livre, recompensas nem jogo roguelike.**

## Instalação sem programas no computador

1. Crie um servidor **novo e independente**, com **Paper 1.21.4**, na Shulkerboxes. Não use o servidor do José.
2. Na [GitHub Actions](https://github.com/hallanbrito/HallanCraft/actions/workflows/torre-do-caos-ci.yml), abra uma execução **verde** da branch `feat/torre-do-caos-f0-f1`.
3. Em **Artifacts**, baixe `TorreDoCaos-Paper-1.21.4-F1.2`. O GitHub entrega um arquivo ZIP que contém o JAR, o checksum SHA256 e um LEIA-ME.
4. Pelo próprio Windows 11, clique com o botão direito e escolha **Extrair tudo**. **Não instale Gradle, Java ou o ZIP no servidor**.
5. Na Shulkerboxes, abra **Version / plugins**, envie o arquivo `TorreDoCaos-Paper-1.21.4-F1.2.jar` e reinicie **somente o novo servidor**.
6. Confirme pelo log que o plugin `TorreDoCaos` foi habilitado e que a migração SQLite foi executada. Não trate esse log como homologação do jogo.

**O JAR é compilado automaticamente na nuvem e não faz deploy.** A Shulkerboxes precisa estar configurada com a mesma versão de Paper do plugin. Se essa versão não estiver disponível, interrompa: revalidaremos compatibilidade antes de instalar.

## Exportação opcional de arena (F1.2)

O exportador é para uma arena construída previamente num mundo descartável, não para um mundo familiar.

- Abra `plugins/TorreDoCaos/config.yml` pelo gerenciador de arquivos do painel.
- Em `arenas`, defina o mundo e as coordenadas mínima e máxima de `arena-1`. O volume aceita até 20.000 blocos; não inclui containers/block entities.
- Reinicie o servidor; carregue os chunks, e rode pelo **console do novo servidor**:
  - `torre admin exportarena arena-1`
  - `torre admin verificarena arena-1`

O blueprint é criado uma única vez e o comando não sobrescreve um arquivo existente. Ele **não restaura blocos**: reset seguro, loot, waves e respawn são fases posteriores.

## Antes de jogar

- [ ] Segunda instância gratuita confirmada no painel.
- [ ] Versão Paper 1.21.4 disponível e testada.
- [ ] Build/CI verde e JAR baixado.
- [ ] Plugin carregando sem erros no servidor novo.
- [ ] Implementação F1.3–F1.6 concluída.
- [ ] Testes de morte, reset após falha, solo/cooperativo, RAM/TPS homologados.

**Importante:** este documento prepara um pacote de instalação, mas não autoriza deploy de gameplay incompleto na Shulkerboxes. Sem merge para `main` e sem alterações no servidor do José.

Referência oficial de upload de plugin: https://shulkerboxes.com/guides/minecraft-realms-alternatives
