package com.lighting;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.event.KeyEvent;

@Getter
@AllArgsConstructor
public enum FKey
{
    NONE(KeyEvent.VK_UNDEFINED, 0),
    F1(KeyEvent.VK_F1, 1),
    F2(KeyEvent.VK_F2, 2),
    F3(KeyEvent.VK_F3, 3),
    F4(KeyEvent.VK_F4, 4),
    F5(KeyEvent.VK_F5, 5),
    F6(KeyEvent.VK_F6, 6),
    F7(KeyEvent.VK_F7, 7),
    F8(KeyEvent.VK_F8, 8),
    F9(KeyEvent.VK_F9, 9),
    F10(KeyEvent.VK_F10, 10),
    F11(KeyEvent.VK_F11, 11),
    F12(KeyEvent.VK_F12, 12),
    ESC(KeyEvent.VK_ESCAPE, 13);

    private final int keyEvent;
    private final int varbitValue;

    static final Map<Integer, FKey> VARBIT_TO_FKEY;
    static
    {
        ImmutableMap.Builder<Integer, FKey> builder = new ImmutableMap.Builder<>();
        for (FKey fkey : FKey.values())
        {
            builder.put(fkey.getVarbitValue(), fkey);
        }
        VARBIT_TO_FKEY = builder.build();
    }
}
