--ui:extend({
--    type = "style",
--    id = "base.style.right_panel",
--    style = {
--        align = {"top", "right"},
--        position = {0, 38},
--        background = 0xff882233,
----        background = blue_dark_4,
--        size = {372, 800},
--    }
--})

ui:extend({
    type = "style",
    id = "base.style.gauge",
    style = {
        src = "[base]/graphics/needbar.png",
        size = {100, 100},
        position = {0, 16},
        texture_rect = {0, 0, 100, 16}
    }
})
