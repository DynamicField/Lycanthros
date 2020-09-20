package com.github.jeuxjeux20.loupsgarous.cards.composition;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.util.CompositionFormatUtil;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator.Problem;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGCompositionUpdateEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.gui.OwnerGui;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class CompositionGui extends OwnerGui {
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

    private List<LGCard> cards;
    private final CompositionValidator compositionValidator;

    public CompositionGui(Player player, LGGameOrchestrator orchestrator) {
        super(player, 6, "Composition", orchestrator);
        this.compositionValidator = orchestrator.getGameBox().handler(LGExtensionPoints.COMPOSITION_VALIDATORS);

        updateCards();
    }

    @Override
    public void open() {
        super.open();

        listenToEvents();
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
        return compositionValidator.validate(getComposition()).stream()
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
        int playerCount = getComposition().getPlayerCount();

        String[] compositionLore = CompositionFormatUtil.format(getComposition()).split("\n");

        Item item = ItemStackBuilder.of(Material.BOOK)
                .name(ChatColor.GOLD.toString() + ChatColor.BOLD + "Partie")
                .lore(ChatColor.GREEN + "Joueurs : " + playerCount)
                .lore(compositionLore)
                .buildItem().build();

        TOP_BAR_BOOK.newPopulator(this).accept(item);
    }

    private void drawCards() {
        MenuPopulator populator = CARDS.newPopulator(this);
        for (LGCard card : cards) {
            int amount = getComposition().getContents().count(card);

            ItemStackBuilder builder = ItemStackBuilder.of(card.createGuiItem())
                    .name(card.getColor() + card.getName())
                    .lore(ChatColor.GOLD.toString() + ChatColor.BOLD + "Quantité : " + amount)
                    .lore(new String[]{LEFT_CLICK_LORE})
                    .lore(new String[]{RIGHT_CLICK_LORE})
                    .lore("")
                    .lore(ChatPaginator.wordWrap(card.getDescription(), 35))
                    .amount(Math.max(1, amount));

            Item item = builder.build(() -> removeCard(card), () -> addCard(card));

            populator.accept(item);
        }
    }

    private void addCard(LGCard card) {
        ImmutableComposition newComposition = getComposition().with(cards -> cards.add(card));

        updateComposition(newComposition);
        LGSoundStuff.ding(getPlayer());

        redraw();
    }

    private void removeCard(LGCard card) {
        ImmutableComposition newComposition = getComposition().with(cards -> cards.remove(card));

        updateComposition(newComposition);
        LGSoundStuff.remove(getPlayer());

        redraw();
    }

    private ImmutableComposition getComposition() {
        return orchestrator.getComposition();
    }

    private void updateComposition(Composition composition) {
        orchestrator.setComposition(composition);
    }

    private void updateCards() {
        cards = orchestrator.getGameBox().contents(LGExtensionPoints.CARDS).stream()
                .sorted(Comparator.comparing(LGCard::getName))
                .collect(Collectors.toList());

        if (isValid()) {
            redraw();
        }
    }

    private void listenToEvents() {
        Events.subscribe(LGCompositionUpdateEvent.class)
                .filter(orchestrator::isMyEvent)
                .handler(e -> redraw())
                .bindWith(this);
    }
}
