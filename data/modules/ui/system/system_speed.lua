ui:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 0},
    size = {400, 32},
    visible = false,
    views = {
        { type = "image", id = "ic_speed", visible = false, src = "[base]/graphics/ic_speed_1.png", size = {32, 32}, position = {100, 8}},
    },

    on_event = function(view, event, data)
    end,
})