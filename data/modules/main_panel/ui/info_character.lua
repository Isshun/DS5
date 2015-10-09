mode = 1
character = nil

game.data:extend(
{
    {
        type = "view",
        name = "ui-test",
        position = {1200, 65},
        size = {400, 800},
        background = 0x121c1e,
        visible = false,
        views =
        {
            { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10},
            
            { type = "grid", position = {10, 50}, columns = 2, column_width = 190, row_height = 60, views =
                {
                    { type = "label", id = "bt_status", text = "Status", text_size = 20, padding = 18, background = 0x5588bb, size = {180, 50}, on_click = "mode = 1", on_refresh = function(view)
                        view:setBackgroundColor(mode == 1 and 0x4be7da or 0x689999)
                    end},
                    { type = "label", id = "bt_inventory", text = "Inventory", text_size = 20, padding = 18, background = 0x5588bb, size = {180, 50}, on_click = "mode = 2", on_refresh = function(view)
                        view:setBackgroundColor(mode == 2 and 0x4be7da or 0x689999)
                    end},
                    { type = "label", id = "bt_info", text = "Info", text_size = 20, padding = 18, background = 0x5588bb, size = {180, 50}, on_click = "mode = 3", on_refresh = function(view)
                        view:setBackgroundColor(mode == 3 and 0x4be7da or 0x689999)
                    end},
                    { type = "label", id = "bt_health", text = "Health", text_size = 20, padding = 18, background = 0x5588bb, size = {180, 50}, on_click = "mode = 4", on_refresh = function(view)
                        view:setBackgroundColor(mode == 4 and 0x4be7da or 0x689999)
                    end},
                }
            },
            
            {
                type = "list",
                position = {10, 180},
                size = {400, 400},
                on_refresh = function(view)
                    view:setVisible(mode == 1)
                end,
                views = {
                    { type = "label", text = "Current occupation", text_size = 28},
                    { type = "label", id = "lb_job", text_size = 18, position = {0, 12}, size = {-1, 40}},

                    { type = "label", position = {0, 10}, text = "Needs", text_size = 28},
                    { type = "grid", position = {0, 24}, columns = 2, column_width = 200, row_height = 44, views = {
                        { type = "view", size = {190, 44}, views = {
                            { type = "label", id = "lb_need_energy", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                            { type = "image", id = "gauge_energy", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        }},
                        { type = "view", size = {190, 44}, views = {
                            { type = "label", id = "lb_need_food", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                            { type = "image", id = "gauge_food", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        }},
                        { type = "view", size = {190, 44}, views = {
                            { type = "label", id = "lb_need_happiness", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                            { type = "image", id = "gauge_happiness", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        }},
                        { type = "view", size = {190, 44}, views = {
                            { type = "label", id = "lb_need_health", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                            { type = "image", id = "gauge_health", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        }},
                        { type = "view", size = {190, 44}, views = {
                            { type = "label", id = "", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                            { type = "image", id = "", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        }},
                        { type = "view", size = {190, 44}, views = {
                            { type = "label", id = "", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                            { type = "image", id = "", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        }},
                        { type = "view", size = {190, 44}, views = {
                            { type = "label", id = "", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                            { type = "image", id = "", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        }},
                    }},
                }
            },
            
            { type = "view", position = {0, 180}, size = {400, 400}, on_refresh = function(view)
                view:setVisible(mode == 2)
            end, views = {
                { type = "grid", position = {0, 24}, columns = 10, column_width = 32, row_height = 32, views = {
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "data/graphics/ic_blueprint.png", size = {32, 32}},
                }}
            }},
            
            { type = "view", position = {0, 180}, size = {400, 400}, on_refresh = function(view)
                view:setVisible(mode == 3)
            end, views = {
                { type = "list", position = {10, 16}, views = {
                    { type = "label", id = "lb_info_birth", position = {0, 10}, text_size = 14},
                    { type = "label", id = "lb_info_enlisted", position = {0, 10}, text_size = 14},
                }},
            }},
            
            { type = "view", position = {0, 180}, size = {400, 400}, on_refresh = function(view)
                view:setVisible(mode == 4)
            end},
        },
        
        on_load =
            function(view)
                mode = 3
            end,
        
        on_event =
            function(event, view, data)
                if event == game.events.on_key_press and data == "ESCAPE" then
                    game.ui:clearSelection();
                    view:setVisible(false)
                    character = nil
                end

                if event == game.events.on_deselect then
                    view:setVisible(false)
                    character = nil
                end

                if event == game.events.on_character_selected then
                    view:setVisible(true)
                    view:findById("lb_name"):setText(data:getName())
                    
                    view:findById("lb_info_birth"):setDashedString("Birth", data:getInfo():getEnlisted(), 47)
                    view:findById("lb_info_enlisted"):setDashedString("Enlisted", data:getInfo():getEnlisted(), 47)
                    character = data;

                    mode = 1
                end
            end,
            
        on_refresh =
            function(view)
                if character ~= nil then
                    if character:getJob() then
                        view:findById("lb_job"):setDashedString(character:getJob():getLabel(), character:getJob():getProgressPercent(), 38)
                    else
                        view:findById("lb_job"):setText("No job")
                    end
                    
                    view:findById("lb_need_energy"):setDashedString("Energy", math.floor(character:getNeeds().energy), 22)
                    view:findById("lb_need_food"):setDashedString("Food: ", math.floor(character:getNeeds().food), 22)
                    view:findById("lb_need_happiness"):setDashedString("Happiness: ", math.floor(character:getNeeds().happiness), 22)
                    view:findById("lb_need_health"):setDashedString("Health: ", math.floor(character:getNeeds().health), 22)
                    
                    view:findById("gauge_energy"):setTextureRect(0, 80, math.floor(character:getNeeds().energy * 200 / 100 / 10) * 10, 16)
                    view:findById("gauge_food"):setTextureRect(0, 80, math.floor(character:getNeeds().food * 200 / 100 / 10) * 10, 16)
                    view:findById("gauge_happiness"):setTextureRect(0, 80, math.floor(character:getNeeds().happiness * 200 / 100 / 10) * 10, 16)
                    view:findById("gauge_health"):setTextureRect(0, 80, math.floor(character:getNeeds().health * 200 / 100 / 10) * 10, 16)
                end
            end
    },
}
)