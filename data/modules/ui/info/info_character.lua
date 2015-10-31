mode = 1
character = nil

data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", text = "Character", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 26}, padding = 10, size = {100, 40}},

        { type = "grid", position = {10, 72}, columns = 2, column_width = 190, row_height = 60, views = {
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
        }},

        -- Status page
        {
            type = "list",
            position = {10, 200},
            size = {400, 400},
            on_refresh = function(view)
                view:setVisible(mode == 1)
            end,
            views = {
                { type = "label", text = "Current occupation", text_size = 28},
                { type = "label", id = "lb_job", text_size = 18, position = {0, 12}, size = {-1, 28}},
                { type = "label", id = "lb_job_detail", text_size = 14, position = {0, 12}, size = {-1, 40}},

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
                        { type = "label", id = "lb_need_joy", position = {0, 0}, text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_joy", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                    { type = "view", size = {190, 44}, views = {
                        { type = "label", id = "lb_need_relation", position = {0, 0}, text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_relation", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                    { type = "view", size = {190, 44}, views = {
                        { type = "label", id = "", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "", position = {0, 16}, src = "data/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                }},

                { type = "label", position = {0, 65}, text = "Buffs", text_size = 28},
                { type = "list", id = "list_buff", position = {0, 75}, adapter = {
                    view = { type = "label", text_size = 14, size = {400, 20}},
                    on_bind = function(view, data)
                        if data.message then
                            view:setDashedString(data.message, data.mood, 47)
                            view:setTextColor(data.mood > 0 and 0xb3d035 or 0xff5555)
                            view:setOnClickListener(function()
                                game.events:send("debug.open_buff", data)
                            end)
                        end
                        if data.onClickListener then
                            view:setOnClickListener(data.onClickListener)
                        end
                    end
                }},

            }
        },

        -- Inventory page
        {
            type = "view",
            position = {0, 200},
            size = {400, 400},
            on_refresh = function(view)
                view:setVisible(mode == 2)
            end,
            views = {
                { type = "label", id = "lb_inventory", position = {0, 10}, text_size = 14},
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
            }
        },

        -- Info page
        {
            type = "view",
            position = {0, 200},
            size = {400, 400},
            on_refresh = function(view)
                view:setVisible(mode == 3)
            end,
            views = {
                { type = "list", position = {10, 16}, views = {
                    { type = "label", text = "Talents", position = {0, 10}, text_size = 24},
                    { type = "list", id = "list_talents", position = {0, 20}},

                    { type = "label", text = "Personal records", position = {0, 24}, text_size = 24},
                    { type = "label", id = "lb_info_birth", position = {0, 30}, text_size = 14},
                    { type = "label", id = "lb_info_enlisted", position = {0, 30}, text_size = 14},
                }},
            }
        },

        -- Heal page
        { type = "view", position = {0, 200}, size = {400, 400}, on_refresh = function(view)
            view:setVisible(mode == 4)
        end,
        views = {
            { type = "list", position = {10, 16}, views = {
                { type = "label", text = "Diseases", position = {0, 10}, text_size = 24},
                { type = "list", id = "list_diseases", position = {0, 20}},
            }},
        }},
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

            view:findById("lb_info_birth"):setDashedString("Birth", data:getPersonals():getEnlisted(), 47)
            view:findById("lb_info_enlisted"):setDashedString("Enlisted", data:getPersonals():getEnlisted(), 47)
            character = data;

            display_talents(view)

            mode = 1
        end
    end,

    on_refresh =
    function(view)
        if character ~= nil then
            local job = character:getJob()
            if job then
                view:findById("lb_job"):setDashedString(job:getLabel(), math.floor(job:getProgress() * 100), 38)
            else
                view:findById("lb_job"):setText("No job")
            end

            if job and job:getMessage() then
                view:findById("lb_job_detail"):setText(job:getMessage())
                view:findById("lb_job_detail"):setVisible(true)
            else
                view:findById("lb_job_detail"):setVisible(false)
            end

            display_diseases(view, character)

            view:findById("lb_need_energy"):setDashedString("Energy", math.floor(character:getNeeds().energy), 22)
            view:findById("lb_need_food"):setDashedString("Food", math.floor(character:getNeeds().food), 22)
            view:findById("lb_need_happiness"):setDashedString("Happiness", math.floor(character:getNeeds().happiness), 22)
            view:findById("lb_need_health"):setDashedString("Health", math.floor(character:getNeeds().health), 22)
            view:findById("lb_need_joy"):setDashedString("Entertainment", math.floor(character:getNeeds().joy), 22)
            view:findById("lb_need_relation"):setDashedString("Relation", math.floor(character:getNeeds().relation), 22)

            view:findById("gauge_energy"):setTextureRect(0, 80, math.floor(character:getNeeds().energy * 180 / 100 / 10) * 10, 16)
            view:findById("gauge_food"):setTextureRect(0, 80, math.floor(character:getNeeds().food * 180 / 100 / 10) * 10, 16)
            view:findById("gauge_happiness"):setTextureRect(0, 80, math.floor(character:getNeeds().happiness * 180 / 100 / 10) * 10, 16)
            view:findById("gauge_health"):setTextureRect(0, 80, math.floor(character:getNeeds().health * 180 / 100 / 10) * 10, 16)
            view:findById("gauge_joy"):setTextureRect(0, 80, math.floor(character:getNeeds().joy * 180 / 100 / 10) * 10, 16)
            view:findById("gauge_relation"):setTextureRect(0, 80, math.floor(character:getNeeds().relation * 180 / 100 / 10) * 10, 16)

            if character:getInventory() then
                view:findById("lb_inventory"):setText("Inventory: " .. character:getInventory():getInfo().label)
            else
                view:findById("lb_inventory"):setText("Inventory: empty")
            end

            local buff_module = game:getModule("BuffModule")
            if buff_module then
                view:findById("list_buff"):getAdapter():setData(buff_module:getActiveBuffs(character))
            end
        end
    end
})

function display_diseases(view, character)
    local list = view:findById("list_diseases")
    list:removeAllViews()

    local iterator = character:getDiseases():iterator()
    while iterator:hasNext() do
        local disease = iterator:next()
        local lb_disease = game.ui:createLabel()
        lb_disease:setText(disease.disease.name)
        lb_disease:setSize(400, 22)
        list:addView(lb_disease)
    end
end

function display_talents(view)
    local list = view:findById("list_talents")
    list:removeAllViews()

    local iterator = character:getTalents():getAll():iterator()
    while iterator:hasNext() do
        local talent = iterator:next()
        local frame_talent = game.ui:createView()
        frame_talent:setSize(400, 24)

        local lb_talent = game.ui:createLabel()
        lb_talent:setText(talent.name)
        frame_talent:addView(lb_talent)

        local lb_up = game.ui:createLabel()
        lb_up:setText("up")
        lb_up:setSize(50, 22)
        lb_up:setBackgroundColor(0xff0000)
        lb_up:setPosition(200, 0)
        lb_up:setOnClickListener(function(v)
            character:moveTalent(talent, -1)
            display_talents(view)
        end)
        frame_talent:addView(lb_up)

        local lb_down = game.ui:createLabel()
        lb_down:setText("down")
        lb_down:setSize(50, 22)
        lb_down:setPosition(250, 0)
        lb_down:setOnClickListener(function(v)
            character:moveTalent(talent, 1)
            display_talents(view)
        end)
        frame_talent:addView(lb_down)

        list:addView(frame_talent)
    end
end