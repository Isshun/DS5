data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 0},
    size = {400, 32},
    background = 0x2b3036,
    visible = true,
    views = {
        { type = "label", id = "lb_time", padding = 5 },
    },

    on_refresh =
    function(view)
        view:findById("lb_time"):setText(game.hour .. "h " .. game.year .. "." .. game.day)
    end,
})