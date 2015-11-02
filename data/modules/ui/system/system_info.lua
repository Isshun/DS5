data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 0},
    size = {400, 38},
    background = 0x2b3036,
    visible = true,
    views = {
        { type = "image", id = "img_time", src = "data/graphics/icons/sun.png", size = {32, 32}, position = {2, 2}},
        { type = "label", id = "lb_time", text_size = 18, position = {28, 3}, padding = 10 },
        { type = "label", id = "lb_day", text_size = 18, position = {67, 3}, padding = 10 },
        { type = "label", id = "lb_light", text_size = 18, position = {200, 3}, padding = 10 },
        { type = "image", src = "data/graphics/icons/menu.png", size = {32, 32}, position = {363, 3}},
    },

    on_refresh =
    function(view)
        view:findById("img_time"):setImage(game.hour > 6 and game.hour < 20 and "data/graphics/icons/sun.png" or "data/graphics/icons/moon.png")
        view:findById("lb_time"):setText(game.hour .. "h")
        view:findById("lb_day"):setText("jour " .. (game.day+1))
        view:findById("lb_light"):setText("light: " .. (game.world:getLight()))
    end,
})