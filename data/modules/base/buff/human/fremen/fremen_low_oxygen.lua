data:extend({
    {
        label = "Health",
        name = "base.buff_health",
        type = "buff",
        on_start = function (data, character)
            return character.type == "human" and character.faction == "fremen"
        end,
        on_update = function (data, character)
            -- character.stats.buff.oxygen = character.stats.buff.oxygen + 20;
            -- character.stats.resist.hot = character.stats.resist.hot + 20;
        end
    }
})
