package com.lighting;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import javax.inject.Inject;

import com.logitech.gaming.LogiLED;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Logitech G LIGHTSYNC"
)
public class LogitechPlugin extends Plugin {

	// Following varbits get the F-Key the player has set in settings; value is numeric
	// such that a value of 12 means that tab is bound to F12
	static final int COMBAT_TAB_BINDING = 4675;
	static final int SKILLS_TAB_BINDING = 4676;
	static final int QUESTS_TAB_BINDING = 4677;
	static final int INVENTORY_TAB_BINDING = 4678;
	static final int EQUIPMENT_TAB_BINDING = 4679;
	static final int PRAYER_TAB_BINDING = 4680;
	static final int MAGIC_TAB_BINDING = 4682;
	static final int FRIENDS_TAB_BINDING = 4684;
	static final int ACCOUNT_MANAGEMENT_TAB_BINDING = 6517;
	static final int LOGOUT_BINDING = 4689;
	static final int SETTINGS_TAB_BINDING = 4686;
	static final int EMOTE_TAB_BINDING = 4687;
	static final int CHAT_CHANNEL_TAB_BINDING = 4683;
	static final int MUSIC_PLAYER_TAB_BINDING = 4688;

	private static final Set<Integer> VARBITS = ImmutableSet.of(
			COMBAT_TAB_BINDING, SKILLS_TAB_BINDING, QUESTS_TAB_BINDING,
			INVENTORY_TAB_BINDING, EQUIPMENT_TAB_BINDING, PRAYER_TAB_BINDING,
			MAGIC_TAB_BINDING, FRIENDS_TAB_BINDING, ACCOUNT_MANAGEMENT_TAB_BINDING,
			LOGOUT_BINDING, SETTINGS_TAB_BINDING, EMOTE_TAB_BINDING,
			CHAT_CHANNEL_TAB_BINDING, MUSIC_PLAYER_TAB_BINDING
	);

	@Inject
	private Client client;

	@Inject
	private LogitechConfig config;

	private enum HealthBarStatus {
		HP,
		POISON,
		VENOM,
		DISEASE
	}

	private HealthBarStatus hpStatus;

	private final Map<Integer, FKey> fkeyVarbitToKey = new HashMap<>();

	@Override
	protected void startUp() throws Exception {
		LogiLED.LogiLedInit();
		hpStatus = HealthBarStatus.HP;
		playLoginScreenEffect();
	}

	@Override
	protected void shutDown() throws Exception {
		LogiLED.LogiLedShutdown();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		switch(event.getGameState()) {
			case LOGIN_SCREEN:
				playLoginScreenEffect();
				break;
			case LOGGING_IN:
				resetKeyboard();
				break;
		}
	}

	private void resetToDefault() {
		resetKeyboard();
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 1, 0, 100, 100);
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 3, 0, 100, 100);

		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 0, 100, 0, 0);
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 2, 100, 0, 0);
	}

	private void resetKeyboard() {
		LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
		LogiLED.LogiLedSetLighting(100,78,0);
		LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_ALL);
	}

	private void playLoginScreenEffect() {
		resetToDefault();
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged) {
		Skill skill = statChanged.getSkill();

		switch(skill) {
			case PRAYER: {
				int stat = (int)((statChanged.getBoostedLevel() * 100f) / statChanged.getLevel());
				LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 1, 0, stat, stat);
				LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 3, 0, stat, stat);
				break;
			}
			case HITPOINTS: {
				switch(hpStatus) {
					case HP: {
						int stat = (int)((statChanged.getBoostedLevel() * 100f) / statChanged.getLevel());
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 0, stat, 0, 0);
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 2, stat, 0, 0);
						break;
					}
					case POISON: {
						int stat = (int)((statChanged.getBoostedLevel() * 100f) / statChanged.getLevel());
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 0, 0, stat, 0);
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 2, 0, stat, 0);
						break;
					}
					case VENOM: {
						int stat = (int)((statChanged.getBoostedLevel() * 100f) / statChanged.getLevel());
						int green = stat/2;
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 0, 0, green, 0);
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 2, 0, green, 0);
						break;
					}
					case DISEASE: {
						int stat = (int)((statChanged.getBoostedLevel() * 100f) / statChanged.getLevel());
						int green = stat*(2/3);
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 0, stat, green, 0);
						LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Speaker, 2, stat, green, 0);
						break;
					}
				}
				break;
			}
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {
		switch(event.getVarpId()) {
			case VarPlayer.POISON: {
				final int poisonValue = event.getValue();

				if(poisonValue <= 0) {
					hpStatus = HealthBarStatus.HP;
				} else if(poisonValue > 0 && poisonValue <= 100) {
					hpStatus = HealthBarStatus.POISON;
				} else if(poisonValue >= 1000000) {
					hpStatus = HealthBarStatus.VENOM;
				}
				break;
			}
			case VarPlayer.DISEASE_VALUE: {
				if(event.getValue() <= 0) {
					hpStatus = HealthBarStatus.HP;
				} else {
					hpStatus = HealthBarStatus.DISEASE;
				}
				break;
			}
		}
		checkFKeys();
	}

	private void checkFKeys() {
		VARBITS.forEach(varbit ->
		{
			final int varbitVal = client.getVarbitValue(varbit);
			final FKey existingValue = fkeyVarbitToKey.get(varbit);
			final FKey newValue = FKey.VARBIT_TO_FKEY.get(varbitVal);
			if(existingValue == null || existingValue != newValue) {
				fkeyVarbitToKey.put(varbit, newValue);
				setFKeyColor(varbit, newValue);
				log.debug("Storing FKey value {} for varbit {}", varbitVal, varbit);
			}

			if(existingValue != null && existingValue != newValue) {
				resetFKey(existingValue);
			}
		});
	}

	private void setFKeyColor(Integer varbit, FKey newKey) {
		LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);

		int newKeyName = newKey.getVarbitValue() + 0x3a;
		if(newKeyName == 0x45) {
			newKeyName = 0x57;
		} else if(newKeyName == 0x46) {
			newKeyName = 0x58;
		} else if(newKeyName == 0x47) {
			newKeyName = 0x01;
		}

		if(newKeyName != 0x3a) {
			switch(varbit) {
				case COMBAT_TAB_BINDING: // combat is red, right?
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 100, 20, 0);
					break;
				case SKILLS_TAB_BINDING: // percentages like icon
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 60, 100, 30);
					break;
				case QUESTS_TAB_BINDING: // white but with more blue in it
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 70, 70, 100);
					break;
				case INVENTORY_TAB_BINDING: // brown
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 59, 30, 0);
					break;
				case EQUIPMENT_TAB_BINDING: // grey?
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 50, 50, 50);
					break;
				case PRAYER_TAB_BINDING: // white
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 100, 100, 100);
					break;
				case MAGIC_TAB_BINDING: // purple
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 80, 0, 100);
					break;
				case FRIENDS_TAB_BINDING: // yellow but not like the default
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 100, 78, 0);
					break;
				case ACCOUNT_MANAGEMENT_TAB_BINDING: // nobody is binding this... just make it green
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 0, 50, 0);
					break;
				case LOGOUT_BINDING: // maybe blue?
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 0, 0, 100);
					break;
				case SETTINGS_TAB_BINDING: // nobody is binding this either... another shade of green
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 0, 50, 10);
					break;
				case EMOTE_TAB_BINDING: // pink for hearts?
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 100, 70, 75);
					break;
				case CHAT_CHANNEL_TAB_BINDING: // even darker grey
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 30, 30, 30);
					break;
				case MUSIC_PLAYER_TAB_BINDING: // dark cyan
					LogiLED.LogiLedSetLightingForKeyWithKeyName(newKeyName, 0, 50, 50);
					break;
			}
		}
		LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_ALL);
	}

	private void resetFKey(FKey oldKey) {
		int oldKeyName = oldKeyName = oldKey.getVarbitValue() + 0x3a;
		if(oldKeyName == 0x45) {
			oldKeyName = 0x57;
		} else if(oldKeyName == 0x46) {
			oldKeyName = 0x58;
		} else if(oldKeyName == 0x47) {
			oldKeyName = 0x01;
		}

		LogiLED.LogiLedSetLightingForKeyWithKeyName(oldKeyName, 100, 82, 0);
	}

	@Provides
	LogitechConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(LogitechConfig.class);
	}
}
