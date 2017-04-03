ui:extend({
    type = "view",
    id = "base.ui.menu_load",
    background = 0xffdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", src = "[base]/graphics/mars_gallery_habitat_3.jpg", size = {1920, 1200}},
        { type = "list", position = {application.info.screen_width / 2 - 500 / 2, application.info.screen_height / 2 - 200}, views = {
            { type = "label", text = "Load game", text_size = 38, size = {500, 50}},
            { type = "view", views = {
                { type = "list", id = "list_games" },
                { type = "list", id = "list_game_saves", position = {180, 0}},
                { type = "label", text = "back", text_size = 18, id = "bt_back", size = {100, 38}, padding = 12, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, position = {0, 300}, on_click = function()
                    ui:find("base.ui.menu_load"):setVisible(false)
                    ui:find("base.ui.menu_main"):setVisible(true)
                end},
                { type = "label", text = "load", text_size = 18, id = "bt_load", size = {100, 38}, padding = 12, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, position = {450, 300}, on_click = function()
                    application:sendEvent("load_game.load")
                end},
            }},
        }},
    },

    on_game_start = function()
        application:sendEvent("on_load_menu_create")
    end,

    on_event = function(view, event, data)
        if view:isVisible() and event == application.events.on_key_press and data == "ESCAPE" then
            ui:find(application.game and "base.ui.menu_pause" or "base.ui.menu_main"):setVisible(true)
            view:setVisible(false)
        end

        if event == "on_refresh_save_directory" then
            local module_load_game = application:getModule("LoadGameModule")
            if module_load_game then
                local list_games = view:findById("list_games")
                list_games:removeAllViews()
                local iterator = module_load_game:getGames():iterator()
                while iterator:hasNext() do
                    local game = iterator:next()
                    local lb_game = ui:createLabel()
                    lb_game:setText(game.name)
                    lb_game:setSize(170, 38)
                    lb_game:setPadding(14)
                    lb_game:setBackgroundColor(0x55ffffff)
                    lb_game:setMargin(0, 0, 10, 0)
                    lb_game:setOnClickListener(function()
                        open_game(view, game)
                    end)
                    lb_game:setOnFocusListener(function(view, is_active)
                        lb_game:setBackgroundColor(is_active and 0x8814dcb9 or 0x55ffffff)
                    end)
                    list_games:addView(lb_game)
                end
            end
        end
    end,
})

function open_game(view, game)
    application:sendEvent("load_game.game", game)

    local list_game_saves = view:findById("list_game_saves")
    list_game_saves:removeAllViews()

    local iterator = game.saveFiles:iterator()
    while iterator:hasNext() do
        local saveFile = iterator:next()
        local view_game = ui:createView()
        view_game:setMargin(0, 0, 10, 0)
        view_game:setSize(370, 38)
        view_game:setBackgroundColor(0x55ffffff)
        view_game:setOnFocusListener(function(view, is_active)
            view_game:setBackgroundColor(is_active and 0x8814dcb9 or 0x55ffffff)
        end)
        view_game:setOnClickListener(function()
            application:sendEvent("load_game.save", saveFile)
        end)

        local lb_game = ui:createLabel()
        lb_game:setText(saveFile.label)
        lb_game:setPadding(14)
        view_game:addView(lb_game)


        local lb_game_type = ui:createLabel()
        lb_game_type:setText(saveFile.type:toString())
        lb_game_type:setPadding(14)
        lb_game_type:setPosition(300, 0)
        view_game:addView(lb_game_type)

        list_game_saves:addView(view_game)
    end
end