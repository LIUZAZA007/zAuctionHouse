# Unreleased

> **Read this section before updating a multi-server network.** Two configurations that used to start are now refused outright, and a number of operations that used to succeed silently by overwriting the database now fail with a message. That is the point of this release, but it is visible to your players and to your staff on day one.

### Operational changes to read first

- **Changed** SQLite storage combined with the Redis multi-server addon is now refused explicitly on both sides at startup. Each server keeps its own SQLite file, so the cluster synchronised nothing while every node reported itself as clustered and held a divergent auction house. Networks in that state must move `storage-type` to `MYSQL` or `MARIADB` before updating; until they do, the addon refuses to install its cluster bridge and the console explains why
- **Changed** A node that cannot reach Redis no longer falls back silently to single-server mode: the addon installs a fail-closed cluster bridge that refuses every sale, purchase and removal until the connection comes back, instead of letting each node operate on its own and duplicate items. Operators who run a single server with the addon installed "just in case" must now uninstall the addon rather than leave it in a failed state
- **Changed** Every write to the items table is now a compare-and-set guarded by the row's current state. An operation that lost a race — against another server, or against a second click on the same listing — now fails with a message instead of succeeding by overwriting a row another node had already changed. This closes several duplication paths, but on a busy network players will start seeing an "item no longer available" message they had never seen before
- **Changed** Item-specific tax rules (`tax.item-rules` in `economies.yml`) are now evaluated on purchase whatever the economy `type` is. A `PURCHASE` or `CAPITALISM` rule declared on a `type: SELL` economy used to be silently inert at purchase time and applies from now on, so review your item rules before updating or your buyers will suddenly start paying more. For a listing containing several stacks, the rule that yields the highest tax wins, which aligns purchases with the behaviour the selling side already had
- **Changed** A listing whose serialised content can no longer be decoded — a Minecraft downgrade, a removed custom-item plugin, a corrupted row — is now quarantined at load time: logged as `SEVERE` and hidden from the auction house instead of being displayed as an empty or `BARRIER` stack that could still be bought. The database row is deliberately left untouched, so a node running an older Minecraft version can never destroy items that are perfectly valid for the other nodes
- **Added** Four database indexes, created by schema migrations on the first start after the update: `items(storage_type)`, `transactions(player_unique_id)`, `logs(created_at)` and `players(name)`. None of them existed, and the queries that run on every connection were full table scans. On a MySQL installation whose tables already hold several hundred thousand rows, each `CREATE INDEX` can block the startup for minutes and they run one after the other: plan the maintenance window, or create the indexes by hand beforehand. If you restart a whole network at once, every node will try to create them simultaneously
- **Added** Three nullable columns and one table, added by migration: `transactions.claim_token` and `transactions.claim_reserved_at` (the claim reservation), `items.pending_publish` (the listing milestone, see below) and a `migration_state` table (the migration idempotence marker). All of them are nullable and additive, so downgrading the jar simply ignores them — the schema is not a one-way door
- **Added** A scheduled expiration sweep, **enabled by default** (`maintenance.expiration-sweep-interval-seconds: 60`, `maintenance.expiration-sweep-batch-size: 50`). Expiration used to be lazy and memory-driven, so a listing past its expiry date stayed on sale until a player happened to open a tab that iterated the store. On a server with a large backlog of listings that were never collected, hundreds of items will move to the expired tab in the minutes following the first restart and their sellers will receive a flood of items to claim; set the interval to `0` to disable the sweep. In multi-server setups each item is expired under the distributed lock, so two nodes can never expire it twice

### Money

- **Fixed** A purchase could withdraw more money than the amount that had been checked: the required balance was computed without the listing's real items, so item-specific tax rules were ignored by the check but applied by the debit. A single resolver now serves the button, the purchase service and the manager, so the amount verified and the amount taken can no longer diverge
- **Fixed** A `CAPITALISM` item tax rule declared on a `PURCHASE` or `BOTH` economy credited the seller with `price + tax` while the buyer only paid `price`, creating money out of nothing on every sale. The tax result now carries the type that was really applied instead of being reinterpreted downstream with the economy's own type
- **Fixed** `withdraw` and `deposit` results were never checked: a refused withdrawal still handed the item to the buyer, and a refused deposit silently destroyed the seller's payment. Both now report success or failure, and a purchase is aborted before any item movement when the payment is refused
- **Fixed** Economies of type `LEVEL`, `EXPERIENCE`, `ITEM` and `ZMENUITEMS` silently destroyed the seller's payment when the seller was offline, because those providers are backed by the player entity and simply do nothing without one. Those payments become claimable pending transactions, and `must-be-online` is forced to `true` for them at startup with a console warning naming the economy
- **Fixed** The sell tax could be reported as paid without ever being collected: the balance check and the withdrawal are now a single verified operation, and a tax refund that fails is logged as critical instead of vanishing
- **Fixed** An exception raised by the economy plugin while writing the transaction history aborted the purchase *after* the buyer had been charged and the seller credited, leaving the listing on sale
- **Fixed** Claiming pending money marked transactions as retrieved even when nothing had been paid: a missing economy, a failed deposit or an offline player each skipped the deposit but not the marking, destroying the money. Only the identifiers actually credited are closed now, and an economy removed from `economies.yml` leaves its rows pending with a warning in console
- **Fixed** The same pending payment could be claimed twice by racing `/ah claim`, the claim button and the auto-claim on join. A claim now reserves its rows with a unique token before paying anything, so two concurrent claims can no longer see the same transaction, and an interrupted claim releases its reservation automatically
- **Changed** For a listing containing several stacks, the purchase tax now uses the highest matching item rule instead of only the first stack, which aligns it with the selling side

### Duplication, removal and purchase

- **Fixed** Item duplication on the withdrawal path in clustered setups. Removing a listed, selling, expired or purchased item now re-reads the authoritative database row **while holding the cluster lock**, exactly like the purchase path already did, and aborts when another server already sold, claimed or removed it. The extra query runs only when a distributed cluster bridge is installed, so single-server installations are unaffected
- **Fixed** A withdrawal could act on a stale item reference captured by an already-rendered inventory button (selling, expired, purchased, combined and admin views). Removal now refuses any reference that is no longer the one held by the in-memory store, and an item removed by identifier is marked terminal so that buttons still armed in an open GUI can no longer act on it
- **Fixed** A purchase mutated memory, caches and the buyer's inventory before the database write was confirmed: if the write failed, the buyer kept the item and the row stayed listed — a plain duplication, reachable without any cluster at all. The row is now committed first and every memory mutation, cache invalidation and item handover is chained behind it
- **Fixed** `adminRemoveItem` ignored a failed lock acquisition and removed the item anyway while another server held the lock, broadcast the removal *before* writing to the database and with a non-terminal destination (other servers reloaded the row, found it still listed and put the item back on sale), and never released the cluster lock when the operation failed
- **Fixed** Removing a listed item evaluated the "give the item back" condition twice — once to pick the destination broadcast to the cluster, once to pick what was written to the database. With `action.remove-listed-item.give-item: true` and a nearly full inventory the two could disagree, leaving the item stored as expired locally while every other server saw it as deleted, making it permanently unclaimable
- **Fixed** Bulk removal ("retrieve everything") abandoned the rest of the batch on the first internal error and said nothing about it; it now carries on through the batch and reports to the player how many listings were actually processed
- **Changed** With `action.remove-listed-item.give-item: true`, an inventory that fills up between the click and the execution no longer silently sends the item to the expired tab: the item is handed back and the overflow drops on the ground
- **Changed** Admin removal of an expired or purchased item now propagates a terminal state across the cluster, so other nodes no longer re-add the row. Networks that relied on that re-add to make the item claimable elsewhere will no longer see it — this is the intent, the admin physically took the item

### Listing an item

- **Fixed** Listing an item was not atomic: the stacks left the seller's inventory before the row and its serialised contents were durably written, so a crash in that window destroyed them. A listing is now reserved and its contents committed first, then published, and any failure refunds both the items and the sell tax on the seller's own thread
- **Added** A startup sweep for listings left half-created by a crash, driven by the new `items.pending_publish` milestone. The milestone distinguishes the only two cases that matter: the stacks had **not** yet left the seller's inventory, or they had. In the first case the orphaned row and its contents are **deleted** — handing them back would give the seller a second copy of a lot they never lost. In the second the row is made claimable in the seller's expired items. Both outcomes are logged, the deletions as `SEVERE` with the item identifiers, because that branch destroys rows. Rows written by earlier versions carry no milestone and are left untouched
- **Fixed** The sell selection is bounded to what the GUI can actually display and retrieve, instead of silently swallowing the surplus

### Frozen confirmations

- **Added** A safety net for an item frozen in a confirmation screen: `maintenance.confirmation-timeout-seconds` (default 60, minimum 60) and `maintenance.confirmation-sweep-interval-seconds` (default 10). A player who disconnected, died, or lost their cache with a purchase or removal confirmation open used to leave the item locked and unbuyable on every server of the network. The status is now released explicitly on quit and on death, and any status older than the timeout is released by the sweep
- **Fixed** The two exits of a confirmation screen (validate, cancel) went through different code paths and could leave the item locked on one of them; they are unified and hardened
- **Fixed** `/ah admin cache clear` wiped the keys tracking operations in progress, releasing items that were legitimately locked inside a confirmation

### Item serialisation

- **Added** `write-itemstack-format-marker` (first-level key in `config.yml`, default `false`), phase two of the new item serialisation format. The plugin now reads both the marked and the unmarked format whatever the server version, so an item serialised by a newer server is no longer mis-decoded by an older one. Leave the key at `false` until every server of the network runs the updated jar: a payload written with the marker cannot be read back by an earlier jar, and until you flip it you can roll back without losing a single listing

### Shutdown, concurrency and performance

- **Added** `server-shutting-down` message, shown when a purchase or a sale is attempted while the server is stopping, instead of letting the operation start and be cut in half
- **Fixed** The storage executor silently dropped database writes that had already been decided. It is now a named, sized pool with an unbounded queue, drained on disable, and every JDBC call is routed through it. A write can still be refused in exactly two cases — the executor is already shutting down, or accepting it would run blocking JDBC on a tick thread — and a refusal is now logged as `SEVERE` with the stack trace of the submission site instead of vanishing
- **Fixed** The per-player cache was not safe under concurrent access and could be resurrected after the player had disconnected; it is now keyed by UUID and its mutations are confined
- **Fixed** The inventory refresh loop stopped at the first skipped player, so only a fraction of the connected players saw the auction house update after a sale or a removal. Every connected player is refreshed now — `action.update-inventory-on-action: false` remains the escape hatch for large servers
- **Fixed** Category counters returned `0` for any category whose key in `categories.yml` contained uppercase letters, because the category was indexed lowercased but built with the raw key. A server that declared, for example, a `Weapons` category will suddenly see a real number where it saw zero
- **Fixed** Search, sorting and the category counters no longer copy the whole item store on every evaluation, and the sorted cache uses a generation counter so an invalidation can no longer be swallowed by a rebuild that was already running
- **Fixed** Admin logs re-split multi-stack payloads when they are read instead of when they are written, and log deletion uses the delete row count instead of pre-counting the rows
- **Fixed** Loading the auction house pages its `IN` clauses, fails loudly instead of half-loading on a database error, and never publishes a listing with no content. Reloading the items in place — after a migration, for instance — is now a real reset instead of an additive load that resurrected ghosts and reset every status

### V3 migration and admin commands

- **Fixed** `/ah admin migrate` reported a green success on a migration that had destroyed money. Under MySQL the pending balances of the V3 installation were written with a foreign key that could not exist, the insert was refused, and the exception was caught by a `catch` that could never see it: every seller's pending money was lost while the command announced a full import. Those rows now hang off a sentinel item and the command reports the real verdict
- **Fixed** Replaying the migration duplicated every item and every pending balance. A marker is now written in the new `migration_state` table once a provider has completed, the command refuses to run twice, and an explicit `force` argument is required to override it
- **Fixed** The migration ran on the executor shared with gameplay and nothing prevented two of them at once. It now runs on its own single thread, a second invocation is refused while one is running, and the command refuses to start at all while players are connected — reloading the items in place pulls the stores out from under them
- **Fixed** The migration overwrote the name of players who already existed in V4 with a placeholder
- **Fixed** `/ah admin logs clear-migrated` matched migrated log entries by an identifier that no longer identifies them, so it reported a success having purged nothing. It now matches on the migration marker
- **Fixed** `/ah admin add` destroyed the item held by the administrator before writing anything to the database and never gave it back when the write failed, computed an expiry date in the past for `expired` and `purchased` — making the item immediately unclaimable — and never announced its listings to the cluster
- **Fixed** `/ah admin generate` ran thousands of blocking queries on the main thread and could hand its generated listings to real players whose name the random generator happened to produce
- **Fixed** The three log purge commands reported a success when the deletion had actually failed

### Redis addon

- **Added** Lock lease renewal: while a purchase or a removal is running, the lock is renewed every `redis-config.lock-ttl-seconds / 3`, and the plugin verifies that it still holds the lock before charging anyone. `redis-config.lock-ttl-seconds` is therefore a *lease*, not a deadline, and no longer needs to be oversized to survive a slow economy plugin or a slow database. The new `redis-config.lock-renew-enabled` (default `true`) turns the watchdog off for installations that would rather abort a too-long transaction than spend a Jedis connection on renewal
- **Fixed** Every message published while the subscriber was reconnecting was lost for good, and the addon re-subscribed blindly, leaving items sold elsewhere as buyable ghosts. After each interruption of the bus, the listed store is reconciled against the authoritative item states held in Redis and terminal items are purged. Known limit: an item whose state key has already expired (`redis-config.item-state-ttl-seconds`, 24 h by default) is kept, so an outage longer than that TTL can still leave ghosts behind
- **Fixed** Cluster messages could be applied out of order and a stale status could overwrite a newer one; messages are now ordered per item and a status change is applied by compare-and-swap instead of blindly
- **Fixed** The addon's shutdown released nothing: the bridge now switches to fail-closed, releases the locks it still holds, and the subscriber thread really stops. The Jedis pool wait is bounded and nested connection borrows are gone, so a saturated pool can no longer deadlock the addon
- **Changed** A node that cannot register its instance identity in Redis now refuses to start instead of joining the bus with an identity it cannot vouch for. The registration failure used to be a warning, and the heartbeat was a bare expiry refresh on a key that could have been evicted or lost to a Redis restart: the node believed itself registered for good, and a duplicate identity was never detected. The heartbeat now rewrites the key, and a collision with another node is caught
- **Fixed** The addon started its subscriber thread and its listeners before the cluster bridge was installed, so incoming messages could mutate memory with no bridge in place. Listeners are registered and the subscription confirmed first, and a main plugin that is disabled is refused at the door
- **Fixed** An item bought while its seller was connected to *another* node did not trigger that seller's auto-claim; the seller is credited on the node they are actually on, and the log entry is marked as read only after the credit has succeeded
- **Fixed** An auction house already open on a remote node is now visually purged when an item is removed or bought there, instead of keeping a clickable ghost until the next refresh
- **Fixed** A single unconvertible field no longer destroys an entire synchronisation message
- **Fixed** The addon's version checker never reported anything

### Configuration and messages

- **Added** New `maintenance` section in `config.yml` (`expiration-sweep-interval-seconds`, `expiration-sweep-batch-size`, `confirmation-timeout-seconds`, `confirmation-sweep-interval-seconds`) and the first-level `write-itemstack-format-marker` key, replicated across the six shipped languages (en/fr/es/it/id/th)
- **Added** `action.save-profile-on-sell` in `config.yml` (default `true`), replicated across the six shipped languages. It forces the seller's profile to be written to disk immediately after their stacks have left their inventory: without it, a crash between the sale and the next periodic profile save hands the items back to a player who is already selling them. It is a synchronous disk write on every sale — turn it off on a heavily loaded server, or when an inventory synchronisation plugin already owns persistence
- **Added** `admin-item-not-available`, `server-shutting-down`, `item-no-longer-available`, `sell-error-invalid-item`, `sell-inventory-full` and `remove-all-items-partial` messages, replicated across the six shipped languages
- **Changed** `economies.yml` documents the two behaviour changes above in all six languages: the `must-be-online` block now names the economy types that cannot credit an offline player and explains that their sellers are paid through `/ah claim`, and the tax block explains that item rules apply on purchase even on a `type: SELL` economy
- **Added** `redis-config.lock-renew-enabled` in the Redis addon's `config.yml`, and `redis-config.lock-ttl-seconds` is re-documented as a lease duration

### Known limitations of this release

- On a purchase, the money still moves before the item row is committed. If the database write loses the race — another node sold the same listing first — the buyer has been charged and the listing stays with its seller, who also keeps a claimable pending payment. The incident is logged as `SEVERE` with the identifiers needed to reconcile it by hand, but it is not compensated automatically. Reversing that order is the next piece of work and it is not in this release
- The listing limit still counts listings and not stacks, so a rank limited to five listings can still hold thirty-six stacks in each of them, and the limit is evaluated once against the local cache rather than re-checked against the database
- The addon is still compiled against a pinned API revision with no compatibility check at startup: update the plugin first and the addon second, never the reverse

# 4.0.1.3

- **Added** FastStats metrics integration alongside the existing bStats reporting. The FastStats client is bundled and relocated in the final JAR, starts after plugin initialization, and shuts down cleanly with the plugin
- **Fixed** The final plugin JAR containing three duplicate `META-INF/faststats.properties` entries. FastStats is now scoped to the root plugin module, and Shadow keeps a single copy of its identical version metadata
- **Added** Indonesian (`id`) locale with translated configuration, messages, rules, categories, Discord settings, inventories, and patterns. Automatic language detection now selects Indonesian for servers located in Indonesia
- **Fixed** Admin expired, purchased, and selling item inventories not displaying their YAML-configured empty-state item when the selected player had no matching items

# 4.0.1.2

- **Updated** CurrenciesAPI dependency from 1.0.13 to 1.0.14 - the misspelled ExcellentEconomy economy type is renamed from `EXCELLENTEECONOMY` to `EXCELLENTECONOMY`. Both spellings are now accepted as a `type` in `economies.yml`; the old one is deprecated and will be removed in a future CurrenciesAPI release
- **Added** Permission for admin button:
   - `admin_expired_items_remove`, allow players to remove expired items from the auction house
   - `admin_logs_give_item_to_player`, allow players to give items to other players from the auction house logs
   - `admin_purchased_items_remove`, allow players to remove purchased items from the auction house
   - `admin_listed_items_remov`, allow players to remove listed items from the auction house

# 4.0.1.1

- **Added** `allow-decimal-prices` option in `config.yml` (default `true`) - when set to `false`, players can no longer list items for a price containing decimals (e.g. `10.5`); only whole-number prices are accepted. Enforced on every sell path (the `/ah sell` command and the sell-inventory confirm button) via `SellService`, and shows the new `price-decimal-not-allowed` message. Option and message synced across all language files (en/fr/es/it/th)
- **Added** CrazyAuction migration.

# 4.0.1.0

- **Fixed** Critical cross-server duplication where a sold item reappeared in the seller's items (multi-server / Redis). When `purchased-item.give-item: true`, a purchase set the database row to `DELETED` before broadcasting the `ItemBought` event; on every other server the `ItemBoughtListener` re-fetched the item via `selectItem` (which filters out `DELETED` rows), got `null`, and bailed out **before** removing the listing from its in-memory store. The ghost listing therefore survived on all non-buyer servers and the seller still saw it in `/ah selling`. The listener now removes the listing from in-memory storage **unconditionally** (mirroring `ItemRemovedListener`), independently of the database lookup. `ItemBoughtMessage` was enriched with the seller UUID/name, item display, price and buyer name so the real-time seller notification (including `%buyer%`, which was already broken on remote servers) works without a database round-trip
- **Fixed** Purchased items not appearing on other servers with `purchased-item.give-item: false` (multi-server / Redis). The `ItemBoughtListener` removed the item from `LISTED` on remote servers but never added it to their in-memory `PURCHASED` store, so a buyer switching servers could not see their purchase until a restart. Remote servers now re-add the item to `PURCHASED` (the buyer-server purchase already did this locally; the buyer-server ignores its own message). For `give-item: true` the message flags `givenToBuyer` so remote servers skip the (always-null) database lookup entirely
- **Fixed** A crashed/timed-out purchase could leave an item permanently un-buyable in multi-server (Redis) setups. The lock key `auction:lock:<id>` expires via TTL but the item hash field `state=LOCKED` persisted (up to the item TTL, ~30 days), so `checkAvailability` reported the item as locked forever. Lock state now keys on the lock key's existence: a `LOCKED` state with no live lock key is treated as a stale lock and recovered. To prevent this recovery from ever enabling a double-purchase, `PurchaseService` now re-validates authoritative database state **under the lock** before charging — a row that is already `DELETED` or has a buyer aborts the purchase with nothing withdrawn
- **Fixed** Expiration is now cluster-aware in multi-server (Redis) setups. Previously `ExpireService` moved items `LISTED -> EXPIRED` purely locally with no cluster coordination, so a lingering ghost of an item sold on another server could be resurrected as an `EXPIRED` item for the seller (and claimed back = duplication). In distributed setups the `LISTED -> EXPIRED` transition now locks the item, re-reads authoritative database state (skipping items sold elsewhere), performs the move, and broadcasts it so all nodes converge. Single-server setups keep the original fast path
- **Fixed** `ItemRepository` could overwrite a sold/removed item when expiring. The `LISTED -> EXPIRED` update is now guarded with `WHERE storage_type = 'LISTED'`, so an expiration `UPDATE` can never clobber a row another server already set to `DELETED`/`PURCHASED`
- **Added** DonutAuction migration - migrate data from the DonutAuction plugin (by EliVB) to zAuctionHouse V4 using `/ah admin migrate donutauction confirm` (aliases `donut`, `donutsmp`, `da`). Reads `plugins/DonutAuction/ah.data` directly, so no database configuration is needed; active auctions are imported as listed items and past-expiry auctions as reclaimable expired items, while unclaimed pending payments (money, not items) are skipped. Legacy Base64 itemstacks are deserialized through a hardened class allowlist to guard against malicious data files
- **Fixed** Cross-server purchases could fail with `IllegalArgumentException: name cannot be null` (multi-server / Redis). When a buyer bought an item whose seller had never logged into that specific backend node, `getSellerName()` returned `null` and was passed straight to the economy provider's `withdraw()` call (and to the `%seller%` placeholder in the purchase notifications), aborting the purchase. The seller name is now resolved from the shared database before the economy call, falling back to the seller's UUID string (with a console warning to verify all servers share the same MySQL database) when even that lookup fails, so a `null` name can never reach downstream code
- **Fixed** Purchases failing or charging incorrectly under CAPITALISM (VAT-style) tax - the balance check before buying only verified the buyer could afford the listed price, not the price plus the CAPITALISM tax added on top, so a player holding exactly the listed amount passed the check and the tax-inclusive withdrawal could then fail. `PurchaseService` and the `ZAUCTIONHOUSE_LISTED_ITEMS` button now check the tax-inclusive required balance when CAPITALISM tax is enabled
- **Fixed** Items getting stuck in the "being purchased" state after a purchase failed for insufficient funds - the item was marked `IS_BEING_PURCHASED` before the funds check but only unlocked (not status-restored) on failure, so in multi-server (Redis) setups it stayed stuck and could no longer be bought. The status is now restored and broadcast to the cluster before unlocking
- **Fixed** Money loss on economy errors during a purchase or claim - buyer withdraw, seller deposit and claim deposits are now wrapped in error handling: if the buyer is charged but the seller deposit throws, the buyer is automatically refunded; failed claim deposits are logged and skipped instead of silently losing the money
- **Fixed** Expired listings could still be displayed and counted in the auction house. Because expiration is processed lazily, an item could remain `AVAILABLE` past its expiry (especially while a cache was stale) and keep showing in the main list, in the per-category counts, and in the `%zauctionhouse_listed_items%` placeholder. A single `Item.isActivelyListed()` predicate (available **and** not expired) is now applied at every display chokepoint, and interacting with a listing that has already expired now moves it to the player's expired items instead of opening a confirm inventory or re-showing it
- **Fixed** The selling, expired, purchased, listed and combined item-list buttons ignored their inventory's `player-inventory` setting - paginated items and the empty-slot placeholder were always rendered into the chest GUI even when the inventory was configured as a player inventory. These buttons now render items into the player's own inventory area when configured (PR #25)
- **Fixed** Auction search button always showed a hardcoded English `None` in `%search_query%` when no search was active, ignoring the server language. The `ZAUCTIONHOUSE_SEARCH` button now reads a configurable `none-value` option on the search button in `inventories/auction.yml`, with a localized default per language (`None` / `Aucune` / `Ninguna` / `Nessuna` / `ไม่มี`), so the empty-search text is translatable and customizable
- **Added** Configurable `%search_active%` value on the auction search button - the `ZAUCTIONHOUSE_SEARCH` button now reads `active-value` (default `true`) and `inactive-value` (default `false`) options in `inventories/auction.yml`, so the value returned by `%search_active%` when a search is / isn't active can be customized (e.g. `Enabled`/`Disabled`, `Yes`/`No`) instead of the hardcoded `true`/`false`
- **Changed** Updated the required zMenu API to `1.1.1.6` - make sure your server runs zMenu `1.1.1.6` or newer

# 4.0.0.9

- **Fixed** Sales history spamming `Item not found for log ID` warnings in console - logs migrated from V3 are created with `item_id = 0` (no direct mapping to V4 items), causing the history service to log a warning for every V3 entry. The history now filters out logs with invalid item references at the SQL level
- **Added** History configuration in `config.yml` - `history.max-entries` limits the number of history entries displayed per player (default: 500, 0 = unlimited), `history.expire-after-days` hides entries older than the specified number of days (default: 0 = never expire). Useful for servers with large transaction volumes to reduce database load and improve history loading times
- **Added** Admin log management commands - `/ah admin logs purge <days>` deletes all logs older than the specified number of days, `/ah admin logs player <player>` deletes all logs for a specific player, `/ah admin logs clear-migrated` deletes all V3 migrated logs (entries with `item_id = 0`). All operations run asynchronously and report the number of deleted entries
- **Fixed** Ghost items in `/ah selling` on multi-server (Redis) setups - when a player removed a selling item on server #1 and switched to server #2, the item could still appear with "being purchased" lore and could not be removed, blocking selling slots. Three fixes applied: (1) `ItemRemovedListener` now removes the item from in-memory storage immediately on message receipt instead of waiting for an async DB query, (2) `RemoveService` no longer restores the item status via Redis when the local removal already completed (preventing ghost items from reappearing as AVAILABLE on other servers after a cluster notification timeout), (3) `getPlayerSellingItems` now filters out items with `DELETED` status as defense-in-depth
- **Changed** Discord webhook `%expires_at%` placeholder now outputs a Discord [dynamic timestamp](https://discord.com/developers/docs/reference#message-formatting-timestamp-styles) (`<t:unix_seconds:f>`) instead of a pre-formatted date string. Discord renders it in each viewer's own local timezone (e.g. `12 June 2026 18:55`), making it easier to read and timezone-agnostic. Items with no expiration still fall back to the formatted date
- **Added** Per-item custom images for Discord webhooks - a new `custom-images` section in `discord.yml` lets you override the `%item_image_url%` for specific items. Each entry pairs a `rule` (same format as `categories.yml`/`rules.yml`: `material`, `name`, `lore`, `custom-model-data`, `oraxen`, `itemsadder`, `nexo`, `mmoitems`, etc.) with an image URL. The first matching rule wins; items matching no rule fall back to the material-based `item-image-url` pattern. Ideal for custom items whose generic material does not represent the real item. Dominant color extraction caches the color per image URL so custom items sharing a material no longer collide

# 4.0.0.8

- **Fixed** Critical item duplication in multi-server (Redis) setups - when a player removed a selling item via the selling inventory, the item was correctly given to the player and marked as `DELETED` in the database, but other servers received a generic `LISTED` removal message and incorrectly recreated the item in their `EXPIRED` cache, allowing it to be claimed a second time on another server
- **Fixed** `ItemRepository.select(int id)` not filtering `DELETED` items - single-item database queries returned items already marked as `DELETED`, while the bulk `select()` method correctly excluded them. This allowed the Redis `ItemRemovedListener` to reload deleted items and re-add them to cache
- **Added** `destinationStorageType` to cluster bridge `removeItem` method - the `ItemRemovedMessage` now includes the exact destination storage type (`DELETED` or `EXPIRED`) so other servers know exactly what happened instead of guessing. Backward compatible via a `default` method on `AuctionClusterBridge`
- **Fixed** `ItemRemovedListener` (Redis addon) rewritten with defense-in-depth - uses the explicit destination from the message first, falls back to database state verification, and never blindly assumes an item should go to `EXPIRED`
- **Fixed** `CreateOptionsMigration` failing on MySQL with `errno: 150 "Foreign key constraint is incorrectly formed"` - the `player_unique_id` column used `uuid()` type which could produce a different collation than the existing `players` table (especially on MySQL 8.0+ with changed default collation). Changed to `string(36)` to match all other foreign key columns referencing `players.unique_id`

# 4.0.0.7 

- **Added** `banned-rules` support for categories - allows excluding specific items from a category even if they match the inclusion rules. For example, a netherite hoe would normally appear in the "Tools" category, but if it has a specific CustomModelData (e.g., 300), it can be excluded using a banned rule. Uses the same rule types as regular rules (material, tag, lore, custom-model-data, etc.)
- **Added** Broadcast system - sends messages to all online players when items are listed or purchased. Configurable per event type (sell/purchase) with options to exclude the seller/buyer. Supports per-category message overrides using MiniMessage format with `%seller%`, `%buyer%`, `%items%`, `%price%`, `%category%` placeholders. Disabled by default, enable in `config.yml` under `broadcast`
- **Added** Player options system - extensible per-player preference system backed by the database. Players can toggle options via `/ah option` (opens GUI) or `/ah option <option_name> [value]` (command toggle). Options are cached in memory and only stored in the database when different from the default value. Currently supports `broadcast_sell` and `broadcast_purchase` to opt out of broadcast messages
- **Added** Options inventory (`/ah option`) - 27-slot GUI with toggle buttons for each broadcast option, showing current status (enabled/disabled)
- **Added** Admin option commands - `/ah admin option set <player> <option> <value>` to set options for a player, `/ah admin option list <player>` to view player options, `/ah admin option reset <player>` to reset all options to defaults
- **Added** `%zauctionhouse_option_<option_name>%` PlaceholderAPI placeholder - returns `true`/`false` for a player's option value (e.g., `%zauctionhouse_option_broadcast_sell%`)

# 4.0.0.6

- **Added** `ZAUCTIONHOUSE_COMBINED_ITEMS` button - combines selling, expired, and purchased items into a single paginated view. Each source is individually togglable via `include-selling`, `include-expired`, and `include-purchased`. Click actions automatically adapt to the item's storage type (cancel listing, claim expired, claim purchased). Each item type uses its own lore configuration from `config.yml`
- **Fixed** Item duplication exploit in multi-server (Redis) setups - removing a listing on one server and claiming the expired item could leave a ghost entry on the original server, allowing the item to be claimed again after natural expiration
- **Fixed** `ItemRemovedListener` (Redis addon) - removed fragile DB state validation that rejected removal messages due to race conditions, added safety-net cleanup across all storage types
- **Fixed** `ItemStatusListener` (Redis addon) - status change propagation now searches across all storage types (LISTED, EXPIRED, PURCHASED) instead of only LISTED
- **Fixed** `ExpireService` - added guard against re-expiring items already deleted/claimed on another server
- **Added** Redis Sentinel support (Redis addon) - enables high-availability Redis setups with automatic master discovery and failover. Configure `mode: "sentinel"` in `config.yml` with sentinel nodes. Fully backward compatible, existing standalone configurations work without changes
- **Fixed** `NullPointerException` when default economy is not configured - `/ah sell` and all sell-related buttons now display an error message instead of crashing. Added startup validation with prominent warnings in console when a default economy is missing from `economies.yml`
- **Fixed** Incorrect economy type names in `economies.yml` comments - `COINS_ENGINE` and `PLAYER_POINTS` did not match the actual CurrenciesAPI enum values (`COINSENGINE`, `PLAYERPOINTS`), causing these economy types to silently fail to load when users followed the documented names
- **Added** All permissions are now programmatically registered in Spigot on startup and reload. Includes static permissions (use, sell, admin, etc.), and dynamic permissions from configuration (listing limits, expiration tiers, tax bypass/reductions, economy access, inventory commands, cooldown bypass). All permissions are grouped under the `zauctionhouse.*` wildcard
- **Fixed** Items dropping on the ground when claiming expired, purchased, or selling items with a full inventory - items now stay in their storage when the player's inventory is full. Added `player-inventory-must-have-free-space` config option under `remove-expired-item` and `selling-item` sections (enabled by default)
- **Added** `ZAUCTIONHOUSE_REMOVE_ALL_EXPIRED`, `ZAUCTIONHOUSE_REMOVE_ALL_SELLING`, `ZAUCTIONHOUSE_REMOVE_ALL_PURCHASED` buttons - allows players to retrieve all items at once from expired, selling, and purchased inventories. Items are given one by one and stops when inventory is full (if `player-inventory-must-have-free-space` is enabled)
- **Added** `ItemContentProvider` API - extensible system for displaying the contents of container items (shulker boxes, custom containers from plugins). External plugins can register their own providers via `AuctionPlugin.getItemContentManager().registerProvider()`
- **Added** AxShulkers hook - displays the contents of shulker boxes managed by the AxShulkers plugin in the item content viewer. AxShulkers stores shulker contents externally instead of in vanilla NBT, so this hook is required to view their contents
- **Fixed** Seller not receiving money in multi-server (Redis) setups when the buyer is on a different server - the plugin tried to deposit money locally on the buyer's server where the seller's economy account may not exist. Money is now deferred to `PENDING` status in distributed environments and claimed by the seller on their own server via `/ah claim` or auto-claim on join
- **Added** `AuctionClusterBridge.isDistributed()` - allows cluster bridge implementations to signal a multi-server environment so the plugin can adapt its behavior (e.g., defer deposits instead of executing them locally)

# 4.0.0.5

- **Added** `ZAUCTIONHOUSE_CLAIM` button - displays pending money per economy with dynamic placeholders and allows players to claim directly from the auction GUI
- **Added** Configurable `loading-item` for the claim button, shown while pending money data is being fetched
- **Added** `PRICE_WITHOUT_DECIMAL` price format - displays prices without decimal places (e.g., `10000.50` -> `10000`)
- **Added** `%price-price-without-decimal%` placeholder - displays the price without decimals in item lore
- **Added** `/ah admin forceopen` can now be executed from the console
- **Added** `timezone` configuration option - allows changing the timezone used for all date placeholders (`%date%`, `%formatted-expire-date%`, `%expires_at%`). Supports all Java TimeZone IDs (e.g., `Europe/Paris`, `America/New_York`, `UTC`). Defaults to `auto` (server timezone)
- **Fixed** `/ah admin open` and `/ah admin history` tab completion no longer loads all offline players, preventing lag on servers with many players
- **Fixed** `updateListedItems` crash on Folia/Canvas - inventory holder access was running on an async thread instead of the main tick thread
- **Fixed** economy name argument configuration for the sell command - when the economy argument index was not configured, the default value was ignored causing a null economy name

### `ZAUCTIONHOUSE_CLAIM` button

Allows players to claim their pending money directly from the auction house inventory. The button displays per-economy pending amounts using dynamic placeholders and supports a loading state while data is being fetched.

**Available placeholders:**

| Placeholder | Description |
|-------------|-------------|
| `%pending_total%` | Total pending money across all economies (formatted) |
| `%pending_<economy_name>%` | Pending money for a specific economy (e.g., `%pending_vault%`) |
| `%has_pending%` | `true` or `false` |

The economy name corresponds to the name defined in your `economies.yml` configuration.

**Example configuration:**

```yaml
claim-money:
  type: ZAUCTIONHOUSE_CLAIM
  slot: 48
  loading-item:
    material: CLOCK
    name: "#2CCED2<bold>ᴄʟᴀɪᴍ ᴍᴏɴᴇʏ"
    lore:
      - "#8c8c8c• #ff3535Loading, please wait..."
  item:
    material: GOLD_INGOT
    name: "#2CCED2<bold>ᴄʟᴀɪᴍ ᴍᴏɴᴇʏ"
    lore:
      - ""
      - "#92ffffPending money: #2CCED2%pending_total%"
      - "#92ffffVault: #2CCED2%pending_vault%"
      - "#92ffffTokens: #2CCED2%pending_tokens%"
      - ""
      - "#8c8c8c• #2CCED2Click to claim your money"
```

# 4.0.0.4

- **Added** ZelAuction migration - migrate data from ZelAuction plugin to zAuctionHouse V4 using `/ah admin migrate zelauction confirm`
- **Added** Search system - players can search items by name, material, lore, or seller directly from the auction house GUI or via `/ah search <query>`
- **Added** `ZAUCTIONHOUSE_SEARCH` button - opens a chat-based search input with support for advanced filter operators
- **Added** `ZAUCTIONHOUSE_CLEAR_SEARCH` button - clears the active search filter, only visible when a search is active
- **Added** `/ah search <query>` command - search items directly from chat without opening the GUI first
- **Added** Advanced search filters with operators: `~` (contains), `=` (exact), `~=` (contains, ignore case), `==` (exact, ignore case)
- **Added** Searchable fields: `name`, `material`, `lore`, `seller` (e.g., `seller = Notch`, `name ~ Diamond`)
- **Added** `/ah admin forceopen <player> <inventory> [page]` - Open any inventory for a player at a specific page. Supports all inventory names (e.g., `auction`, `admin-selling-items`, `history`, `admin-logs`, etc.) with tab completion. Page defaults to 1
- **Added** `reset-search-on-open` config option - When enabled (default: `true`), the search filter is cleared every time a player opens the auction house, matching the existing `reset-category-on-open` behavior

### Search system

Players can search for items in the auction house using the search button or the `/ah search` command. The default search checks item name, material, lore, and seller name (case-insensitive substring).

**Advanced filters:**

```
field operator value
```

| Operator | Description |
|----------|-------------|
| `~` | Contains (case-sensitive) |
| `=` | Exact match (case-sensitive) |
| `~=` | Contains (ignore case) |
| `==` | Exact match (ignore case) |

| Field | Description |
|-------|-------------|
| `name` | Item display name |
| `material` | Item material type |
| `lore` | Item lore text |
| `seller` | Seller player name |

**Examples:**
```
seller = Notch
name ~ Diamond
material ~= sword
lore ~ Sharpness
```

# 4.0.0.3

- **Added** `ZAUCTIONHOUSE_SELL_LIMIT` button - displays remaining sell slots visually using a list of inventory slots, configurable per item type (auction, bid, rent)
- **Added** `%zauctionhouse_max_items_<type>%` placeholder - returns the maximum number of items a player can list for a specific type (auction, bid, rent)
- **Optimized** item lore placeholder resolution, placeholders are now pre-detected at config load and only resolved when referenced, significantly reducing CPU and memory usage per item render
- **Fixed** `/ah history` stuck on "Loading..." when `action.purchased-item.give-item: true` is enabled
- **Fixed** default config values
- **Fixed** default table prefix to `zauctionhousev4` (fixes compatibility with zAuctionHouse V3)

### `ZAUCTIONHOUSE_SELL_LIMIT` button

```yaml
sell-limit:
  type: ZAUCTIONHOUSE_SELL_LIMIT
  types:
    - auction
  slots:
    - 0-9
  item:
    material: LIME_STAINED_GLASS_PANE
    name: '&aAvailable slot'
```

### `%zauctionhouse_max_items_<type>%` placeholder

Returns the maximum number of items a player can list based on their permissions for the given type.

- `%zauctionhouse_max_items_auction%`
- `%zauctionhouse_max_items_bid%`
- `%zauctionhouse_max_items_rent%`

# 4.0.0.2

- **Added** `zauctionhouse_category` permissible for zMenu - allows conditional button visibility based on the player's currently selected category (defaults to `main`)
- **Added** `ZAUCTIONHOUSE_CATEGORY_SWITCHER` button - combines category cycling (left/right click) with dynamic lore showing enable/disable state per category
- **Added** `%zauctionhouse_category_id%` placeholder - returns the player's currently selected category ID
- **Added** `ZAUCTIONHOUSE_HISTORY_INVENTORY` button - opens the sales history inventory directly from any UI
- **Added** `force-amount-one` option in `config.yml` (`item-lore` section) - forces displayed item amount to 1 in the auction inventory while preserving the real amount internally
- **Changed** `AuctionEconomy` API - economy methods (`get`, `has`, `deposit`, `withdraw`) now accept `UUID` instead of `OfflinePlayer` for better compatibility
- **Updated** CurrenciesAPI dependency from 1.0.12 to 1.0.13
- **Fixed** category display name fallback - now uses the configured "all" category name instead of a hardcoded `"All"` string
- **Fixed** empty slot crash - list buttons (`ListedItems`, `ExpiredItems`, `PurchasedItems`, `SellingItems`) no longer crash when `emptySlot` is `-1` and the list is empty
- **Fixed** history loading slot - `HistoryItemsButton` now properly handles `loadingSlot` set to `-1` without throwing an error
- **Fixed** `LoadingSlotLoader` - added validation for invalid slot values with a clear error message

### `zauctionhouse_category` permissible

```yaml
requirements:
  - type: zauctionhouse_category
    category: "weapons"
```

### `ZAUCTIONHOUSE_CATEGORY_SWITCHER` button

```yaml
category-switcher:
  type: ZAUCTIONHOUSE_CATEGORY_SWITCHER
  slot: 49
  enable-text: "&a● %category%"
  disable-text: "&7○ %category%"
  categories:
    - "main"
    - "weapons"
    - "armor"
    - "tools"
    - "blocks"
    - "consumables"
    - "resources"
    - "enchanted-books"
    - "misc"
  item:
    material: COMPASS
    name: "&6Categories &7(&f%category%&7)"
    lore:
      - ""
      - "%main%"
      - "%weapons%"
      - "%armor%"
      - "%tools%"
      - "%blocks%"
      - "%consumables%"
      - "%resources%"
      - "%enchanted-books%"
      - "%misc%"
      - ""
      - "&7Left-click &8» &fNext"
      - "&7Right-click &8» &fPrevious"
```

### `force-amount-one` option

```yaml
item-lore:
  # Forces the displayed item amount to 1 in the auction inventory.
  # The real amount is preserved internally and given to the buyer on purchase.
  # Useful to keep a clean, uniform display.
  force-amount-one: false
```

# 4.0.0.1

- **Added** Thai as a supported language
- **Fixed** support for Minecraft **1.20.4**
- **Fixed** the `/zauctionhouse` command - it is no longer the default main command (this can be changed in `config.yml`)
- **Fixed** message system errors that could appear without reason
- **Added** the `reset-category-on-open` option, allowing categories to reset when reopening the inventory
- **Added** `EXCELLENTEECONOMY` economy support