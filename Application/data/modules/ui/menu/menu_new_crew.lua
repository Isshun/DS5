ui:extend({
    type = "view",
    id = "base.ui.menu_new_crew",
    size = {300, -1},
    background = 0xffdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", size = {1920, 1200}},
        { type = "view", position = {application.info.screen_width / 2 - 300 / 2, application.info.screen_height / 2 - 200}, views = {
            { type = "label", text = "Crew", text_size = 38},
            { type = "list", id = "list_crew", position = {0, 40}},
            { type = "label", id = "bt_back", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "back", position = {0, 350}, text_size = 22, size = {100, 40},
                on_click = function()
                    ui:find("base.ui.menu_new_crew"):setVisible(false)
                    ui:find("base.ui.menu_new_planet_region"):setVisible(true)
                end},
            { type = "label", id = "bt_next", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "next", text_size = 22, size = {100, 40}, position = {200, 350},
                on_click = function()
                    ui:find("base.ui.menu_new_crew"):setVisible(false)
                    application:sendEvent("new_game.start")
                end},
        }}
    },

    on_game_start = function(view)
    end,

    on_event = function(view, event, data)
        if event == "new_game.planet" then
            if data.graphics.background then
                ui:find("base.ui.menu_new_crew"):findById("img_background"):setVisible(true)
                ui:find("base.ui.menu_new_crew"):findById("img_background"):setImage(data.graphics.background.path)
            else
                ui:find("base.ui.menu_new_crew"):findById("img_background"):setVisible(false)
            end
        end
    end,
})