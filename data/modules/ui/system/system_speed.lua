data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 0},
    size = {400, 32},
    visible = false,
    views = {
        { type = "image", id = "ic_speed", visible = false, src = "[base]/graphics/ic_speed_1.png", size = {32, 32}, position = {100, 8}},
    },

    on_event = function(view, event, data)
        if event == game.events.on_key_press and data == "SPACE" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_0.png")
            game:setSpeed(0)
        end

        if event == game.events.on_key_press and data == "D_1" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_1.png")
            game:setSpeed(1)
        end

        if event == game.events.on_key_press and data == "D_2" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_2.png")
            game:setSpeed(2)
        end

        if event == game.events.on_key_press and data == "D_3" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_3.png")
            game:setSpeed(3)
        end

        if event == game.events.on_key_press and data == "D_4" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_4.png")
            game:setSpeed(4)
        end
    end,
})