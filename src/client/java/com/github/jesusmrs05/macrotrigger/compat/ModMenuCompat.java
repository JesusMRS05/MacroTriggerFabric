package com.github.jesusmrs05.macrotrigger.compat;

import com.github.jesusmrs05.macrotrigger.client.ConfigScreen;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent);
    }
}