character = nil

data:extend({
    type = "view",
    name = "base.ui.info_character",
    style = "base.style.right_panel",
    visible = false,
    views = {
        { type = "label", text = "Character", text_size = 12, position = {12, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {12, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {12, 37}, size = {100, 40}},

        { type = "grid", position = {12, 72}, columns = 2, column_width = 180, row_height = 60, views = {
            { type = "label", id = "bt_status", text = "Status", text_size = 20, padding = 18, size = {170, 50}, on_click = function() open_page("bt_status", "page_status") end},
            { type = "label", id = "bt_inventory", text = "Inventory", text_size = 20, padding = 18, size = {170, 50}, on_click = function() open_page("bt_inventory", "page_inventory") end},
            { type = "label", id = "bt_info", text = "Info", text_size = 20, padding = 18, size = {170, 50}, on_click = function() open_page("bt_info", "page_info") end},
            { type = "view", id = "bt_health", background = 0x5588bb, size = {170, 50}, on_click = function() open_page("bt_health", "page_health") end, views = {
                { type = "label", text = "Health", text_size = 20, padding = 18 },
                { type = "label", text = "!", id = "bt_health_warning", text_size = 26, padding = 9, size = {32, 32}, position = {138, 9} },
            }},
        }},

        -- Status page
        {
            type = "list",
            id = "page_status",
            position = {12, 200},
            size = {400, 400},
            views = {
                { type = "label", text = "Current occupation", text_size = 28, position = {0, 5}},
                { type = "label", id = "lb_job", text_size = 18, position = {0, 15}, size = {-1, 28}},
                { type = "label", id = "lb_job_detail", visible = false, text_size = 14, position = {0, 12}, size = {-1, 40}},

                { type = "label", position = {0, 20}, text = "Needs", text_size = 28},
                { type = "grid", position = {0, 33}, columns = 2, column_width = 182, row_height = 44, views = {
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_food", position = {0, 0}, text = "food", text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_food", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        --                        { type = "label", id = "lb_need_food_offset", position = {158, 19}, text = "<<", text_size = 14, text_color = 0xb3d035},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_drink", position = {0, 0}, text = "drink", text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_drink", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        --                        { type = "label", id = "lb_need_drink_offset", position = {158, 19}, text = "<<", text_size = 14, text_color = 0xb3d035},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_energy", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_energy", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                        --                        { type = "label", id = "lb_need_energy_offset", position = {158, 19}, text = "<<", text_size = 14, text_color = 0xb3d035},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_happiness", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_happiness", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_health", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_health", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_joy", position = {0, 0}, text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_joy", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_relation", position = {0, 0}, text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_relation", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "lb_need_oxygen", position = {0, 0}, text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "gauge_oxygen", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                    { type = "view", size = {170, 44}, views = {
                        { type = "label", id = "", position = {0, 0}, text = "energy", text_size = 14, text_color = 0xb3d035},
                        { type = "image", id = "", position = {0, 16}, src = "[base]/graphics/needbar.png", size = {100, 100}, texture_rect = {0, 0, 100, 16}},
                    }},
                }},

                { type = "label", position = {0, 70}, text = "Buffs", text_size = 28},
                { type = "list", id = "list_buffs", position = {0, 85}}
            }
        },

        -- Inventory page
        {
            type = "view",
            id = "page_inventory",
            position = {0, 200},
            size = {400, 400},
            views = {
                { type = "label", id = "lb_inventory", position = {0, 10}, text_size = 14},
                { type = "grid", position = {0, 24}, columns = 10, column_width = 32, row_height = 32, views = {
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                    { type = "image", position = {0, 16}, src = "[base]/graphics/ic_blueprint.png", size = {32, 32}},
                }}
            }
        },

        -- Info page
        {
            type = "view",
            id = "page_info",
            position = {0, 200},
            size = {400, 400},
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

        -- Health page
        {
            type = "view",
            id = "page_health",
            position = {0, 200},
            size = {400, 400},
            views = {
                { type = "list", position = {10, 16}, views = {
                    { type = "label", text = "Diseases", position = {0, 10}, text_size = 24},
                    { type = "list", id = "list_diseases", position = {0, 20}},
                }},
            }
        },
    },

    on_game_start = function()
        open_page("bt_status", "page_status")
    end,

    on_event = function(view, event, data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            application.game:clearSelection();
            view:setVisible(false)
            character = nil
        end

        if event == application.events.on_deselect then
            view:setVisible(false)
            character = nil
        end

        if event == application.events.on_character_selected then
            view:setVisible(true)
            view:findById("lb_name"):setText(data:getName())

            view:findById("lb_info_birth"):setDashedString("Birth", data:getPersonals():getEnlisted(), 47)
            view:findById("lb_info_enlisted"):setDashedString("Enlisted", data:getPersonals():getEnlisted(), 47)
            character = data;

            display_talents(view)
        end
    end,

    on_refresh = function(view)
        if character ~= nil then
            local job = character:getJob()
            if job then
                view:findById("lb_job"):setDashedString(job:getLabel(), math.floor(job:getProgress() * 100), 35)
            else
                view:findById("lb_job"):setText("No job")
            end

            --            if job and job:getMessage() then
            --                view:findById("lb_job_detail"):setText(job:getMessage())
            --                view:findById("lb_job_detail"):setVisible(true)
            --            else
            --                view:findById("lb_job_detail"):setVisible(false)
            --            end

            display_buffs(view, character)
            display_diseases(view, character)

            local needs = character:getNeeds()
            displayNeed(view, "lb_need_energy", "gauge_energy", "Energy", character:getNeeds():get("energy"))
            displayNeed(view, "lb_need_food", "gauge_food", "Food", character:getNeeds():get("food"))
            displayNeed(view, "lb_need_drink", "gauge_drink", "Drink", needs:get("drink"))
            displayNeed(view, "lb_need_happiness", "gauge_happiness", "Mood", character:getNeeds():get("happiness"))
            displayNeed(view, "lb_need_health", "gauge_health", "Health", 0)
            displayNeed(view, "lb_need_joy", "gauge_joy", "Entertainment", character:getNeeds():get("entertainment"))
            displayNeed(view, "lb_need_relation", "gauge_relation", "Relation", character:getNeeds():get("relation"))
            displayNeed(view, "lb_need_oxygen", "gauge_oxygen", "Oxygen", character:getNeeds():get("oxygen"))

            if character:getInventory() then
                view:findById("lb_inventory"):setText("Inventory: " .. character:getInventory():getInfo().label)
            else
                view:findById("lb_inventory"):setText("Inventory: empty")
            end
        end
    end
})

function display_buffs(view, character)
    local list = view:findById("list_buffs")
    list:removeAllViews()

    local buff_module = application:getModule("BuffModule")
    if buff_module then
        local iterator = buff_module:getActiveBuffs(character):iterator()
        while iterator:hasNext() do
            local buff = iterator:next()
            if buff.level > 0 then
                local is_warning = buff.mood < 0 and buff.level > 2
                local view_buff = application.ui:createView()
                view_buff:setSize(400, 22)

                if is_warning then
                    local lb_buff_warning = application.ui:createLabel()
                    lb_buff_warning:setText("!")
                    lb_buff_warning:setSize(11, 13)
                    lb_buff_warning:setTextColor(0x121c1e)
                    lb_buff_warning:setPadding(2)
                    lb_buff_warning:setBackgroundColor(buff.mood > 0 and 0xb3d035 or 0xff5555)
                    view_buff:addView(lb_buff_warning)
                end

                local lb_buff = application.ui:createLabel()
                lb_buff:setDashedString(buff.message, buff.mood, is_warning and 42 or 44)
                lb_buff:setSize(400, 22)
                lb_buff:setPosition(is_warning and 18 or 0, 3)
                lb_buff:setTextColor(buff.mood > 0 and 0xb3d035 or 0xff5555)
                lb_buff:setOnClickListener(function()
                    application.events:send("debug.open_buff", data)
                end)
                view_buff:addView(lb_buff)

                list:addView(view_buff)
            end
        end
    end
end

function display_diseases(view, character)
    local list = view:findById("list_diseases")
    list:removeAllViews()

    local level = 0
    local iterator = character:getDiseases():iterator()
    while iterator:hasNext() do
        local disease = iterator:next()

        local lb_disease = application.ui:createLabel()
        lb_disease:setText(disease.message)
        lb_disease:setSize(400, 22)
        list:addView(lb_disease)

        if disease.level > level then
            level = disease.level
        end
    end

    --    local bt_health_image = view:findById("bt_health_image")
    --    bt_health_image:setVisible(level > 0)
    --    bt_health_image:setImage(level > 2 and "[base]/graphics/icons/warning_hight.png" or "[base]/graphics/icons/warning_low.png")

    local bt_health_warning = view:findById("bt_health_warning")
    bt_health_warning:setVisible(level > 0)
    bt_health_warning:setBackgroundColor(level > 2 and 0x801a1c or 0x164b4b)

end

function display_talents(view)
    local list = view:findById("list_talents")
    list:removeAllViews()

    local iterator = character:getTalents():getAll():iterator()
    while iterator:hasNext() do
        local talent = iterator:next()
        local frame_talent = application.ui:createView()
        frame_talent:setSize(400, 24)

        local lb_talent = application.ui:createLabel()
        lb_talent:setText(talent.name)
        frame_talent:addView(lb_talent)

        local lb_up = application.ui:createLabel()
        lb_up:setText("up")
        lb_up:setSize(50, 22)
        lb_up:setBackgroundColor(0xff0000)
        lb_up:setPosition(200, 0)
        lb_up:setOnClickListener(function(v)
            character:getTalents():moveTalent(talent, -1)
            display_talents(view)
        end)
        frame_talent:addView(lb_up)

        local lb_down = application.ui:createLabel()
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

function displayNeed(view, lb_res_id, gauge_res_id, label, value)
    view:findById(lb_res_id):setDashedString(label, math.floor(value), 21)
    view:findById(gauge_res_id):setTextureRect(0, 80, math.floor(value * 170 / 100 / 10) * 10, 16)
end

function open_page(bt_name, page_name)
    local panel = application.ui:findById("base.ui.info_character")

    for key, value in ipairs({"page_status", "page_inventory", "page_info", "page_health"}) do
        panel:findById(value):setVisible(false)
    end
    panel:findById(page_name):setVisible(true)

    for key, value in ipairs({"bt_status", "bt_inventory", "bt_info", "bt_health"}) do
        panel:findById(value):setBackgroundColor(0x689999)
    end
    panel:findById(bt_name):setBackgroundColor(0x4be7da)
end