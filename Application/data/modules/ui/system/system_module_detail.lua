ui:extend(
    {
        {
            type = "view",
            name = "system-ui-modules",
            position = {820, 100},
            size = {600, 400},
            background = 0x2b3036ff,
            visible = false,
            views = {
                type = "list",
                views = {
                    { type = "label", text = "Modules", text_size = 16, size = {600, 24}, padding = 5, background = 0x00000055 },
                    { type = "label", id = "lb_name", text_size = 14, size = {400, 24}, padding = 5 },
                    { type = "label", id = "lb_desc", text_size = 14, size = {400, 24}, padding = 5 },
                },
            },

            on_game_start =
            function(view)
                speed = 1
            end,

            on_event =
            function(view, event, data)
                if event == "module.open_detail" then
                    local module = data

                    view:setVisible(true)
                    view:findById("lb_name"):setText(module:getInfo().name)
                    view:findById("lb_desc"):setText(module:getInfo().description)
                end

                if event == application.events.on_key_press and data == "ESCAPE" then
                    view:setVisible(false)
                end
            end,
        },
    }
)