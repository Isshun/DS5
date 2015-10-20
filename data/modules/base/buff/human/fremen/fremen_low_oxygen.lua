data:extend({
    {
        label = "Health",
        name = "base.buff_health",
        type = "buff",
        on_start = function (data, character)
            return character.type == "human" and character.faction == "fremen"
        end,
        on_update = function (data, character)
            -- model.stats.buff.oxygen = model.stats.buff.oxygen + 20;
            -- model.stats.resist.hot = model.stats.resist.hot + 20;
        end
    }
})
