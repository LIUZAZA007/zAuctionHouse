# Analyse des fonctionnalités - Plugin Auction House (HDV) Minecraft

> **Date :** 2026-02-01
> **Sources analysées :** SpigotMC, Modrinth, BuiltByBit, Hangar, GitHub, CurseForge, forums communautaires

---

## Stratégie de recherche

### Mots-clés utilisés
- "auction house minecraft plugin", "HDV spigot", "auctionhouse features"
- "minecraft marketplace plugin", "player shop plugin", "bazaar minecraft"
- "auction house dupe exploit", "cross-server redis sync"
- "auction bidding system", "buy orders", "anti-snipe"

### Plugins concurrents analysés
| Plugin | Prix | Plateforme | Popularité |
|--------|------|------------|------------|
| **zAuctionHouse** | Premium (€15+) | BuiltByBit/Polymart | Très populaire |
| **Fadah** | Gratuit (Open Source) | SpigotMC/GitHub | En croissance |
| **AuctionHouse (kiranhart)** | Gratuit | SpigotMC/GitHub | Standard |
| **AxAuctions** | €11.99 | BuiltByBit | Premium |
| **CrazyAuctions** | Gratuit | SpigotMC/Modrinth | Classique |
| **Player Auctions** | Premium | SpigotMC | Multi-fonctionnel |
| **AuctionHousePlus** | Premium | SpigotMC | Web interface |
| **DeluxeAuctions** | Premium | BuiltByBit | Multi-serveur |
| **Best Auction** | Gratuit | CurseForge | MMORPG-style |
| **NexusAuctionHouse** | Gratuit | Modrinth | Simple/Léger |

---

## 1. Liste des fonctionnalités par catégorie

### 1.1 Core HDV (Listing, achat, annulation, expire)

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Listing d'items** | Mettre en vente un item avec prix fixe | P0 | Standard tous plugins |
| **Achat direct (BIN)** | Buy It Now - achat instantané au prix affiché | P0 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) |
| **Système d'enchères (Bidding)** | Enchères avec prix de départ et incréments | P1 | [CrazyAuctions](https://github.com/Crazy-Crew/CrazyAuctions), [Player Auctions](https://www.spigotmc.org/resources/player-auctions.83073/) |
| **Expiration automatique** | Items retournés au vendeur après X temps | P0 | Standard tous plugins |
| **Annulation de listing** | Retirer un item de la vente | P0 | Standard tous plugins |
| **Achat partiel (Partial Buy)** | Acheter une partie d'un stack | P2 | [AuctionHouse Hangar](https://hangar.papermc.io/ElaineQheart/AuctionHousePlugin) - `partial-selling: true` |
| **Vente en masse (Bulk Sell)** | Vendre tout l'inventaire en un clic | P2 | [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) |
| **Relisting automatique** | Relancer une vente expirée automatiquement | P3 | [CrazyAuctions](https://github.com/Crazy-Crew/CrazyAuctions/releases) - `/ca force_end_all` |
| **Anti-snipe** | Prolonger enchère si bid dans les dernières X secondes | P2 | Concept standard enchères - Extension 5-15 min si bid tardif |
| **Durée configurable** | Temps de vente personnalisable par grade | P1 | [zAuctionHouse docs](https://zauctionhouse.groupez.dev/configurations/commands-and-permissions) |

### 1.2 UX/UI (Menus, filtres, recherche)

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **GUI principal** | Menu d'inventaire pour naviguer | P0 | Standard tous plugins |
| **Pagination** | Navigation entre pages de listings | P0 | Standard tous plugins |
| **Catégories configurables** | Armor, Tools, Blocks, Food, etc. | P1 | [AdvancedAuctionHouse](https://advancedplugins.net/item/AdvancedAuctionHouse.4), [Fadah](https://github.com/Finally-A-Decent/Fadah) |
| **Recherche par nom** | `/ah search <terme>` | P1 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) |
| **Recherche par enchantement** | Filtrer par enchant spécifique | P2 | [EzAuction](https://hangar.papermc.io/Shadow48402/EzAuction) - Recherche roman numerals |
| **Recherche par vendeur** | Voir items d'un joueur spécifique | P2 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) |
| **Tri par prix** | Croissant/Décroissant | P1 | Standard tous plugins |
| **Tri par date** | Plus récent/Plus ancien | P1 | Standard tous plugins |
| **Tri par temps restant** | Items expirant bientôt en premier | P2 | [XAuctions](https://builtbybit.com/resources/xauctions-auction-house-system.53697/) |
| **Favoris/Watchlist** | Suivre des items ou vendeurs | P2 | [Fadah](https://github.com/Finally-A-Decent/Fadah) - `/ah watch`, [AuctionHouse Bukkit](https://dev.bukkit.org/projects/auctionhouse) |
| **Historique des transactions** | Voir achats/ventes passés | P1 | [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/), [AxAuctions](https://docs.artillex-studios.com/axauctions.html) |
| **Preview Shulker/Barrel** | Voir contenu avant achat (clic droit) | P2 | [AxAuctions](https://builtbybit.com/resources/axauctions-all-in-one-auction-house.40242/) |
| **Affichage enchantements** | Montrer enchants dans le lore | P1 | [Unique Auction House](https://www.spigotmc.org/resources/unique-auction-house-1-21.128532/) |
| **Affichage durabilité** | Montrer durabilité des outils | P2 | [Unique Auction House](https://www.spigotmc.org/resources/unique-auction-house-1-21.128532/) |
| **Menus personnalisables** | YAML/zMenu pour les layouts | P1 | [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) - zMenu integration |
| **Raccourcis clavier** | Drop key pour actions rapides | P3 | [Tweetzy AuctionHouse](https://docs.tweetzy.ca/official-plugins/auction-house/commands-and-permissions) |

### 1.3 Économie & Taxes

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Support Vault** | Économie standard Minecraft | P0 | Standard tous plugins |
| **Multi-économies** | Vault, PlayerPoints, TokenManager, CoinsEngine, etc. | P1 | [zAuctionHouse](https://www.9minecraft.net/zauctionhouse-plugin/), [Player Auctions](https://docs.olziedev.com/projects/playerauctions/plugins) |
| **Taxe de mise en vente (Listing Fee)** | Frais pour poster un item | P1 | [Best Auction](https://0x48lab.github.io/best_auction/) - `listing_fee_rate: 0.05` |
| **Taxe de vente (Sales Tax)** | Pourcentage prélevé sur vente réussie | P1 | [Unique Auction House](https://www.spigotmc.org/resources/unique-auction-house-1-21.128532/) - anti-inflation |
| **Taxe progressive** | Taxe variable selon prix (tiered) | P3 | [Hypixel Forums](https://hypixel.net/threads/new-auction-house-tax-bazaar-tax-system.5929347/), [PikaNetwork](https://pika-network.net/threads/better-ah-tax-system.558118/) |
| **Taxe par permission** | Réduction de taxe selon grade | P2 | [Fadah](https://github.com/Finally-A-Decent/Fadah) - `fadah.listing-tax.<percent>` |
| **Prix minimum** | Empêcher ventes à 0$ ou très bas | P1 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) - `minSellPrice` |
| **Prix maximum** | Limiter prix max contre inflation | P2 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) - `maxSellPrice` |
| **Anti-self-buy** | Empêcher d'acheter ses propres items | P1 | Bug fixé dans plusieurs plugins |
| **Économie item-based** | Payer avec des items (diamants, etc.) | P3 | [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) |
| **Prévisualisation profit** | Montrer gain net après taxes | P2 | [Unique Auction House](https://www.spigotmc.org/resources/unique-auction-house-1-21.128532/) |

### 1.4 Modération & Anti-abus

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Anti-dupe système** | Prévention duplication d'items | P0 | [AuctionHouseAntiDupe](https://www.spigotmc.org/resources/auctionhouseantidupe.109427/), [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) - "100% efficient" |
| **Blacklist items** | Interdire certains items (Bedrock, etc.) | P0 | [Fadah](https://github.com/Finally-A-Decent/Fadah), [NexusAuctionHouse](https://modrinth.com/plugin/nexusauctionhouse) |
| **Blacklist par lore** | Interdire items avec lore spécifique (Soulbound) | P1 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) |
| **Blacklist par enchant** | Interdire items avec enchants bannis | P2 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) |
| **Ban de joueurs** | Empêcher un joueur d'utiliser l'AH | P1 | [AuctionHousePlugin](https://hangar.papermc.io/ElaineQheart/AuctionHousePlugin) - `/ah ban` |
| **Logs des transactions** | Historique pour admins | P1 | [Fadah](https://github.com/Finally-A-Decent/Fadah), [DeluxeAuctions](https://nullforums.net/resources/deluxeauctions.10216/) |
| **Admin remove** | Supprimer un listing (mod/admin) | P1 | [Tweetzy AuctionHouse](https://docs.tweetzy.ca/official-plugins/auction-house/commands-and-permissions) - Drop key en admin mode |
| **Admin view expired** | Voir items expirés de tout le monde | P2 | [Tweetzy AuctionHouse](https://docs.tweetzy.ca/official-plugins/auction-house/commands-and-permissions) |
| **Anti-exploit offline** | Bloquer packets joueurs offline | P1 | [ExploitFixer](https://builtbybit.com/resources/exploitfixer-anti-crash-dupe-plugin.26463/) |
| **Unique ID par item** | Hash unique pour détecter dupes | P2 | [Anti-Dupe Modrinth](https://modrinth.com/plugin/anti-dupe) - Hybrid hash + UUID |
| **Détection stacks illégaux** | Retirer items avec stack > 64 | P2 | [Dupe Fixes SpigotMC](https://www.spigotmc.org/resources/dupe-fixes-illegal-stack-remover.44411/) |

### 1.5 Permissions & Ranks

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Limite listings par grade** | VIP=10, MVP=20, etc. | P1 | [zAuctionHouse](https://zauctionhouse.groupez.dev/configurations/commands-and-permissions), [AuctionX](https://builtbybit.com/resources/auctionx.84045/) |
| **Durée par grade** | VIP=3j, MVP=7j | P1 | [AuctionHousePlugin](https://hangar.papermc.io/ElaineQheart/AuctionHousePlugin) |
| **Réduction taxes par grade** | VIP=-10%, MVP=-25% | P2 | [AuctionHousePlus](https://www.spigotmc.org/resources/auctionhouseplus.123740/) |
| **Permission moderator** | Droits admin AH | P1 | [AuctionHousePlugin](https://hangar.papermc.io/ElaineQheart/AuctionHousePlugin) - `auctionhouse.moderator` |
| **LuckPerms meta** | Utiliser meta pour limites | P2 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/) - `meta set auctions <value>` |
| **Bypass blacklist** | Admins peuvent vendre items bannis | P3 | Hypothèse - demande fréquente admins |
| **Permission par catégorie** | Accès catégorie selon grade | P3 | Hypothèse - serveurs MMORPG |

### 1.6 Intégrations

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **PlaceholderAPI** | `%ah_active_listings%`, `%ah_player_listings%` | P1 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/update?update=362652) |
| **Discord webhooks** | Notifications listings/ventes | P1 | [AxAuctions](https://builtbybit.com/resources/axauctions-all-in-one-auction-house.40242/), [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) |
| **DiscordSRV** | Intégration native DiscordSRV | P2 | [AuctionHouse SpigotMC](https://www.spigotmc.org/resources/auctionhouse.61836/update?update=296288) |
| **LuckPerms** | Permissions + offline check | P1 | [AuctionHousePlus](https://www.spigotmc.org/resources/auctionhouseplus.123740/) |
| **ItemsAdder/Oraxen** | Support items custom | P2 | [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) |
| **MMOItems** | Support items MMORPG | P2 | [MMOItems Wiki](https://git.mythiccraft.io/mythiccraft/mmoitems/-/wikis/Enchant%20Plugins) |
| **Citizens NPC** | Ouvrir AH via NPC | P2 | [EconomyShopGUI](https://www.spigotmc.org/resources/economyshopgui.69927/) |
| **Interface Web** | Accès web à l'AH | P3 | [AuctionHousePlus](https://modrinth.com/plugin/auctionhouseplus), [Best Auction](https://www.curseforge.com/minecraft/bukkit-plugins/best-auction) |
| **API REST** | Pour sites/apps externes | P3 | [Best Auction](https://0x48lab.github.io/best_auction/) |
| **Leaderboards** | Top vendeurs/acheteurs | P2 | [ajLeaderboards](https://www.spigotmc.org/resources/ajleaderboards.85548/), [LeaderHeads](https://panoply.tech/leaderheads) |

### 1.7 Compatibilité & Technique

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Support Folia** | Multi-threading Paper fork | P1 | [Fadah](https://www.spigotmc.org/resources/fadah-finally-a-decent-auction-house-folia-shreddedpaper-support.116157/) |
| **Support ShreddedPaper** | Autre fork multi-thread | P2 | [Fadah](https://www.spigotmc.org/resources/fadah-finally-a-decent-auction-house-folia-shreddedpaper-support.116157/) |
| **Multi-versions (1.8-1.21)** | JAR unique toutes versions | P1 | [kiranhart Auction-House](https://github.com/kiranhart/Auction-House) |
| **MySQL/MariaDB** | Base de données distante | P0 | Standard plugins sérieux |
| **PostgreSQL** | Alternative à MySQL | P2 | [AxAuctions](https://builtbybit.com/resources/axauctions-all-in-one-auction-house.40242/) |
| **MongoDB** | Base NoSQL | P3 | [Fadah](https://github.com/Finally-A-Decent/Fadah) |
| **SQLite** | Base locale par défaut | P0 | Standard tous plugins |
| **Redis sync** | Sync multi-serveur temps réel | P1 | [zAuctionHouseRedis](https://zauctionhouse.groupez.dev/configurations/multi-servers), [Fadah](https://github.com/Finally-A-Decent/Fadah) |
| **BungeeCord/Velocity** | Support proxy | P1 | [AuctionHousePlus](https://www.spigotmc.org/resources/auctionhouseplus.123740/) |
| **Async operations** | Pas de lag main thread | P0 | Standard moderne |
| **Migrations auto** | Upgrade schéma DB automatique | P1 | Bonne pratique |
| **Backup automatique** | Sauvegarde périodique | P2 | [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) |

### 1.8 Admin Tools

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Force end auction** | Terminer enchère manuellement | P1 | [CrazyAuctions](https://github.com/Crazy-Crew/CrazyAuctions) - `/ca force_end_all` |
| **Clear all auctions** | Supprimer tous les listings | P2 | [Tweetzy AuctionHouse](https://docs.tweetzy.ca/official-plugins/auction-house/commands-and-permissions) - `/ah admin clear` |
| **View player auctions** | Voir listings d'un joueur | P1 | [Fadah](https://github.com/Finally-A-Decent/Fadah) - `/ah active [player]` |
| **Inspect item** | Voir détails NBT d'un item | P2 | Outil debug standard |
| **Reload config** | Recharger sans restart | P1 | Standard tous plugins |
| **Stats globales** | Nombre ventes, volume, etc. | P2 | PlaceholderAPI + dashboard |
| **Purge expired** | Nettoyer items expirés anciens | P2 | Maintenance DB |
| **Import/Export** | Migration entre plugins | P2 | [Fadah](https://github.com/Finally-A-Decent/Fadah) - Migrations zAH, kiranhart |
| **World blacklist** | Désactiver dans certains mondes | P2 | [NPCAuctions](https://www.spigotmc.org/resources/npcauctions.47020/update?update=261470) |

### 1.9 Qualité (Perf, Cache, i18n)

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Pagination efficace** | Réutiliser menu vs recréer | P1 | [Fadah](https://github.com/Finally-A-Decent/Fadah) - "More efficient pagination" |
| **Cache listings** | Éviter requêtes DB répétées | P1 | Architecture standard |
| **Cache joueurs** | Données joueur en mémoire | P1 | FastUtil collections recommandé |
| **Async DB operations** | CompletableFuture partout | P0 | Standard moderne |
| **Connection pooling** | HikariCP pour MySQL | P1 | Standard ORM |
| **Messages configurables** | messages.yml complet | P0 | Standard tous plugins |
| **Multi-langue** | Fichiers lang séparés | P2 | [Best Auction](https://www.curseforge.com/minecraft/bukkit-plugins/best-auction) |
| **MiniMessage support** | Formatage moderne | P1 | [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) |
| **HEX colors** | Couleurs hex dans messages | P1 | Standard moderne |
| **bStats metrics** | Statistiques anonymes | P2 | Bonne pratique |

### 1.10 Extras (Enchères, offres, buy orders)

| Fonctionnalité | Description | Priorité | Sources |
|----------------|-------------|----------|---------|
| **Enchères vraies (Bidding)** | Système d'enchères compétitif | P1 | [Player Auctions](https://www.spigotmc.org/resources/player-auctions.83073/), [CrazyAuctions](https://github.com/Crazy-Crew/CrazyAuctions) |
| **Buy Orders** | Demandes d'achat (je veux acheter X à Y$) | P3 | Hypixel Bazaar style - Rare dans plugins |
| **Offres (Offers)** | Proposer prix différent au vendeur | P3 | Hypothèse - demande communauté |
| **Enchères silencieuses** | Blind auction - bids cachés | P3 | Hypothèse - niche |
| **Mail system** | Recevoir items achetés offline | P1 | [Best Auction](https://www.curseforge.com/minecraft/bukkit-plugins/best-auction), [BeeAuction](https://modrinth.com/plugin/beeauction) |
| **Claim system** | Récupérer items/argent | P0 | Standard - `/ah collect`, `/ah expired` |
| **Notifications offline** | Message à la connexion | P1 | [BeeAuction](https://modrinth.com/plugin/beeauction), [NPCAuctions](https://www.spigotmc.org/resources/npcauctions.47020/update?update=261470) |
| **Alertes prix** | Notifier quand item dispo à X prix | P2 | [Fadah](https://github.com/Finally-A-Decent/Fadah) - Auction Watcher |
| **Quick sell NPC** | Vendre instantanément à NPC | P3 | Hypixel style - [EconomyShopGUI](https://www.spigotmc.org/resources/economyshopgui.69927/) |
| **Price history** | Historique des prix d'un item | P3 | [MarketPlacePlus](https://modrinth.com/plugin/marketplaceplus), [Coflnet](https://sky.coflnet.com/) |
| **Market analytics** | Tendances, moyenne prix | P3 | [Donut Stats](https://www.donutstats.net/), [Bazaar Tracker](https://bazaartracker.com/) |
| **Advert system** | Promouvoir son listing (broadcast) | P2 | [Fadah](https://github.com/Finally-A-Decent/Fadah) - Listing Adverts |

---

## 2. Priorisation des fonctionnalités

### Légende
- **P0** : Indispensable - Bloquant pour release MVP
- **P1** : Important - Standard du marché, attendu par les utilisateurs
- **P2** : Différenciateur - Améliore l'expérience, pas critique
- **P3** : Nice-to-have - Fonctionnalités avancées/niche

### P0 - MVP (Bloquant)

| Fonctionnalité | Justification |
|----------------|---------------|
| Listing d'items | Core feature |
| Achat direct (BIN) | Core feature |
| Expiration automatique | Gestion cycle de vie |
| Annulation de listing | Contrôle utilisateur |
| GUI principal + Pagination | UX de base |
| Support Vault | Économie standard |
| SQLite + MySQL | Stockage |
| Blacklist items | Sécurité serveur |
| Anti-dupe système | **CRITIQUE** - Risque économie |
| Async operations | Performance |
| Claim system | Récupération items |
| Messages configurables | Personnalisation |

### P1 - Version 1.0 (Standard marché)

| Fonctionnalité | Justification |
|----------------|---------------|
| Catégories configurables | Organisation |
| Recherche par nom | Trouvabilité |
| Tri par prix/date | UX standard |
| Système d'enchères (Bidding) | Feature demandée |
| Multi-économies | Flexibilité serveurs |
| Taxe listing + vente | Balance économique |
| Prix min/max | Protection économie |
| Historique transactions | Confiance/Debug |
| Ban joueurs | Modération |
| Logs transactions | Admin tools |
| Limite listings par grade | Monétisation serveur |
| Durée par grade | Monétisation serveur |
| PlaceholderAPI | Intégration écosystème |
| Discord webhooks | Communication |
| Support Folia | Futur-proof |
| Redis sync | Multi-serveur |
| Mail system | UX offline |
| Notifications offline | UX offline |
| Affichage enchantements | Clarté items |

### P2 - Version 2.0 (Différenciateur)

| Fonctionnalité | Justification |
|----------------|---------------|
| Achat partiel | UX avancée |
| Vente en masse | Gain de temps |
| Anti-snipe | Équité enchères |
| Recherche par enchant | Power users |
| Recherche par vendeur | Navigation |
| Tri par temps restant | UX enchères |
| Favoris/Watchlist | Engagement |
| Preview Shulker/Barrel | Trust items |
| Taxe progressive | Balance fine |
| Taxe par permission | Monétisation |
| Unique ID par item | Anti-dupe avancé |
| Réduction taxes par grade | Monétisation |
| ItemsAdder/Oraxen | Compatibilité custom |
| Citizens NPC | UX immersive |
| Leaderboards | Gamification |
| PostgreSQL/MongoDB | Options DB |
| Backup automatique | Sécurité data |
| Purge expired | Maintenance |
| Multi-langue | Accessibilité |
| Alertes prix | Engagement |
| Advert system | Visibilité ventes |

### P3 - Future (Nice-to-have)

| Fonctionnalité | Justification |
|----------------|---------------|
| Relisting auto | Automatisation |
| Économie item-based | Niche |
| Bypass blacklist admin | Edge case |
| Permission par catégorie | MMORPG niche |
| Interface Web | Complexe à maintenir |
| API REST | Développeurs externes |
| ShreddedPaper | Adoption limitée |
| Buy Orders | Système Bazaar complexe |
| Offres (Offers) | Négociation complexe |
| Enchères silencieuses | Niche |
| Quick sell NPC | Système différent |
| Price history | Beaucoup de données |
| Market analytics | Complexe |

---

## 3. Top Sources (25+ liens)

### SpigotMC
1. [AuctionHouse (klugemonkey)](https://www.spigotmc.org/resources/auctionhouse.61836/) - Plugin classique, maintenu par communauté
2. [Player Auctions](https://www.spigotmc.org/resources/player-auctions.83073/) - Multi-serveur, catégories auto
3. [Fadah](https://www.spigotmc.org/resources/fadah-finally-a-decent-auction-house-folia-shreddedpaper-support.116157/) - Gratuit, Folia, moderne
4. [AuctionHousePlus](https://www.spigotmc.org/resources/auctionhouseplus.123740/) - Interface web, Velocity
5. [Unique Auction House](https://www.spigotmc.org/resources/unique-auction-house-1-21.128532/) - Taxes, anti-inflation
6. [CrazyAuctions](https://www.spigotmc.org/resources/crazy-auctions.25219/) - Classique, simple

### BuiltByBit (ex MC-Market)
7. [zAuctionHouse](https://builtbybit.com/resources/zauctionhouse-auction-house-plugin.8987/) - Premium, très complet
8. [AxAuctions](https://builtbybit.com/resources/axauctions-all-in-one-auction-house.40242/) - Multi-currency, SQL messaging
9. [AuctionX](https://builtbybit.com/resources/auctionx.84045/) - Moderne, bidding, mail

### Modrinth
10. [NexusAuctionHouse](https://modrinth.com/plugin/nexusauctionhouse) - Simple, gratuit
11. [BeeAuction](https://modrinth.com/plugin/beeauction) - Claims offline, SQLite
12. [AzAuctionHouse](https://modrinth.com/plugin/azauctionhouse) - Performance, customisation
13. [MarketPlacePlus](https://modrinth.com/plugin/marketplaceplus) - Prix analytics, ratings

### Hangar (PaperMC)
14. [AuctionHousePlugin](https://hangar.papermc.io/ElaineQheart/AuctionHousePlugin) - Paper natif, partial buy
15. [EzAuction](https://hangar.papermc.io/Shadow48402/EzAuction) - Recherche enchants intelligente

### GitHub
16. [kiranhart/Auction-House](https://github.com/kiranhart/Auction-House) - Open source, 1.8-1.21
17. [Finally-A-Decent/Fadah](https://github.com/Finally-A-Decent/Fadah) - Open source, bien documenté
18. [Crazy-Crew/CrazyAuctions](https://github.com/Crazy-Crew/CrazyAuctions) - Open source, changelogs
19. [Shock95/AuctionHouse (PMMP)](https://github.com/Shock95/AuctionHouse) - PocketMine, issues actifs

### Documentation
20. [zAuctionHouse Docs](https://zauctionhouse.groupez.dev/configurations/multi-servers) - Multi-serveur Redis
21. [Tweetzy AuctionHouse Docs](https://docs.tweetzy.ca/official-plugins/auction-house/commands-and-permissions) - Commandes/Permissions
22. [AxAuctions Docs](https://docs.artillex-studios.com/axauctions.html) - Configuration détaillée
23. [Best Auction Docs](https://0x48lab.github.io/best_auction/) - MMORPG-style config

### CurseForge
24. [Best Auction](https://www.curseforge.com/minecraft/bukkit-plugins/best-auction) - Bidding, mail, REST API

### Discussions communautaires
25. [Hypixel Forums - Tax System](https://hypixel.net/threads/new-auction-house-tax-bazaar-tax-system.5929347/) - Réflexions équilibre économique
26. [PikaNetwork - Better AH Tax](https://pika-network.net/threads/better-ah-tax-system.558118/) - Taxe tiered
27. [SpigotMC - Best AH plugin?](https://www.spigotmc.org/threads/best-auction-or-auction-house-plugin.313063/) - Comparatifs communauté
28. [SpigotMC - Dupe glitch](https://www.spigotmc.org/threads/auction-plugin-duplication-glitch.489388/) - Problèmes dupes

---

## 4. Analyse & Tendances

### Top demandes récurrentes

1. **Anti-dupe robuste** - Préoccupation #1 des admins. Plusieurs plugins ont eu des vulnérabilités.
2. **Support multi-serveur (Redis)** - Essentiel pour réseaux, souvent addon payant.
3. **Système d'enchères (Bidding)** - Différenciateur vs simple "Buy It Now".
4. **Catégories personnalisables** - Organisation UX demandée.
5. **Discord integration** - Communication serveur standard.
6. **Support Folia** - Future-proofing pour performances.
7. **Limite/durée par grade** - Monétisation pour serveurs.

### Ce qui manque souvent

| Fonctionnalité | Présent dans | Absent de |
|----------------|--------------|-----------|
| Buy Orders (demandes d'achat) | Hypixel Bazaar (custom) | Tous plugins publics |
| Price history/analytics | Outils externes (Coflnet) | Plugins in-game |
| Anti-snipe | Plugins enchères dédiés | Plugins AH génériques |
| Enchères silencieuses | Aucun identifié | Tous |
| Système d'offres | Aucun identifié | Tous |

### Ce qui différencie les meilleurs plugins

| Plugin | Différenciateur clé |
|--------|---------------------|
| **zAuctionHouse** | Écosystème complet (zMenu), anti-dupe, Redis |
| **Fadah** | Open source gratuit avec features premium, Folia |
| **AxAuctions** | Multi-currency natif, SQL messaging |
| **AuctionHousePlus** | Interface web unique |
| **Best Auction** | Style MMORPG, REST API |

### Controverses & Risques

| Risque | Description | Mitigation |
|--------|-------------|------------|
| **Duplication** | Items dupliqués via exploits timing/packets | Hash unique + vérification transactions + block offline packets |
| **Économie cassée** | Inflation/déflation par mauvais pricing | Taxes, prix min/max, taxe progressive |
| **Performance DB** | Lag avec beaucoup de listings | Pagination efficace, cache, async, indexes |
| **Self-buy exploit** | Blanchiment d'argent | Bloquer achat propres items |
| **Enchères sniping** | Bid dernière seconde injuste | Anti-snipe (extension temps) |
| **UX complexe** | Trop d'options = confusion | Config simple par défaut, advanced opts cachées |
| **Cross-server sync** | Race conditions, data inconsistency | Redis avec locking distribué |

---

## 5. Roadmap recommandée

### Phase MVP (Base fonctionnelle)

**Objectif :** Plugin utilisable avec core features solides

| Feature | Notes |
|---------|-------|
| Listing/Achat/Annulation | Core flow |
| GUI avec pagination | zMenu intégré |
| SQLite + MySQL | Storage options |
| Blacklist items | Matériaux + lore |
| Anti-dupe basique | Hash items, block offline |
| Taxe de vente simple | % configurable |
| Prix min/max | Protection économie |
| Expiration + claim | Cycle complet |
| Messages configurables | i18n ready |
| Vault economy | Standard |

### Phase 1.0 (Standard marché)

**Objectif :** Compétitif avec plugins gratuits existants

| Feature | Notes |
|---------|-------|
| Catégories configurables | Via config YAML |
| Recherche par nom/type | AnvilGUI ou commande |
| Tri prix/date/durée | Boutons GUI |
| Système bidding | Prix départ + incréments |
| Historique transactions | GUI dédié |
| Logs admin | Fichier + commande |
| Limite listings/grade | Permissions |
| PlaceholderAPI | Placeholders standards |
| Discord webhooks | Listing/vente/achat |
| Notifications offline | À la connexion |
| Folia support | Via FoliaLib |

### Phase 2.0 (Différenciateur premium)

**Objectif :** Meilleur que la concurrence

| Feature | Notes |
|---------|-------|
| Redis multi-serveur | Via addon séparé |
| Multi-économies | CoinsEngine, TokenManager, etc. |
| Anti-snipe | Extension temps configurable |
| Watchlist/Favoris | Notifications quand dispo |
| Achat partiel | Quantité sélectionnable |
| Taxe par permission | Grades VIP |
| Preview containers | Shulkers, barrels |
| Import autres plugins | zAHv3, kiranhart, Fadah |
| Leaderboards | PlaceholderAPI |
| Citizens NPC | Open AH via NPC |
| Advert system | Broadcast listing payant |

### Phase Future (Innovation)

| Feature | Notes |
|---------|-------|
| Buy Orders | Système "je veux acheter" |
| Price history | Graphiques in-game |
| Market analytics | Tendances, moyennes |
| Interface Web | Via API REST |
| Enchères silencieuses | Bids cachés |

---

## 6. Recommandations spécifiques pour zAuctionHouse V4

### Forces actuelles (à conserver)
- Architecture service layer avec CompletableFuture
- zMenu pour GUI flexible
- Sarah ORM pour DB
- FoliaLib pour compatibility
- Cluster bridge pour multi-serveur

### Améliorations suggérées

1. **Anti-dupe renforcé**
   - Ajouter hash unique par item (PDC)
   - Vérifier lors de listing ET achat
   - Logger toutes transactions suspectes

2. **Système bidding**
   - Nouveau type `AuctionType.BIDDING` vs `BIN`
   - `BidRepository` pour historique bids
   - Event `PreBid` / `PostBid`

3. **Watchlist**
   - Table `player_watches` (player_uuid, material, max_price, notify)
   - Notification async à la connexion ou création listing

4. **Multi-économies**
   - Interface `AuctionEconomy` déjà prévue
   - Ajouter implementations CoinsEngine, TokenManager

5. **Anti-snipe**
   - Config `anti-snipe-enabled`, `anti-snipe-seconds`, `anti-snipe-extend`
   - Vérifier dans `BidService` si temps restant < threshold

6. **Discord natif**
   - Webhooks dans `MainConfiguration`
   - `DiscordWebhookService` pour envoyer embeds

---

*Rapport généré le 2026-02-01 - Basé sur l'analyse de 25+ plugins et sources communautaires*
