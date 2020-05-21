package com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator.Problem;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidatorAggregator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.util.CompositionFormatUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CompositionGui extends Gui {
    private static final MenuScheme TOP_BAR_GLASS = new MenuScheme()
            .mask("111101111");

    private static final MenuScheme TOP_BAR_BOOK = new MenuScheme()
            .mask("000010000");

    private static final MenuScheme CARDS = new MenuScheme()
            .mask("000000000")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("000000000");

    private static final String LEFT_CLICK_LORE
            = ChatColor.GREEN + "Clic gauche" + ChatColor.WHITE + " pour ajouter une carte";

    private static final String RIGHT_CLICK_LORE
            = ChatColor.GREEN + "Clic droit" + ChatColor.WHITE + " pour retirer une carte";

    private static final char BULLET = '\u2022'; // Bullet: •

    private final MutableComposition composition;

    private final Map<LGCard, Provider<LGCard>> cardsToProvider;
    private final CompositionValidatorAggregator compositionValidatorAggregator;

    @Inject
    public CompositionGui(@Assisted Player player, @Assisted MutableComposition composition,
                          Collection<Provider<LGCard>> cardProviders,
                          CompositionValidatorAggregator compositionValidatorAggregator) {
        super(player, 6, "Composition");
        this.composition = composition;
        this.cardsToProvider = cardProviders.stream()
                .collect(Collectors.toMap(Provider::get, Function.identity(),
                        this::throwDuplicate, this::createSortedCardsMap));
        this.compositionValidatorAggregator = compositionValidatorAggregator;
    }

    private <K extends LGCard, V extends Provider<K>> TreeMap<K, V> createSortedCardsMap() {
        return new TreeMap<>(Comparator.comparing(LGCard::getName));
    }

    @Override
    public void redraw() {
        drawTopBarGlass();
        drawTopBarBook();
        drawCards();
    }

    private void drawTopBarGlass() {
        Map<Problem.Type, List<Problem>> validationResultsPerType = getValidationProblemsPerType();

        String description = generateValidationDescription(validationResultsPerType);

        Material material = getGlassMaterialFromResults(validationResultsPerType);

        fillTopBarGlass(ItemStackBuilder.of(material)
                .name(ChatColor.AQUA.toString() + ChatColor.BOLD + "Vérification de la composition")
                .lore(ChatPaginator.wordWrap(description, 40))
                .buildItem().build());
    }

    private Material getGlassMaterialFromResults(Map<Problem.Type, List<Problem>> validationProblemsPerType) {
        return validationProblemsPerType.containsKey(Problem.Type.IMPOSSIBLE) ?
                Material.RED_STAINED_GLASS_PANE :
                validationProblemsPerType.containsKey(Problem.Type.RULE_BREAKING) ?
                        Material.YELLOW_STAINED_GLASS_PANE :
                        Material.LIME_STAINED_GLASS_PANE;
    }

    private Map<Problem.Type, List<Problem>> getValidationProblemsPerType() {
        return compositionValidatorAggregator.validate(composition)
                .collect(Collectors.groupingBy(Problem::getType, TreeMap::new, Collectors.toList()));
    }

    private String generateValidationDescription(Map<Problem.Type, List<Problem>> validationProblemsPerType) {
        StringBuilder descriptionBuilder = new StringBuilder();

        validationProblemsPerType.forEach((type, results) -> {
            // Add an empty line if there is another type.
            if (descriptionBuilder.length() != 0) {
                descriptionBuilder.append('\n');
            }

            descriptionBuilder.append(type.getColor())
                    .append(ChatColor.BOLD)
                    .append(type.getDisplayName())
                    .append(" :");

            for (Problem problem : results) {
                descriptionBuilder.append('\n')
                        .append(type.getColor())
                        .append(BULLET)
                        .append(' ')
                        .append(problem.getMessage());
            }
        });

        if (validationProblemsPerType.isEmpty()) {
            descriptionBuilder.append(ChatColor.GREEN)
                    .append(ChatColor.BOLD)
                    .append("Tout est correct !");
        }

        return descriptionBuilder.toString();
    }

    private void fillTopBarGlass(Item item) {
        MenuPopulator topBarGlassPopulator = TOP_BAR_GLASS.newPopulator(this);
        while (topBarGlassPopulator.hasSpace()) {
            topBarGlassPopulator.accept(item);
        }
    }

    private void drawTopBarBook() {
        int playerCount = composition.getPlayerCount();

        String[] compositionLore = CompositionFormatUtil.format(composition.getCards().stream()).split("\n");

        Item item = ItemStackBuilder.of(Material.BOOK)
                .name(ChatColor.GOLD.toString() + ChatColor.BOLD + "Partie")
                .lore(ChatColor.GREEN + "Joueurs : " + playerCount)
                .lore(compositionLore)
                .buildItem().build();

        TOP_BAR_BOOK.newPopulator(this).accept(item);
    }

    private void drawCards() {
        MenuPopulator populator = CARDS.newPopulator(this);
        for (LGCard card : cardsToProvider.keySet()) {
            int amount = (int) composition.getCards().stream().filter(x -> x.getClass() == card.getClass()).count();
            boolean canRemoveCard = composition.canRemove() && amount > 0;

            ItemStackBuilder builder = ItemStackBuilder.of(card.createGuiItem())
                    .name(card.getColor() + card.getName())
                    .lore(ChatColor.GOLD.toString() + ChatColor.BOLD + "Quantité : " + amount)
                    .lore(LEFT_CLICK_LORE)
                    .lore(canRemoveCard ? new String[]{RIGHT_CLICK_LORE} : new String[0])
                    .lore("")
                    .lore(ChatPaginator.wordWrap(card.getDescription(), 35))
                    .amount(Math.max(1, amount));

            Item item = builder.build(() -> removeCard(card), () -> addCard(card));

            populator.accept(item);
        }
    }

    private void addCard(LGCard card) {
        Provider<LGCard> cardProvider = cardsToProvider.get(card);
        LGCard newCard = cardProvider.get();

        composition.addCard(newCard);
        LGSoundStuff.ding(getPlayer());
        redraw();
    }

    private void removeCard(LGCard card) {
        if (!composition.removeCardOfClass(card.getClass())) {
            getPlayer().sendMessage(ChatColor.RED + "Impossible de retirer la carte.");
            LGSoundStuff.nah(getPlayer());
            return;
        }
        LGSoundStuff.remove(getPlayer());
        redraw();
    }

    private Provider<LGCard> throwDuplicate(Provider<LGCard> v1, Provider<LGCard> v2) {
        throw new UnsupportedOperationException("Duplicate keys: " + v1 + " ; " + v2);
    }

    public interface Factory {
        CompositionGui create(Player player, MutableComposition composition);
    }
}
