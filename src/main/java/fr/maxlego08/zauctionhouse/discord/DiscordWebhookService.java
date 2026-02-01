package fr.maxlego08.zauctionhouse.discord;

import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.item.items.AuctionItem;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class DiscordWebhookService {

    private final AuctionPlugin plugin;
    private final HttpClient httpClient;
    private DiscordConfiguration configuration;

    public DiscordWebhookService(AuctionPlugin plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        loadConfiguration();
    }

    public void loadConfiguration() {
        this.configuration = DiscordConfiguration.of(plugin);
        if (configuration.enabled()) {
            plugin.getLogger().info("Discord webhooks enabled");
        }
    }

    public boolean isEnabled() {
        return configuration != null && configuration.enabled();
    }

    public CompletableFuture<Void> notifyItemSold(Player seller, AuctionItem auctionItem) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(null);
        }

        var webhook = configuration.sellWebhook();
        if (!webhook.isValid()) {
            return CompletableFuture.completedFuture(null);
        }

        return sendWebhook(webhook, auctionItem, seller, null);
    }

    public CompletableFuture<Void> notifyItemPurchased(Player buyer, AuctionItem auctionItem) {
        if (!isEnabled()) {
            return CompletableFuture.completedFuture(null);
        }

        var webhook = configuration.purchaseWebhook();
        if (!webhook.isValid()) {
            return CompletableFuture.completedFuture(null);
        }

        return sendWebhook(webhook, auctionItem, null, buyer);
    }

    private CompletableFuture<Void> sendWebhook(DiscordConfiguration.WebhookConfiguration webhook, AuctionItem auctionItem, Player seller, Player buyer) {
        return CompletableFuture.runAsync(() -> {
            try {
                var resolver = new DiscordPlaceholderResolver(configuration.serverName(), auctionItem, seller, buyer);

                String json = buildWebhookJson(webhook, resolver);

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(webhook.url())).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).timeout(Duration.ofSeconds(30)).build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 400) {
                    plugin.getLogger().warning("Discord webhook failed with status " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to send Discord webhook: " + e.getMessage());
            }
        }, plugin.getExecutorService());
    }

    private String buildWebhookJson(DiscordConfiguration.WebhookConfiguration webhook, DiscordPlaceholderResolver resolver) {
        var embedConfig = webhook.embed();
        var builder = DiscordEmbed.builder()
                .title(resolver.resolve(embedConfig.title()))
                .description(resolver.resolve(embedConfig.description()))
                .color(embedConfig.color())
                .timestamp(embedConfig.timestamp());

        // Webhook-level options
        if (webhook.hasUsername()) {
            builder.username(resolver.resolve(webhook.username()));
        }

        if (webhook.hasAvatarUrl()) {
            builder.avatarUrl(resolver.resolve(webhook.avatarUrl()));
        }

        if (webhook.hasContent()) {
            builder.content(resolver.resolve(webhook.content()));
        }

        // Embed fields
        for (var field : embedConfig.fields()) {
            builder.addField(resolver.resolve(field.name()), resolver.resolve(field.value()), field.inline());
        }

        if (embedConfig.footer().hasContent()) {
            builder.footer(resolver.resolve(embedConfig.footer().text()), resolver.resolve(embedConfig.footer().iconUrl()));
        }

        if (embedConfig.author().hasContent()) {
            builder.author(resolver.resolve(embedConfig.author().name()), resolver.resolve(embedConfig.author().url()), resolver.resolve(embedConfig.author().iconUrl()));
        }

        if (embedConfig.thumbnail().hasContent()) {
            builder.thumbnail(resolver.resolve(embedConfig.thumbnail().url()));
        }

        if (embedConfig.image().hasContent()) {
            builder.image(resolver.resolve(embedConfig.image().url()));
        }

        return builder.build().toJson();
    }
}
