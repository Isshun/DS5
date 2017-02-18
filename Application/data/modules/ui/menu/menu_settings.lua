local RESOLUTIONS = {}

RESOLUTIONS["4/3"] = {
    {800, 600},
    {1024, 768},
    {1152, 864},
    {1280, 960},
    {1400, 1050},
    {1600, 1200},
}
RESOLUTIONS["16/9"] = {
    {1280, 720},
    {1360, 768},
    {1366, 768},
    {1600, 900},
    {1600, 1200},
    {1920, 1080},
    {2048, 1152},
    {2560, 1440},
}
RESOLUTIONS["16/10"] = {
    {1280, 800},
    {1440, 900},
    {1680, 1050},
    {1920, 1200},
    {2560, 1600},
}

local settings = {}

local function set_screen_mode(mode)
    settings.screen_mode = mode

    local window = ui:find("base.ui.menu_settings")

    window:findById("lb_screen_mode_0"):setText((mode == "borderless" and "[x]" or "[ ]") .. " Borderless")
    window:findById("lb_screen_mode_1"):setText((mode == "fullscreen" and "[x]" or "[ ]") .. " Fullscreen")
    window:findById("lb_screen_mode_2"):setText((mode == "window" and "[x]" or "[ ]") .. " Window")
end

local function set_resolution(resolution)
    settings.resolution = resolution

    local window = ui:find("base.ui.menu_settings")

    local dd_resolutions = window:findById("dd_resolutions")
    for key, value in ipairs(RESOLUTIONS[settings.ratio]) do
        if value[1] == resolution[1] and value[2] == resolution[2] then
            dd_resolutions:setCurrentIndex(key-1)
        end
    end
end

local function set_ratio(ratio)
    settings.ratio = ratio

    local window = ui:find("base.ui.menu_settings")
    window:findById("bt_ratio"):setText(ratio)

    local dd_resolutions = window:findById("dd_resolutions")
    dd_resolutions:removeAllViews()
    for key, value in ipairs(RESOLUTIONS[settings.ratio]) do
        local dd_entry = ui:createLabel()
        dd_entry:setText(value[1] .. "x" .. value[2]);
        dd_entry:setTextSize(16)
        dd_entry:setSize(100, 22)
        dd_entry:setPadding(5, 0, 0, 10)
        dd_entry:setBackgroundColor(0x121c1e)
        dd_entry:setOnClickListener(function()
        end)
        dd_resolutions:addView(dd_entry)
        dd_resolutions:setCurrentIndex(0)
    end
end

local function open_settings_menu(view, sub_menu_id)
    local main_view = ui:find("base.ui.menu_settings")

    local iterator = main_view:findById("grid_settings_sections"):getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x121c1e)
    end
    view:setBackgroundColor(0x25c9cb)

    main_view:findById("graphic_sub_menu"):setVisible(false)
    main_view:findById("sound_sub_menu"):setVisible(false)
    main_view:findById("bindings_sub_menu"):setVisible(false)
    main_view:findById("gameplay_sub_menu"):setVisible(false)
    main_view:findById(sub_menu_id):setVisible(true)
end

ui:extend({
    type = "view",
    id = "base.ui.menu_settings",
    size = {application.info.screen_width, application.info.screen_height},
    background = 0x000000,
    in_game = false,
    visible = false,
    views = {
        { type = "list", size = {800, 600}, background = 0xdd121c1e, position = {application.info.screen_width / 2 - 800 / 2, application.info.screen_height / 2 - 200}, views = {
            { type = "grid", id = "grid_settings_sections", columns = 4, column_width = 200, row_height = 50, background = 0x121c1e, size = {800, 50}, views = {
                { type = "label", text = "Graphic", text_size = 28, padding = 16, size = {200, 50}, padding = 14, background = 0x25c9cb, on_click = function(v) open_settings_menu(v, "graphic_sub_menu") end},
                { type = "label", text = "Sound", text_size = 28, padding = 16, size = {200, 50}, padding = 14, on_click = function(v) open_settings_menu(v, "sound_sub_menu") end},
                { type = "label", text = "Bindings", text_size = 28, padding = 16, size = {200, 50}, padding = 14, on_click = function(v) open_settings_menu(v, "bindings_sub_menu") end},
                { type = "label", text = "Gameplay", text_size = 28, padding = 16, size = {200, 50}, padding = 14, on_click = function(v) open_settings_menu(v, "gameplay_sub_menu") end},
            }},
            { type = "list", background = 0x25c9cb, size = {800, 4}},
            { type = "list", id = "graphic_sub_menu", visible = true, position = {0, 2}, size = {800, 490}, views = {
                { type = "view", size = {100, 26}, views = {
                    { type = "label", text = "Resolution", text_size = 20, padding = 10},
                    { type = "label", id = "bt_ratio", size = {100, 22}, text_size = 16, padding = 10, position = {300, 0}, on_click = function(v)
                        set_ratio(application.config:getNextRatio(settings.ratio))
                    end},
                    { type = "dropdown", id = "dd_resolutions", size = {100, 28}, position = {360, 5} },
                }},
                { type = "view", size = {100, 26}, views = {
                    { type = "label", text = "Screen mode", text_size = 20, padding = 10},
                    { type = "grid", columns = 3, column_width = 140, row_height = 32, position = {300, 0}, views = {
                        { type = "label", id = "lb_screen_mode_0", text = "[ ] Borderless", text_size = 16, padding = 10, size = {149, 32}, on_click = function() set_screen_mode("borderless") end},
                        { type = "label", id = "lb_screen_mode_1", text = "[ ] Fullscreen", text_size = 16, padding = 10, size = {149, 32}, on_click = function() set_screen_mode("fullscreen") end},
                        { type = "label", id = "lb_screen_mode_2", text = "[ ] Window", text_size = 16, padding = 10, size = {149, 32}, on_click = function() set_screen_mode("window") end},
                    }},
                }},
                { type = "label", text = "UI Scale", text_size = 16, padding = 10},
            }},
            { type = "list", id = "sound_sub_menu", visible = false, position = {0, 2}, size = {800, 490}, views = {
                { type = "label", text = "Music", text_size = 16, padding = 10},
                { type = "label", text = "Game sounds", text_size = 16, padding = 10},
                { type = "label", text = "Notifications", text_size = 16, padding = 10},
            }},
            { type = "list", id = "bindings_sub_menu", visible = false, position = {0, 2}, size = {800, 490}, views = {
                { type = "label", text = "UI", text_size = 16, padding = 10},
                { type = "label", text = "Game", text_size = 16, padding = 10},
            }},
            { type = "list", id = "gameplay_sub_menu", visible = false, position = {0, 2}, size = {800, 490}, views = {
                { type = "view", size = {100, 26}, views = {
                    { type = "label", text = "Restraint mouse to window", text_size = 20, padding = 10},
                    { type = "grid", columns = 3, column_width = 140, row_height = 32, position = {300, 0}, views = {
                        { type = "label", id = "lb_screen_mode_2", text = "[ ]", text_size = 16, padding = 10, size = {149, 32}, on_click = function() set_screen_mode(2) end},
                    }},
                }},
                { type = "view", size = {100, 26}, views = {
                    { type = "label", text = "Scrolling edges", text_size = 20, padding = 10},
                    { type = "grid", columns = 3, column_width = 140, row_height = 32, position = {300, 0}, views = {
                        { type = "label", id = "lb_screen_mode_2", text = "[ ]", text_size = 16, padding = 10, size = {149, 32}, on_click = function() set_screen_mode(2) end},
                    }},
                }},
            }},
            { type = "view", size = {800, 45}, views = {
                { type = "label", text = "Close", text_size = 22, padding = 16, size = {160, 45}, position = {458, 0}, background = {regular = 0x01000000, focus = 0x25c9cb}, on_click = function()
                    ui:find("base.ui.menu_settings"):setVisible(false)
                    ui:find(application.game and "base.ui.menu_pause" or "base.ui.menu_main"):setVisible(true)
                end},
                { type = "label", text = "Apply", text_size = 22, padding = 16, size = {160, 45}, position = {630, 0}, background = {regular = 0x01000000, focus = 0x25c9cb}, on_click = function()
                    application:sendEvent("game_settings.apply", settings)
                end},
            }}
        }},
    },

    on_game_start = function(view)
        set_ratio( application.config.screen.ratio)
        set_resolution({1600, 1200})
        set_screen_mode( application.config.screen.mode)
    end,

--    on_event = function(view, event, data)
--        if view:isVisible() and event == application.events.on_key_press and data == "ESCAPE" then
--            ui:find(application.game and "base.ui.menu_pause" or "base.ui.menu_main"):setVisible(true)
--            view:setVisible(false)
--        end
--    end,
})