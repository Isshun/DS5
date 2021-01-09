local g_visitor

data:extend({
    label = "Refugee",
    id = "base.quest.refugee",
    type = "quest",

    open_message = "Un groupe de chercheurs s'est perdu lors d'une expédition et vous demande de\nles heberger pour la nuit, l'un d'eux semble être bléssé.\n\nIls n'ont helas aucuns biens de valeur à vous proposer en échange",
    open_options = {
        "Les loger et leur fournir du matériel / vivre",
        "Les loger uniquement",
        "Leur refuser l'entrée"
    },

    on_check = function (quest)
        return true
    end,

    on_start = function (quest, option_index)
        if quest.option == 3 then
            return false
        end

        local character_module = application:getModule("CharacterModule")
        if character_module then
            g_visitor = character_module:addVisitor()
--            print("add " .. g_visitor.name)
        end
    end,

    on_update = function (quest)
        return quest.tick > 100
    end,

    on_close = function (quest)
        if g_visitor and g_visitor:isAlive() then
            application.friendly:remove(g_visitor)

            local reward = math.random(3)
            if reward == 1 then
                quest.closeMessage = "Le groupe de chercheur est reparti sans encombre, pour vous remercier il vous\nlaisse leur robot de protocole: B5"
                quest.rewards:addCrew(application.factory:createCharacter("droid"))
            elseif reward == 2 then
                quest.closeMessage = "Le groupe de chercheur est parvenu à rentrer à leur base et il vous envoie en\nremerciment des fournitures medicales"
                quest.rewards:addConsumable(application.factory:createConsumable("base.seaweed", 20))
            else
                quest.closeMessage = "Le groupe de chercheur est parvenu à rentrer à leur base, ils n'ont aucun biens\nà vous envoyer mais vous fournissent une copie de leur recherches"
                quest.rewards:addResource("science", 100)
            end
            return true
        end

        return false
    end,
})