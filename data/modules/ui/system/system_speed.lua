data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 0},
    size = {400, 32},
    visible = true,
    views = {
        { type = "image", id = "ic_speed", visible = false, src = "[base]/graphics/ic_speed_1.png", size = {32, 32}, position = {100, 8}},
        { type = "label", id = "lb_speed", text = "x1", text_size = 22, position = {360, 300}},
    },

    on_event = function(view, event, data)
        if event == game.events.on_key_press and data == "SPACE" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_0.png")
            view:findById("lb_speed"):setText("||");
            game:setSpeed(0)
        end

        if event == game.events.on_key_press and data == "D_1" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_1.png")
            view:findById("lb_speed"):setText("x1");
            game:setSpeed(1)
        end

        if event == game.events.on_key_press and data == "D_2" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_2.png")
            view:findById("lb_speed"):setText("x2");
            game:setSpeed(2)
        end

        if event == game.events.on_key_press and data == "D_3" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_3.png")
            view:findById("lb_speed"):setText("x3");
            game:setSpeed(3)
        end

        if event == game.events.on_key_press and data == "D_4" then
            view:findById("ic_speed"):setImage("[base]/graphics/ic_speed_4.png")
            view:findById("lb_speed"):setText("x4");
            game:setSpeed(4)
        end
    end,
})