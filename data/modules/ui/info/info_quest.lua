local quest

data:extend({
    type = "view",
    name = "base.ui.info_quest",
    align = {"center", "center"},
    size = {680, 400},
    visible = false,
    level = 100,
    views = {
        { type = "image", src = "[base]/graphics/bg_quest.png", size = {680, 400}},
        { type = "view", margin = {10, 0, 0, 10}, views = {
            { type = "label", id = "lb_label", text = "name", text_size = 32, padding = 10, position = {6, 5}, size = {100, 30}},
            { type = "label", id = "lb_message", text = "name", text_size = 16, padding = 10, position = {6, 40}, size = {100, 30}},
            { type = "list", id = "list_options", position = {16, 396}},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_open_quest then
            quest = data

            view:setVisible(true)
            view:findById("lb_label"):setText(data.info.label)
            view:findById("lb_message"):setText(data.info.openMessage)

            local list_options = application.ui:findById("list_options")
            list_options:removeAllViews()
            list_options:setPosition(16, 396 - (26 * data.info.openOptions.length))
            for i = 1, data.info.openOptions.length, 1 do
                local lb_option = application.ui:createLabel()
                lb_option:setText("[" .. i .. "] " .. data.info.openOptions[i])
                lb_option:setTextSize(16)
                lb_option:setSize(100, 26)
                list_options:addView(lb_option)
            end
        end

        if quest and event == application.events.on_key_press then
            local quest_module = application:getModule("QuestModule")
            if data == "D_1" then quest_module:setChoice(quest, 1) end
            if data == "D_2" then quest_module:setChoice(quest, 2) end
            if data == "D_3" then quest_module:setChoice(quest, 3) end
            if data == "D_4" then quest_module:setChoice(quest, 4) end
            if data == "D_5" then quest_module:setChoice(quest, 5) end
            quest = null
            view:setVisible(false)
        end
    end,
})