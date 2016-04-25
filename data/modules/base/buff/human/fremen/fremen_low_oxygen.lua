data:extend({
    {
        label = "Health",
        name = "base.buff_health",
        type = "buff",
        on_start = function (data, character)
            return character.type == "base.character.human" and character.faction == "base.character.fremen"
        end,
        on_update = function (data, character)
            -- org.smallbox.faraway.core.game.module.room.model.stats.buff.oxygen = org.smallbox.faraway.core.game.module.room.model.stats.buff.oxygen + 20;
            -- org.smallbox.faraway.core.game.module.room.model.stats.resist.hot = org.smallbox.faraway.core.game.module.room.model.stats.resist.hot + 20;
        end
    }
})
