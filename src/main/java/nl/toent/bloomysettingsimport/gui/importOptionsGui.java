package nl.toent.bloomysettingsimport.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class importOptionsGui extends LightweightGuiDescription {
    public importOptionsGui(){

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(292, 240);
        root.setInsets(Insets.ROOT_PANEL);

        WText resultText = new WText(Text.empty());
        root.add(resultText, 0, 8, 16, 3);

        WButton openFileExplorerButton = new WButton(Text.translatable("settings.bloomysettingsimport.open_explorer"));
        openFileExplorerButton.setIcon(new ItemIcon(new ItemStack(Items.SPYGLASS)));
        root.add(openFileExplorerButton, 0, 0, 8, 1);
        openFileExplorerButton.setOnClick(() -> {
            String filePath = String.valueOf(FabricLoader.getInstance().getConfigDir());
            resultText.setText(Text.of(filePath));
            try {
                Runtime.getRuntime().exec("explorer.exe /select,\"" + filePath + "\"");
            } catch (IOException e) {
            }
        });

        WTextField pathDirectory = new WTextField(Text.of("Enter your file directory to save to/load from!"));
        pathDirectory.setMaxLength(100);
        root.add(pathDirectory, 0, 2, 16, 2);

        WButton importSettingsButton = new WButton(Text.translatable("settings.bloomysettingsimport.import_settings"));
        importSettingsButton.setIcon(new ItemIcon(new ItemStack(Items.WRITABLE_BOOK)));
        root.add(importSettingsButton, 0, 4, 8, 1);
        importSettingsButton.setOnClick(() -> {
            Path userInputtedPath = Path.of(pathDirectory.getText().replace("\"", "").replace("'", "") + FileSystems.getDefault().getSeparator());
            Path findOptions = userInputtedPath.resolve("options.txt");
            Path findOriginalOptions = FabricLoader.getInstance().getConfigDir().resolve(MinecraftClient.getInstance().options.getOptionsFile().toPath());
            try {

                Files.copy(findOptions, findOriginalOptions, StandardCopyOption.REPLACE_EXISTING);

                MinecraftClient.getInstance().options.load();

                resultText.setText(Text.translatable("settings.bloomysettingsimport.import_success"));

            } catch (IOException e) {
                resultText.setText(Text.translatable("settings.bloomysettingsimport.import_failed"));
            }
        });

        WToggleButton replaceIfDuplicateTogg = new WToggleButton(Text.translatable("settings.bloomysettingsimport.replace_dupes"));
        replaceIfDuplicateTogg.setToggle(true);
        root.add(replaceIfDuplicateTogg, 9, 6);

        WToggleButton addDirTogg = new WToggleButton(Text.translatable("settings.bloomysettingsimport.create_dir"));
        addDirTogg.setToggle(true);
        root.add(addDirTogg, 9, 7);

        WButton exportSettingsButton = new WButton(Text.translatable("settings.bloomysettingsimport.export_settings"));
        exportSettingsButton.setIcon(new ItemIcon(new ItemStack(Items.WRITTEN_BOOK)));
        root.add(exportSettingsButton, 0, 6, 8, 1);
        exportSettingsButton.setOnClick(() -> {
            Path userInputtedPath = Path.of(pathDirectory.getText().replace("\"", "").replace("'", ""));
            Path optionsToExport = FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve(MinecraftClient.getInstance().options.getOptionsFile().toPath());

            Path targetPath = userInputtedPath.resolve("options.txt");

            try {
                if (!Files.exists(userInputtedPath) && addDirTogg.getToggle()) {
                    Files.createDirectories(userInputtedPath);
                }

                if (replaceIfDuplicateTogg.getToggle()) {
                    Files.copy(optionsToExport, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    resultText.setText(Text.translatable("settings.bloomysettingsimport.export_success"));
                } else {
                    Files.copy(optionsToExport, targetPath);
                    resultText.setText(Text.translatable("settings.bloomysettingsimport.export_success"));
                }

                Runtime.getRuntime().exec("explorer.exe /select,\"" + targetPath + "\"");
            } catch (IOException e) {
                if (e.getMessage().contains("FileAlreadyExistsException")) {
                    resultText.setText(Text.translatable("settings.bloomysettingsimport.file_already_exists"));
                } else {
                    resultText.setText(Text.translatable("settings.bloomysettingsimport.export_failed"));
                }
            }
        });

        WButton btnDone = new WButton(Text.translatable("settings.bloomysettingsimport.exit_bsi"));
        root.add(btnDone, 4, 11, 8, 1);
        btnDone.setOnClick(() -> {
            MinecraftClient.getInstance().options.load();
            MinecraftClient.getInstance().setScreen(new OptionsScreen(null, MinecraftClient.getInstance().options));
        });

        root.validate(this);
    }
}
