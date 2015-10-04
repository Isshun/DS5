game.data:extend(
{
    {
        type = "view",
        name = "ui-test",
        position = {1200, 0},
        size = {400, 65},
        background = 0x2b3036,
        visible = true,
        views =
        {
            { type = "image", id = "ic_speed", src = "data/graphics/ic_speed_1.png", size = {32, 32}, position = {100, 4}},
        },
        
        on_load =
            function(view)
                speed = 1
            end,
        
        on_event =
            function(event, view, data)
            end,
            
        on_refresh =
            function(view)
            end
    },
}
)