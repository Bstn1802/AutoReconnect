package autoreconnect.config;

import autoreconnect.config.AutoReconnectConfig.AutoMessages;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerListListEntry;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::createConfigScreen;
    }

    private static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("text.autoreconnect.config.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        builder.getOrCreateCategory(Text.empty()) // text will be ignored since it's the only category
            .addEntry(entryBuilder.startIntList(
                    Text.translatable("text.autoreconnect.config.option.delays"),
                    AutoReconnectConfig.getInstance().delays)
                .setCreateNewInstance(list -> new IntegerListListEntry.IntegerListCell(AutoReconnectConfig.defaultDelay, list))
                .setInsertInFront(false)
                .setMin(1)
                .setExpanded(true)
                .setDefaultValue(AutoReconnectConfig.defaultDelays)
                .setSaveConsumer(delays -> AutoReconnectConfig.getInstance().delays = delays)
                .setTooltip(Text.translatable("text.autoreconnect.config.tooltip.option.delays"))
                .build())
            .addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("text.autoreconnect.config.option.infinite"),
                    AutoReconnectConfig.getInstance().infinite)
                .setDefaultValue(AutoReconnectConfig.defaultInfinite)
                .setTooltip(Text.translatable("text.autoreconnect.config.tooltip.option.infinite"))
                .setSaveConsumer(infinite -> AutoReconnectConfig.getInstance().infinite = infinite)
                .build())
            .addEntry(entryBuilder.startTextField(
                    Text.translatable("text.autoreconnect.config.tooltip.option.serveraddress"),
                    AutoReconnectConfig.getInstance().serverAddress)
                 .setDefaultValue(AutoReconnectConfig.defaultServerAddress)
                 .setTooltip(Text.translatable("text.autoreconnect.config.tooltip.option.serveraddress"))
                 .setSaveConsumer(serverAddress -> AutoReconnectConfig.getInstance().serverAddress = serverAddress)
                 .build())
            .addEntry(new NestedListListEntry<AutoMessages, MultiElementListEntry<AutoMessages>>(
                Text.translatable("text.autoreconnect.config.option.automessages"),
                AutoReconnectConfig.getInstance().autoMessages,
                true,
                () -> Optional.of(new Text[]{Text.translatable("text.autoreconnect.config.tooltip.option.automessages")}),
                list -> AutoReconnectConfig.getInstance().autoMessages = list,
                () -> AutoReconnectConfig.defaultAutoMessages,
                entryBuilder.getResetButtonKey(),
                true,
                false,
                (autoMessages, listListEntry) -> createAutoMessagesEntry(entryBuilder, autoMessages != null ? autoMessages : new AutoMessages())));
        return builder
            .setSavingRunnable(AutoReconnectConfig.getInstance()::save)
            .build();
    }

    private static MultiElementListEntry<AutoMessages> createAutoMessagesEntry(ConfigEntryBuilder entryBuilder, AutoMessages autoMessages) {
        var tmp = new MultiElementListEntry<>(
            Text.translatable("text.autoreconnect.config.option.automessages.instance"),
            autoMessages,
            Arrays.asList(
                entryBuilder.startTextField(
                        Text.translatable("text.autoreconnect.config.option.automessages.name"),
                        autoMessages.name)
                    .setErrorSupplier(ModMenuIntegration::emptyStringErrorSupplier)
                    .setDefaultValue(AutoMessages.defaultName)
                    .setTooltip(Text.translatable("text.autoreconnect.config.tooltip.option.automessages.name"))
                    .setSaveConsumer(name -> autoMessages.name = name)
                    .build(),
                entryBuilder.startStrList(
                        Text.translatable("text.autoreconnect.config.option.automessages.messages"),
                        autoMessages.messages)
                    .setErrorSupplier(ModMenuIntegration::emptyListErrorSupplier)
                    .setCellErrorSupplier(ModMenuIntegration::emptyStringErrorSupplier)
                    .setDefaultValue(AutoMessages.defaultMessages)
                    .setInsertInFront(false)
                    .setExpanded(true)
                    .setTooltip(Text.translatable("text.autoreconnect.config.tooltip.option.automessages.messages"))
                    .setSaveConsumer(messages -> autoMessages.messages = messages)
                    .build(),
                entryBuilder.startIntField(
                        Text.translatable("text.autoreconnect.config.option.automessages.delay"),
                        autoMessages.delay)
                    .setDefaultValue(AutoMessages.defaultDelay)
                    .setTooltip(Text.translatable("text.autoreconnect.config.tooltip.option.automessages.delay"))
                    .setMin(1)
                    .setSaveConsumer(delay -> autoMessages.delay = delay)
                    .build()
            ),
            true);
        tmp.setTooltipSupplier(() -> Optional.of(new Text[] {
            Text.translatable("text.autoreconnect.config.tooltip.option.automessages.instance")
        }));
        return tmp;
    }

    private static Optional<Text> emptyListErrorSupplier(List<?> list) {
        return list == null || list.isEmpty() ? Optional.of(Text.translatable("text.autoreconnect.config.error.empty_list")) : Optional.empty();
    }

    private static Optional<Text> emptyStringErrorSupplier(String str) {
        return str == null || str.isEmpty() ? Optional.of(Text.translatable("text.autoreconnect.config.error.empty_string")) : Optional.empty();
    }
}
